package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OracleDataTypeMapperTest {

    @MethodSource("toJavaTypeArgumentProvider")
    @ParameterizedTest
    void testOracleDataTypeToJavaType(String oracleType, String javaType) {

        // given
        NamingStrategy namingStrategy = mock(NamingStrategy.class);
        OracleDataTypeMapper oracleDataTypeMapper = new OracleDataTypeMapper(namingStrategy);

        // when
        String actual = oracleDataTypeMapper.oracleDataTypeToJavaType(oracleType, null, null);

        // then
        assertThat(actual).isEqualTo(javaType);
    }

    @MethodSource("toJdbcTypeArgumentProvider")
    @ParameterizedTest
    void testToJdbcType(String oracleType, String jdbcType) {

        // given
        NamingStrategy namingStrategy = mock(NamingStrategy.class);
        OracleDataTypeMapper oracleDataTypeMapper = new OracleDataTypeMapper(namingStrategy);

        // when
        String actual = oracleDataTypeMapper.toJdbcType(oracleType);

        // then
        assertThat(actual).isEqualTo(jdbcType);
    }

    private static Stream<Arguments> toJavaTypeArgumentProvider() {
        return Stream.of(
                Arguments.of("VARCHAR2", "java.lang.String"),
                Arguments.of("CHAR", "java.lang.String"),
                Arguments.of("CLOB", "java.sql.Clob"),
                Arguments.of("NUMBER", "java.math.BigDecimal"),
                Arguments.of("FLOAT", "java.lang.Double"),
                Arguments.of("DATE", "java.sql.Date"),
                Arguments.of("TIMESTAMP", "java.sql.Timestamp"),
                Arguments.of("RAW", "byte[]"),
                Arguments.of("BLOB", "java.sql.Blob")
        );
    }

    private static Stream<Arguments> toJdbcTypeArgumentProvider() {
        return Stream.of(
                Arguments.of("VARCHAR2", "VARCHAR"),
                Arguments.of("CHAR", "CHAR"),
                Arguments.of("CLOB", "CLOB"),
                Arguments.of("NUMBER", "NUMERIC"),
                Arguments.of("FLOAT", "FLOAT"),
                Arguments.of("DATE", "DATE"),
                Arguments.of("TIMESTAMP", "TIMESTAMP"),
                Arguments.of("RAW", "BINARY"),
                Arguments.of("BLOB", "BLOB")
        );
    }

}