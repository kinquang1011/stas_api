package vng.ge.stats.ub.sql;

import vng.ge.stats.ub.domain.ActiveParam;
import vng.ge.stats.ub.domain.DeviceParam;
import vng.ge.stats.ub.domain.ParamFilters;
import vng.ge.stats.ub.domain.PayingUserParam;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by canhtq on 21/09/2017.
 */
public class SqlBuilder {



    public static String activeTime(String fieldName,String selectedDate,int days)
    {
        String sql = String.format("datediff('%s',%s) < %d and datediff('%s',%s)>0",selectedDate,fieldName,days,selectedDate,fieldName);
        return  sql;
    }

    public static String churnTime(String fieldName,String selectedDate,int days)
    {
        String sql = String.format("datediff('%s',%s) > %d",selectedDate,fieldName,days);
        return  sql;
    }

    public static String rangeValue(String fieldName,long fromVal, long toVal)
    {
        String sql = String.format("%s < %d and %s >%d ",fieldName,fromVal,fieldName,toVal);
        return  sql;
    }
    public static String fieldIn(String fieldName,String[] values)
    {
        String sqlIn ="(";
        for (String val: values) {
            sqlIn += String.format("'%s'",val) +',';
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

    public static String fieldIn(String fieldName,List<String> values) {
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
        String fieldName = "LastLoginTime";
        return  activeTime(fieldName,selectedDate,days);
    }
    public static String addWhere(String sqlIn, String conditionIn){
        if(sqlIn.isEmpty()){
            return  conditionIn;
        }else {
            return sqlIn + " and " + conditionIn;
        }
    }



    public static String buildPaying(PayingUserParam params){
        String sql = "";
        if(!params.chargeChannel.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("ChargeChannel",params.chargeChannel));
            sql = addWhere(sql, "TotalCharge >0");
        }
        if(!params.gameCode.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("ProductCode",params.gameCode));
        }

        return  sql;
    }
    public static String buildActive(ActiveParam params){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate = simpleDateFormat.format(calendar.getTime());

        String sql = "";
        if("active".equalsIgnoreCase(params.userType)){
            sql = addWhere(sql, SqlBuilder.activeTime("LastLoginTime", selectedDate, params.days));
        }else {
            sql = addWhere(sql, SqlBuilder.churnTime("LastLoginTime", selectedDate, params.days));
        }
        if(!params.gameCode.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("ProductCode",params.gameCode));
        }
        if(!params.loginChannel.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("TypeSDK",params.loginChannel));
        }
        return  sql;
    }
    public static String buildDevice(DeviceParam params){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate = simpleDateFormat.format(calendar.getTime());
        String sql = "";
        List<String> os = new ArrayList<>();
        List<String> oss = new ArrayList<>();
        if(params.aos.equalsIgnoreCase("yes")){
            os.add("android");
            if(params.aosVers.length>0) {
                for (String osv: params.aosVers){
                    oss.add(osv);
                }
            }
        }
        if(params.ios.equalsIgnoreCase("yes")){
            os.add("IOS");
            for (String osv: params.iosVers){
                oss.add(osv);
            }
        }
        if(os.size()>0){
            sql = addWhere(sql, SqlBuilder.fieldIn("DeviceOSSDK",os));
            if(oss.size()>0) {
                sql = addWhere(sql, SqlBuilder.fieldIn("LastOSSDK",oss ));
            }
        }

        if(!params.gameCode.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("ProductCode",params.gameCode));
        }


        return  sql;
    }

    public static String buildGameCode(String gameCode){
        String sql = "";
        if(!gameCode.isEmpty()){
            sql = addWhere(sql, SqlBuilder.fieldEqual("ProductCode",gameCode));
        }

        return  sql;
    }


}
