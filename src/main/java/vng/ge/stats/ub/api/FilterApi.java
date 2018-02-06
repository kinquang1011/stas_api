package vng.ge.stats.ub.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vng.ge.stats.ub.async.AsyncService;
import vng.ge.stats.ub.queue.KafkaSender;
import vng.ge.stats.ub.repository.UserProfileRepository;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.repository.UserProfileReps;
import vng.ge.stats.ub.sql.SqlBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by canhtq on 20/09/2017.
 */

@RestController
@RequestMapping("/query")
public class FilterApi {


    @Autowired
    private UserProfileReps userProfileRepository;
    @Autowired
    private KafkaSender sender;

    @Autowired
    public AsyncService sampleAsyncService;

    private static final Logger logger = LoggerFactory.getLogger(FilterApi.class);

    @RequestMapping(value = "/paying_users", method = RequestMethod.POST)
    public ResponseEntity<ApiQueryResult> payingUsers(@RequestBody PayingUserParam filters) {
        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));

        ApiQueryResult apiQueryResult = new ApiQueryResult();
        QueryResult result = userProfileRepository.getPayingDevice(filters);
        apiQueryResult.result = result;
        return new ResponseEntity<ApiQueryResult>(apiQueryResult, HttpStatus.OK);
    }



    @RequestMapping(value = "/paying_users", method = RequestMethod.GET)
    public ResponseEntity<PayingUserParam> payingUsers() {
        PayingUserParam apiQueryResult = new PayingUserParam();
        return new ResponseEntity<PayingUserParam>(apiQueryResult, HttpStatus.OK);
    }


    @RequestMapping(value = "/active_users", method = RequestMethod.POST)
    public ResponseEntity<ApiQueryResult> activeUsers(@RequestBody ActiveParam filters) {
        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));
        ApiQueryResult apiQueryResult = new ApiQueryResult();
        QueryResult result = userProfileRepository.getActiveDevice(filters);
        apiQueryResult.result = result;
        return new ResponseEntity<ApiQueryResult>(apiQueryResult, HttpStatus.OK);
    }
    @RequestMapping(value = "/active_users", method = RequestMethod.GET)
    public ResponseEntity<ActiveParam> activeUsers() {
        ActiveParam apiQueryResult = new ActiveParam();
        return new ResponseEntity<ActiveParam>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/all_users", method = RequestMethod.POST)
    public ResponseEntity<ApiQueryResult> allUsers(@RequestBody AllUserParam filters) {
        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));
        ApiQueryResult apiQueryResult = new ApiQueryResult();
        QueryResult result = userProfileRepository.getAllDevice(filters);
        apiQueryResult.result = result;
        return new ResponseEntity<ApiQueryResult>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/all_users", method = RequestMethod.GET)
    public ResponseEntity<AllUserParam> allUsers() {
        AllUserParam apiQueryResult = new AllUserParam();
        return new ResponseEntity<AllUserParam>(apiQueryResult, HttpStatus.OK);
    }
    @RequestMapping(value = "/device_users", method = RequestMethod.POST)
    public ResponseEntity<ApiQueryResult> deviceOSUsers(@RequestBody DeviceParam filters) {
        Gson gson = new GsonBuilder().create();
        logger.info(gson.toJson(filters));
        ApiQueryResult apiQueryResult = new ApiQueryResult();
        QueryResult result = userProfileRepository.getOSDevice(filters);
        apiQueryResult.result = result;
        return new ResponseEntity<ApiQueryResult>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/device_users", method = RequestMethod.GET)
    public ResponseEntity<DeviceParam> deviceOSUsers() {
        DeviceParam apiQueryResult = new DeviceParam();
        return new ResponseEntity<DeviceParam>(apiQueryResult, HttpStatus.OK);
    }

}
