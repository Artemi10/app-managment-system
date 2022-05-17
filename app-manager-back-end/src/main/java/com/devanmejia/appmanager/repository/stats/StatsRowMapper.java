package com.devanmejia.appmanager.repository.stats;

import com.devanmejia.appmanager.transfer.stat.StatResponseDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRowMapper implements RowMapper<StatResponseDTO> {
    @Override
    public StatResponseDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new StatResponseDTO(
                resultSet.getString("date"),
                resultSet.getInt("amount"));
    }
}
