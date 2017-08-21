package org.base.framework.jms;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestSubdata2parent {


    public void child2parent() throws Exception {


        C c = new C();
        c.setApp("cApp");


        String string = JmsMessageHelper.toString(c);

        JmsMessage message = JmsMessageHelper.toJmsMessage(string, JmsMessage.class);


        message.getApp();

    }


    public void child2parentWithList() throws Exception {
        CC c = new CC();
        c.setApp("cApp");
        c.setName("ccccc");
        c.setS(new ArrayList<String>());
        c.getS().add("cccc1");


        String string = JmsMessageHelper.toString(c);

        JmsMessage message = JmsMessageHelper.toJmsMessage(string, JmsMessage.class);


        message.getApp();

    }


    public void child2parentWithEmptyList() throws Exception {
        CC c = new CC();
        c.setApp("cApp");
        c.setName("ccccc");
//			c.setS(new ArrayList<String>());
//			c.getS().add("cccc1");


        String string = JmsMessageHelper.toString(c);

        JmsMessage message = JmsMessageHelper.toJmsMessage(string, JmsMessage.class);


        message.getApp();

    }


//    @Test
//    public void child2parentWithSuperComplated() throws Exception {
//        A a = new A();
////        a.setNamea("aaaaaa");
//        a.setApp("appa");
//        C c = new C();
//        c.setNamec("ccccccccccc");
//        c.setApp("appc");
//        B<C> b = new B<C>();
//        b.setNameb("bbbbbbbbbb");
//        b.getListt().add(c);
//
//        a.getListb().add(b);
//
//
//        String string = JmsMessageHelper.toString(a);
//
//        JmsMessage message = JmsMessageHelper.toJmsMessage(string, JmsMessage.class);
//        message.getApp();
//        //TypeReference<A<B>>(){}
//        A aa = JmsMessageHelper.toObject(string, A.class);
//
//        B<C> bb = aa.getListb().get(0);
//        bb.getNameb();
//        C cc = bb.getListt().get(0);
//        cc.getNamec();
//        cc.getApp();
//    }

}
