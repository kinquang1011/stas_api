package vng.ge.stats.ub.async;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vng.ge.stats.ub.api.PushFB;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.queue.KafkaSender;
import vng.ge.stats.ub.queue.RedisDb;
import vng.ge.stats.ub.repository.UserProfileRepository;
import vng.ge.stats.ub.repository.UserProfileReps;
import vng.ge.stats.ub.sql.SqlBuilder;
import vng.ge.stats.ub.sql.SqlBuilder2;
import vng.ge.stats.ub.validate.ErrorCode;

import javax.swing.*;
import java.util.*;

/**
 * Created by canhtq on 18/09/2017.
 */
//@Service
@Component
public class AsyncService {
    @Autowired
    private KafkaSender sender;
    @Autowired
    RedisTemplate< String, Object > redisTemplate;
    @Autowired
    private UserProfileReps userProfileRepository;
    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    @Async
    private void push(String sql, String type, String filters, String id){
        long time = System.currentTimeMillis();
        try {
            int pageNum = 1;
            int pageSize = 500;
            int n = 0;
            Gson gson = new GsonBuilder().create();
            do {
                Map<String,GameCnf> gameCnf = userProfileRepository.getGames();
                for (Map.Entry<String, GameCnf> entry : gameCnf.entrySet())
                {
                    logger.info(entry.getKey() + "/" + entry.getValue().aosAppId);
                    logger.info(entry.getKey() + "/" + entry.getValue().iosAppId);
                }

                List<Map<String, Object>> list = userProfileRepository.getDeviceList(sql, pageNum, pageSize);
                n = list.size();
                for (Map<String, Object> row : list) {

                    String jsonRow = gson.toJson(row);
                    PushItemInfo itemInfo = new PushItemInfo();
                    itemInfo.filterJson = filters;
                    itemInfo.rowJson = jsonRow;
                    itemInfo.type = type;
                    GameCnf cnf = gameCnf.get((String)row.get("ProductCode"));
                    if(cnf!=null){
                        String os =(String)row.get("OSPlatform");
                        switch (os.toLowerCase()){
                            case "ios":
                                itemInfo.bundleId = cnf.iosAppId;
                                break;
                            case "android":
                            itemInfo.bundleId = cnf.aosAppId;
                            break;
                        }
                    }

                    String jsonItem = gson.toJson(itemInfo);
                    logger.info(jsonItem);
                    long rs = redisTemplate.opsForList().leftPush("vng2fb2", jsonItem);
                }
                //redisTemplate.opsForList().leftPush("vng2fb2", jsonItem);
                //RedisDb.getInstance().incr(id,"total",n);
                pageNum++;
            } while (n > 0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
    }

    @Async
    public int asyncPush(String gameId, String appId, String eventName, String sqlWhere, String id) {
        long time = System.currentTimeMillis();
        int errorCode = ErrorCode.SUCCESS;

        try {
            errorCode = userProfileRepository.validateAppId(appId,gameId);
            if(errorCode!=ErrorCode.SUCCESS){
                return errorCode;
            }

            int pageNum = 1;
            int pageSize = 500;
            int n = 0;
            Gson gson = new GsonBuilder().create();
            Random random = new Random();
            do {
                List<String> list = userProfileRepository.getDevices(gameId, sqlWhere, pageNum, pageSize);
                n = list.size();
                int i=1;
                for (Object row : list) {
                    String deviceId = row.toString();
                    PushDeviceInfo info = new PushDeviceInfo();
                    info.appId = appId;
                    info.deviceId = deviceId;
                    info.eventName = eventName;
                    info.gameCode = gameId;
                    String js = gson.toJson(info);
                    int part = random.nextInt(16);
                    String chnl = String.format("push_from_s2s_tool_%d",part);
                    redisTemplate.opsForList().rightPush(chnl, js);
                    logger.info(deviceId);
                    i++;
                }
                pageNum++;
            } while (n > 0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            System.out.println(e.getMessage());
            errorCode = ErrorCode.SERVER_EXCEPTION;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
        return errorCode;
    }



    @Async
    public void payingAsync(PayingUserParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder2.buildPaying(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="paying_users";
            push(sql,type,jsonFilters,id);
        } catch (Exception e) {
           // System.out.println(String.format("Task interrupted after %d milliseconds", System.currentTimeMillis() - time));
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
    }


    @Async
    public void activeAsync(ActiveParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder2.buildActive(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="active_users";
            push(sql,type,jsonFilters,id);
        } catch (Exception e) {
            System.out.println(String.format("Task interrupted after %d milliseconds", System.currentTimeMillis() - time));
            return;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
    }
    @Async
    public void allAsync(AllUserParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder2.buildGameCode(filters.gameCode);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="all_users";
            push(sql,type,jsonFilters,id);
        } catch (Exception e) {
            System.out.println(String.format("Task interrupted after %d milliseconds", System.currentTimeMillis() - time));
            return;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
    }

    @Async
    public void deviceAsync(DeviceParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder2.buildDevice(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="device_users";
            push(sql,type,jsonFilters,id);
        } catch (Exception e) {
            System.out.println(String.format("Task interrupted after %d milliseconds", System.currentTimeMillis() - time));
            return;
        }

        System.out.println(String.format("Task completed after %d milliseconds", System.currentTimeMillis() - time));
    }
}