package model.builder;

import connection.MySQL;
import gui.OrderIceCream;
import static gui.OrderIceCream.notifyObservers;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.IceCreamOrder;


public class IceCreamOrderBuilder {
    private String name;
    private String flavor;
    private List<String> toppings;
    private List<String> syrups;
    private int quantity;
    public IceCreamOrderBuilder setFlavor(String flavor) {
        this.flavor = flavor;
        return this;
    }
    public IceCreamOrderBuilder setName(String name) {
        this.name = name;
        return this;
    }
    public IceCreamOrderBuilder addToppings(List<String> topping) {
        if (toppings == null) {
            toppings = new ArrayList<>();
        }
        toppings.addAll(topping);
        return this;
    }
    public IceCreamOrderBuilder addSyrups(List<String> syrup) {
        if (syrups == null) {
            syrups = new ArrayList<>();
        }
        syrups.addAll(syrup);
        return this;
    }
    public IceCreamOrderBuilder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
    public IceCreamOrder build() {
        IceCreamOrder order = new IceCreamOrder();
        order.setName(name);
        order.setFlavor(flavor);
        order.setToppings(toppings);
        order.setSyrups(syrups);
        order.setQuantity(quantity);
        StringBuilder toppingBuilder = new StringBuilder();
        for (String topping : toppings) {
            toppingBuilder.append(topping).append("\n");
        }
        StringBuilder syrupBuilder = new StringBuilder();
        for (String syrup : syrups) {
            syrupBuilder.append(syrup).append("\n");
        }
        try {

            MySQL.execute("INSERT INTO orders(`customer_id`,`flavor`,`toppings`,`syrups`,`quantity`,`total_amount`,`name`)"
                    + " VALUES('" + OrderIceCream.user + "','" + flavor + "','" + toppingBuilder.toString() + "','" + syrupBuilder.toString() + "','" + quantity + "','" + OrderIceCream.total_amount + "','" + name + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(IceCreamOrderBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return order;
    }
}
