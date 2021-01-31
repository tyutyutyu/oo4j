package com.tyutyutyu.oo4j.core.template;

import com.tyutyutyu.oo4j.core.DatabaseIntegrationTestExtension;
import com.tyutyutyu.oo4j.core.OracleTypeField;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.MetadataQuery.TypeCode;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

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
    void testQueryTypes() {

        // given

        // when
        List<String> actual = metadataQuery.queryTypes("OO4J", TypeCode.OBJECT);

        // then
        assertThat(actual).containsExactly("T_TEST_TYPE");
    }

    @Test
    void testQueryTypeFields() {

        // given

        // when
        List<OracleTypeField> actual = metadataQuery.queryTypeFields("OO4J", "T_TEST_TYPE");

        // then
        assertThat(actual).hasSize(9);
    }

    @Test
    void testQueryProcedures() {

        // given

        // when
        List<OracleProcedure> actual = metadataQuery.queryProcedures("OO4J");

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    void testQueryProcedureFields() {

        // given

        // when
        List<OracleProcedureField> actual = metadataQuery.queryProcedureFields("OO4J", null, "TEST_PROCEDURE");

        // then
        assertThat(actual).hasSize(20);
    }

}
