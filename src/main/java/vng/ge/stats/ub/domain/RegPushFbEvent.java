package vng.ge.stats.ub.domain;

/**
 * Created by canhtq on 27/12/2017.
 */
public class RegPushFbEvent {
    public String appId="";
    public String sqlWhere="";
    public String eventName="";
    public String gameCode="";
    public String id="";

    public  RegPushFbEvent(String appId, String gameCode, String eventName, String sqlWhere, String id){
        this.appId=appId;
        this.eventName=eventName;
        this.gameCode = gameCode;
        this.sqlWhere =sqlWhere;
        this.id=id;
    }
}
