package com.tyutyutyu.oo4j.core.template;

import com.tyutyutyu.oo4j.core.DatabaseIntegrationTestExtension;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleBasicType;
import com.tyutyutyu.oo4j.core.query.OracleObjectType;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import com.tyutyutyu.oo4j.core.query.OracleType;
import com.tyutyutyu.oo4j.core.query.OracleTypeField;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(DatabaseIntegrationTestExtension.class)
@RequiredArgsConstructor
class MetadataQueryIT {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private MetadataQuery metadataQuery;

    @BeforeEach
    void beforeEach() {
        metadataQuery = new MetadataQuery(jdbcTemplate);
    }

    @DisplayName("queryTypes() should return all types")
    @Test
    void testQueryTypes1() {

        // given
        List<String> schemas = List.of("OO4J");
        Collection<String> typeExcludes = List.of();

        // when
        Map<String, OracleType> actual = metadataQuery.queryTypes(schemas, typeExcludes);

        // then
        assertThat(actual).containsOnlyKeys("OO4J.T_SIMPLE_TYPE", "OO4J.T_TEST_TYPE", "OO4J.T_TEST_TYPE_TABLE");
    }

    @DisplayName("queryTypes() should return the T_TEST_TYPE type with appropriate fields")
    @Test
    void testQueryType2() {

        // given
        List<String> schemas = List.of("OO4J");
        Collection<String> typeExcludes = List.of();

        // when
        Map<String, OracleType> actual = metadataQuery.queryTypes(schemas, typeExcludes);

        // then
        System.err.println("actual: " + actual);
        OracleObjectType oracleObjectType = (OracleObjectType) actual.get("OO4J.T_TEST_TYPE");
        assertThat(oracleObjectType.getFields().get(8).getType()).isSameAs(actual.get("OO4J.T_SIMPLE_TYPE"));

        assertThat(actual).containsEntry(
                "OO4J.T_TEST_TYPE",
                new OracleObjectType(
                        "OO4J",
                        "T_TEST_TYPE",
                        List.of(
                                new OracleTypeField("TEST_VARCHAR2", OracleBasicType.VARCHAR2),
                                new OracleTypeField("TEST_CHAR", OracleBasicType.CHAR),
                                new OracleTypeField("TEST_CLOB", OracleBasicType.CLOB),
                                new OracleTypeField("TEST_NUMBER", OracleBasicType.NUMBER),
                                new OracleTypeField("TEST_FLOAT", OracleBasicType.FLOAT),
                                new OracleTypeField("TEST_DATE", OracleBasicType.DATE),
                                new OracleTypeField("TEST_TIMESTAMP", OracleBasicType.TIMESTAMP),
                                new OracleTypeField("TEST_BLOB", OracleBasicType.BLOB),
                                new OracleTypeField("TEST_SIMPLE_TYPE",
                                        new OracleObjectType(
                                                "OO4J",
                                                "T_SIMPLE_TYPE",
                                                List.of(
                                                        new OracleTypeField("TEST_VARCHAR2", OracleBasicType.VARCHAR2)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @DisplayName("queryProcedures() should return all procedures")
    @Test
    void testQueryProcedures() {

        // given
        List<String> schemas = List.of("OO4J");
        Map<String, OracleType> typesMap = Map.of();

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures(schemas, typesMap);

        // then
        assertThat(actual)
                .map(OracleProcedure::getFullyQualifiedName)
                .containsExactly(
                        "OO4J.MIX_ORDER_PARAMS",
                        "OO4J.TEST_PROCEDURE",
                        "OO4J.TEST_PACKAGE.TEST_PROCEDURE2",
                        "OO4J.TEST_PACKAGE.TEST_PROCEDURE2"
                );
    }

    @DisplayName("Check fields count")
    @Test
    void testQueryProcedureFields1() {

        // given
        List<String> schemas = List.of("OO4J");
        Collection<String> typeExcludes = List.of();
        Map<String, OracleType> typesMap = metadataQuery.queryTypes(schemas, typeExcludes);

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures(schemas, typesMap);

        // then
        assertThat(actual
                .stream()
                .filter(oracleProcedure -> oracleProcedure.getFullyQualifiedName().equals("OO4J.TEST_PROCEDURE"))
                .findAny()
                .get()
                .getFields()
        )
                .hasSize(20);
    }

    @DisplayName("Check fields count with overloaded procedure")
    @Test
    void testQueryProcedureFields2() {

        // given
        List<String> schemas = List.of("OO4J");
        Collection<String> typeExcludes = List.of();
        Map<String, OracleType> typesMap = metadataQuery.queryTypes(schemas, typeExcludes);

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures(schemas, typesMap);

        // then
        assertThat(actual
                .stream()
                .filter(oracleProcedure ->
                        oracleProcedure.getFullyQualifiedName().equals("OO4J.TEST_PACKAGE.TEST_PROCEDURE2")
                                && oracleProcedure.getOverload() == 1
                )
                .findAny()
                .get()
                .getFields()
        )
                .containsExactly(
                        new OracleProcedureField("P_TEST_VARCHAR2", "IN", OracleBasicType.VARCHAR2),
                        new OracleProcedureField("TEST_VARCHAR2", "OUT", OracleBasicType.VARCHAR2)
                );
    }

    @DisplayName("Check fields order")
    @Test
    void testQueryProcedureFields3() {

        // given
        List<String> schemas = List.of("OO4J");
        Collection<String> typeExcludes = List.of();
        Map<String, OracleType> typesMap = metadataQuery.queryTypes(schemas, typeExcludes);

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures(schemas, typesMap);

        // then
        assertThat(actual
                .stream()
                .filter(oracleProcedure -> oracleProcedure.getFullyQualifiedName().equals("OO4J.MIX_ORDER_PARAMS"))
                .findAny()
                .get()
                .getFields()
        )
                .containsExactly(
                        new OracleProcedureField("OUT1", "OUT", OracleBasicType.VARCHAR2),
                        new OracleProcedureField("IN1", "IN", OracleBasicType.VARCHAR2),
                        new OracleProcedureField("INOUT1", "IN/OUT", OracleBasicType.VARCHAR2)
                );
    }

//    @DisplayName("With overloaded procedure")
//    @Test
//    void testQueryProcedureFields2() {
//
//        // given
//        List<String> schemas = List.of("OO4J");
//        OracleProcedure oracleProcedure = new OracleProcedure("OO4J", "TEST_PACKAGE", "TEST_PROCEDURE2", "PACKAGE", 1, 2);
//
//        // when
//        Map<OracleProcedure, List<OracleProcedureField>> actual = metadataQuery.queryProcedureFields(
//                schemas,
//                List.of(oracleProcedure),
//                typesMap);
//
//        // then
//        assertThat(actual.get(oracleProcedure)).hasSize(1);
//    }

}
