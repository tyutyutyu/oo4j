package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.OracleTypeField;
import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.result.SourceWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.tyutyutyu.oo4j.core.query.MetadataQuery.TypeCode;

@RequiredArgsConstructor
@Slf4j
class TypeGenerator {

    private final MetadataQuery metadataQuery;
    private final TypeMetadataMapper typeMetadataMapper;
    private final SourceWriter sourceWriter;

    void generateTypes(String schema) {
        metadataQuery.queryTypes(schema, TypeCode.OBJECT)
                .forEach(typeName -> generateType(schema, typeName));
    }

    void generateType(String schema, String typeName) {

        List<OracleTypeField> oracleTypeFields = metadataQuery.queryTypeFields(schema, typeName);

        JavaTypeMetadata javaTypeMetadata = typeMetadataMapper.toJavaTypeMetadata(schema, typeName, oracleTypeFields);

        sourceWriter.writeType(javaTypeMetadata);
    }

}
