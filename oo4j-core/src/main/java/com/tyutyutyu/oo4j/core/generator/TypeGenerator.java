package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.TypeMetadataMapper;
import com.tyutyutyu.oo4j.core.query.OracleObjectType;
import com.tyutyutyu.oo4j.core.query.OracleTableType;
import com.tyutyutyu.oo4j.core.query.OracleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class TypeGenerator {

    private final TypeMetadataMapper typeMetadataMapper;

    List<JavaTypeModel> generateObjectTypes(Collection<OracleType> types) {
        return types
                .stream()
                .filter(oracleType -> oracleType instanceof OracleObjectType)
                .map(oracleType -> (OracleObjectType) oracleType)
                .map(typeMetadataMapper::toJavaTypeMetadata)
                .collect(Collectors.toUnmodifiableList());
    }

    List<JavaTableTypeModel> generateTableTypes(Collection<OracleType> types) {
        return types
                .stream()
                .filter(oracleType -> oracleType instanceof OracleTableType)
                .map(oracleType -> (OracleTableType) oracleType)
                .map(typeMetadataMapper::toJavaTableTypeMetadata)
                .collect(Collectors.toUnmodifiableList());
    }

}
