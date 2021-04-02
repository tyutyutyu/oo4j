package com.tyutyutyu.oo4j.core.generator;

import com.google.common.base.CaseFormat;
import com.tyutyutyu.oo4j.core.query.OracleBasicType;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleType;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class DefaultNamingStrategy implements NamingStrategy {

    private static final String PACKAGE_NAME_REPLACE_REGEX = "[_\\-]";

    private final String basePackage;

    @Override
    public String oracleTypeNameToJavaClassName(String oracleTypeName) {
        if (OracleType.isBasicType(oracleTypeName)) {
            return OracleBasicType.valueOf(oracleTypeName).getJavaClass().getClassName();
        }
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, oracleTypeName);
    }

    @Override
    public String oracleAttributeNameToJavaVariableName(String oracleAttributeName) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, oracleAttributeName);
    }

    @Override
    public String getBasePackage() {
        return basePackage;
    }

    @Override
    public String getTypePackage(String schema) {
        return String.format("%s.%s.type", basePackage, scheamToPackageName(schema));
    }

    @Override
    public String getProcedurePackage(String schema) {
        return String.format("%s.%s.procedure", basePackage, scheamToPackageName(schema));
    }

    public String getProcedureClassName(OracleProcedure oracleProcedure) {
        String overloadMark = oracleProcedure.getOverload() != null
                ? String.valueOf(oracleProcedure.getOverload())
                : "";
        return oracleProcedure.getType() == OracleProcedure.Type.IN_PACKAGE
                ? oracleTypeNameToJavaClassName(oracleProcedure.getObjectName() + "_" + oracleProcedure.getProcedureName()) + overloadMark
                : oracleTypeNameToJavaClassName(oracleProcedure.getObjectName());
    }

    private static String scheamToPackageName(String packageName) {
        return packageName.toLowerCase(Locale.ENGLISH).replaceAll(PACKAGE_NAME_REPLACE_REGEX, "");
    }

}
