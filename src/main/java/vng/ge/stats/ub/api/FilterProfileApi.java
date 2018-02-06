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
import vng.ge.stats.ub.async.RegisterFilterEvent;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.repository.FilterCounter;
import vng.ge.stats.ub.repository.UserProfileReps;
import vng.ge.stats.ub.sql.SqlBuilder3;
import vng.ge.stats.ub.validate.ErrorCode;
import vng.ge.stats.ub.validate.ParamValidation;

/**
 * Created by canhtq on 20/09/2017.
 */

@RestController
@RequestMapping("/profile")
public class FilterProfileApi {


    @Autowired
    private UserProfileReps userProfileRepository;
    @Autowired
    public AsyncService asyncService;

    private static final Logger logger = LoggerFactory.getLogger(FilterProfileApi.class);

    @RequestMapping(value = "/get/{action}", method = RequestMethod.POST)
    public ResponseEntity<ApiFilterResult> doFilter(@RequestBody PayingUserParam filters, @PathVariable String action) {
        Gson gson = new GsonBuilder().create();

        logger.info(gson.toJson(filters));

        ApiFilterResult apiQueryResult = new ApiFilterResult();
        String sqlWhere = "last_login>'2017-10-11' and last_login <'2017-11-10'";

        FilterCounter result = userProfileRepository.getCounters("gunga", sqlWhere);
        apiQueryResult.result = result;
        return new ResponseEntity<ApiFilterResult>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/active/{action}", method = RequestMethod.POST)
    public ResponseEntity<ApiFilterResult> doActive(@RequestBody ActiveParam2 param, @PathVariable String action) {
        Gson gson = new GsonBuilder().create();
        ApiFilterResult apiQueryResult = new ApiFilterResult();
        try {
            logger.info(action + ":"+ gson.toJson(param));
            apiQueryResult.status= ParamValidation.validateActive(param);
            if(apiQueryResult.status==ErrorCode.SUCCESS) {
                String sqlWhere = SqlBuilder3.buildActive(param);
                FilterCounter result = userProfileRepository.getCounters(param.gameCode, sqlWhere);
                apiQueryResult.result = result;
                if ("push".equalsIgnoreCase(action)) {
                    if (param.appId.isEmpty() || param.eventName.isEmpty()) {
                        apiQueryResult.status = ErrorCode.INVALID_PARAMS;
                    } else {
                        //apiQueryResult.status=asyncService.asyncPush(param.gameCode, param.appId, param.eventName, sqlWhere, "");
                        RegPushFbEvent event = new RegPushFbEvent(param.appId, param.gameCode, param.eventName, sqlWhere,"");
                        RegisterFilterEvent.getInstance().registerEvent(event);
                    }
                }
            }
        } catch (Exception ex) {
            apiQueryResult.status = ErrorCode.SERVER_EXCEPTION;
            logger.error(ex.getMessage(),ex);
        }
        logger.info(gson.toJson(apiQueryResult));
        return new ResponseEntity<>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/churn/{action}", method = RequestMethod.POST)
    public ResponseEntity<ApiFilterResult> doChurn(@RequestBody ChurnParam2 param, @PathVariable String action) {
        Gson gson = new GsonBuilder().create();
        ApiFilterResult apiQueryResult = new ApiFilterResult();
        try {
            logger.info(action + ":"+ gson.toJson(param));
            apiQueryResult.status= ParamValidation.validateChurn(param);
            if(apiQueryResult.status==ErrorCode.SUCCESS) {
                String sqlWhere = SqlBuilder3.buildChurn(param);

                FilterCounter result = userProfileRepository.getCounters(param.gameCode, sqlWhere);
                apiQueryResult.result = result;
                if ("push".equalsIgnoreCase(action)) {
                    if (param.appId.isEmpty() || param.eventName.isEmpty()) {
                        apiQueryResult.status = ErrorCode.INVALID_PARAMS;
                    } else {
                        //apiQueryResult.status=asyncService.asyncPush(param.gameCode, param.appId, param.eventName, sqlWhere, "");
                        RegPushFbEvent event = new RegPushFbEvent(param.appId, param.gameCode, param.eventName, sqlWhere,"");
                        RegisterFilterEvent.getInstance().registerEvent(event);

                    }
                }
            }
        } catch (Exception ex) {
            apiQueryResult.status = ErrorCode.SERVER_EXCEPTION;
            logger.error(ex.getMessage(),ex);
        }
        logger.info(gson.toJson(apiQueryResult));
        return new ResponseEntity<>(apiQueryResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/{action}", method = RequestMethod.POST)
    public ResponseEntity<ApiFilterResult> doCharge(@RequestBody PayParam2 param, @PathVariable String action) {
        Gson gson = new GsonBuilder().create();
        ApiFilterResult apiQueryResult = new ApiFilterResult();
        try {
            logger.info(action + ":"+ gson.toJson(param));
            apiQueryResult.status= ParamValidation.validatePayment(param);
            if(apiQueryResult.status==ErrorCode.SUCCESS) {
                String sqlWhere = SqlBuilder3.buildPayment(param);
                FilterCounter result = userProfileRepository.getCounters(param.gameCode, sqlWhere);
                apiQueryResult.result = result;
                if ("push".equalsIgnoreCase(action)) {
                    if (param.appId.isEmpty() || param.eventName.isEmpty()) {
                        apiQueryResult.status = ErrorCode.INVALID_PARAMS;
                    } else {
                        //apiQueryResult.status= asyncService.asyncPush(param.gameCode, param.appId, param.eventName, sqlWhere, "");
                        RegPushFbEvent event = new RegPushFbEvent(param.appId, param.gameCode, param.eventName, sqlWhere,"");
                        RegisterFilterEvent.getInstance().registerEvent(event);
                    }
                }
            }
        } catch (Exception ex) {
            apiQueryResult.status = ErrorCode.SERVER_EXCEPTION;
            logger.error(ex.getMessage(),ex);
        }
        logger.info(gson.toJson(apiQueryResult));
        return new ResponseEntity<>(apiQueryResult, HttpStatus.OK);
    }
}
