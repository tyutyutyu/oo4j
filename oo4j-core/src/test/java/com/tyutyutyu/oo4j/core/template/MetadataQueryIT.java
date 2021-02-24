package com.tyutyutyu.oo4j.core.template;

import com.tyutyutyu.oo4j.core.DatabaseIntegrationTestExtension;
import com.tyutyutyu.oo4j.core.query.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static com.tyutyutyu.oo4j.core.query.MetadataQuery.TypeCode;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DatabaseIntegrationTestExtension.class)
@RequiredArgsConstructor
class MetadataQueryIT {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private MetadataQuery metadataQuery;

    @BeforeEach
    void beforeEach() {
        metadataQuery = new MetadataQuery(jdbcTemplate);
    }

    @Test
    void testQueryTypesWithObjectType() {

        // given

        // when
        List<String> actual = metadataQuery.queryTypeNames("OO4J", TypeCode.OBJECT);

        // then
        assertThat(actual).containsExactlyInAnyOrder("T_SIMPLE_TYPE", "T_TEST_TYPE");
    }

    @Test
    void testQueryTypesWithTableType() {

        // given

        // when
        List<String> actual = metadataQuery.queryTypeNames("OO4J", TypeCode.COLLECTION);

        // then
        assertThat(actual).containsExactly("T_TEST_TYPE_TABLE");
    }

    @Test
    void testQueryTypeFields() {

        // given

        // when
        List<OracleTypeField> actual = metadataQuery.queryTypeFields("OO4J", "T_TEST_TYPE");

        // then
        assertThat(actual).containsExactly(
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
                                "T_SIMPLE_TYPE",
                                List.of(
                                        new OracleTypeField("TEST_VARCHAR2", OracleBasicType.VARCHAR2)
                                )
                        )
                )
        );
    }

    @Test
    void testQueryProcedures() {

        // given

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures("OO4J");

        // then
        assertThat(actual).containsExactly(
                new OracleProcedure("OO4J", "TEST_PROCEDURE", null, "PROCEDURE", 1, null),
                new OracleProcedure("OO4J", "TEST_PACKAGE", "TEST_PROCEDURE2", "PACKAGE", 1, 1),
                new OracleProcedure("OO4J", "TEST_PACKAGE", "TEST_PROCEDURE2", "PACKAGE", 2, 2)
        );
    }

    @Test
    void testQueryProcedureFields1() {

        // given

        // when
        List<OracleProcedureField> actual = metadataQuery.queryProcedureFields(
                new OracleProcedure("OO4J", null, "TEST_PROCEDURE", "PROCEDURE", 1, null)
        );

        // then
        assertThat(actual).hasSize(20);
    }

    @DisplayName("With overloaded procedure")
    @Test
    void testQueryProcedureFields2() {

        // given

        // when
        List<OracleProcedureField> actual = metadataQuery.queryProcedureFields(
                new OracleProcedure("OO4J", "TEST_PACKAGE", "TEST_PROCEDURE2", "PACKAGE", 1, 2)
        );

        // then
        assertThat(actual).hasSize(1);
    }

}
