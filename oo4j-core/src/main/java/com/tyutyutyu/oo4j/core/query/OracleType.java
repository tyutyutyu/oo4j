package com.tyutyutyu.oo4j.core.query;

import java.sql.JDBCType;
import java.util.Arrays;

public interface OracleType {

    String getName();

    JDBCType getJdbcType();

    static boolean isBasicType(String typeName) {
        return Arrays
                .stream(OracleBasicType.values())
                .map(OracleBasicType::getOracleDataType)
                .anyMatch(name -> name.equals(typeName));
    }

    static Class<? extends OracleType> getTypeByDataType(String dataType) {
        if (isBasicType(dataType)) {
            return OracleBasicType.class;
        } else if ("OBJECT".equals(dataType)) {
            return OracleObjectType.class;
        } else if ("REF CURSOR".equals(dataType)) {
            return OracleCursorType.class;
        } else if ("TABLE".equals(dataType)) {
            return OracleTableType.class;
        } else {
            throw new IllegalStateException("TODO, dataType: " + dataType);
        }
    }

}
