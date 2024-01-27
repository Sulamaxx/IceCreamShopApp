package model.observer;

import model.IceCreamOrder;

public interface OrderObserver {
     String update(IceCreamOrder order);
}
