package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.query.OracleProcedure;

public interface NamingStrategy {

    String oracleAttributeNameToJavaVariableName(String oracleAttributeName);

    String oracleTypeNameToJavaClassName(String oracleTypeName);

    String getBasePackage();

    String getTypePackage(String schema);

    String getProcedurePackage(String schema);

    String getProcedureClassName(OracleProcedure oracleProcedure);

}
