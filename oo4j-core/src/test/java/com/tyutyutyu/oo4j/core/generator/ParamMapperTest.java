package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.query.OracleBasicType;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tyutyutyu.oo4j.core.query.OracleBasicType.VARCHAR2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ParamMapperTest {

    @Test
    void testToParams() {

        // given
        NamingStrategy namingStrategy = mock(NamingStrategy.class);
        OracleDataTypeMapper oracleDataTypeMapper = mock(OracleDataTypeMapper.class);
        ParamMapper paramMapper = new ParamMapper(namingStrategy, oracleDataTypeMapper);

        OracleProcedure oracleProcedure = new OracleProcedure(
                "OO4J",
                "PACKAGE1",
                "PROCEDURE1",
                OracleProcedure.Type.IN_PACKAGE,
                null,
                List.of(
                        new OracleProcedureField("p_param1", "IN", VARCHAR2)
                )
        );
        AtomicInteger rowMapperIndex = new AtomicInteger(0);
        String inOut = "IN";

        // when
        List<Param> params = paramMapper.toParams(oracleProcedure, rowMapperIndex, inOut);

        // then
        assertThat(params).hasSize(1);
    }

}