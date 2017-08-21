package org.base.framework.context;

import org.base.framework.jms.JmsMessage;
import org.base.framework.jms.JmsMessageHelper;
import org.junit.Test;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestToObjectFromdotnetJsonString {

    @Test
    public void test() throws Exception {

        String tmp="{\"TestString\":\"635467241796172239\",\"Buffer\":\"aaaaaaaaaaaaaaa\",\"Reply\":null,\"MsgDeliveryMode\":0,\"MsgPriority\":1,\"MsgTimeToLiveInSecond\":180,\"Id\":null,\"App\":\"testApp\",\"Service\":\"testService\",\"Version\":null,\"Sign\":null,\"ReplyId\":\"1\"}";

        JmsMessage message= JmsMessageHelper.toJmsMessage(tmp);

        int i=0;
        i=i++;

    }

}
