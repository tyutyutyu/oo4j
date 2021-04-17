package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.query.OracleBasicType;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleObjectType;
import com.tyutyutyu.oo4j.core.query.OracleTypeField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeMetadataMapperTest {

    private TypeMetadataMapper typeMetadataMapper;

    private NamingStrategy namingStrategy;
    private OracleDataTypeMapper oracleDataTypeMapper;
    private TypeFieldMetadataMapper typeFieldMetadataMapper;

    @BeforeEach
    public void beforeEach() {
        namingStrategy = mock(NamingStrategy.class);
        oracleDataTypeMapper = mock(OracleDataTypeMapper.class);
        typeFieldMetadataMapper = mock(TypeFieldMetadataMapper.class);

        typeMetadataMapper = new TypeMetadataMapper(
                namingStrategy,
                oracleDataTypeMapper,
                typeFieldMetadataMapper
        );
    }

    @DisplayName("Check mapped JavaType.packageName")
    @Test
    void testToJavaTypeMetadata1() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of();
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        String targetPackage = "target.package";
        when(namingStrategy.getTypePackage(schema)).thenReturn(targetPackage);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getPackageName()).isEqualTo(targetPackage);
    }

    @DisplayName("Check mapped JavaType.imports")
    @Test
    void testToJavaTypeMetadata2() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of();
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        String basePackage = "target.package";
        when(namingStrategy.getBasePackage()).thenReturn(basePackage);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getImports()).containsExactly(
                basePackage + ".SqlReturnTypeFactory"
        );
    }

    @DisplayName("Check mapped JavaType.className")
    @Test
    void testToJavaTypeMetadata3() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of();
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        String className = "RtClass";
        when(namingStrategy.oracleTypeNameToJavaClassName(any())).thenReturn(className);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getClassName()).isEqualTo(className);
    }

    @DisplayName("Check mapped JavaType.schema")
    @Test
    void testToJavaTypeMetadata4() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of();
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getSchema()).isEqualTo(schema);
    }

    @DisplayName("Check mapped JavaType.typeName")
    @Test
    void testToJavaTypeMetadata5() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of();
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getTypeName()).isEqualTo(name);
    }

    @DisplayName("Check mapped JavaType.fields")
    @Test
    void testToJavaTypeMetadata6() {

        // given
        String schema = "ASD_SCHEMA";
        String name = "QWERTY_PROC";
        List<OracleTypeField> fields = List.of(
                new OracleTypeField("field1", OracleBasicType.FLOAT)
        );
        OracleObjectType oracleObjectType = new OracleObjectType(schema, name, fields);

        JavaTypeField javaTypeField = new JavaTypeField("name", JavaClass.DOUBLE, false);
        when(typeFieldMetadataMapper.toJavaTypeField(fields.get(0))).thenReturn(javaTypeField);

        // when
        JavaType actual = typeMetadataMapper.toJavaTypeMetadata(oracleObjectType);

        // then
        assertThat(actual.getFields()).containsExactly(javaTypeField);
    }

    @Test
    void testToJavaTableTypeMetadata() {
    }

}