package com.tyutyutyu.oo4j.core.query;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.JDBCType;
import java.util.List;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class OracleObjectType implements OracleComplexType {

    private final String name;
    private final List<OracleTypeField> fields;

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.STRUCT;
    }

}

