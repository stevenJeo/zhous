/**
 *
 */
package org.base.framework.jms;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Data
public class A extends JmsMessage {


    private String name;
    private List<B<C>> list = new ArrayList<B<C>>();


}
