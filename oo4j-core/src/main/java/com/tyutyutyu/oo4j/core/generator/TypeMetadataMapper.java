package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.javalang.ToImportListFunction;
import com.tyutyutyu.oo4j.core.query.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TypeMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaType toJavaTypeMetadata(OracleObjectType oracleObjectType) {

        List<JavaTypeField> javaTypeFields = getJavaTypeFields(oracleObjectType);

        String packageName = namingStrategy.getTypePackage(oracleObjectType.getSchema());
        List<String> imports = getImports(javaTypeFields);
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleObjectType.getName());

        return new JavaType(
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
        List<String> imports = new ArrayList<>();
        imports.add(namingStrategy.getBasePackage() + ".SqlTypeValueFactory");
        JavaClass componentClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleTableType.getComponentType());
        imports.addAll(new ToImportListFunction().apply(List.of(componentClass)));
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getName());
        String componentClassName = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getComponentType().getName());

        return new JavaTableTypeModel(
                packageName,
                imports,
                className,
                componentClassName,
                oracleTableType.getSchema(),
                oracleTableType.getName()
        );
    }

    private List<JavaTypeField> getJavaTypeFields(OracleObjectType oracleObjectType) {
        return oracleObjectType.getFields()
                .stream()
                .map(oracleTypeField -> {
                    JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleTypeField.getType());
                    return new JavaTypeField(
                            namingStrategy.oracleAttributeNameToJavaVariableName(oracleTypeField.getName()),
                            javaClass,
                            "Object".equals(javaClass.getJdbcAdaptedType())
                    );

                })
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getImports(List<JavaTypeField> javaTypeFields) {
        List<String> imports = new ToImportListFunction().apply(
                javaTypeFields
                        .stream()
                        .map(JavaTypeField::getJavaClass)
                        .collect(Collectors.toUnmodifiableList())
        );
        imports.add(namingStrategy.getBasePackage() + ".SqlReturnTypeFactory");

        return imports;
    }

}
