package vng.ge.stats.ub.sql;

import vng.ge.stats.ub.domain.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by canhtq on 21/09/2017.
 */
public class SqlBuilder3 {
    public static String activeTime(String selectedDate,int days)
    {
        String fieldName="last_login_date";
        String sql = String.format("datediff('%s',%s) <= %d",selectedDate,fieldName,days);
        return  sql;
    }

    public static String churnTime(String selectedDate,int days)
    {
        String fieldName="last_login_date";
        String sql = String.format("datediff('%s',%s) = %d",selectedDate,fieldName,days);
        return  sql;
    }

    public static String rangeValue(String fieldName,long fromVal, long toVal)
    {
        String sql="";
        if(fromVal>0 && toVal>0) {
            sql = String.format("%s >= %d and %s <=%d ", fieldName, fromVal, fieldName, toVal);
        }
        return  sql;
    }

    public static String rangeDateValue(String fieldName,String fromVal, String toVal) {
        String sql = "";
        if (fromVal.trim().isEmpty() || toVal.trim().isEmpty()) {
            return sql;
        }
        sql = String.format("%s >= '%s' and %s <='%s' ", fieldName, fromVal.trim(), fieldName, toVal.trim());
        return sql;
    }

    public static String fieldIn(String fieldName,String[] values)
    {
        String sqlIn ="(";
        int count =0;
        for (String val: values) {
            if(!val.isEmpty()) {
                sqlIn += String.format("'%s'", val) + ',';
                count++;
            }
        }
        if(count==0){
            return "";
        }
        sqlIn+="#";
        sqlIn = sqlIn.replace(",#",")");

        String sql = String.format("%s in %s",fieldName,sqlIn);
        return  sql;
    }
    public static String fieldEqual(String fieldName,String value)
    {
        String sql = String.format("%s = '%s'",fieldName,value);
        return  sql;
    }

    public static String fieldNotNull(String fieldName)
    {
        String sql = String.format("%s is not null",fieldName);
        return  sql;
    }


    public static String fieldIn(String fieldName,List<String> values) {
        if(values.isEmpty()) return "";

        String sqlIn = "(";
        for (String val : values) {
            sqlIn += String.format("'%s'", val) + ',';
        }
        sqlIn += "#";
        sqlIn = sqlIn.replace(",#", ")");

        String sql = String.format("%s in %s", fieldName, sqlIn);
        return sql;
    }

    public static String lastLogin(String selectedDate,int days)
    {
        if(selectedDate.isEmpty()) return "";

        return  activeTime(selectedDate,days);
    }
    public static String churn(String selectedDate,int days)
    {
        if(selectedDate.isEmpty()) return "";

        return  churnTime(selectedDate,days);
    }

    public static String addWhere(String sqlIn, String conditionIn){
        if(conditionIn.isEmpty()){
            return sqlIn;
        }

        if(sqlIn.isEmpty()){
            return  conditionIn;
        }else {
            return sqlIn + " and " + conditionIn;
        }
    }


    public static String buildActive(ActiveParam2 param) {
        String sql = "";
        sql = addWhere(sql, lastLogin(param.selectedDate, param.days));
        if(param.platforms.length>0) {
            sql = addWhere(sql, fieldIn("os", param.platforms));
        }
        if(param.loginChannels.length>0) {
            sql = addWhere(sql, fieldIn("last_login_channel", param.loginChannels));
        }
        return sql;
    }

    public static String buildChurn(ChurnParam2 param) {
        String sql = "";
        sql = addWhere(sql, churn(param.selectedDate, param.days));
        if(param.platforms.length>0) {
            sql = addWhere(sql, fieldIn("os", param.platforms));
        }
        if(param.loginChannels.length>0) {
            sql = addWhere(sql, fieldIn("last_login_channel", param.loginChannels));
        }
        return sql;
    }

    public static String buildPayment(PayParam2 param) {
        String sql = "";
        sql = addWhere(sql, rangeValue("total_net", param.fromValue, param.toValue));
        sql = addWhere(sql, rangeDateValue("last_charge_date", param.fromDate, param.toDate));

        if(param.platforms.length>0) {
            sql = addWhere(sql, fieldIn("os", param.platforms));
        }
        if(param.channels.length>0) {
            sql = addWhere(sql, fieldIn("last_pay_channel", param.channels));
        }
        return sql;
    }
}
