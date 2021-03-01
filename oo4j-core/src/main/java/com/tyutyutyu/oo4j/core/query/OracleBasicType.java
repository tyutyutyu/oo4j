package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import lombok.*;

import java.sql.JDBCType;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum OracleBasicType implements OracleType {

    BLOB("BLOB", oracle.jdbc.OracleType.BLOB, JDBCType.BLOB, JavaClass.BYTE_ARRAY),
    CHAR("CHAR", oracle.jdbc.OracleType.CHAR, JDBCType.CHAR, JavaClass.STRING),
    CLOB("CLOB", oracle.jdbc.OracleType.CLOB, JDBCType.CLOB, JavaClass.STRING),
    DATE("DATE", oracle.jdbc.OracleType.DATE, JDBCType.DATE, JavaClass.DATE),
    FLOAT("FLOAT", oracle.jdbc.OracleType.FLOAT, JDBCType.FLOAT, JavaClass.DOUBLE),
    NUMBER("NUMBER", oracle.jdbc.OracleType.NUMBER, JDBCType.NUMERIC, JavaClass.BIG_DECIMAL),
    NVARCHAR2("NVARCHAR2", oracle.jdbc.OracleType.NVARCHAR, JDBCType.NVARCHAR, JavaClass.STRING),
    RAW("RAW", oracle.jdbc.OracleType.RAW, JDBCType.BINARY, JavaClass.BYTE_ARRAY),
    TIMESTAMP("TIMESTAMP", oracle.jdbc.OracleType.TIMESTAMP, JDBCType.TIMESTAMP, JavaClass.TIMESTAMP),
    VARCHAR2("VARCHAR2", oracle.jdbc.OracleType.VARCHAR2, JDBCType.VARCHAR, JavaClass.STRING);

    private final String oracleDataType;
    private final oracle.jdbc.OracleType oracleType;
    private final JDBCType jdbcType;
    private final JavaClass javaClass;

    @Override
    public String getName() {
        return oracleDataType;
    }

    @Override
    public String getFullyQualifiedName() {
        return getName();
    }

}
