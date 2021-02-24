package com.tyutyutyu.oo4j.core.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.JDBCType;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class OracleCursorType implements OracleComplexType {

    @Override
    public String getName() {
        return "CURSOR";
    }

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.REF_CURSOR;
    }

}
