/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package gui;

import connection.MySQL;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.FeedbackSystem;
import model.IceCreamOrder;
import model.User;
import model.builder.IceCreamOrderBuilder;
import model.chain.FlavorHandler;
import model.chain.SyrupHandler;
import model.chain.ToppingHandler;
import model.decorator.OrderDecorator;
import model.observer.NotificationService;
import model.strategy.CreditCardPayment;
import model.strategy.DigitalWalletPayment;
import model.strategy.PaymentProcessor;

/**
 *
 * @author sjeew
 */
public class OrderIceCream extends javax.swing.JDialog {
    
    public static Double total_amount = 0.00;
    public static Double flavorPrice = 0.00;
    public static int user = 1;
    public static List<IceCreamOrder> orderList = new ArrayList<>();
    public static List<IceCreamOrder> orderListCompleted = new ArrayList<>();
    public static List<List<String>> notifyObservers = new ArrayList<>();
    public static List<List<String>> notifyObserversCompleted = new ArrayList<>();
    public static User userObject = new User(user);
    
    public void loadOrders() {
        
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `orders` WHERE customer_id='" + user + "' AND `status`='OutForDelivery' ORDER BY order_date DESC");
            
            DefaultTableModel dtm = (DefaultTableModel) jTable3.getModel();
            dtm.setRowCount(0);
            
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString("order_id"));
                v.add(rs.getString("name"));
                v.add(rs.getString("flavor"));
                v.add(rs.getString("toppings"));
                v.add(rs.getString("syrups"));
                v.add(rs.getString("quantity"));
                v.add(rs.getString("total_amount"));
                v.add(rs.getString("order_date"));
                
                v.add(rs.getString("status"));
                dtm.addRow(v);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(OrderControl.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
    }
    
    public void clearData() {
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jRadioButton1.setSelected(false);
        jTextField1.setText("");
        name_txt.setText("");
        total_price.setText("0.00");
        
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);
        
