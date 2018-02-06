package vng.ge.stats.ub.repository;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by canhtq on 04/12/2017.
 */
public class FilterCounterMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        FilterCounter filterCounter = new FilterCounter();
        filterCounter.setTotalDevices(resultSet.getLong("total_device_id"));
        filterCounter.setTotalUsers(resultSet.getLong("total_user_id"));
        return filterCounter;
    }
}
