package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.generator.JavaTableTypeModel;
import com.tyutyutyu.oo4j.core.generator.JavaTypeModel;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.JavaClassUtils;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleObjectType;
import com.tyutyutyu.oo4j.core.query.OracleTableType;
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

    public JavaTypeModel toJavaTypeMetadata(OracleObjectType oracleObjectType) {

        List<JavaTypeField> javaTypeFields = getJavaTypeFields(oracleObjectType);

        String packageName = namingStrategy.getTypePackage(oracleObjectType.getSchema());
        List<String> imports = getImports(javaTypeFields);
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleObjectType.getName());

        return new JavaTypeModel(
                packageName,
                imports,
                className,
                oracleObjectType.getSchema(),
                oracleObjectType.getName(),
                javaTypeFields
        );
    }

    public JavaTableTypeModel toJavaTableTypeMetadata(OracleTableType oracleTableType) {

        String packageName = namingStrategy.getTypePackage(oracleTableType.getSchema());
        List<String> imports = List.of();
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getName());
        String componentClassName = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getComponentTypeName());

        return new JavaTableTypeModel(
                packageName,
                imports,
                className,
                componentClassName,
                oracleTableType.getSchema(),
                oracleTableType.getName()
        );
    }

    @Nonnull
    private List<JavaTypeField> getJavaTypeFields(OracleObjectType oracleObjectType) {
        return oracleObjectType.getFields()
                .stream()
                .map(oracleTypeField -> {
                    JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(
                            oracleTypeField.getType(),
                            oracleObjectType.getSchema()
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
