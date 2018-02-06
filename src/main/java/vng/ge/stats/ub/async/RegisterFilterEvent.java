package vng.ge.stats.ub.async;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vng.ge.stats.ub.domain.RegPushFbEvent;
import vng.ge.stats.ub.queue.RedisDb;

/**
 * Created by canhtq on 27/12/2017.
 */
public class RegisterFilterEvent {
    private static RegisterFilterEvent instance=null;
    private Gson gson = new GsonBuilder().create();
    String channelName="push_profile_filter_event_queue";
    public  static RegisterFilterEvent getInstance(){
        if(instance==null){
            instance = new RegisterFilterEvent();
        }
        return instance;
    }
    public void registerEvent(RegPushFbEvent event){
        String js = gson.toJson(event);
        RedisDb.getInstance().push(channelName,js);
    }

}
