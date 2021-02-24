package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.MetadataQuery;
import com.tyutyutyu.oo4j.core.query.OracleTypeField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.tyutyutyu.oo4j.core.query.MetadataQuery.TypeCode;

@RequiredArgsConstructor
@Slf4j
class TypeGenerator {

    private final MetadataQuery metadataQuery;
    private final TypeMetadataMapper typeMetadataMapper;

    List<JavaTypeModel> generateTypes(String schema, Collection<String> excludes) {
        return metadataQuery.queryTypeNames(schema, TypeCode.OBJECT)
                .stream()
                .filter(typeName ->
                        !excludes.contains(typeName)
                                && !excludes.contains(schema + "." + typeName)
                )
                .map(typeName -> generateType(schema, typeName))
                .collect(Collectors.toUnmodifiableList());
    }

    JavaTypeModel generateType(String schema, String typeName) {

        List<OracleTypeField> oracleTypeFields = metadataQuery.queryTypeFields(schema, typeName);

        return typeMetadataMapper.toJavaTypeMetadata(schema, typeName, oracleTypeFields);
    }

}
