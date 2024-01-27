package model.chain;

import gui.OrderIceCream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JOptionPane;
import model.IceCreamOrder;

public class FlavorHandler implements IceCreamCustomizationHandler {

    private OrderIceCream OrderIceCream;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Runnable myTask;

    @Override
    public void handleCustomization(IceCreamOrder order) {
        System.out.println("Handling flavor customization");

        String flavor = order.getFlavor();

        switch (flavor.toLowerCase()) {
            case "chocolate":
                System.out.println("Adding chocolate flavor to the ice cream");

                myTask = () -> {

                    JOptionPane.showMessageDialog(OrderIceCream, ("Adding chocolate flavor to the ice cream"), "Info", JOptionPane.INFORMATION_MESSAGE);
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
                break;
            case "vanilla":
                System.out.println("Adding vanilla flavor to the ice cream");

                myTask = () -> {

                    JOptionPane.showMessageDialog(OrderIceCream, ("Adding vanilla flavor to the ice cream"), "Info", JOptionPane.INFORMATION_MESSAGE);
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
                break;
            case "strawberry":
                System.out.println("Adding strawberry flavor to the ice cream");

                myTask = () -> {

                    JOptionPane.showMessageDialog(OrderIceCream, ("Adding strawberry flavor to the ice cream"), "Info", JOptionPane.INFORMATION_MESSAGE);
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
                break;
            default:
                System.out.println("Unknown flavor: " + flavor);

                myTask = () -> {

                    JOptionPane.showMessageDialog(OrderIceCream, ("Unknown flavor: " + flavor), "Info", JOptionPane.INFORMATION_MESSAGE);
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
                break;
        }

    }

}
