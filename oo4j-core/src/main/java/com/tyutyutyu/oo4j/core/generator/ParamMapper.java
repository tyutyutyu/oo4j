package com.tyutyutyu.oo4j.core.generator;

import com.tyutyutyu.oo4j.core.javalang.JavaClass;
import com.tyutyutyu.oo4j.core.query.OracleCursorType;
import com.tyutyutyu.oo4j.core.query.OracleDataTypeMapper;
import com.tyutyutyu.oo4j.core.query.OracleObjectType;
import com.tyutyutyu.oo4j.core.query.OracleProcedure;
import com.tyutyutyu.oo4j.core.query.OracleProcedureField;
import com.tyutyutyu.oo4j.core.query.OracleTableType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class ParamMapper {

    private static final char[] GENERIC_TYPES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final NamingStrategy namingStrategy;
    private final OracleDataTypeMapper oracleDataTypeMapper;

    List<Param> toParams(OracleProcedure oracleProcedure, AtomicInteger rowMapperIndex) {
        return oracleProcedure
                .getFields()
                .stream()
                .map(oracleProcedureField -> getParam(oracleProcedure.getSchema(), oracleProcedureField, rowMapperIndex))
                .collect(Collectors.toUnmodifiableList());
    }

    private Param getParam(String schema, OracleProcedureField oracleProcedureField, AtomicInteger rowMapperIndex) {

        log.debug("getParam - schema: {}, oracleProcedureField: {}", schema, oracleProcedureField);

        JavaClass javaClass = oracleDataTypeMapper.oracleDataTypeToJavaClass(oracleProcedureField.getType());

        return new Param(
                javaClass,
                namingStrategy.oracleAttributeNameToJavaVariableName(oracleProcedureField.getName()),
                oracleProcedureField.getName(),
                oracleProcedureField.getType().getJdbcType().name(),
                oracleProcedureField.getType() instanceof OracleObjectType,
                oracleProcedureField.getType() instanceof OracleCursorType
                        ? String.valueOf(GENERIC_TYPES[rowMapperIndex.getAndIncrement()])
                        : null,
                oracleProcedureField.getType() instanceof OracleTableType
                        ? namingStrategy.oracleTypeNameToJavaClassName(
                        ((OracleTableType) oracleProcedureField.getType()).getComponentType().getName()
                )
                        : null,
                oracleProcedureField.getInOut()
        );
    }

}
