package com.tyutyutyu.oo4j.core;

import com.tyutyutyu.oo4j.core.generator.JavaTypeMetadata;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class TypeMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaTypeMetadata toJavaTypeMetadata(String schema, String typeName, List<OracleTypeField> oracleTypeFields) {

        List<JavaTypeField> javaTypeFields = oracleTypeFields
                .stream()
                .map(oracleTypeField -> {
                    String javaTypeName = oracleDataTypeMapper.oracleDataTypeToJavaType(
                            "OBJECT",
                            schema,
                            oracleTypeField.getTypeName()
                    );
                    return new JavaTypeField(
                            namingStrategy.oracleAttributeNameToJavaVariableName(oracleTypeField.getName()),
                            javaTypeName,
                            ClassUtils.getShortName(javaTypeName)
                    );

                })
                .collect(Collectors.toUnmodifiableList());


        String packageName = namingStrategy.getTypePackage(schema);
        List<String> imports = javaTypeFields
                .stream()
                .filter(javaTypeField -> !javaTypeField.getClassName().startsWith("java.lang"))
                .map(JavaTypeField::getClassName)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
        String className = namingStrategy.oracleTypeNameToJavaClassName(typeName);

        return new JavaTypeMetadata(
                packageName,
                imports,
                className,
                schema,
                typeName,
                javaTypeFields
        );
    }

}
