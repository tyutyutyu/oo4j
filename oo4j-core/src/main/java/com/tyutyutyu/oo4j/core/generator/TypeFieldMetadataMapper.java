package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleTypeField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TypeFieldMetadataMapper {

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    public JavaTypeField toJavaTypeField(OracleTypeField oracleTypeField) {
        JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleTypeField.getType());
        return new JavaTypeField(
                namingStrategy.oracleAttributeNameToJavaVariableName(oracleTypeField.getName()),
                javaClass,
                "Object".equals(javaClass.getJdbcAdaptedType())
        );

    }

}
