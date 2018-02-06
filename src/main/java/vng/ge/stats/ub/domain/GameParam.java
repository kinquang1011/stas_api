package vng.ge.stats.ub.domain;

/**
 * Created by canhtq on 21/09/2017.
 */
public class GameParam {
    public GameParam(){

    }
    public GameParam(String gameCode){
        games = new String[]{gameCode};
    }
    public String[] games = new String[] {"OMG"};
}
