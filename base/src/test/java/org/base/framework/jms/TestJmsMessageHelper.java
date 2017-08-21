package org.base.framework.jms;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestJmsMessageHelper {


    public void testListConvert_AcontainsListB() throws Exception {

        List lista = new ArrayList();


        List list = new ArrayList();
        B b = new B();
        b.setName("b111111");
        list.add(b);

        b = new B();
        b.setName("b22222");
        list.add(b);


        A a = new A();
        a.setName("a1111111");
        a.setList(list);

        lista.add(a);

        a = new A();
        a.setName("a222222");
        a.setList(list);

        lista.add(a);

        String tmp = JmsMessageHelper.toString(a);

        A a1 = JmsMessageHelper.toObject(tmp, A.class);

        //A a1=(A)JmsMessageHelper.toJmsMessage(tmp,A.class);

        Assert.assertNotNull(a1);

        Assert.assertEquals(a.getName(), a1.getName());

        Assert.assertEquals(a.getList().size(), a1.getList().size());
        for (int i = 0; i < a1.getList().size(); i++) {
            Assert.assertEquals(a.getList().get(i).getName(), a1.getList().get(i).getName());

        }

    }


//	public static <T>T toObject(String src,Class<?> valueType) throws Exception {
//        return (T)mapper.readValue(src.getBytes("UTF-8"), valueType);
//    }


    private List aaa;


    public static <T> T jacksonToCollection(String src, Class<? extends Collection> collectionClass, Class<?>... valueType)
            throws Exception {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, valueType);


        //mapper.getTypeFactory().

        return (T) mapper.readValue(src.getBytes("UTF-8"), javaType);
    }


    public void testListConvert_ListA() throws Exception {

        List lista = new ArrayList();


        List list = new ArrayList();
        B<C> b = new B<C>();
        b.setName("b111111");
        //list.add(b);

        C c = new C();
        c.setName("c111111");
        list.add(c);

        c = new C();
        c.setName("c222222");
        list.add(c);

        b.setList(list);

        A a = new A();
        a.setName("a1111111");
        //a.setList(list);
//        a.setB(b);

        //lista.add(a);

//		a=new A();
//		a.setName("a222222");
//		a.setList(list);
//
//		lista.add(a);

        String tmp = JmsMessageHelper.toString(a);

        //List lista1=(List)JmsMessageHelper.toObject(tmp, List.class);

        //mapper.enableDefaultTyping();


        A a1 = JmsMessageHelper.toObject(tmp, A.class);


        int i = 0;
        i++;


//        JavaType javaType= mapper.getTypeFactory().constructParametricType(ArrayList.class, A.class);
//        List<A> lista1=mapper.readValue(tmp.getBytes("UTF-8"), javaType);

//
//		Assert.assertNotNull(lista1);
//
//		Assert.assertEquals(2, lista1.size());
//		for(int i=0;i<lista1.size();i++){
//			Assert.assertTrue(lista1.get(i).getName().equalsIgnoreCase("a1111111") || lista1.get(i).getName().equalsIgnoreCase("a222222") );
//		}


    }

    /**
     * 对象A，使用list-B
     * <p>
     * 检查转换完的结果，listB
     *
     * @throws Exception
     */
    @Test
    public void t2() throws Exception {
        AA<BB> a = new AA<BB>();
        a.setName("a_name");
        BB b = new BB();
        b.setName("b_name");

        List listb = new ArrayList();
        listb.add(b);

        a.setList(listb);

        String json = JmsMessageHelper.toString(a);

        AA<BB> a1 = null;


        a1 = mapper.readValue(json, new TypeReference<AA<BB>>() {
        });
        List<BB> listb1 = a1.getList();
        BB b1 = listb1.get(0);

        a1 = JmsMessageHelper.toObject(json, AA.class, BB.class);

        b1 = a1.getList().get(0);

        a1 = null;


    }


    public void t1estListConvert_ListA() throws Exception {

        List lista = new ArrayList();


        List list = new ArrayList();
        B<C> b = new B<C>();
        b.setName("b111111");
        //list.add(b);

        C c = new C();
        c.setName("c111111");
        list.add(c);

        c = new C();
        c.setName("c222222");
        list.add(c);

        b.setList(list);

        A a = new A();
        a.setName("a1111111");
        //a.setList(list);
//        a.setB(b);

        //lista.add(a);

//		a=new A();
//		a.setName("a222222");
//		a.setList(list);
//
//		lista.add(a);

        String tmp = JmsMessageHelper.toString(b);

        //List lista1=(List)JmsMessageHelper.toObject(tmp, List.class);

        //mapper.enableDefaultTyping();


        B<C> b1 = JmsMessageHelper.toObject(tmp, B.class);


        int i = 0;
        i++;


//        JavaType javaType= mapper.getTypeFactory().constructParametricType(ArrayList.class, A.class);
//        List<A> lista1=mapper.readValue(tmp.getBytes("UTF-8"), javaType);

//
//		Assert.assertNotNull(lista1);
//
//		Assert.assertEquals(2, lista1.size());
//		for(int i=0;i<lista1.size();i++){
//			Assert.assertTrue(lista1.get(i).getName().equalsIgnoreCase("a1111111") || lista1.get(i).getName().equalsIgnoreCase("a222222") );
//		}


    }


    private static ObjectMapper mapper = new ObjectMapper();


}
