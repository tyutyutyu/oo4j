package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.rowset.RowSetMetaDataImpl;

@RequiredArgsConstructor
@Slf4j
public class OracleDataTypeMapper {

    private final NamingStrategy namingStrategy;

    public JavaClass oracleDataTypeToJavaClass(OracleType oracleType, String schema) {

        log.trace("oracleDataTypeToJavaClass - oracleType: {}, schema: {}, ", oracleType, schema);

        if (oracleType instanceof OracleBasicType) {
            return ((OracleBasicType) oracleType).getJavaClass();
        } else if (oracleType instanceof OracleComplexType) {
            if (oracleType instanceof OracleObjectType) {
                return new JavaClass(
                        namingStrategy.getTypePackage(schema),
                        namingStrategy.oracleTypeNameToJavaClassName(oracleType.getName()),
                        false,
                        null,
                        false,
                        null,
                        "Object"
                );
            } else if (oracleType instanceof OracleTableType) {
                String typePackage = namingStrategy.getTypePackage(schema);
                JavaClass componentJavaClass;
                if (OracleType.isBasicType(((OracleTableType) oracleType).getComponentTypeName())) {
                    componentJavaClass = OracleBasicType.valueOf(((OracleTableType) oracleType).getComponentTypeName()).getJavaClass();
                } else {
                    componentJavaClass = new JavaClass(
                            typePackage,
                            namingStrategy.oracleTypeNameToJavaClassName(
                                    ((OracleTableType) oracleType).getComponentTypeName()
                            ),
                            false,
                            null,
                            false,
                            null,
                            null
                    );
                }
                return new JavaClass(
                        typePackage,
                        namingStrategy.oracleTypeNameToJavaClassName(oracleType.getName()),
                        false,
                        componentJavaClass,
                        true,
                        JavaClass.ContainerType.LIST,
                        "Array"
                );
            } else if (oracleType instanceof OracleCursorType) {
                return JavaClass.listOf(null);
            } else {
                throw new IllegalStateException("Unknown OracleComplexType");
            }
        } else {
            throw new IllegalStateException("Unknown OracleType");
        }
    }

    @SneakyThrows
    private String toJavaType(String oracleType) {

        Integer vendorTypeNumber = oracle.jdbc.OracleType.valueOf(oracleType).getVendorTypeNumber();

        RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
        metaData.setColumnCount(1);
        metaData.setColumnType(1, vendorTypeNumber);
        return metaData.getColumnClassName(1);
    }

}
