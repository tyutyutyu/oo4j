package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tyutyutyu.oo4j.core.query.OracleBasicType.VARCHAR2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ParamMapperTest {

    @DisplayName("Check parameters count")
    @Test
    void testToParams1() {

        // given
        NamingStrategy namingStrategy = mock(NamingStrategy.class);
        OracleDataTypeMapper oracleDataTypeMapper = mock(OracleDataTypeMapper.class);
        ParamMapper paramMapper = new ParamMapper(namingStrategy, oracleDataTypeMapper);

        OracleProcedure oracleProcedure = new OracleProcedure(
                "OO4J",
                "SOME_PACKAGE",
                "SOME_PROC_IN_PACKAGE",
                OracleProcedure.Type.IN_PACKAGE,
                null,
                List.of(
                        new OracleProcedureField("p_param1", "IN", VARCHAR2)
                )
        );
        AtomicInteger rowMapperIndex = new AtomicInteger(0);

        // when
        List<Param> params = paramMapper.toParams(oracleProcedure, rowMapperIndex);

        // then
        assertThat(params).hasSize(1);
    }

    @DisplayName("Check parameters order")
    @Test
    void testToParams2() {

        // given
        NamingStrategy namingStrategy = mock(NamingStrategy.class);
        OracleDataTypeMapper oracleDataTypeMapper = mock(OracleDataTypeMapper.class);
        ParamMapper paramMapper = new ParamMapper(namingStrategy, oracleDataTypeMapper);

        OracleProcedure oracleProcedure = new OracleProcedure(
                "OO4J",
                null,
                "SOME_PROC",
                OracleProcedure.Type.IN_PACKAGE,
                null,
                List.of(
                        new OracleProcedureField("param1", "OUT", VARCHAR2),
                        new OracleProcedureField("param2", "IN/OUT", VARCHAR2),
                        new OracleProcedureField("param3", "IN", VARCHAR2)
                )
        );
        AtomicInteger rowMapperIndex = new AtomicInteger(0);

        // when
        List<Param> params = paramMapper.toParams(oracleProcedure, rowMapperIndex);

        // then
        assertThat(params.get(0).getName()).isEqualTo("param1");
        assertThat(params.get(1).getName()).isEqualTo("param2");
        assertThat(params.get(2).getName()).isEqualTo("param3");
    }

}