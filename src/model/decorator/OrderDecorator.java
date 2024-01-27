
package model.decorator;

import model.IceCreamOrder;

public class OrderDecorator {

    public static IceCreamOrder decorateWithGiftWrapping(IceCreamOrder order) {
        GiftWrappingDecorator giftWrappingDecorator = new GiftWrappingDecorator(order);
        return giftWrappingDecorator.save();
    }

    public static IceCreamOrder decorateWithSpecialPackaging(IceCreamOrder order) {
        SpecialPackagingDecorator specialPackagingDecorator = new SpecialPackagingDecorator(order);
        return specialPackagingDecorator.save();
    }
}
