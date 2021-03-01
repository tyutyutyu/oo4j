package com.tyutyutyu.oo4j.core.query;

import lombok.*;

import java.sql.JDBCType;

@Value
public class OracleTableType implements OracleComplexType {

    String schema;
    String name;
    String componentTypeName;

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.ARRAY;
    }

    @Override
    public String getFullyQualifiedName() {
        return schema + "." + name;
    }
}
