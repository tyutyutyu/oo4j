package com.tyutyutyu.oo4j.core.query;

public interface OracleComplexType extends OracleType {

    String getSchema();

    default String getFullyQualifiedName() {
        return String.format("%s.%s", getSchema(), getName());
    }

}
