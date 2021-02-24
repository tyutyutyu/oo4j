package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.NamingStrategy;
import com.tyutyutyu.oo4j.core.query.OracleTableType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TableTypeMetadataMapper {

    private final NamingStrategy namingStrategy;

    public JavaTableTypeModel toJavaTableTypeMetadata(OracleTableType oracleTableType) {

        String packageName = namingStrategy.getTypePackage(oracleTableType.getSchema());
        List<String> imports = List.of();
        String className = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getName());
        String componentClassName = namingStrategy.oracleTypeNameToJavaClassName(oracleTableType.getComponentTypeName());

        return new JavaTableTypeModel(
                packageName,
                imports,
                className,
                componentClassName,
                oracleTableType.getSchema(),
                oracleTableType.getName()
        );
    }

}
