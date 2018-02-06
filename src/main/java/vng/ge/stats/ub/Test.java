package vng.ge.stats.ub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vng.ge.stats.ub.api.FilterApi;
import vng.ge.stats.ub.domain.*;
import vng.ge.stats.ub.sql.SqlBuilder;
import vng.ge.stats.ub.validate.ParamValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canhtq on 21/09/2017.
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args) {
         //   System.out.print(SqlBuilder.fieldIn("abc",new  String[]{"aaa","aaa"}));
        //System.out.print(SqlBuilder.rangeValue("abc",10,11));
        ParamFilters filters = new ParamFilters("OMG");
        Gson gson = new GsonBuilder().create();
        ActiveParam2 payParam2 = new ActiveParam2();
        payParam2.selectedDate="2017-12-20";
        payParam2.days=1;
        int v = ParamValidation.validateActive(payParam2);
        String json = gson.toJson(v);
        logger.info(json);
    }
}

