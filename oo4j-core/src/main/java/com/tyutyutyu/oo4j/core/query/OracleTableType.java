package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

import java.sql.JDBCType;

@Value
public class OracleTableType implements OracleComplexType {

    String schema;
    String name;
    OracleType componentType;

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.ARRAY;
    }

}
