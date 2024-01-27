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
public class OrderDecorator {
     public static IceCreamOrder decorateWithGiftWrapping(IceCreamOrder order) {
        return new GiftWrappingDecorator(order);
    }

    public static IceCreamOrder decorateWithSpecialPackaging(IceCreamOrder order) {
        return new SpecialPackagingDecorator(order);
    }
}
