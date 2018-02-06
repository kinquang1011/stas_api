package vng.ge.stats.ub.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vng.ge.stats.ub.async.AsyncService;

import java.util.*;

/**
 * Created by canhtq on 19/09/2017.
 */
@Configuration
public class RedisDb {
    private static RedisDb instance=null;
    private JedisPool pool = null;
    private Gson gson = new GsonBuilder().create();
    private static final Logger logger = LoggerFactory.getLogger(RedisDb.class);
    @Autowired
    @Value("${profile.redis.host}")
    private String redisHost;

    @Autowired
    @Value("${profile.redis.port}")
    private int redisPort;

    public RedisDb(){

        //pool = new JedisPool(new JedisPoolConfig(),redisHost,redisPort);
        pool = new JedisPool(new JedisPoolConfig(),"10.60.43.47",6379);
    }
    public  static RedisDb getInstance(){
        if(instance==null){
            instance = new RedisDb();
        }
        return instance;
    }

    public String push(String queue, String data) {
        String result = "";
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(queue,data);
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }
        return result;
    }

    public String incr(String key, String valKey, long value) {
        String result = "";
        Jedis jedis = pool.getResource();
        try {
            jedis.hincrBy(key,valKey,value);
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }
        return result;
    }
    public Map<String,Object> getProfile(String game_id, String user_id){
        Map<String,Object> profile = new HashMap();
        Jedis jedis = pool.getResource();
        try {
            String jsonData = (String)jedis.hget("users:"+game_id,""+user_id);
            Gson gson = new GsonBuilder().create();
            logger.info(jsonData);
            profile = gson.fromJson(jsonData, profile.getClass());
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }

        return profile;
    }

    public Map<String,Object> getGameDevice(String game_id, String device_id){
        Map<String,Object> profile = new HashMap();
        Jedis jedis = pool.getResource();
        try {
            String jsonData = (String)jedis.hget("devices:"+game_id,device_id);
            Gson gson = new GsonBuilder().create();
            logger.info(jsonData);
            profile = gson.fromJson(jsonData, profile.getClass());
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }

        return profile;
    }

    public Map<String,Object> getDevice(String device_id){
        device_id=device_id.toLowerCase();
        Map<String,Object> profile = new HashMap();
        Jedis jedis = pool.getResource();
        try {
            String jsonData = (String)jedis.hget("devices:vng",device_id);
            Gson gson = new GsonBuilder().create();
            logger.info(jsonData);
            profile = gson.fromJson(jsonData, profile.getClass());
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }

        return profile;
    }

    public long getHlen(String key){
        Jedis jedis = pool.getResource();
        long rs =0;
        try {
            rs = jedis.hlen(key);
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }

        return rs;
    }

    public Map<String,Object> getCrossGame(String[] games){
        SortedSet<String> keyGames = new TreeSet<>();
        for(String game:games){
            keyGames.add(game);
        }
        String xKey = "";
        for (String game : keyGames) {
            xKey += game;
        }
        Map<String,Object> profile = new HashMap();
        Jedis jedis = pool.getResource();
        xKey ="x:"+xKey;
        logger.info(xKey);
        try {
            long total = jedis.scard(xKey);
            profile.put("total_devices", total);
            if(total>0) {
                profile.put("sample_devices",jedis.srandmember(xKey, 10));
            }
        } catch (Exception ex) {

        } finally {
            jedis.close();
        }

        return profile;
    }

}
