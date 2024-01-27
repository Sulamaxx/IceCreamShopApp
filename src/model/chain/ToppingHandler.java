package model.chain;

import gui.OrderIceCream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JOptionPane;
import model.IceCreamOrder;

public class ToppingHandler implements IceCreamCustomizationHandler {

    private OrderIceCream OrderIceCream;

    @Override
    public void handleCustomization(IceCreamOrder order) {

        System.out.println("Handling topping customization");

        List<String> toppings = order.getToppings();

        for (String topping : toppings) {
            System.out.println("Adding " + topping + " topping to the ice cream");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Runnable myTask = () -> {
                JOptionPane.showMessageDialog(OrderIceCream, ("Adding " + topping + " syrup to the ice cream"), "Info", JOptionPane.INFORMATION_MESSAGE);
            };

            try {

                Future<?> future = executorService.submit(myTask);

                long timeout = 5;
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
}
