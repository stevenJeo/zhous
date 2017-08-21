package org.base.framework.jms;

import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestMessageStringConvert {


    @Test
    public void test() throws Exception {

        OrderMessage o = new OrderMessage();
        o.setAbc("abc.........");
        o.setOrderId(2);
//        o.setSubDate(new Date());


        o.setSign("aaaaaaaaaaaaaaa");

        String jmsMsgString = JmsMessageHelper.toString(o);

        Assert.isTrue(jmsMsgString.indexOf("abc") > -1, "contains abc substring");


        JmsMessage jm = JmsMessageHelper.toJmsMessage(jmsMsgString);

        Assert.isInstanceOf(JmsMessage.class, jm);

        OrderMessage om = (OrderMessage) JmsMessageHelper.toJmsMessage(jmsMsgString, OrderMessage.class);

        Assert.isInstanceOf(OrderMessage.class, om);


    }

}
