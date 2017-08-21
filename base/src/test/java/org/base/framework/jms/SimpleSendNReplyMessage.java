package org.base.framework.jms;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class SimpleSendNReplyMessage extends JmsMessage {

    public final static String creation="creation";
    public final static String sending="sending";
    public final static String delivery="delivery";
    public final static String reply="reply";




    public SimpleSendNReplyMessage() {
        super();
        this.setApp("unitTest");
        this.setService("localSimpleSendNReply");

        this.tMap=new HashMap<String,Date>();
        this.tMap.put(creation, new Date());
        this.activites=new ArrayList<String>();
        this.activites.add("creation:");
    }



    @JsonProperty(value = "TMap")
    public Map<String,Date> tMap;

    @JsonProperty(value = "Activites")
    public List<String> activites;

    public Map<String, Date> gettMap() {
        return tMap;
    }

    public void settMap(Map<String, Date> tMap) {
        this.tMap = tMap;
    }

    public List<String> getActivites() {
        return activites;
    }

    public void setActivites(List<String> activites) {
        this.activites = activites;
    }

}
