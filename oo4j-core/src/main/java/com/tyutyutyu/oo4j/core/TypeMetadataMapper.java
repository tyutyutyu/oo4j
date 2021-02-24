package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.generator.JavaTypeModel;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.JavaClassUtils;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleTypeField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TypeMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaTypeModel toJavaTypeMetadata(String schema, String typeName, List<OracleTypeField> oracleTypeFields) {

        List<JavaTypeField> javaTypeFields = getJavaTypeFields(schema, oracleTypeFields);

        String packageName = namingStrategy.getTypePackage(schema);
        List<String> imports = getImports(javaTypeFields);
        String className = namingStrategy.oracleTypeNameToJavaClassName(typeName);

        return new JavaTypeModel(
                packageName,
                imports,
                className,
                schema,
                typeName,
                javaTypeFields
        );
    }

    @Nonnull
    private List<JavaTypeField> getJavaTypeFields(String schema, List<OracleTypeField> oracleTypeFields) {
        return oracleTypeFields
                .stream()
                .map(oracleTypeField -> {
                    JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(
                            oracleTypeField.getType(),
                            schema
                    );
                    return new JavaTypeField(
                            namingStrategy.oracleAttributeNameToJavaVariableName(oracleTypeField.getName()),
                            javaClass,
                            "Object".equals(javaClass.getJdbcAdaptedType()) // TODO: FI: review
                    );

                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Nonnull
    private List<String> getImports(List<JavaTypeField> javaTypeFields) {
        return JavaClassUtils.toImportList(javaTypeFields
                .stream()
                .map(JavaTypeField::getJavaClass));
    }

}
