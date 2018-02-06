package vng.ge.stats.ub.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.sql.SqlBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by canhtq on 21/09/2017.
 */
@Repository
public class UserProfileRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileRepository.class);
    @Autowired
    @Qualifier("jdbcUserProfile")
    private JdbcTemplate jdbcTemplate;

    public QueryResult getPayingDevice(PayingUserParam params) {
        String sql = SqlBuilder.buildPaying(params);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;
        return result;
    }

    public QueryResult getActiveDevice(ActiveParam params) {
        String sql = SqlBuilder.buildActive(params);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;
        return result;
    }

    public QueryResult getAllDevice(AllUserParam params) {
        String sql = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sql, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDeviceQuery = countTotal;
        result.totalDevice = countTotal;
        return result;
    }

    public QueryResult getOSDevice(DeviceParam params) {
        String sql = SqlBuilder.buildDevice(params);
        logger.info(sql);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from profile_user where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;
        return result;
    }



    public List<Map<String, Object>> getDeviceList(String sqlCondition, int pageNum, int pageSize) {
        String sql = "select * from profile_user where DeviceId !='' and " + sqlCondition + " Limit " + (pageNum-1) * pageSize + ", " + pageSize;
        logger.info(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return  list;
    }


}
