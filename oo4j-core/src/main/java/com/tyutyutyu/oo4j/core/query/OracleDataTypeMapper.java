package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import oracle.jdbc.OracleType;

import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.JDBCType;

@RequiredArgsConstructor
public class OracleDataTypeMapper {

    private final NamingStrategy namingStrategy;

    public String oracleDataTypeToJavaType(String sqlType, String schema, String typeName) {
        if ("OBJECT".equals(sqlType)) {
            return namingStrategy.getTypePackage(schema)
                    + "."
                    + namingStrategy.oracleAttributeNameToJavaVariableName(typeName);
        }

        return toJavaType(sqlType);
    }

    @SneakyThrows
    private String toJavaType(String oracleType) {

        if ("OBJECT".equals(oracleType)) {
            throw new IllegalStateException();
        }

        Integer vendorTypeNumber = OracleType.valueOf(oracleType).getVendorTypeNumber();

        RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
        metaData.setColumnCount(1);
        metaData.setColumnType(1, vendorTypeNumber);
        return metaData.getColumnClassName(1);
    }

    public String toJdbcType(String oracleType) {
        Integer vendorTypeNumber = OracleType.valueOf(oracleType).getVendorTypeNumber();
        return JDBCType.valueOf(vendorTypeNumber).getName();
    }

}
