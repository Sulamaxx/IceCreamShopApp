package model;

public class FeedbackSystem {

    public void collectFeedback(IceCreamOrder order, String feedback) {
        System.out.println("Feedback collected for order " + order.getFlavor() + ": " + feedback);
    }

    public void displayHighlyRatedCombinations() {
        System.out.println("Displaying highly-rated ice cream combinations.");
    }
}
