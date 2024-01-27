
package model.state;

import model.IceCreamOrder;

public class PlacedState implements OrderState{
     @Override
    public boolean processOrder(IceCreamOrder order,int cid) {
        order.setStatus("Placed");
        order.processCustomizations();
        return true;
        
    }
}
