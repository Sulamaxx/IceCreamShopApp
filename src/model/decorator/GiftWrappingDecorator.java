/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.decorator;

import model.IceCreamOrder;

/**
 *
 * @author sjeew
 */
public class GiftWrappingDecorator extends IceCreamOrder{
     private IceCreamOrder decoratedOrder;

    public GiftWrappingDecorator(IceCreamOrder decoratedOrder) {
        this.decoratedOrder = decoratedOrder;
    }

    @Override
    public String getStatus() {
        return "Gift Wrapped " + decoratedOrder.getStatus();
    }
}
