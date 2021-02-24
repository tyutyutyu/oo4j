package com.tyutyutyu.oo4j.core.javalang;

import com.tyutyutyu.oo4j.core.javalang.JavaClassUtils.ImportPredicate;
import com.tyutyutyu.oo4j.core.query.OracleBasicType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ImportPredicateTest {

    @DisplayName("Class from java.lang")
    @Test
    void testTest1() {

        // given
        ImportPredicate importPredicate = new ImportPredicate();
        JavaClass javaClass = OracleBasicType.VARCHAR2.getJavaClass();

        // when
        boolean actual = importPredicate.test(javaClass);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("Class from java.util")
    @Test
    void testTest2() {

        // given
        ImportPredicate importPredicate = new ImportPredicate();
        JavaClass javaClass = new JavaClass("java.util", "List", false, null, true, null, null);

        // when
        boolean actual = importPredicate.test(javaClass);

        // then
        assertThat(actual).isTrue();
    }

}