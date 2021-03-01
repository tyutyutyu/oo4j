package com.tyutyutyu.oo4j.core.query;

import lombok.*;

import java.sql.JDBCType;

@Value
public class OracleCursorType implements OracleComplexType {

    @Override
    public String getName() {
        return "CURSOR";
    }

    @Override
    public String getFullyQualifiedName() {
        return getName();
    }

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.REF_CURSOR;
    }

}
