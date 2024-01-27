package model;

import connection.MySQL;
import gui.OrderIceCream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class FeedbackSystem {

    private OrderIceCream OrderIceCream;

    public void collectFeedback(IceCreamOrder order, String feedback) {
        try {
            MySQL.execute("INSERT INTO feedback(order_id,comment) VALUES('" + order.getId() + "','" + feedback + "')");
            JOptionPane.showMessageDialog(OrderIceCream, "Seend message successfully", "info", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FeedbackSystem.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        System.out.println("Feedback collected for order " + order.getFlavor() + ": " + feedback);
    }

}
