package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

import java.sql.JDBCType;

@Value
public class OracleCursorType implements OracleType {

    @Override
    public String getName() {
        return "CURSOR";
    }

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.REF_CURSOR;
    }

}
