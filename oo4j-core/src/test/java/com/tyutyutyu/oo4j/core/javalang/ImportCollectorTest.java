package com.tyutyutyu.oo4j.core.javalang;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

import static com.tyutyutyu.oo4j.core.query.OracleBasicType.*;
import static org.assertj.core.api.Assertions.*;

class ImportCollectorTest {

    @MethodSource("testToImportListSource")
    @ParameterizedTest
    void testToImportList(List<JavaClass> javaClasses, List<String> extraImports, List<String> expected) {

        // given

        // when
        SortedSet<String> actual = javaClasses
                .stream()
                .collect(new ImportCollector(extraImports));

        // then
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> testToImportListSource() {
        return Stream.of(
                Arguments.of(List.of(VARCHAR2.getJavaClass()), List.of(), List.of()),
                Arguments.of(List.of(JavaClass.listOf(VARCHAR2.getJavaClass())), List.of(), List.of("java.util.List")),
                Arguments.of(List.of(jc("a.b.c", "MyClass")), List.of(), List.of("a.b.c.MyClass")),
                Arguments.of(List.of(jc("d.e.f", "MyClass2")), List.of("g.h.i.ExtraClass"), List.of("d.e.f.MyClass2", "g.h.i.ExtraClass"))
        );
    }

    private static JavaClass jc(String packageName, String className) {
        return new JavaClass(packageName, className, false, null, false, null, null);
    }

}