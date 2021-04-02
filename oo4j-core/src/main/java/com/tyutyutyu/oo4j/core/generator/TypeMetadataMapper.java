package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.ImportCollector;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.query.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TypeMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaType toJavaTypeMetadata(OracleObjectType oracleObjectType) {

        List<JavaTypeField> javaTypeFields = getJavaTypeFields(oracleObjectType);

        String packageName = namingStrategy.getTypePackage(oracleObjectType.getSchema());
        Collection<String> imports = getImports(javaTypeFields);
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
        List<String> extraImports = List.of(
                namingStrategy.getBasePackage() + ".SqlReturnTypeFactory",
                namingStrategy.getBasePackage() + ".SqlTypeValueFactory",
                "org.springframework.jdbc.core.SqlReturnType"
        );
        JavaClass componentClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleTableType.getComponentType());
        Collection<String> imports = List
                .of(componentClass)
                .stream()
                .collect(new ImportCollector(extraImports));
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getName());
        String componentClassName = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getComponentType().getName());

        return new JavaTableTypeModel(
                packageName,
                imports,
                className,
                componentClassName,
                !(oracleTableType.getComponentType() instanceof OracleBasicType),
                oracleTableType.getSchema(),
                oracleTableType.getName()
        );
    }

    private List<JavaTypeField> getJavaTypeFields(OracleObjectType oracleObjectType) {
        return oracleObjectType.getFields()
                .stream()
                .map(this::toJavaTypeField)
                .collect(Collectors.toUnmodifiableList());
    }

    private SortedSet<String> getImports(List<JavaTypeField> javaTypeFields) {
        List<String> extraImports = new ArrayList<>();
        extraImports.add(namingStrategy.getBasePackage() + ".SqlReturnTypeFactory");
        if (javaTypeFields.stream().anyMatch(field -> field.getJavaClass() == JavaClass.BYTE_ARRAY)) {
            extraImports.add(namingStrategy.getBasePackage() + ".TypeConverter");
        }
        return javaTypeFields
                .stream()
                .map(JavaTypeField::getJavaClass)
                .collect(new ImportCollector(extraImports));
    }

    private JavaTypeField toJavaTypeField(OracleTypeField oracleTypeField) {
        JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleTypeField.getType());
        return new JavaTypeField(
                namingStrategy.oracleAttributeNameToJavaVariableName(oracleTypeField.getName()),
                javaClass,
                "Object".equals(javaClass.getJdbcAdaptedType())
        );

    }
}
