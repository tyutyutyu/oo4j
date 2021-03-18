package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.generator.NamingStrategy;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OracleDataTypeMapperTest {

    private OracleDataTypeMapper oracleDataTypeMapper;

    private NamingStrategy namingStrategy;

    @BeforeEach
    public void beforeEach() {
        namingStrategy = mock(NamingStrategy.class);
        oracleDataTypeMapper = new OracleDataTypeMapper(namingStrategy);
    }

    @MethodSource("toJavaTypeArgumentProvider")
    @ParameterizedTest
    void testOracleDataTypeToJavaType(OracleType givenOracleType, JavaClass expectedJavaType) {

        // given
        when(namingStrategy.getTypePackage(any())).thenReturn("basepackage.testschema");
        when(namingStrategy.oracleTypeNameToJavaClassName(any())).thenReturn("MyType");

        // when
        JavaClass actual = oracleDataTypeMapper.oracleDataTypeToJavaClass(givenOracleType);

        // then
        assertThat(actual).isEqualTo(expectedJavaType);
    }

    private static Stream<Arguments> toJavaTypeArgumentProvider() {
        return Stream.of(
                Arguments.of(OracleBasicType.BLOB, JavaClass.BYTE_ARRAY),
                Arguments.of(OracleBasicType.CHAR, JavaClass.STRING),
                Arguments.of(OracleBasicType.CLOB, JavaClass.STRING),
                Arguments.of(OracleBasicType.DATE, JavaClass.DATE),
                Arguments.of(OracleBasicType.FLOAT, JavaClass.DOUBLE),
                Arguments.of(OracleBasicType.NUMBER, JavaClass.BIG_DECIMAL),
                Arguments.of(OracleBasicType.RAW, JavaClass.BYTE_ARRAY),
                Arguments.of(OracleBasicType.TIMESTAMP, JavaClass.TIMESTAMP),
                Arguments.of(OracleBasicType.VARCHAR2, JavaClass.STRING),
                Arguments.of(new OracleObjectType("MY_SCHEMA", "T_TYPE", List.of()), new JavaClass(
                        "basepackage.testschema",
                        "MyType",
                        false,
                        null,
                        false,
                        null,
                        "Object"
                )),
                Arguments.of(new OracleCursorType(), JavaClass.listOf(null)),
                Arguments.of(new OracleTableType("MY_SCHEMA", "T_TYPE_TAB", new OracleObjectType("OO4J", "T_TYPE", List.of())),
                        new JavaClass(
                                "basepackage.testschema",
                                "MyType",
                                false,
                                new JavaClass(
                                        "basepackage.testschema",
                                        "MyType",
                                        false,
                                        null,
                                        false,
                                        null,
                                        null
                                ),
                                true,
                                JavaClass.ContainerType.LIST,
                                "Array"
                        ))
        );
    }

}