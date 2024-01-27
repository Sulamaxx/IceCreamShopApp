
package model.state;

import model.IceCreamOrder;

public interface OrderState {
    boolean processOrder(IceCreamOrder order,int cid);
}
