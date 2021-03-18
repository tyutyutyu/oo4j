package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.generator.DefaultNamingStrategy;
import com.tyutyutyu.oo4j.core.generator.NamingStrategy;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tyutyutyu.oo4j.core.query.OracleProcedure.Type.IN_PACKAGE;
import static com.tyutyutyu.oo4j.core.query.OracleProcedure.Type.STANDALONE;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultNamingStrategyTest {

    @Test
    void testOracleTypeNameToJavaClassName() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        String oracleTypeName = "T_TYPE1";

        // when
        String actual = namingStrategy.oracleTypeNameToJavaClassName(oracleTypeName);

        // then
        assertThat(actual).isEqualTo("TType1");
    }

    @Test
    void testOracleAttributeNameToJavaVariableName() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        String oracleAttributeName = "oracle_attribute";

        // when
        String actual = namingStrategy.oracleAttributeNameToJavaVariableName(oracleAttributeName);

        // then
        assertThat(actual).isEqualTo("oracleAttribute");
    }

    @Test
    void testGetTypePackage() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        String schema = "MY_SCHEMA";

        // when
        String actual = namingStrategy.getTypePackage(schema);

        // then
        assertThat(actual).isEqualTo("a.b.c.myschema.type");
    }

    @Test
    void testGetProcedurePackage() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        String schema = "MY_SCHEMA";

        // when
        String actual = namingStrategy.getProcedurePackage(schema);

        // then
        assertThat(actual).isEqualTo("a.b.c.myschema.procedure");
    }

    @DisplayName("With procedure in package")
    @Test
    void testGetProcedureClassName1() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        OracleProcedure oracleProcedure = new OracleProcedure(null, "SOME_PACKAGE", "SOME_PROCEDURE", IN_PACKAGE, null, null);

        // when
        String actual = namingStrategy.getProcedureClassName(oracleProcedure);

        // then
        assertThat(actual).isEqualTo("SomePackageSomeProcedure");
    }

    @DisplayName("With overloaded procedure in package")
    @Test
    void testGetProcedureClassName2() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        OracleProcedure oracleProcedure = new OracleProcedure(null, "SOME_PACKAGE", "SOME_PROCEDURE", IN_PACKAGE, 1, null);

        // when
        String actual = namingStrategy.getProcedureClassName(oracleProcedure);

        // then
        assertThat(actual).isEqualTo("SomePackageSomeProcedure1");
    }

    @DisplayName("With standalone procedure")
    @Test
    void testGetProcedureClassName3() {

        // given
        String basePackage = "a.b.c";
        NamingStrategy namingStrategy = new DefaultNamingStrategy(basePackage);
        OracleProcedure oracleProcedure = new OracleProcedure(null, "SOME_PROCEDURE", null, STANDALONE, null, null);

        // when
        String actual = namingStrategy.getProcedureClassName(oracleProcedure);

        // then
        assertThat(actual).isEqualTo("SomeProcedure");
    }

}