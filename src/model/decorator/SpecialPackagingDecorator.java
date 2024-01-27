package model.decorator;

import connection.MySQL;
import gui.OrderIceCream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.IceCreamOrder;
import model.builder.IceCreamOrderBuilder;

public final class SpecialPackagingDecorator extends IceCreamOrder {

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

            Double offer = (OrderIceCream.total_amount / 100) * 10;

            MySQL.execute("INSERT INTO orders(`customer_id`,`flavor`,`toppings`,`syrups`,`quantity`,`total_amount`,`name`)"
                    + " VALUES('" + OrderIceCream.user + "','" + decoratedOrder.getFlavor() + "','" + toppingBuilder.toString() + "','" + syrupBuilder.toString() + "','" + decoratedOrder.getQuantity() + "','" + (OrderIceCream.total_amount - offer) + "','" + ("Special : " + decoratedOrder.getName()) + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(IceCreamOrderBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        JOptionPane.showMessageDialog(OrderIceCream, "You got special offer successfully", "info", JOptionPane.INFORMATION_MESSAGE);

        return decoratedOrder;
    }

    public SpecialPackagingDecorator(IceCreamOrder decoratedOrder) {
        this.decoratedOrder = decoratedOrder;
        setName("SpecialOffer : " + decoratedOrder.getName());
    }

}
