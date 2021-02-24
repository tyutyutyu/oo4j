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
public class OracleTableType implements OracleComplexType {

    private final String schema;
    private final String name;
    private final String componentTypeName;

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.ARRAY;
    }

}
