package model.observer;

import model.IceCreamOrder;

public class NotificationService implements OrderObserver {

    @Override
    public String update(IceCreamOrder order) {
        System.out.println("Notification: Order status updated - " + order.getStatus());
        StringBuilder msgBuilder = new StringBuilder();

        msgBuilder.append("*** Notification ***").append("\n");
        msgBuilder.append("Order status updated - ").append(order.getStatus()).append("\n");
        msgBuilder.append("Create name : ").append(order.getName()).append("\n");
        msgBuilder.append("Order price : ").append(order.getAmoount()).append("\n");
        msgBuilder.append("Order date : ").append(order.getDate()).append("\n");
       
        return msgBuilder.toString();
 
    }

    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message);
    }
}
