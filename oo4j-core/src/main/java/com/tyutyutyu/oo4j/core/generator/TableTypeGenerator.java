package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.NoPrivilegeException;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.MetadataQuery.TypeCode;
import com.tyutyutyu.oo4j.core.query.OracleTableType;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
class TableTypeGenerator {

    private final MetadataQuery metadataQuery;
    private final TableTypeMetadataMapper tableTypeMetadataMapper;
    private final SourceWriter sourceWriter;

    void generateTableTypes(String schema, Collection<String> excludes) {
        metadataQuery.queryTypeNames(schema, TypeCode.COLLECTION)
                .stream()
                .filter(typeName ->
                        !excludes.contains(typeName)
                                && !excludes.contains(schema + "." + typeName)
                )
                .forEach(typeName -> generateType(schema, typeName));
    }

    void generateType(String schema, String typeName) throws NoPrivilegeException {

        OracleTableType oracleTableType = metadataQuery.queryTableType(schema, typeName);

        JavaTableTypeModel javaTableTypeModel = tableTypeMetadataMapper.toJavaTableTypeMetadata(oracleTableType);

        sourceWriter.writeTableType(javaTableTypeModel);
    }

}
