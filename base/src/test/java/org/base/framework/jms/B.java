package org.base.framework.jms;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class B<T> {

    private String name;

    private List<T> list = new ArrayList<T>();

    public B() {
    }


}
