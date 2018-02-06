package vng.ge.stats.ub.repository;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by canhtq on 04/12/2017.
 */
public class DeviceIdMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString("device_id");
    }
}
