package model.decorator;

import connection.MySQL;
import gui.OrderIceCream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.IceCreamOrder;
import model.builder.IceCreamOrderBuilder;

public final class GiftWrappingDecorator extends IceCreamOrder {

    private IceCreamOrder decoratedOrder;
    private OrderIceCream OrderIceCream;

    public IceCreamOrder save() {
        StringBuilder toppingBuilder = new StringBuilder();
        for (String topping : decoratedOrder.getToppings()) {
            toppingBuilder.append(topping).append("\n");
        }

        StringBuilder syrupBuilder = new StringBuilder();
        for (String syrup : decoratedOrder.getSyrups()) {
            syrupBuilder.append(syrup).append("\n");
        }
        try {

            Double offer = (OrderIceCream.total_amount / 100) * 5;

            MySQL.execute("INSERT INTO orders(`customer_id`,`flavor`,`toppings`,`syrups`,`quantity`,`total_amount`,`name`)"
                    + " VALUES('" + OrderIceCream.user + "','" + decoratedOrder.getFlavor() + "','" + toppingBuilder.toString() + "','" + syrupBuilder.toString() + "','" + decoratedOrder.getQuantity() + "','" + (OrderIceCream.total_amount - offer) + "','" + ("Gift : " + decoratedOrder.getName()) + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(IceCreamOrderBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        JOptionPane.showMessageDialog(OrderIceCream, "Yo got sift offer succefully", "info", JOptionPane.INFORMATION_MESSAGE);

        return decoratedOrder;
    }

    public GiftWrappingDecorator(IceCreamOrder decoratedOrder) {
        this.decoratedOrder = decoratedOrder;

    }

}
