package com.tyutyutyu.oo4j.core;

import com.google.common.base.CaseFormat;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultNamingStrategy implements NamingStrategy {

    private final String basePackage;

    @Override
    public String oracleTypeNameToJavaClassName(String oracleTypeName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, oracleTypeName);
    }

    @Override
    public String oracleAttributeNameToJavaVariableName(String oracleAttributeName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, oracleAttributeName);
    }

    @Override
    public String getTypePackage(String schema) {
        return String.format("%s.%s.type", basePackage, schema.toLowerCase());
    }

    @Override
    public String getProcedurePackage(String schema) {
        return String.format("%s.%s.procedure", basePackage, schema.toLowerCase());
    }

    public String getProcedureClassName(OracleProcedure oracleProcedure) {
        return "PACKAGE".equals(oracleProcedure.getObjectType())
                ? oracleTypeNameToJavaClassName(oracleProcedure.getObjectName() + "_" + oracleProcedure.getProcedureName())
                : oracleTypeNameToJavaClassName(oracleProcedure.getObjectName());
    }

}
