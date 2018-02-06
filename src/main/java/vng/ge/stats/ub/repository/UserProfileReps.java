package vng.ge.stats.ub.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.sql.SqlBuilder;
import vng.ge.stats.ub.sql.SqlBuilder2;
import vng.ge.stats.ub.validate.ErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by canhtq on 21/09/2017.
 */
@Repository
public class UserProfileReps {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileReps.class);
    @Autowired
    @Qualifier("jdbcUserProfile")
    private JdbcTemplate jdbcTemplate;
    private final  String TABLE_NAME="device_profile";

    public QueryResult getPayingDevice(PayingUserParam params) {
        String sql = SqlBuilder2.buildPaying(params);
        logger.info(sql);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;

        return result;
    }

    public QueryResult getActiveDevice(ActiveParam params) {
        String sql = SqlBuilder2.buildActive(params);
        logger.info(sql);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;
        return result;
    }

    public QueryResult getAllDevice(AllUserParam params) {
        String sql = SqlBuilder2.buildGameCode(params.gameCode);
        logger.info(sql);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sql, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDeviceQuery = countTotal;
        result.totalDevice = countTotal;
        return result;
    }

    public QueryResult getOSDevice(DeviceParam params) {
        String sql = SqlBuilder2.buildDevice(params);
        logger.info(sql);
        long count = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sql, null, Long.class);
        String sqlGameCode = SqlBuilder.buildGameCode(params.gameCode);
        long countTotal = jdbcTemplate.queryForObject("select count(DISTINCT DeviceID) as result from device_user_info where " + sqlGameCode, null, Long.class);
        QueryResult result = new QueryResult();
        result.totalDevice = countTotal;
        result.totalDeviceQuery = count;
        return result;
    }

    public List<Map<String, Object>> getDeviceList(String sqlCondition, int pageNum, int pageSize) {
        String sql = "select * from device_user_info where DeviceId not in ('','00000000-0000-0000-0000-000000000000') and " + sqlCondition + " Limit " + (pageNum-1) * pageSize + ", " + pageSize;
        logger.info(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return  list;
    }

    public Map<String, GameCnf> getGames() {
        String sql = "select * from up_games";
        Map<String, GameCnf> results = new HashMap<>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : list) {
            String gameCode= (String) row.get("gameId");
            GameCnf cnf = new GameCnf();
            cnf.aosAppId = (String) row.get("aos_bundle_id");
            cnf.iosAppId = (String) row.get("ios_bundle_id");
            results.put(gameCode, cnf);
        }
        return  results;
    }
    private String getUserTable(String gameId){
        return  String.format("%s_v1_users", gameId);
    }
    public FilterCounter getCounters(String gameId,String sqlWhere){
        String tableName = getUserTable(gameId);
        String sql = String.format("select count(distinct(user_id)) as total_user_id,count(distinct(device_id)) as total_device_id from %s where %s", tableName, sqlWhere);
        logger.info(sql);
        FilterCounter result = (FilterCounter)jdbcTemplate.queryForObject(sql,null, new FilterCounterMapper());
        return result;
    }
    public List<String> getDevices(String gameId,String sqlWhere, int pageNum, int pageSize){
        String tableName = getUserTable(gameId);
        String pageSql =" Limit " + (pageNum-1) * pageSize + ", " + pageSize;
        String sql = String.format("select device_id from %s where %s %s",tableName, sqlWhere, pageSql);
        logger.info(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<String> result  = new ArrayList<>();
        for (Map<String, Object> row : list) {
            String id= (String) row.get("device_id");
            result.add(id);
        }
        return result;
    }

    public List<GameApp> getApps(String gameId){
        String sql = "select * from apps where game_id=?";
        List<GameApp> results = new ArrayList<>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,gameId);
        for (Map<String, Object> row : list) {
            String app_id= (String) row.get("app_id");
            String app_Name= (String) row.get("game_name");
            GameApp app = new GameApp();
            app.appId =app_id;
            app.appName = app_Name;
            results.add(app);
        }
        return  results;
    }

    public int validateAppId(String appId, String gameId){
        String sql = "select * from apps where game_id=? and app_id=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,gameId, appId);
        if(list.size()>0){
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.INVALID_PARAMS;
    }

}
