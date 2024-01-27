package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.chain.IceCreamCustomizationHandler;
import model.observer.OrderObserver;

public class IceCreamOrder {

    private int id;
    private String name;
    private String flavor;
    private List<String> toppings;
    private List<String> syrups;
    private int quantity;
    private String status;
    
   private String date;
   private Double amoount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmoount() {
        return amoount;
    }

    public void setAmoount(Double amoount) {
        this.amoount = amoount;
    }

    private List<OrderObserver> observers = new ArrayList<>();
    private List<IceCreamCustomizationHandler> customizationHandlers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public List<String> notifyObservers() {
        List<String> updates = new ArrayList<>();
        for (OrderObserver observer : observers) {

            updates.add(observer.update(this));
        }
        return updates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addCustomizationHandler(IceCreamCustomizationHandler handler) {
        customizationHandlers.add(handler);
    }

    public String getName() {
        return name;
    }

    public String getFlavor() {
        return flavor;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public List<String> getSyrups() {
        return syrups;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<OrderObserver> getObservers() {
        return observers;
    }

    public List<IceCreamCustomizationHandler> getCustomizationHandlers() {
        return customizationHandlers;
    }

    public void processCustomizations() {
        for (IceCreamCustomizationHandler handler : customizationHandlers) {
            handler.handleCustomization(this);
        }
    }

    // Other getters and setters
    public String getStatus() {
        return status;
    }

    public List<String> setStatus(String status) {
        this.status = status;
        return notifyObservers();
    }

    // Additional methods as needed
    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public void setSyrups(List<String> syrups) {
        this.syrups = syrups;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }
}
