package org.base.framework.jms;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsMessageHelper {

    private static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(new UpperCaseNameStrategy());

    static {
        //mapper.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    public static String toString(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    @Deprecated
    public static JmsMessage toJmsMessage(String jsonString) throws Exception {
        return mapper.readValue(jsonString.getBytes("UTF-8"), JmsMessage.class);
    }

    @Deprecated
    public static JmsMessage toJmsMessage(String jsonString, Class clazz) throws Exception {
        return (JmsMessage) mapper.readValue(jsonString.getBytes("UTF-8"), clazz);
    }

    /**
     * 处理jsonString，返回指定类型clazz的对象
     * <p>
     * 指定类型中如果含有集合，且明确集合元素类型，也能转。
     * <p>
     * 建议使用typeReference
     *
     * @param jsonString
     * @param clazz
     * @return
     * @throws Exception
     */
    @Deprecated
    public static <T> T toObject(String jsonString, Class<?>... clazz) throws Exception {
        if (clazz.length == 2) {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(clazz[0], clazz[1]);
            return (T) mapper.readValue(jsonString.getBytes("UTF-8"), javaType);
        } else {
            return (T) mapper.readValue(jsonString.getBytes("UTF-8"), clazz[0]);
        }
    }

    public static <T> T toObject(String jsonString, Class clazz) throws Exception {
        return (T) mapper.readValue(jsonString.getBytes("UTF-8"), clazz);
    }

    /**
     * 处理jsonString，返回指定类型clazz的对象
     * <p>
     * 取代<T>T toObject(String jsonString, Class<?>... clazz)
     *
     * @param jsonString    json字符串
     * @param typeReference 目标类型，
     *                      new TypeReference<A>(){} ---转换成A.class
     *                      new TypeReference<A<B>>(){} ---转换成A<B>.class
     * @return
     * @throws Exception
     */
    public static <T> T toObject(String jsonString, TypeReference typeReference) throws Exception {
        return (T) mapper.readValue(jsonString.getBytes("UTF-8"), typeReference);
    }


    /**
     * 处理jsonString，返回指定的指定的集合类型，已经元素的类型。
     *
     * @param jsonString
     * @param collectionClasz
     * @param elementClazz
     * @return
     * @throws Exception
     */
    public static <T> T toCollection(String jsonString, Class<?> collectionClasz, Class<?> elementClazz) throws Exception {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClasz, elementClazz);
        return (T) mapper.readValue(jsonString.getBytes("UTF-8"), javaType);
    }
}
