package model.strategy;

import gui.OrderIceCream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JOptionPane;

public class DigitalWalletPayment implements PaymentStrategy {

    private OrderIceCream OrderIceCream;

    @Override
    public void processPayment(double amount, String name) {
        System.out.println("Processing digital wallet payment: LRK" + amount);
        JOptionPane.showMessageDialog(OrderIceCream, ("Processing credit card payment: LKR" + amount), "info", JOptionPane.INFORMATION_MESSAGE);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Runnable myTask = () -> {

            JOptionPane.showMessageDialog(OrderIceCream, ("Completed credit card payment: LKR" + amount + ". Ice Cream name : " + name), "info", JOptionPane.INFORMATION_MESSAGE);
        };

        try {

            Future<?> future = executorService.submit(myTask);

            long timeout = 20;
            TimeUnit timeUnit = TimeUnit.SECONDS;

            future.get(timeout, timeUnit);
        } catch (TimeoutException e) {

            System.err.println("Task did not complete within the specified timeout.");
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            executorService.shutdown();
        }

    }
}
