package vng.ge.stats.ub.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.repository.FilterCounter;
import vng.ge.stats.ub.repository.UserProfileReps;
import vng.ge.stats.ub.sql.SqlBuilder3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canhtq on 20/09/2017.
 */

@RestController
@RequestMapping("/apps")
public class AppsApi {


    @Autowired
    private UserProfileReps userProfileRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppsApi.class);

    @RequestMapping(value = "/get_apps/{game_id}", method = RequestMethod.GET)
    public ResponseEntity<ApiAppsIdResult> doFilter(@PathVariable String game_id) {
        logger.info("get apps:" + game_id);
        List<GameApp> ls = userProfileRepository.getApps(game_id);
        ApiAppsIdResult result = new ApiAppsIdResult();
        result.result = ls;
        return new ResponseEntity<ApiAppsIdResult>(result, HttpStatus.OK);
    }

}
