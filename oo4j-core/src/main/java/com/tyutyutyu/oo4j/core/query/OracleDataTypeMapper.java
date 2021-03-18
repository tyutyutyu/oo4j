package com.tyutyutyu.oo4j.core.query;

import com.tyutyutyu.oo4j.core.generator.NamingStrategy;
import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class OracleDataTypeMapper {

    private final NamingStrategy namingStrategy;

    public JavaClass oracleDataTypeToJavaClass(OracleType oracleType) {

        log.trace("oracleDataTypeToJavaClass - oracleType: {}", oracleType);

        if (oracleType instanceof OracleBasicType) {
            return ((OracleBasicType) oracleType).getJavaClass();
        } else if (oracleType instanceof OracleCursorType) {
            return JavaClass.listOf(null);
        } else if (oracleType instanceof OracleComplexType) {
            if (oracleType instanceof OracleObjectType) {
                return new JavaClass(
                        namingStrategy.getTypePackage(((OracleObjectType) oracleType).getSchema()),
                        namingStrategy.oracleTypeNameToJavaClassName(oracleType.getName()),
                        false,
                        null,
                        false,
                        null,
                        "Object"
                );
            } else if (oracleType instanceof OracleTableType) {
                String typePackage = namingStrategy.getTypePackage(((OracleTableType) oracleType).getSchema());
                JavaClass componentJavaClass;
                if (OracleType.isBasicType(((OracleTableType) oracleType).getComponentType().getName())) {
                    componentJavaClass = OracleBasicType.valueOf(((OracleTableType) oracleType).getComponentType().getName()).getJavaClass();
                } else {
                    componentJavaClass = new JavaClass(
                            typePackage,
                            namingStrategy.oracleTypeNameToJavaClassName(
                                    ((OracleTableType) oracleType).getComponentType().getName()
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

            } else {
                throw new IllegalStateException("Unknown OracleComplexType, oracleType=" + oracleType);
            }
        } else {
            throw new IllegalStateException("Unknown OracleType, oracleType=" + oracleType);
        }
    }

}
