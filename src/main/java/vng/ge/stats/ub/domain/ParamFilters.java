package vng.ge.stats.ub.domain;

/**
 * Created by canhtq on 20/09/2017.
 */
public class ParamFilters {

    public ActiveParam active;
    public DeviceParam device;
    public PayingUserParam pay;
    public GameParam games;
    public  ParamFilters(){
        active = new ActiveParam();
        device = new DeviceParam();
        pay = new PayingUserParam();
        games = new GameParam();

    }
    public  ParamFilters(String gameCode){
        active = new ActiveParam();
        device = new DeviceParam();
        pay = new PayingUserParam();
        games = new GameParam(gameCode);

    }


}
