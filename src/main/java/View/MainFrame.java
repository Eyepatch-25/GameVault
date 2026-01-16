package View;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Admin
 */
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.*;
import Controller.GameController;
import Model.Game;
import Model.GameCollection; 
import Controller.DataManager;

public class MainFrame extends javax.swing.JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private static java.util.Map<String, String> registeredUsers = new java.util.HashMap<>();
    private DataManager dataManager;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
            initComponents();
            
            dataManager = DataManager.getInstance();

            cardLayout = new CardLayout();
            mainContainer = new JPanel(cardLayout);

            // Create a new panel that contains jPanel1 and jPanel2 (the role selection UI)
            JPanel rolePanel = new JPanel(new BorderLayout());
            rolePanel.add(jPanel2, BorderLayout.NORTH);
            rolePanel.add(jPanel1, BorderLayout.CENTER);

            // Login tabs created via methods
            mainContainer.add(rolePanel, "ROLE");
            mainContainer.add(UserLoginPanel(), "USER_LOGIN");
            mainContainer.add(AdminLoginPanel(), "ADMIN_LOGIN");

            setContentPane(mainContainer);
            cardLayout.show(mainContainer, "ROLE");

            setTitle("GameVault");
            setSize(900, 600);
            setLocationRelativeTo(null);
        }

    private JPanel UserLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("User Login");
        title.setFont(new Font("Times New Roman", Font.BOLD, 30));

        JTextField username = new JTextField(15);
        JPasswordField password = new JPasswordField(15);
        
        JButton register = new JButton("Register");
        JButton login = new JButton("Login");
        JButton back = new JButton("Back");



        login.addActionListener(e -> {
        String user = username.getText().trim();
        String pass = new String(password.getPassword()).trim();

        // Admin allowed to login as user
        if (user.equals("Ilesh Maskey") && pass.equals("24046585")) {
            JOptionPane.showMessageDialog(this, "User login successful");

            // Load admin's games from DataManager
            String userId = "U001"; // Admin's user ID
            Map<String, List<Game>> userGames = dataManager.getUserGames();

            // Create GameCollection with admin's actual games
            GameCollection collection = new GameCollection();
            if (userGames.containsKey(userId)) {
                for (Game game : userGames.get(userId)) {
                    collection.addGame(game);
                }
            }

            UserPanel userPanel = new UserPanel(collection, userGames);
            userPanel.setCurrentUser(userId);
            userPanel.setVisible(true);
            this.dispose();
        } 
        // Registered user check using DataManager
        else if (!dataManager.validateLogin(user, pass)) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid credentials!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        } else {
            JOptionPane.showMessageDialog(this, "User login successful");

            // Get user ID from DataManager
            String userId = dataManager.findUserIdByUsername(user);

            if (userId == null) {
                JOptionPane.showMessageDialog(this, "User ID not found!");
                return;
            }

            // Get user's games from DataManager
            Map<String, List<Game>> userGames = dataManager.getUserGames();

            // Create GameCollection with user's actual games
            GameCollection collection = new GameCollection();
            if (userGames.containsKey(userId)) {
                for (Game game : userGames.get(userId)) {
                    collection.addGame(game);
                }
            }

            UserPanel userPanel = new UserPanel(collection, userGames);
            userPanel.setCurrentUser(userId);
            userPanel.setVisible(true);
            this.dispose();
        }
    });

        
        back.addActionListener(e -> {
        cardLayout.show(mainContainer, "ROLE");
        });


        register.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password required");
                return;
            }

            // Use DataManager instead of local map
            dataManager = DataManager.getInstance();

            // Check if user already exists
            String existingUserId = dataManager.findUserIdByUsername(user);
            if (existingUserId != null) {
                JOptionPane.showMessageDialog(this, "User already registered!");
                return;
            }

            // Register user through DataManager
            String newUserId = dataManager.registerUser(user, pass);

            JOptionPane.showMessageDialog(
                this,
                "User registered successfully! User ID: " + newUserId,
                "Registration Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
        });


        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Username
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        panel.add(username, gbc);

        // Password
        gbc.gridx = 0; 
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(password, gbc);

        // Buttons — SAME COLUMN, DIFFERENT ROWS
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(register, gbc);

        gbc.gridy++;
        panel.add(login, gbc);

        gbc.gridy++;
        panel.add(back, gbc);

        return panel;


    }

    private JPanel AdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Times New Roman", Font.BOLD, 30));

        JTextField adminId = new JTextField(15);
        JPasswordField password = new JPasswordField(15);

        JButton login = new JButton("Login");
        JButton back = new JButton("Back");



        login.addActionListener(e -> {
            String admin = adminId.getText().trim();
            String pass = new String(password.getPassword()).trim();

            if (admin.equals("Ilesh Maskey") && pass.equals("24046585")) {
                JOptionPane.showMessageDialog(this, "Admin login successful");

                // Open AdminPanel
                GameCollection collection = new GameCollection();
                Map<String, List<Game>> userGames = new LinkedHashMap<>();
                AdminPanel adminPanel = new AdminPanel(collection, userGames);
                adminPanel.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid admin credentials!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });


        
        back.addActionListener(e -> {
        cardLayout.show(mainContainer, "ROLE");
        });




        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        panel.add(new JLabel("Admin ID:"), gbc);
        gbc.gridx = 1;
        panel.add(adminId, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(password, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(login, gbc);
        gbc.gridx = 1;
        panel.add(back, gbc);

        return panel;
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(66, 73, 87));

        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\rdsup\\Downloads\\image-2025-12-25-081325197.png")); // NOI18N
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\rdsup\\Downloads\\image-2025-12-25-014318461.png")); // NOI18N
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 157, 92));
        jLabel3.setText("Your Personal Game Library");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Customize and manage your libraries however you want.");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Track your progress and priorities.");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Supports any and every type of game.");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Advanced sort and search algorithms.");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("User-friendly environment and fundamental features.");

        jLabel9.setFont(new java.awt.Font("Tempus Sans ITC", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Login to your Account");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(66, 66, 66)))
                .addGap(153, 153, 153))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(263, 263, 263))
        );

        jPanel2.setBackground(new java.awt.Color(69, 69, 69));

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\rdsup\\Downloads\\image-2025-12-25-011908855.png")); // NOI18N
        jLabel1.setText("jLabel1");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 48)); // NOI18N
        jLabel2.setText("Admin / Users");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(204, 204, 204)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainContainer, "USER_LOGIN");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainContainer, "ADMIN_LOGIN");
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
