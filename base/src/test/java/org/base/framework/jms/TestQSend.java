package org.base.framework.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestQSend implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(TestQSend.class);

    public final String doesPerformanceTrackKey = "doesPerformanceTrack";


    private static boolean onlySedn;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

//    	String path=Thread.currentThread().getContextClassLoader().getResource(".").getPath();
//    	LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
//	    loggerContext.reset();
//	    JoranConfigurator joranConfigurator = new JoranConfigurator();
//	    joranConfigurator.setContext(loggerContext);
//	    try {
//	    	if(args.length>0 && args[0].length()>0)
//	    		joranConfigurator.doConfigure(path+args[0]);
//	    	else
//	    		joranConfigurator.doConfigure(path+"logback.xml");
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    }

        TestQSend qs = new TestQSend();

        System.out.print("return key to start");
        BufferedReader brs = new BufferedReader(new InputStreamReader(System.in));
        brs.readLine();

        stopFlag = false;
        onlySedn = "onlySend".equalsIgnoreCase(args[0]);
        qs.send(args[1], args[2], args[3]);

        System.out.print("return key to stop");

        brs.readLine();

        stopFlag = true;

        brs.close();

    }

    private static boolean stopFlag = true;


    private String url;
    private String url1;
    private String qname;


    public void send(String url, String qname, String url1) {

        this.url = url;
        this.url1 = url1;
        this.qname = qname;

        if (onlySedn) {

            Thread thread = new Thread(this);
            thread.start();
        } else {

            Thread thread1 = new Thread(new RECEVIVE());
            thread1.start();

            Thread thread2 = new Thread(new RECEVIVE());
            thread2.start();

            Thread thread3 = new Thread(new RECEVIVE());
            //thread3.start();
        }
    }


    ActiveMQConnectionFactory connectionFactoryReceive;


    @Override
    public void run() {
        Connection connection = null;
        Session session = null;
        //MessageConsumer consumer=null;
        MessageProducer producer = null;
        Destination destination = null;

        while (!stopFlag) {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                connectionFactory.setBrokerURL(url);
                connectionFactory.setUseAsyncSend(true);
                ///connectionFactory.setMaxThreadPoolSize(1);


                //TODO 错误处理
                //if(consumer!=null) {consumer.close(); consumer=null;}
                //if(session!=null){session.close();session=null;}
                if (connection == null) {
                    connection = connectionFactory.createConnection();
                    connection.start();
                    //connection.setExceptionListener(this);
                    if (producer != null) {
                        producer.close();
                        producer = null;
                    }
                    if (destination != null) destination = null;
                    if (session != null) {
                        session.close();
                        session = null;
                    }
                }
                if (session == null) {
                    session = connection.createSession(false, 1);
                    if (destination != null) destination = null;
                    destination = session.createQueue(qname);
                    if (producer != null) {
                        producer.close();
                        producer = null;
                    }
                    producer = session.createProducer(destination);
                }
                if (logger.isDebugEnabled()) logger.debug("Start send message");
                long i = 0;
                while (!stopFlag) {

                    TextMessage m = session.createTextMessage("test i=" + i + " " + System.currentTimeMillis());
                    m.setLongProperty(doesPerformanceTrackKey, i);
                    producer.send(m);
                    i++;
                }


            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            } finally {
                try {
                    if (producer != null) {
                        producer.close();
                        producer = null;
                    }
                    if (destination != null) destination = null;
                    if (session != null) {
                        session.close();
                        session = null;
                    }
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                } catch (Exception fe) {
                    //
                }
            }
        }

    }


    public class RECEVIVE implements Runnable {

        @Override
        public void run() {

            Session session = null;
            Connection connectionReceive = null;
            MessageConsumer consumer = null;
            Destination destination = null;

            while (!stopFlag) {
                try {
                    if (connectionFactoryReceive == null) {
                        connectionFactoryReceive = new ActiveMQConnectionFactory();
                        connectionFactoryReceive.setBrokerURL(url1);
                    }
                    //connectionFactory.setUseAsyncSend(true);
                    //connectionFactory.setMaxThreadPoolSize(1);


                    //TODO 错误处理
                    //if(consumer!=null) {consumer.close(); consumer=null;}
                    //if(session!=null){session.close();session=null;}
                    if (connectionReceive == null) {
                        connectionReceive = connectionFactoryReceive.createConnection();
                        connectionReceive.start();
                        //connection.setExceptionListener(this);
                        if (consumer != null) {
                            consumer.close();
                            consumer = null;
                        }
                        if (destination != null) destination = null;
                        if (session != null) {
                            session.close();
                            session = null;
                        }
                    }
                    if (session == null) {
                        session = connectionReceive.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
                        if (destination != null) destination = null;
                        destination = session.createQueue(qname);
                        if (consumer != null) {
                            consumer.close();
                            consumer = null;
                        }
                        consumer = session.createConsumer(destination);
                    }


                    long i = 0;
                    if (logger.isDebugEnabled()) logger.debug("Start receive message");
                    while (!stopFlag) {
                        TextMessage m = (TextMessage) consumer.receive(10000);
                        if (m != null) {

                            long t0 = m.getLongProperty(doesPerformanceTrackKey);
                            if (i != t0) System.out.println("ERROR expect i=" + i + "but  r=" + t0);
                            i = t0 + 1;

                            String tmp[] = m.getText().split(" ");
                            long st = Long.parseLong(tmp[2]);
                            long t = System.currentTimeMillis();
                            if (t - st > 2000) System.out.println("long delay i=" + tmp[1] + " delay=" + (t - st));


                        }
                    }


                } catch (Exception e) {
                    System.out.println("Caught: " + e);
                    e.printStackTrace();
                } finally {
                    try {
                        if (consumer != null) {
                            consumer.close();
                            consumer = null;
                        }
                        if (destination != null) destination = null;
                        if (session != null) {
                            session.close();
                            session = null;
                        }
                        if (connectionReceive != null) {
                            connectionReceive.close();
                            connectionReceive = null;
                        }
                    } catch (Exception fe) {
                        //
                    }
                }
            }

        }

    }
}
