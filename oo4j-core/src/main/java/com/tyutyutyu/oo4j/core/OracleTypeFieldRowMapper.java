package com.tyutyutyu.oo4j.core;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class OracleTypeFieldRowMapper implements RowMapper<OracleTypeField> {

    @Override
    public OracleTypeField mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OracleTypeField(
                rs.getString("ATTR_NAME"),
                rs.getString("ATTR_TYPE_NAME")
        );
    }

}
