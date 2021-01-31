package com.tyutyutyu.oo4j.core.query;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class OracleProcedureRowMapper implements RowMapper<OracleProcedure> {

    private final String schema;

    @Override
    public OracleProcedure mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OracleProcedure(
                schema,
                rs.getString("OBJECT_NAME"),
                rs.getString("PROCEDURE_NAME"),
                rs.getString("OBJECT_TYPE")
        );
    }

}
