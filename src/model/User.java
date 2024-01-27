package model;

import connection.MySQL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    
    private int user_id;
    private List<IceCreamOrder> favorites = new ArrayList<>();
    private int loyaltyPoints;
    
    public User(int user_id) {
        this.user_id = user_id;
        
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM customers WHERE customer_id ='" + user_id + "' ");
            
            rs.next();
            loyaltyPoints += rs.getInt("loyalty_points");
            ResultSet rs1 = MySQL.execute("SELECT * FROM favorites WHERE customer_id ='" + user_id + "' ");
            while (rs1.next()) {                
                IceCreamOrder iceCreamOrder = new IceCreamOrder();
                
                iceCreamOrder.setId(rs1.getInt("favorite_id"));
                String string = rs.getString("toppings");
                 String[] toppingsArray = string.split("\\n");
                 List<String> toppings=new ArrayList<>();
                for (String string1 : toppingsArray) {
                    toppings.add(string1);
                }
                iceCreamOrder.setToppings(toppings);
                
                String stringSyrup = rs.getString("syrups");
                 String[] syrupsArray = stringSyrup.split("\\n");
                 List<String> syrups=new ArrayList<>();
                for (String string1 : syrupsArray) {
                    syrups.add(string1);
                }
                iceCreamOrder.setToppings(syrups);
                
                iceCreamOrder.setFlavor(rs.getString("flavor"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
    }
    
    public void saveFavoriteOrder(IceCreamOrder order) {
        favorites.add(order);
    }
    
    public void reorderFavoriteOrder(int index) {
        if (index >= 0 && index < favorites.size()) {
            IceCreamOrder favoriteOrder = favorites.get(index);
            System.out.println("Reordering favorite: " + favoriteOrder.getFlavor());
        } else {
            System.out.println("Invalid index for reordering.");
        }
    }
    
    public void earnLoyaltyPoints(int points) {
        loyaltyPoints += points;
        System.out.println("Earned " + points + " loyalty points. Total: " + loyaltyPoints);
    }
}
