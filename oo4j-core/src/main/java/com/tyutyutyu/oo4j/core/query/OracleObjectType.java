package com.tyutyutyu.oo4j.core.query;

import lombok.Value;

import java.sql.JDBCType;
import java.util.List;

@Value
public class OracleObjectType implements OracleComplexType {

    String schema;
    String name;
    List<OracleTypeField> fields;

    @Override
    public JDBCType getJdbcType() {
        return JDBCType.STRUCT;
    }

}

