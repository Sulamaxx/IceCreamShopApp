package model.state;

import connection.MySQL;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.IceCreamOrder;

public class OutForDeliveryState implements OrderState {

    @Override
    public boolean processOrder(IceCreamOrder order, int cid) {
        try {
            order.setStatus("OutForDelivery");
              order.processCustomizations();
            MySQL.execute("Update orders SET status='" + order.getStatus() + "' WHERE customer_id='" + cid + "' AND order_id='"+order.getId()+"'");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(OutForDeliveryState.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }
}
