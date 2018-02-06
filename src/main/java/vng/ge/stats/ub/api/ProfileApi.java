package vng.ge.stats.ub.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vng.ge.stats.ub.domain.ApiQueryResult;
import vng.ge.stats.ub.domain.CrossGameParam;
import vng.ge.stats.ub.domain.PayingUserParam;
import vng.ge.stats.ub.domain.QueryResult;
import vng.ge.stats.ub.queue.RedisDb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by canhtq on 20/09/2017.
 */

@RestController
@RequestMapping("/profile")
public class ProfileApi {

    @Autowired
    RedisTemplate< String, Object > redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProfileApi.class);
    public static String getStr(String key,Map<String,Object> profile){
        return (profile.containsKey(key) && profile.get(key)!=null) ? (String) profile.get(key): "";
    }

    @RequestMapping(value = "/user/{game_id}/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getUser(@PathVariable String game_id,@PathVariable String id) {
        logger.info(game_id);
        logger.info(id);
        Map<String,Object> profile = RedisDb.getInstance().getProfile(game_id,id);
        return new ResponseEntity<Map<String,Object>>(profile, HttpStatus.OK);
    }

    private Set<String> getSetData(Map<String,Object> profile, String fieldName){
        Gson gson = new GsonBuilder().create();
        Set<String> sets = new HashSet<>();
        if(profile!=null){
            String zData = getStr(fieldName, profile);
            if (!zData.isEmpty()) {
                try {
                    sets = gson.fromJson(zData, sets.getClass());

                } catch (Exception ex) {
                    sets = new HashSet<>();
                }
            }
        }

        return sets;
    }


    @RequestMapping(value = "/device/{device_id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getDevice(@PathVariable String device_id) {
        Map<String, Object> result = new HashMap();
        Map<String, Object> vngProfile = RedisDb.getInstance().getDevice(device_id);
        if(vngProfile!=null){
            result.put("info", vngProfile);
            Map<String, Object> gameList = new HashMap();

            Set<String> games = getSetData(vngProfile, "gs");

            for (String game : games) {
                Map<String,Object> deviceGame = RedisDb.getInstance().getGameDevice(game,device_id);
                Set<String> users = getSetData(deviceGame,"us");
                for (String user : users) {
                    Map<String,Object> userProfile = RedisDb.getInstance().getProfile(game,user);
                    String keyRs = String.format("%s-%s",game,user);
                    gameList.put(keyRs,userProfile);
                }
            }
            result.put("games", gameList);
        }
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getStats() {
        Map<String, Object> result = new HashMap();
        Set<String> games = new HashSet<>();
        games.add("jxm");
        games.add("gunga");
        games.add("cack");
        games.add("ztm");
        games.add("omg");
        long allUser=0l;
        for (String game : games) {
            String usersKey = "users:"+game;
            String devicesKey = "devices:"+game;
            long len = RedisDb.getInstance().getHlen(usersKey);
            allUser+=len;
            Map<String, Object> gStats = new HashMap();
            gStats.put("total_users", len);
            len = RedisDb.getInstance().getHlen(devicesKey);
            gStats.put("total_devices", len);
            String[] sOs = new String[]{"android","ios"};
            Map<String, Object> gOs = new HashMap();
            for(String os:sOs){
                Map<String, Object> hOs = new HashMap();
                hOs.put("total_users", 0);
                hOs.put("total_devices", 0);
                gOs.put(os, hOs);
            }
            gStats.put("os",gOs);
            result.put(game, gStats);
        }
        String game="vng";
        String devicesKey = "devices:"+game;
        long allDevice = RedisDb.getInstance().getHlen(devicesKey);
        Map<String, Object> gStats = new HashMap();
        gStats.put("total_users", allUser);
        gStats.put("total_devices", allDevice);
        String[] sOs = new String[]{"android","ios"};
        Map<String, Object> gOs = new HashMap();
        for(String os:sOs){
            Map<String, Object> hOs = new HashMap();
            hOs.put("total_users", 0);
            hOs.put("total_devices", 0);
            gOs.put(os, hOs);
        }
        gStats.put("os",gOs);
        result.put(game, gStats);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/cross", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> payingUsers(@RequestBody CrossGameParam filters) {
        Map<String, Object> result = new HashMap();
        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));

        result = RedisDb.getInstance().getCrossGame(filters.games);
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }
}
