package org.base.framework.chain;

import org.base.framework.context.SpringContextHelper;
import org.base.framework.jms.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class LoopJmsMessage implements Handler {

    private String retString=null;


    private long timeLong=Long.parseLong("62135596800000");

    private MessageFromDotNetElement elment=null;

    ArrayList<MessageFromDotNetElement> list=null;


    /**
     *
     */
    public LoopJmsMessage() {
        StringBuffer buf=new StringBuffer();
        for(int i=0;i<1024;i++)buf.append("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        retString=buf.toString();
        list=new ArrayList<MessageFromDotNetElement>();
        elment=new MessageFromDotNetElement();
        elment.setId(1);
        elment.setName("name");
        list.add(elment);


    }

    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     */
    @Override
    public void exceute(Map args) throws Exception {




        String jsonString=(String)args.get("jmsMessage");
        MessageFromDotNet jm=(MessageFromDotNet) JmsMessageHelper.toJmsMessage(jsonString, MessageFromDotNet.class);

        CoreService jmsCoreService=(JmsCoreService) SpringContextHelper.getBean("jmsCoreService");

        MessageFromDotNet reply=jm;

        //返回的队列名 根据service_app_jmsDestination--->
        //决定
        reply.setService("reply");
        reply.setSign("aaaaaabbbbbbbbbbbbbbbbb"); //返回值。。
        reply.setReply(""+(System.currentTimeMillis()+timeLong)*10000);

        reply.setList(list);



        //621355968000000000) / 10000
        double d=Math.random();
        if(d>0.8){
            reply.setBuffer("aaaaaaa");

        }

        jmsCoreService.send(jm);

    }
}
