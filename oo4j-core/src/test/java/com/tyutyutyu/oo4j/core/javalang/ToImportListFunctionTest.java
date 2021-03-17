package com.tyutyutyu.oo4j.core.javalang;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.tyutyutyu.oo4j.core.query.OracleBasicType.VARCHAR2;
import static org.assertj.core.api.Assertions.assertThat;

class ToImportListFunctionTest {

    @MethodSource("testToImportListSource")
    @ParameterizedTest
    void testToImportList(List<JavaClass> javaClasses, List<String> expected) {

        // given

        // when
        List<String> actual = new ToImportListFunction().apply(javaClasses);

        // then
        assertThat(actual).containsExactly(expected.toArray(String[]::new));
    }

    private static Stream<Arguments> testToImportListSource() {
        return Stream.of(
                Arguments.of(List.of(VARCHAR2.getJavaClass()), List.of()),
                Arguments.of(List.of(JavaClass.listOf(VARCHAR2.getJavaClass())), List.of("java.util.List")),
                Arguments.of(List.of(jc("a.b.c", "MyClass", false)), List.of("a.b.c.MyClass"))
        );
    }

    private static JavaClass jc(String packageName, String className, boolean primitive) {
        return new JavaClass(packageName, className, primitive, null, false, null, null);
    }

}