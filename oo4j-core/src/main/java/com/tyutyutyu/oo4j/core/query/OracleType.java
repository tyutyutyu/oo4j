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

    static Class<? extends OracleType> getTypeByDataType(String typeCode, String dataType) {
        if (isBasicType(dataType)) {
            return OracleBasicType.class;
        } else if ("OBJECT".equals(typeCode)) {
            return OracleObjectType.class;
        } else if ("COLLECTION".equals(typeCode)) {
            return OracleTableType.class;
        } else if ("REF CURSOR".equals(dataType)) {
            return OracleCursorType.class;
        } else {
            throw new IllegalStateException(String.format("TODO, typeCode: %s, dataType: %s", typeCode, dataType));
        }
    }

}
