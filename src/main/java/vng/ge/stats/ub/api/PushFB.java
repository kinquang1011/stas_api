package vng.ge.stats.ub.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import vng.ge.stats.ub.async.AsyncService;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.queue.KafkaSender;
import vng.ge.stats.ub.queue.RedisDb;
import vng.ge.stats.ub.repository.UserProfileRepository;
import vng.ge.stats.ub.sql.SqlBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by canhtq on 20/09/2017.
 */

@RestController
@RequestMapping("/push")
public class PushFB {


    @Autowired
    public AsyncService asyncService;


    private static final Logger logger = LoggerFactory.getLogger(PushFB.class);

    @RequestMapping(value = "/paying_users", method = RequestMethod.POST)
    public ResponseEntity<ApiPushResult> payingUsers(@RequestBody PayingUserParam filters) {
        ApiPushResult apiQueryResult = new ApiPushResult();
        UUID uuid = UUID.randomUUID();
        apiQueryResult.id =uuid.toString();
        asyncService.payingAsync(filters,apiQueryResult.id);
        return new ResponseEntity<ApiPushResult>(apiQueryResult, HttpStatus.OK);
    }
    @RequestMapping(value = "/active_users", method = RequestMethod.POST)
    public ResponseEntity<ApiPushResult> activeUsers(@RequestBody ActiveParam filters) {
        ApiPushResult apiQueryResult = new ApiPushResult();
        UUID uuid = UUID.randomUUID();
        apiQueryResult.id =uuid.toString();
        asyncService.activeAsync(filters,apiQueryResult.id);
        return new ResponseEntity<ApiPushResult>(apiQueryResult, HttpStatus.OK);
    }


    @RequestMapping(value = "/all_users", method = RequestMethod.POST)
    public ResponseEntity<ApiPushResult> allUsers(@RequestBody AllUserParam filters) {
        ApiPushResult apiQueryResult = new ApiPushResult();
        UUID uuid = UUID.randomUUID();
        apiQueryResult.id =uuid.toString();
        asyncService.allAsync(filters,apiQueryResult.id);
        return new ResponseEntity<ApiPushResult>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/device_users", method = RequestMethod.POST)
    public ResponseEntity<ApiPushResult> deviceOSUsers(@RequestBody DeviceParam filters) {
        ApiPushResult apiQueryResult = new ApiPushResult();
        UUID uuid = UUID.randomUUID();
        apiQueryResult.id =uuid.toString();
        asyncService.deviceAsync(filters,apiQueryResult.id);
        return new ResponseEntity<ApiPushResult>(apiQueryResult, HttpStatus.OK);
    }
    @RequestMapping(value = "/test_paying_users", method = RequestMethod.POST)
    public ResponseEntity<ApiPushResult>  payingTestUsers() {
        ApiPushResult apiQueryResult = new ApiPushResult();
        PayingUserParam filters = new PayingUserParam();
        filters.gameCode="OMG";
        filters.chargeChannel="playstore";

        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));
        UUID uuid = UUID.randomUUID();
        apiQueryResult.id =uuid.toString();
        asyncService.payingAsync(filters,apiQueryResult.id);
        //payingAsync(filters,apiQueryResult.id);
        return new ResponseEntity<ApiPushResult>(apiQueryResult, HttpStatus.OK);
    }


    @RequestMapping(value = "/paying_users/{id}", method = RequestMethod.GET)
    public ResponseEntity<ApiAsyncPushResult> payingUsers(@PathVariable String id) {
        ApiAsyncPushResult apiQueryResult = new ApiAsyncPushResult();
        apiQueryResult.result = new AsyncPushResult();
        return new ResponseEntity<ApiAsyncPushResult>(apiQueryResult, HttpStatus.OK);
    }

    private void push(String type, String filters, String id){
        try {
            Gson gson = new GsonBuilder().create();
            PushQueryInfo itemInfo = new PushQueryInfo();
            itemInfo.filterJson = filters;
            itemInfo.type = type;
            itemInfo.id = id;
            String jsonItem = gson.toJson(itemInfo);
            //RedisDb.getInstance().push("vng2fb2_push_query",jsonItem);

            //redisTemplate.opsForList().leftPush("vng2fb2_push", jsonItem);
        } catch (Exception e) {
            return;
        }
    }

    public void payingAsync(PayingUserParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder.buildPaying(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="paying_users";
            push(type,jsonFilters,id);
        } catch (Exception e) {
            return;
        }
    }
    @Async
    public void activeAsync(ActiveParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder.buildActive(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="active_users";
            push(type,jsonFilters,id);
        } catch (Exception e) {
            return;
        }

    }
    @Async
    public void allAsync(AllUserParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder.buildGameCode(filters.gameCode);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="all_users";
            push(type,jsonFilters,id);
        } catch (Exception e) {
            return;
        }

    }

    @Async
    public void deviceAsync(DeviceParam filters, String id) {
        long time = System.currentTimeMillis();
        try {
            String sql = SqlBuilder.buildDevice(filters);
            Gson gson = new GsonBuilder().create();
            String jsonFilters = gson.toJson(filters);
            String type="device_users";
            push(type,jsonFilters,id);
        } catch (Exception e) {
            return;
        }

    }
}
