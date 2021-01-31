package com.tyutyutyu.oo4j.core.query;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleProcedureFieldRowMapper implements RowMapper<OracleProcedureField> {

    @Override
    public OracleProcedureField mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OracleProcedureField(
                rs.getString("ARGUMENT_NAME"),
                rs.getString("DATA_TYPE"),
                rs.getString("IN_OUT"),
                rs.getString("TYPE_NAME")
        );
    }

}
