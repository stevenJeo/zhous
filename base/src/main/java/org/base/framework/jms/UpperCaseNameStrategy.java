package org.base.framework.jms;

import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class UpperCaseNameStrategy extends PropertyNamingStrategy {

    public String nameForField(MapperConfig config, AnnotatedField field,
                               String defaultName) {
        return convert(defaultName);

    }

    public String nameForGetterMethod(MapperConfig config,
                                      AnnotatedMethod method, String defaultName) {
        return convert(defaultName);
    }

    public String nameForSetterMethod(MapperConfig config,
                                      AnnotatedMethod method, String defaultName) {
        String a = convert(defaultName);
        return a;
    }

    public String convert(String defaultName) {
        char[] arr = defaultName.toCharArray();
        if (arr.length != 0) {
            if (Character.isLowerCase(arr[0])) {
                char lower = Character.toUpperCase(arr[0]);
                arr[0] = lower;
            }
        }
        return new StringBuilder().append(arr).toString();
    }
}