        DefaultTableModel dtm1 = (DefaultTableModel) jTable2.getModel();
        dtm1.setRowCount(0);
        payment_cost.setText("");
    }
    
    public void loadFlavours() {
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM icecreamflavors");
            Vector v = new Vector();
            v.add("Select");
            while (rs.next()) {
                
                v.add(rs.getString("flavor_name"));
                
            }
            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            jComboBox1.setModel(dcm);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadSyrups() {
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM syrups");
            Vector v = new Vector();
            v.add("Select");
            while (rs.next()) {
                
                v.add(rs.getString("syrup_name"));
                
            }
            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            jComboBox3.setModel(dcm);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadToppings() {
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM toppings");
            Vector v = new Vector();
            v.add("Select");
            while (rs.next()) {
                
                v.add(rs.getString("topping_name"));
                
            }
            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            jComboBox2.setModel(dcm);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadNotifications() {
        
        Iterator<IceCreamOrder> orders = orderList.iterator();
        notifyObservers.clear();
        orders.forEachRemaining((t) -> {
            notifyObservers.add(t.notifyObservers());
        });
        
        StringBuilder msgBuilder = new StringBuilder();
        for (List<String> statusList : notifyObservers) {
            for (String status : statusList) {
                msgBuilder.append(status).append("\n");
            }
        }
        
        jTextArea1.setText(msgBuilder.toString());
    }
    
    public void loadNotificationsCompleted() {
        poiint_lbl.setText(String.valueOf(userObject.getLoyaltyPoints()));
        Iterator<IceCreamOrder> orders = orderListCompleted.iterator();
        notifyObserversCompleted.clear();
        orders.forEachRemaining((t) -> {
            notifyObserversCompleted.add(t.notifyObservers());
        });
        
        StringBuilder msgBuilder = new StringBuilder();
        for (List<String> statusList : notifyObserversCompleted) {
            for (String status : statusList) {
                msgBuilder.append(status).append("\n");
            }
        }
        
        jTextArea2.setText(msgBuilder.toString());
    }
    
    public void loadExistOrdersCompleted() {
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `orders` WHERE `customer_id`='" + OrderIceCream.user + "' AND `status`='OutForDelivery' ORDER BY `order_date` DESC");
            orderListCompleted.clear();
            while (rs.next()) {
                String toppingsString = rs.getString("toppings");
                IceCreamOrder iceCreamOrder = new IceCreamOrder();
                iceCreamOrder.setName(rs.getString("name"));
                iceCreamOrder.setStatus(rs.getString("status"));
                iceCreamOrder.setAmoount(Double.valueOf(rs.getString("total_amount")));
                iceCreamOrder.setDate(rs.getString("order_date"));
                
                iceCreamOrder.addObserver(new NotificationService());
                orderListCompleted.add(iceCreamOrder);
                
            }
            
        } catch (Exception ex) {
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    public void loadExistOrders() {
        try {
            ResultSet rs = MySQL.execute("SELECT * FROM `orders` WHERE `customer_id`='" + OrderIceCream.user + "' AND (`status`='Placed' OR `status`='InPreparation') ORDER BY `order_date` DESC");
            orderList.clear();
            while (rs.next()) {
                
                IceCreamOrder iceCreamOrder = new IceCreamOrder();
                iceCreamOrder.setName(rs.getString("name"));
                iceCreamOrder.setStatus(rs.getString("status"));
                iceCreamOrder.setAmoount(Double.valueOf(rs.getString("total_amount")));
                iceCreamOrder.setDate(rs.getString("order_date"));
                iceCreamOrder.addObserver(new NotificationService());
                orderList.add(iceCreamOrder);
                
            }
            
        } catch (Exception ex) {
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    public void NotificationService() {
        loadExistOrders();
        loadNotifications();
        loadExistOrdersCompleted();
        loadNotificationsCompleted();
        poiint_lbl.setText(String.valueOf(userObject.getLoyaltyPoints()));
        loadOrders();
    }
    
    Thread infiniteThread = new Thread(() -> {
        try {
            while (true) {
                NotificationService();
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    });
    
    public OrderIceCream(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadFlavours();
        loadSyrups();
        loadToppings();
        loadOrders();
        infiniteThread.start();
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Interrupt the thread and close the dialog
                infiniteThread.interrupt();
                System.out.println("Thread died");
            }
        });
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        name_txt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        payment_cost = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        total_price = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        order_id_txt = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        order_name_txt = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        comment_txt = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        poiint_lbl = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        giftCard = new javax.swing.JRadioButton();
        specialOffer = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Order Creation ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Select Topping :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Qty :");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Name :");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Select Syrup :");

        jComboBox3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setText("Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
        }

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        name_txt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Payment Type :");

        payment_cost.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Payment :");

        jComboBox4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Credit Card", "Digital Walet" }));

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setText("Palce Order");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Price :");

        total_price.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        total_price.setText("0.00");

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("Calculate");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane5.setViewportView(jTextArea2);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Our of Delivery");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Placed and In Preaparing");

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setText("Clear");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "name", "flavor", "toppings", "syrups", "quantity", "total", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setResizable(false);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
            jTable3.getColumnModel().getColumn(2).setResizable(false);
            jTable3.getColumnModel().getColumn(3).setResizable(false);
            jTable3.getColumnModel().getColumn(4).setResizable(false);
            jTable3.getColumnModel().getColumn(5).setResizable(false);
            jTable3.getColumnModel().getColumn(6).setResizable(false);
            jTable3.getColumnModel().getColumn(7).setResizable(false);
            jTable3.getColumnModel().getColumn(8).setResizable(false);
        }

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Order ID");

        order_id_txt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        order_id_txt.setText("None");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Order Name");

        order_name_txt.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        order_name_txt.setText("None");

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setText("Send Feedback");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        comment_txt.setColumns(20);
        comment_txt.setRows(5);
        jScrollPane6.setViewportView(comment_txt);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 958, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 688, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(order_id_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(order_name_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(8, 8, 8))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(order_id_txt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(order_name_txt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(211, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Point :");

        poiint_lbl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        poiint_lbl.setText("0");

        jRadioButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jRadioButton1.setText("favourit");

        buttonGroup1.add(giftCard);
        giftCard.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        giftCard.setText("Gift Card");
        giftCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giftCardActionPerformed(evt);
            }
        });

        buttonGroup1.add(specialOffer);
        specialOffer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        specialOffer.setText("Special Offer");
        specialOffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specialOfferActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(poiint_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(name_txt, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(total_price, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(1, 1, 1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(payment_cost)
                                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(giftCard, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(specialOffer, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton2))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane5)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButton5)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(poiint_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(name_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(total_price)
                                    .addComponent(jButton4)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2)
                            .addComponent(jRadioButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(giftCard)
                                    .addComponent(specialOffer))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(payment_cost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                            .addComponent(jScrollPane5))))
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:

        flavorPrice = 0.00;
        total_price.setText("0.00");
        int id = jComboBox1.getSelectedIndex();
        if (id > 0) {
            try {
                ResultSet rs = MySQL.execute("SELECT * FROM `icecreamflavors` WHERE flavor_id='" + id + "'");
                
                if (rs.next()) {
                    double price = rs.getDouble("flavor_price");
                    flavorPrice = price;
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
        total_price.setText("0.00");

    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        int id = jComboBox2.getSelectedIndex();
        if (id > 0) {
            try {
                ResultSet rs = MySQL.execute("SELECT * FROM `toppings` WHERE topping_id='" + id + "'");
                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                if (rs.next()) {
                    Vector v = new Vector();
                    v.add(rs.getString("topping_id"));
                    v.add(rs.getString("topping_name"));
                    v.add(rs.getDouble("topping_price"));
                    dtm.addRow(v);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int id = jComboBox3.getSelectedIndex();
        if (id > 0) {
            try {
                ResultSet rs = MySQL.execute("SELECT * FROM `syrups` WHERE syrup_id='" + id + "'");
                DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();
                if (rs.next()) {
                    Vector v = new Vector();
                    v.add(rs.getString("syrup_id"));
                    v.add(rs.getString("syrup_name"));
                    v.add(rs.getDouble("syrup_price"));
                    dtm.addRow(v);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(OrderIceCream.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        int rowCount = jTable1.getRowCount();
        int rowCount1 = jTable2.getRowCount();
        
        if (jTextField1.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select qty", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (flavorPrice == 0.00) {
            JOptionPane.showMessageDialog(this, "Please select required flavor", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Please add required toopings", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (rowCount1 == 0) {
            JOptionPane.showMessageDialog(this, "Please add required syrups", "warning", JOptionPane.WARNING_MESSAGE);
        } else {
            Double toppingPrice = 0.00;
            Double syrupPrice = 0.00;
            
            for (int x = 0; x < rowCount; x++) {
                String price = jTable1.getValueAt(x, 2).toString();
                toppingPrice += Double.valueOf(price);
            }
            for (int x = 0; x < rowCount1; x++) {
                String price = jTable2.getValueAt(x, 2).toString();
                syrupPrice += Double.valueOf(price);
            }
            Double totalPrie = flavorPrice + toppingPrice + syrupPrice;
            
            int qty = Integer.parseInt(jTextField1.getText());
            
            if (qty > 0) {
                total_price.setText(String.valueOf((totalPrie * qty)));
                total_amount = (totalPrie * qty);
            } else {
                JOptionPane.showMessageDialog(this, "Please set valid qty", "warning", JOptionPane.WARNING_MESSAGE);
            }
            
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
        total_price.setText("0.00");
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String price = total_price.getText();
        String payment = payment_cost.getText();
        String name = name_txt.getText();
        String qty = jTextField1.getText();
        int rowCount = jTable1.getRowCount();
        int rowCount1 = jTable2.getRowCount();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter created ice cream name", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (price.equals("0.00")) {
            JOptionPane.showMessageDialog(this, "Please create required ice cream", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (payment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please set payment", "warning", JOptionPane.WARNING_MESSAGE);
        } else if (Double.parseDouble(payment) != Double.parseDouble(price)) {
            JOptionPane.showMessageDialog(this, "Please set enogh money to order it", "warning", JOptionPane.WARNING_MESSAGE);
        } else {
            List<String> toppings = new ArrayList<>();
            List<String> syrups = new ArrayList<>();
            for (int x = 0; x < rowCount; x++) {
                String toppingName = jTable1.getValueAt(x, 1).toString();
                toppings.add(toppingName);
            }
            for (int x = 0; x < rowCount1; x++) {
                String syrupName = jTable2.getValueAt(x, 1).toString();
                syrups.add(syrupName);
            }
            
            NotificationService notificationService = new NotificationService();
            
            IceCreamOrder iceCreamOrder;
            List<String> Status;
            if (giftCard.isSelected()) {
                iceCreamOrder = new IceCreamOrder();
                iceCreamOrder.setName(name);
                iceCreamOrder.setFlavor(jComboBox1.getSelectedItem().toString());
                iceCreamOrder.addToppings(toppings);
                iceCreamOrder.addSyrups(syrups);
                iceCreamOrder.setQuantity(Integer.parseInt(qty));
                OrderDecorator.decorateWithGiftWrapping(iceCreamOrder);
                Status = iceCreamOrder.setStatus("Order Placed");
            } else if (specialOffer.isSelected()) {
                iceCreamOrder = new IceCreamOrder();
                iceCreamOrder.setName(name);
                iceCreamOrder.setFlavor(jComboBox1.getSelectedItem().toString());
                iceCreamOrder.addToppings(toppings);
                iceCreamOrder.addSyrups(syrups);
                iceCreamOrder.setQuantity(Integer.parseInt(qty));
                OrderDecorator.decorateWithSpecialPackaging(iceCreamOrder);
                Status = iceCreamOrder.setStatus("Order Placed");
            } else {
                iceCreamOrder = new IceCreamOrderBuilder()
                        .setName(name)
                        .setFlavor((String) jComboBox1.getSelectedItem())
                        .addToppings(toppings)
                        .addSyrups(syrups)
                        .setQuantity(Integer.parseInt(qty))
                        .build();
                iceCreamOrder.addObserver(notificationService);
                Status = iceCreamOrder.setStatus("Order Placed");
            }
            
            userObject.earnLoyaltyPoints(5);
            
            if (jRadioButton1.isSelected()) {
                userObject.saveFavoriteOrder(iceCreamOrder);
            }
            
            String msg = "";
            for (String Statu : Status) {
                msg += Statu;
            }
            
            JOptionPane.showMessageDialog(this, msg, "info", JOptionPane.INFORMATION_MESSAGE);
            
            loadExistOrders();
            loadNotifications();
            
            loadExistOrdersCompleted();
            loadNotificationsCompleted();
            
            iceCreamOrder.addCustomizationHandler(new FlavorHandler());
            iceCreamOrder.addCustomizationHandler(new SyrupHandler());
            iceCreamOrder.addCustomizationHandler(new ToppingHandler());
            iceCreamOrder.processCustomizations();
            
            if (jComboBox4.getSelectedIndex() == 0) {
                PaymentProcessor creditCardPaymentProcessor = new PaymentProcessor(new CreditCardPayment());
                creditCardPaymentProcessor.processPayment(Double.parseDouble(payment_cost.getText()), name);
            } else {
                PaymentProcessor digitalWalletPaymentProcessor = new PaymentProcessor(new DigitalWalletPayment());
                digitalWalletPaymentProcessor.processPayment(Double.parseDouble(payment_cost.getText()), name);
            }
            
            clearData();
            
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:


    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        // TODO add your handling code here:
        boolean match = jTextField1.getText().matches("^[+-]?\\d*$");
        
        if (!match) {
            jTextField1.setText("");
            evt.consume();
        }
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        clearData();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        // TODO add your handling code here:
        int selectedRow = jTable3.getSelectedRow();
        
        String id = jTable3.getValueAt(selectedRow, 0).toString();
        String name = jTable3.getValueAt(selectedRow, 1).toString();
        
        order_id_txt.setText(id);
        order_name_txt.setText(name);
        

    }//GEN-LAST:event_jTable3MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String id = order_id_txt.getText();
        String comment = comment_txt.getText();
        if (id.equals("None")) {
            JOptionPane.showMessageDialog(this, "Please select order first", "warning", JOptionPane.WARNING_MESSAGE);
            
        } else if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please set your feedback", "warning", JOptionPane.WARNING_MESSAGE);
            
        } else {
            FeedbackSystem feedbackSystem = new FeedbackSystem();
            IceCreamOrder order = new IceCreamOrder();
            order.setId(Integer.parseInt(id));
            feedbackSystem.collectFeedback(order, comment);
            order_id_txt.setText("None");
            order_name_txt.setText("None");
            comment_txt.setText("");
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void specialOfferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specialOfferActionPerformed
        // TODO add your handling code here:
        if (giftCard.isSelected()) {
            Double offer = (OrderIceCream.total_amount / 100) * 5;
            Double t = OrderIceCream.total_amount - offer;
            total_price.setText(String.valueOf(t));
        } else if (specialOffer.isSelected()) {
            Double offer = (OrderIceCream.total_amount / 100) * 10;
            total_price.setText(String.valueOf(OrderIceCream.total_amount - offer));
        }
    }//GEN-LAST:event_specialOfferActionPerformed

    private void giftCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giftCardActionPerformed
        // TODO add your handling code here:
        if (giftCard.isSelected()) {
            Double offer = (OrderIceCream.total_amount / 100) * 5;
            Double t = OrderIceCream.total_amount - offer;
            total_price.setText(String.valueOf(t));
        } else if (specialOffer.isSelected()) {
            Double offer = (OrderIceCream.total_amount / 100) * 10;
            total_price.setText(String.valueOf(OrderIceCream.total_amount - offer));
        }
    }//GEN-LAST:event_giftCardActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderIceCream.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderIceCream.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderIceCream.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderIceCream.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OrderIceCream dialog = new OrderIceCream(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        
                        System.exit(0);
                    }
                    
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextArea comment_txt;
    private javax.swing.JRadioButton giftCard;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField name_txt;
    private javax.swing.JLabel order_id_txt;
    private javax.swing.JLabel order_name_txt;
    private javax.swing.JTextField payment_cost;
    private javax.swing.JLabel poiint_lbl;
    private javax.swing.JRadioButton specialOffer;
    private javax.swing.JLabel total_price;
    // End of variables declaration//GEN-END:variables

}
