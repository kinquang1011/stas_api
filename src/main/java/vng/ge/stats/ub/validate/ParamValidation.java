package vng.ge.stats.ub.validate;

import vng.ge.stats.ub.domain.ActiveParam2;
import vng.ge.stats.ub.domain.ChurnParam2;
import vng.ge.stats.ub.domain.PayParam2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by canhtq on 22/12/2017.
 */
public class ParamValidation {
    private static final String DATE_VALIDATION_PATTERN = "([0-9]{4}-[0-9]{2}-[0-9]{2})";
    private static int validDate(String date){
        Pattern pattern = Pattern.compile(DATE_VALIDATION_PATTERN);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches()){
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.INVALID_PARAMS;
    }

    public static int validatePayment(PayParam2 param2){
        int result = ErrorCode.INVALID_PARAMS;
        if(param2.fromDate.isEmpty() || param2.toDate.isEmpty()){
            return ErrorCode.INVALID_PARAMS;
        }
        if(param2.fromDate.compareToIgnoreCase(param2.toDate)>=0){
            return ErrorCode.INVALID_PARAMS;
        }

        if(param2.fromValue >= param2.toValue){
            return ErrorCode.INVALID_PARAMS;
        }

        result = validDate(param2.fromDate);
        if(result!=ErrorCode.SUCCESS){
            return  result;
        }
        result = validDate(param2.toDate);
        if(result!=ErrorCode.SUCCESS){
            return  result;
        }

        result=ErrorCode.SUCCESS;
        return result;
    }

    public static int validateActive(ActiveParam2 param2){
        int result = ErrorCode.SUCCESS;
        if(param2.selectedDate.isEmpty()){
            return ErrorCode.INVALID_PARAMS;
        }
        result = validDate(param2.selectedDate);
        if(result!=ErrorCode.SUCCESS){
            return  result;
        }
        if(param2.days <=0){
            return ErrorCode.INVALID_PARAMS;
        }
        return result;
    }

    public static int validateChurn(ChurnParam2 param2){
        int result = ErrorCode.SUCCESS;
        if(param2.selectedDate.isEmpty()){
            return ErrorCode.INVALID_PARAMS;
        }
        result = validDate(param2.selectedDate);
        if(result!=ErrorCode.SUCCESS){
            return  result;
        }
        if(param2.days <=0){
            return ErrorCode.INVALID_PARAMS;
        }
        return result;
    }

}
