package View;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Admin
 */

import View.MainFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.text.SimpleDateFormat;
import Model.Game;
import Model.GameCollection;
import Controller.DataManager;

public class AdminPanel extends javax.swing.JFrame {
    
    private GameCollection gameCollection;
    private Map<String, List<Game>> userGames = new LinkedHashMap<>();
    private Map<String, String[]> userCredentials = new LinkedHashMap<>();
    private DataManager dataManager;

    private void searchUserById() {
        String searchId = jTextField4.getText().trim();

        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter User ID");
            return;
        }

        // Get all users
        List<String[]> allUsers = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : dataManager.getUserCredentials().entrySet()) {
            allUsers.add(new String[]{entry.getKey(), entry.getValue()[0], entry.getValue()[1]});
        }

        // Bubble Sort
        for (int i = 0; i < allUsers.size() - 1; i++) {
            for (int j = 0; j < allUsers.size() - i - 1; j++) {
                if (allUsers.get(j)[0].compareToIgnoreCase(allUsers.get(j + 1)[0]) > 0) {
                    String[] temp = allUsers.get(j);
                    allUsers.set(j, allUsers.get(j + 1));
                    allUsers.set(j + 1, temp);
                }
            }
        }

        // Binary Search
        int left = 0;
        int right = allUsers.size() - 1;
        boolean found = false;

        while (left <= right && !found) {
            int mid = (left + right) / 2;
            String currentId = allUsers.get(mid)[0].toLowerCase();

            if (currentId.contains(searchId.toLowerCase())) {
                // Show result
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);
                model.addRow(new Object[]{
                    allUsers.get(mid)[0], 
                    allUsers.get(mid)[1], 
                    allUsers.get(mid)[2]
                });
                JOptionPane.showMessageDialog(this, "User found!");
                found = true;
            } else if (currentId.compareTo(searchId.toLowerCase()) > 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "User not found");
        }
    }
    
    private void searchGamesByUserId() {
        String searchId = jTextField5.getText().trim();

        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter User ID");
            return;
        }

        // Get all users who have games
        List<String> allUserIds = new ArrayList<>(dataManager.getUserGames().keySet());

        // Bubble Sort
        for (int i = 0; i < allUserIds.size() - 1; i++) {
            for (int j = 0; j < allUserIds.size() - i - 1; j++) {
                if (allUserIds.get(j).compareToIgnoreCase(allUserIds.get(j + 1)) > 0) {
                    String temp = allUserIds.get(j);
                    allUserIds.set(j, allUserIds.get(j + 1));
                    allUserIds.set(j + 1, temp);
                }
            }
        }

        // Binary Search
        int left = 0;
        int right = allUserIds.size() - 1;
        boolean found = false;

        while (left <= right && !found) {
            int mid = (left + right) / 2;
            String currentId = allUserIds.get(mid).toLowerCase();

            if (currentId.contains(searchId.toLowerCase())) {
                // Show games for this user
                DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
                model.setRowCount(0);

                List<Game> userGames = dataManager.getUserGames().get(allUserIds.get(mid));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                if (userGames != null) {
                    for (Game game : userGames) {
                        String completionDateStr = (game.getCompletionDate() != null) ? 
                            dateFormat.format(game.getCompletionDate()) : "N/A";

                        model.addRow(new Object[]{
                            allUserIds.get(mid),
                            game.getTitle(),
                            game.getGenre(),
                            game.getPlatform(),
                            game.getReleaseYear(),
                            game.getHoursPlayed(),
                            game.getCompletionStatus(),
                            completionDateStr,
                            game.getRating()
                        });
                    }
                }

                JOptionPane.showMessageDialog(this, "Games found for user!");
                found = true;
            } else if (currentId.compareTo(searchId.toLowerCase()) > 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No games found for user");
        }
    }
    
    private void showAllUsers() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        // Get all users from DataManager
        Map<String, String[]> credentials = dataManager.getUserCredentials();

        for (Map.Entry<String, String[]> entry : credentials.entrySet()) {
            model.addRow(new Object[]{
                entry.getKey(),      // User ID
                entry.getValue()[0], // Username
                entry.getValue()[1]  // Password
            });
        }

        // Clear search field
        jTextField4.setText("");
    }
    
    /**
     * Creates new form AdminPanel
     */
    public AdminPanel(GameCollection gameCollection, Map<String, List<Game>> userGames) {
        dataManager = DataManager.getInstance();
        this.gameCollection = gameCollection;
        this.userGames = dataManager.getUserGames(); // FIX: Get from DataManager

        initComponents();
        
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click
                    int row = jTable1.getSelectedRow();
                    if (row >= 0) {
                        // Get user data from selected row
                        String userId = jTable1.getValueAt(row, 0).toString();
                        String username = jTable1.getValueAt(row, 1).toString();
                        String password = jTable1.getValueAt(row, 2).toString();

                        // Fill the update fields
                        jTextField2.setText(userId);
                        jTextField3.setText(username);
                        jPasswordField1.setText(password);

                        // Show instruction message
                        JOptionPane.showMessageDialog(AdminPanel.this,
                            "User loaded for update:\n" +
                            "User ID: " + userId + "\n" +
                            "Current Username: " + username + "\n" +
                            "Current Password: " + password + "\n\n" +
                            "Instructions:\n" +
                            "1. Leave username field empty to keep current username\n" +
                            "2. Leave password field empty to keep current password\n" +
                            "3. Fill both to update both",
                            "Update User",
                            JOptionPane.INFORMATION_MESSAGE);

                        // Switch to Update Lists tab
                        showCard("card4");
                    }
                }
            }
        });
        
        refreshUserTable();
        refreshGameTable();
    }

        private void initializeSampleCredentials() {
            userCredentials.put("U001", new String[]{"john_doe", "password123"});
            userCredentials.put("U002", new String[]{"jane_smith", "pass456"});
            userCredentials.put("U003", new String[]{"bob_wilson", "secret789"});
            userCredentials.put("U004", new String[]{"alice_brown", "admin123"});
        }



    
    private void addGameForUser(String userId, Game game) {
        gameCollection.addGame(game);
        userGames.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(game);
    }
    
    private void addSampleGames() {
        Calendar cal = Calendar.getInstance();

        cal.set(2023, Calendar.OCTOBER, 15);
        addGameForUser("U001", new Game("Minecraft", "Sandbox", "PC", 2011, 250, "Completed", cal.getTime(), 5));

        cal.set(2023, Calendar.DECEMBER, 5);
        addGameForUser("U002", new Game("God of War", "Action", "PlayStation", 2018, 45, "Completed", cal.getTime(), 5));

        cal.set(2024, Calendar.FEBRUARY, 20);
        addGameForUser("U002", new Game("The Last of Us", "Survival", "PlayStation", 2013, 25, "Completed", cal.getTime(), 5));

        addGameForUser("U003", new Game("Red Dead Redemption 2", "Action-Adventure", "PlayStation", 2018, 80, "In Progress", null, 5));

        cal.set(2024, Calendar.APRIL, 15);
        addGameForUser("U004", new Game("Resident Evil 4", "Horror", "PC", 2023, 35, "Completed", cal.getTime(), 4));
    }

        private void refreshUserTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        // Get credentials from DataManager
        Map<String, String[]> credentials = dataManager.getUserCredentials();

        for (Map.Entry<String, String[]> entry : credentials.entrySet()) {
            String userId = entry.getKey();
            String[] creds = entry.getValue();

            model.addRow(new Object[]{
                userId,
                creds[0], // username
                creds[1]  // password
            });
        }
    }

    
    private void refreshGameTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0); // Clear existing rows

        // Use dataManager.getUserGames() instead of userGames
        Map<String, List<Game>> allUserGames = dataManager.getUserGames();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Map.Entry<String, List<Game>> entry : allUserGames.entrySet()) {
            String userId = entry.getKey();

            for (Game g : entry.getValue()) {
                String completionDateStr = (g.getCompletionDate() != null) ? 
                    dateFormat.format(g.getCompletionDate()) : "N/A";

                model.addRow(new Object[]{
                        userId,
                        g.getTitle(),
                        g.getGenre(),
                        g.getPlatform(),
                        g.getReleaseYear(),
                        g.getHoursPlayed(),
                        g.getCompletionStatus(),
                        completionDateStr,
                        g.getRating()
                });
            }
        }
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
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        Container = new javax.swing.JPanel();
        SubContainer1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        SubContainer2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        SubContainer3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(221, 222, 221));

        jButton1.setBackground(new java.awt.Color(0, 40, 66));
        jButton1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(116, 227, 255));
        jButton1.setText("View Users");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 40, 66));
        jButton2.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(116, 227, 255));
        jButton2.setText("View Game Lists");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 40, 66));
        jButton3.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(116, 227, 255));
        jButton3.setText("Update Lists");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        jPanel2.setBackground(new java.awt.Color(69, 69, 69));

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\rdsup\\Downloads\\image-2025-12-25-011908855.png")); // NOI18N
        jLabel1.setText("jLabel1");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("GameVault");

        jButton4.setBackground(new java.awt.Color(26, 26, 26));
        jButton4.setFont(new java.awt.Font("Yu Gothic Medium", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Logout");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        Container.setLayout(new java.awt.CardLayout());

        SubContainer1.setBackground(new java.awt.Color(149, 149, 149));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "User_ID", "Username", "User_Password"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 0, 36)); // NOI18N
        jLabel3.setText("Users");

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel4.setText("User_ID :");

        jTextField1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jButton8.setBackground(new java.awt.Color(0, 153, 255));
        jButton8.setText("View User");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton5.setText("Search");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SubContainer1Layout = new javax.swing.GroupLayout(SubContainer1);
        SubContainer1.setLayout(SubContainer1Layout);
        SubContainer1Layout.setHorizontalGroup(
            SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubContainer1Layout.createSequentialGroup()
                .addGroup(SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SubContainer1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                            .addGroup(SubContainer1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(SubContainer1Layout.createSequentialGroup()
                        .addGap(207, 207, 207)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(SubContainer1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SubContainer1Layout.setVerticalGroup(
            SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SubContainer1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addGroup(SubContainer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        Container.add(SubContainer1, "card2");

        jPanel3.setBackground(new java.awt.Color(149, 149, 149));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Title", "Genre", "Platform", "Release Year", "Hours Played", "Rating"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 0, 36)); // NOI18N
        jLabel5.setText("Games");

        jButton10.setText("Search");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout SubContainer2Layout = new javax.swing.GroupLayout(SubContainer2);
        SubContainer2.setLayout(SubContainer2Layout);
        SubContainer2Layout.setHorizontalGroup(
            SubContainer2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubContainer2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        SubContainer2Layout.setVerticalGroup(
            SubContainer2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubContainer2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Container.add(SubContainer2, "card3");

        SubContainer3.setBackground(new java.awt.Color(149, 149, 149));

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 0, 36)); // NOI18N
        jLabel6.setText("Admin Operations");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel7.setText("User ID :");

        jTextField2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel8.setText("Username:");

        jTextField3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        jButton6.setBackground(new java.awt.Color(116, 227, 255));
        jButton6.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jButton6.setText("Add User");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(151, 53, 53));
        jButton7.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jButton7.setText("Delete User");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel9.setText("Password:");

        jButton9.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        jButton9.setText("Update User");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SubContainer3Layout = new javax.swing.GroupLayout(SubContainer3);
        SubContainer3.setLayout(SubContainer3Layout);
        SubContainer3Layout.setHorizontalGroup(
            SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubContainer3Layout.createSequentialGroup()
                .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SubContainer3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(SubContainer3Layout.createSequentialGroup()
                                .addGap(224, 224, 224)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(19, 19, 19)
                        .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))
                    .addGroup(SubContainer3Layout.createSequentialGroup()
                        .addGap(280, 280, 280)
                        .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(227, Short.MAX_VALUE))
        );
        SubContainer3Layout.setVerticalGroup(
            SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubContainer3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114)
                .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(SubContainer3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jButton6)
                .addGap(28, 28, 28)
                .addComponent(jButton7)
                .addGap(18, 18, 18)
                .addComponent(jButton9)
                .addContainerGap(125, Short.MAX_VALUE))
        );

        Container.add(SubContainer3, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showCard(String cardName) {
    java.awt.CardLayout cl = (java.awt.CardLayout) Container.getLayout();
    cl.show(Container, cardName);
    
    if (cardName.equals("card2")) { // Users tab
        showAllUsers();
        jTextField4.setText(""); // Clear user search
    }
    
    if (cardName.equals("card3")) { // Game Lists tab
        refreshGameTable();
        jTextField5.setText(""); // Clear game search
    }
}

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        showCard("card4");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        showCard("card2");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        showCard("card3"); 
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new MainFrame().setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String userId = jTextField2.getText().trim();
        String username = jTextField3.getText().trim();
        String password = new String(jPasswordField1.getPassword()).trim();

        if (userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID, Username, and Password are required");
            return;
        }

        // Check duplicate User ID
        if (dataManager.getUserCredentials().containsKey(userId)) {
            JOptionPane.showMessageDialog(this, "User ID already exists");
            return;
        }

        // Add user through DataManager (you need to add this method to DataManager)
        dataManager.getUserCredentials().put(userId, new String[]{username, password});
        dataManager.getUserGames().put(userId, new java.util.ArrayList<>());

        JOptionPane.showMessageDialog(this, "User added successfully");

        jTextField2.setText("");
        jTextField3.setText("");
        jPasswordField1.setText("");

        refreshUserTable();
        refreshGameTable();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String userId = jTextField2.getText().trim();
        
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID only");
            return;
        }

        if (!dataManager.getUserCredentials().containsKey(userId)) {
            JOptionPane.showMessageDialog(this, "User ID not found");
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user " + userId + "?\nThis will also delete all their games.",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete user from DataManager
        dataManager.deleteUser(userId);

        JOptionPane.showMessageDialog(this, "User deleted successfully");

        // Clear fields
        jTextField2.setText("");
        jTextField3.setText("");
        jPasswordField1.setText("");

        // Refresh tables
        refreshUserTable();
        refreshGameTable();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
         String userId = jTextField1.getText().trim();

        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID");
            return;
        }

        // Get user info from DataManager
        Map<String, String[]> credentials = dataManager.getUserCredentials();
        String[] creds = credentials.get(userId);

        if (creds == null) {
            JOptionPane.showMessageDialog(this, "User not found");
            return;
        }

        String username = creds[0];
        String password = creds[1];

        StringBuilder gameInfo = new StringBuilder();

        // Get games from DataManager
        Map<String, List<Game>> allUserGames = dataManager.getUserGames();
        List<Game> games = allUserGames.get(userId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (games != null && !games.isEmpty()) {
            for (Game g : games) {
                String completionDateStr = (g.getCompletionDate() != null) ? 
                    dateFormat.format(g.getCompletionDate()) : "N/A";

                gameInfo.append("\nTitle: ").append(g.getTitle())
                        .append("\nGenre: ").append(g.getGenre())
                        .append("\nPlatform: ").append(g.getPlatform())
                        .append("\nRelease Year: ").append(g.getReleaseYear())
                        .append("\nHours Played: ").append(g.getHoursPlayed())
                        .append("\nRating: ").append(g.getRating())
                        .append("\nCompletion Status: ").append(g.getCompletionStatus())
                        .append("\nCompletion Date: ").append(completionDateStr)
                        .append("\n----------------------");
            }
        } else {
            gameInfo.append("\nNo games found.");
    }

    JOptionPane.showMessageDialog(this,
            "User Details\n\n" +
                    "User ID: " + userId +
                    "\nUsername: " + username +
                    "\nPassword: " + password +
                    "\n\nGames Owned:" + gameInfo
    );
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
         String userId = jTextField2.getText().trim();
        String username = jTextField3.getText().trim();
        String password = new String(jPasswordField1.getPassword()).trim();

        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID is required");
            return;
        }

        Map<String, String[]> credentials = dataManager.getUserCredentials();

        if (!credentials.containsKey(userId)) {
            JOptionPane.showMessageDialog(this, "User ID not found. Cannot update non-existent user.");
            return;
        }

        String[] currentCreds = credentials.get(userId);
        String currentUsername = currentCreds[0];
        String currentPassword = currentCreds[1];

        String newUsername = username.isEmpty() ? currentUsername : username;
        String newPassword = password.isEmpty() ? currentPassword : password;

        boolean usernameChanged = !newUsername.equals(currentUsername);
        boolean passwordChanged = !newPassword.equals(currentPassword);

        if (!usernameChanged && !passwordChanged) {
            JOptionPane.showMessageDialog(this, "No changes detected. Both username and password are the same as before.");
            return;
        }

        credentials.put(userId, new String[]{newUsername, newPassword});

        StringBuilder message = new StringBuilder("User updated successfully");
        if (usernameChanged && passwordChanged) {
            message.append(": Username and Password updated");
        } else if (usernameChanged) {
            message.append(": Username updated (Password unchanged)");
        } else if (passwordChanged) {
            message.append(": Password updated (Username unchanged)");
        }

        JOptionPane.showMessageDialog(this, message.toString());

        jTextField2.setText("");
        jTextField3.setText("");
        jPasswordField1.setText("");

        refreshUserTable();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        searchUserById();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        searchGamesByUserId();
    }//GEN-LAST:event_jButton10ActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Container;
    private javax.swing.JPanel SubContainer1;
    private javax.swing.JPanel SubContainer2;
    private javax.swing.JPanel SubContainer3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
