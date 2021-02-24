package com.tyutyutyu.oo4j.core.javalang;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.tyutyutyu.oo4j.core.query.OracleBasicType.VARCHAR2;
import static org.assertj.core.api.Assertions.assertThat;

class JavaClassUtilsTest {

    @MethodSource("testToImportListSource")
    @ParameterizedTest
    void testToImportList(Stream<JavaClass> javaClassStream, List<String> expected) {

        // given

        // when
        List<String> actual = JavaClassUtils.toImportList(javaClassStream);

        // then
        assertThat(actual).containsExactly(expected.toArray(String[]::new));
    }

    private static Stream<Arguments> testToImportListSource() {
        return Stream.of(
                Arguments.of(Stream.of(VARCHAR2.getJavaClass()), List.of()),
                Arguments.of(Stream.of(JavaClass.listOf(null)), List.of("java.util.List")),
                Arguments.of(Stream.of(jc("a.b.c", "MyClass", false)), List.of("a.b.c.MyClass"))
        );
    }

    private static JavaClass jc(String packageName, String className, boolean primitive) {
        return new JavaClass(packageName, className, primitive, null, false, null, null);
    }

}