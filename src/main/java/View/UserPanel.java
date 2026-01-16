/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Admin
 */

package View;

import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import Model.Game;
import Model.GameCollection;
import Controller.DataManager;



public class UserPanel extends javax.swing.JFrame {
    
    private GameCollection gameCollection;
    private Map<String, List<Game>> userGames;
    private String currentUserId;
    private DataManager dataManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private class GameStack {
    private static final int MAX_SIZE = 10;
    private UndoAction[] stack;
    private int top;
    
    public GameStack() {
        stack = new UndoAction[MAX_SIZE];
        top = -1;
    }
        public void push(UndoAction action) {
            if (top < MAX_SIZE - 1) {
                stack[++top] = action;
            } else {
                // Shift elements to make room (remove oldest)
                for (int i = 0; i < MAX_SIZE - 1; i++) {
                    stack[i] = stack[i + 1];
                }
                stack[MAX_SIZE - 1] = action;
            }
        }
        public UndoAction pop() {
            if (top >= 0) {
                return stack[top--];
            }
            return null;
        }
        public boolean isEmpty() {
            return top == -1;
        }

        public void clear() {
            top = -1;
        }
    }

    // UndoAction class to track action type
    private class UndoAction {
        public static final int TYPE_ADD = 1;
        public static final int TYPE_DELETE = 2;

        private Game game;
        private int actionType;

        public UndoAction(Game game, int actionType) {
            this.game = game;
            this.actionType = actionType;
        }

        public Game getGame() { return game; }
        public int getActionType() { return actionType; }
    }


    
    // Custom Queue for Recently Added functionality
    private class GameQueue {
        private static final int MAX_SIZE = 5;
        private Game[] queue;
        private int front, rear, size;
        
        public GameQueue() {
            queue = new Game[MAX_SIZE];
            front = 0;
            rear = -1;
            size = 0;
        }
        
        public void enqueue(Game game) {
            if (size == MAX_SIZE) {
                dequeue(); // Remove oldest if full
            }
            rear = (rear + 1) % MAX_SIZE;
            queue[rear] = game;
            size++;
        }
        
        public Game dequeue() {
            if (size == 0) {
                return null;
            }
            Game game = queue[front];
            front = (front + 1) % MAX_SIZE;
            size--;
            return game;
        }
        
        public boolean isEmpty() {
            return size == 0;
        }
        
        public Game[] getAllGames() {
            Game[] games = new Game[size];
            for (int i = 0; i < size; i++) {
                int index = (front + i) % MAX_SIZE;
                games[i] = queue[index];
            }
            return games;
        }
        
        public void removeGame(Game game) {
            // Find and remove the game if present
            for (int i = 0; i < size; i++) {
                int index = (front + i) % MAX_SIZE;
                if (queue[index] != null && queue[index].equals(game)) {
                    // Shift elements
                    for (int j = i; j < size - 1; j++) {
                        int currIndex = (front + j) % MAX_SIZE;
                        int nextIndex = (front + j + 1) % MAX_SIZE;
                        queue[currIndex] = queue[nextIndex];
                    }
                    rear = (rear - 1 + MAX_SIZE) % MAX_SIZE;
                    size--;
                    queue[rear + 1] = null;
                    break;
                }
            }
        }
        
        public void clear() {
            front = 0;
            rear = -1;
            size = 0;
        }
    }
    
    private GameStack undoStack;
    private GameQueue recentlyAddedQueue;
    
    // ==================== SORTING ALGORITHMS ====================


    private void selectionSortByTitle(List<Game> games) {
        int n = games.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (games.get(j).getTitle().compareToIgnoreCase(games.get(minIndex).getTitle()) < 0) {
                    minIndex = j;
                }
            }
            // Swap
            if (minIndex != i) {
                Game temp = games.get(i);
                games.set(i, games.get(minIndex));
                games.set(minIndex, temp);
            }
        }
    }


    private void insertionSortByReleaseYear(List<Game> games) {
        int n = games.size();
        for (int i = 1; i < n; i++) {
            Game key = games.get(i);
            int j = i - 1;

            // Move elements that are greater than key to one position ahead
            while (j >= 0 && games.get(j).getReleaseYear() > key.getReleaseYear()) {
                games.set(j + 1, games.get(j));
                j = j - 1;
            }
            games.set(j + 1, key);
        }
    }


    private void mergeSortByHoursPlayed(List<Game> games, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Sort first and second halves
            mergeSortByHoursPlayed(games, left, mid);
            mergeSortByHoursPlayed(games, mid + 1, right);

            // Merge the sorted halves
            merge(games, left, mid, right);
        }
    }

    private void merge(List<Game> games, int left, int mid, int right) {
        // Create temp arrays
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Game[] leftArr = new Game[n1];
        Game[] rightArr = new Game[n2];

        // Copy data to temp arrays
        for (int i = 0; i < n1; i++) {
            leftArr[i] = games.get(left + i);
        }
        for (int j = 0; j < n2; j++) {
            rightArr[j] = games.get(mid + 1 + j);
        }

        // Merge temp arrays
        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (leftArr[i].getHoursPlayed() <= rightArr[j].getHoursPlayed()) {
                games.set(k, leftArr[i]);
                i++;
            } else {
                games.set(k, rightArr[j]);
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i < n1) {
            games.set(k, leftArr[i]);
            i++;
            k++;
        }

        while (j < n2) {
            games.set(k, rightArr[j]);
            j++;
            k++;
        }
    }


    private List<Game> linearSearchByTitle(List<Game> games, String searchTerm) {
        List<Game> results = new ArrayList<>();
        searchTerm = searchTerm.toLowerCase().trim();

        for (Game game : games) {
            if (game.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(game);
            }
        }
        return results;
    }
    
    /**
     * Creates new form UserPanel
     */
    public void refreshGamesTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        if (currentUserId != null) {
            // Get games from DataManager, NOT from gameCollection
            List<Game> games = dataManager.getUserGames().get(currentUserId);

            if (games != null) {
                for (Game g : games) {
                    String completionDateStr = (g.getCompletionDate() != null) ? 
                        dateFormat.format(g.getCompletionDate()) : "N/A";

                    model.addRow(new Object[]{
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
    }



    public void refreshGamesTable(GameCollection collection) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        for (Game g : collection.getGames()) {
            String completionDateStr = (g.getCompletionDate() != null) ? 
                dateFormat.format(g.getCompletionDate()) : "N/A";
            
            model.addRow(new Object[]{
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
    
    
    public UserPanel(GameCollection gameCollection, Map<String, List<Game>> userGames) {
        dataManager = DataManager.getInstance();
        this.userGames = userGames;

        // IMPORTANT: Use the gameCollection passed from MainFrame, don't create a new one
        this.gameCollection = gameCollection;

        // Initialize undoStack and recentlyAddedQueue
        this.undoStack = new GameStack();
        this.recentlyAddedQueue = new GameQueue();

        // Initialize the UI components
        initComponents();

        // Initialize listeners
        initializeListeners();

        // Set window properties
        setTitle("GameVault - User Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Clear wishlist field specifically
    public void clearWishlistField() {
        jTextField1.setText("");
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    // Method to add game to favorites
    public void addToFavorites(String title) {
        if (currentUserId == null) {
            showError("No user logged in");
            return;
        }

        // Find the game in user's collection
        Game gameToAdd = null;
        List<Game> games = userGames.get(currentUserId);
        if (games != null) {
            for (Game game : games) {
                if (game.getTitle().equalsIgnoreCase(title)) {
                    gameToAdd = game;
                    break;
                }
            }
        }

        if (gameToAdd == null) {
            showError("Game not found in your collection: " + title);
            return;
        }

        // Add to favorites in GameCollection (you need this method in GameCollection)
        gameCollection.addToFavorites(gameToAdd);
        refreshFavoriteGamesTable();
        showError("Added to favorites: " + title);
    }

    // Refresh favorite games table
    private void refreshFavoriteGamesTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        for (Game g : gameCollection.getFavoriteGames()) {
            String completionDateStr = (g.getCompletionDate() != null) ? 
                dateFormat.format(g.getCompletionDate()) : "N/A";

            model.addRow(new Object[]{
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

    // Method to delete game from everywhere
    public boolean deleteGame(String title) {
        if (currentUserId == null) {
            showError("No user logged in");
            return false;
        }

        // Get all lists that need to be checked
        List<Game> userGameList = dataManager.getUserGames().get(currentUserId);
        List<Game> favoritesList = gameCollection.getFavoriteGames();
        Game[] recentlyAddedGames = recentlyAddedQueue.getAllGames();
        DefaultTableModel wishlistModel = (DefaultTableModel) jTable4.getModel();

        Game gameToDelete = null;

        if (userGameList != null) {
            for (Game game : userGameList) {
                if (game.getTitle().equalsIgnoreCase(title)) {
                    gameToDelete = game;
                    break;
                }
            }
        }

        if (gameToDelete == null) {
            showError("Game not found: " + title);
            return false;
        }

        if (userGameList != null) {
            userGameList.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        }

        List<Game> localUserGames = userGames.get(currentUserId);
        if (localUserGames != null) {
            localUserGames.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        }

        gameCollection.removeGameByTitle(title);

        gameCollection.removeFromFavorites(gameToDelete);

        recentlyAddedQueue.removeGame(gameToDelete);

        for (int i = 0; i < wishlistModel.getRowCount(); i++) {
            Object cellValue = wishlistModel.getValueAt(i, 0);
            if (cellValue != null && cellValue.toString().equalsIgnoreCase(title)) {
                // Clear this row in the wishlist
                wishlistModel.setValueAt("", i, 0);
                wishlistModel.setValueAt("", i, 1);
                wishlistModel.setValueAt("", i, 2);
                wishlistModel.setValueAt("", i, 3);
            }
        }

        // 8. Push DELETE action to undo stack
        undoStack.push(new UndoAction(gameToDelete, UndoAction.TYPE_DELETE));

        // 9. Refresh ALL tables
        refreshGamesTable();
        refreshFavoriteGamesTable();
        refreshRecentlyAddedTable();

        return true;
    }

    // Method to undo last action
    public void undoLastAction() {
        if (undoStack.isEmpty()) {
            showError("Nothing to undo");
            return;
        }

        UndoAction lastAction = undoStack.pop();

        if (lastAction == null || lastAction.getGame() == null) {
            showError("Nothing to undo");
            return;
        }

        Game game = lastAction.getGame();

        try {
            if (lastAction.getActionType() == UndoAction.TYPE_ADD) {
                // Undo addition = delete the game
                boolean deleted = deleteGameWithoutUndo(game.getTitle());

                if (deleted) {
                    showError("Undo: Removed " + game.getTitle());
                } else {
                    showError("Game not found for removal: " + game.getTitle());
                }

            } else if (lastAction.getActionType() == UndoAction.TYPE_DELETE) {
                // Undo deletion = add the game back
                // Use the EXACT game object from undo stack, don't create a new one
                boolean added = addGameForCurrentUserWithoutUndo(game);

                if (added) {
                    showError("Undo: Restored " + game.getTitle());
                } else {
                    showError("Failed to restore: " + game.getTitle());
                }
            }

            clearInputFields();

        } catch (Exception e) {
            showError("Error during undo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to delete without pushing to undo stack
    private boolean deleteGameWithoutUndo(String title) {
        if (currentUserId == null) return false;

        List<Game> userGameList = dataManager.getUserGames().get(currentUserId);
        boolean found = false;

        if (userGameList != null) {
            found = userGameList.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        }

        if (found) {
            // Also remove from local structures
            List<Game> localUserGames = userGames.get(currentUserId);
            if (localUserGames != null) {
                localUserGames.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
            }

            gameCollection.removeGameByTitle(title);
            refreshGamesTable();
            return true;
        }

        return false;
    }

// Helper method to add without pushing to undo stack
private boolean addGameForCurrentUserWithoutUndo(Game game) {
    if (currentUserId == null) return false;
    
    // Check if already exists
    List<Game> userGameList = dataManager.getUserGames().get(currentUserId);
    if (userGameList != null) {
        for (Game existing : userGameList) {
            if (existing.same(game)) {
                return false; // Already exists
            }
        }
    }
    
    // Add to DataManager
    dataManager.addGameForUser(currentUserId, game);
    
    // Add to local collection
    gameCollection.addGame(game);
    
    // Refresh UI
    refreshGamesTable();
    return true;
}
    
    public boolean addGameForCurrentUser(Game game) {
        if (currentUserId == null) {
            showError("No user logged in");
            return false;
        }

        // Check if game already exists in user's games (using DataManager's data)
        List<Game> userGameList = dataManager.getUserGames().get(currentUserId);
        if (userGameList != null) {
            for (Game existingGame : userGameList) {
                if (existingGame.same(game)) { // Using the Game.same() method
                    showError("Game already exists: " + game.getTitle() + " on " + game.getPlatform());
                    return false;
                }
            }
        }

        // 1. Add to DataManager FIRST
        dataManager.addGameForUser(currentUserId, game);

        // 2. Get the updated list from DataManager
        List<Game> updatedUserGames = dataManager.getUserGames().get(currentUserId);

        // 3. Find the actual game that was added (to ensure we have the right reference)
        Game addedGame = null;
        if (updatedUserGames != null) {
            for (Game g : updatedUserGames) {
                if (g.same(game)) {
                    addedGame = g;
                    break;
                }
            }
        }

        if (addedGame == null) {
            showError("Failed to add game");
            return false;
        }

        // 4. Add to local gameCollection (checks for duplicates)
        boolean addedToCollection = gameCollection.addGame(addedGame);

        // 5. Only push to undo stack if successfully added to collection
        if (addedToCollection) {
            undoStack.push(new UndoAction(addedGame, UndoAction.TYPE_ADD));

            // Add to recently added queue
            recentlyAddedQueue.enqueue(addedGame);
            refreshRecentlyAddedTable();

            // Refresh the table from DataManager (not from gameCollection)
            refreshGamesTable();

            return true;
        } else {
            showError("Game already exists in collection");
            return false;
        }
    }

    
    // Refresh the recently added table
    private void refreshRecentlyAddedTable() {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);

        Game[] recentGames = recentlyAddedQueue.getAllGames();
        for (int i = recentGames.length - 1; i >= 0; i--) {
            Game g = recentGames[i];
            if (g != null) {
                String completionDateStr = (g.getCompletionDate() != null) ? 
                    dateFormat.format(g.getCompletionDate()) : "N/A";

                model.addRow(new Object[]{
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

    public void refreshWishlist(String title) {
        DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
        // Clear first column only (Title 1)
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0) == null || model.getValueAt(i, 0).toString().isEmpty()) {
                model.setValueAt(title, i, 0);
                return;
            }
        }
        // If all rows are full, add a new row
        model.addRow(new Object[]{title, "", "", ""});
    }

    public String getWishlistTitle() {
    String title = jTextField1.getText().trim();

    if (title.isEmpty()) {
        throw new IllegalArgumentException("Game title cannot be empty for wishlist");
    }

    return title;
}

    
    private void clearInputFields() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        if (jComboBox1 != null) {
            jComboBox1.setSelectedIndex(0);
        }
    }


    public void setAddGameListener(ActionListener l) {
        jButton1.addActionListener(l);
    }

    public void setWishlistListener(ActionListener l) {
        jButton2.addActionListener(l);
    }

    public void setUndoListener(ActionListener l) {
        jButton3.addActionListener(l);
    }

    public void setDeleteGameListener(ActionListener l) {
        jButton4.addActionListener(l);
    }
    
    public void setCurrentUser(String userId) {
        this.currentUserId = userId;

        // Clear undo stack and recently added queue
        undoStack.clear();
        recentlyAddedQueue.clear();

        // DO NOT reload gameCollection - it's already loaded in MainFrame
        // Just update the recently added queue with user's games
        if (userGames.containsKey(userId)) {
            List<Game> games = userGames.get(userId);
            int start = Math.max(0, games.size() - 5);
            for (int i = start; i < games.size(); i++) {
                recentlyAddedQueue.enqueue(games.get(i));
            }
        }

        refreshGamesTable();
        refreshRecentlyAddedTable();
        refreshFavoriteGamesTable();
    }
    
    private void addSampleGamesForUser(String userId) {
    // Only add if the user doesn't have any games yet
        if (userGames.containsKey(userId) && userGames.get(userId).isEmpty()) {
            List<Game> userGameList = userGames.get(userId);
            Calendar cal = Calendar.getInstance();

            // Add different sample games based on user ID
            if (userId.equals("U001")) {
                cal.set(2023, Calendar.OCTOBER, 15);
                userGameList.add(new Game("Minecraft", "Sandbox", "PC", 2011, 250, "Completed", cal.getTime(), 5));

                cal.set(2024, Calendar.JANUARY, 10);
                userGameList.add(new Game("Terraria", "Sandbox", "PC", 2011, 120, "In Progress", cal.getTime(), 4));
            } else if (userId.equals("U002")) {
                cal.set(2023, Calendar.DECEMBER, 5);
                userGameList.add(new Game("God of War", "Action", "PlayStation", 2018, 45, "Completed", cal.getTime(), 5));

                cal.set(2024, Calendar.FEBRUARY, 20);
                userGameList.add(new Game("The Last of Us", "Survival", "PlayStation", 2013, 25, "Completed", cal.getTime(), 5));
            } else if (userId.equals("U003")) {
                userGameList.add(new Game("Red Dead Redemption 2", "Action-Adventure", "PlayStation", 2018, 80, "Not Started", null, 5));
            } else if (userId.equals("U004")) {
                cal.set(2024, Calendar.APRIL, 15);
                userGameList.add(new Game("Resident Evil 4", "Horror", "PC", 2023, 35, "Completed", cal.getTime(), 4));
            } else {
                // Default sample game for new users
                userGameList.add(new Game("Portal 2", "Puzzle", "PC", 2011, 15, "Not Started", null, 5));
            }

            // Also add to gameCollection
            for (Game game : userGameList) {
                gameCollection.addGame(game);
            }
        }
    }   


    
    public Game getGameFromInput() {
        try {
            String title = jTextField1.getText().trim();
            if (title.isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            int year = Integer.parseInt(jTextField4.getText().trim());
            if (year < 1970 || year > 2025) {
                throw new IllegalArgumentException("Release year must be between 1970 and 2025");
            }

            int hours = Integer.parseInt(jTextField5.getText().trim());
            if (hours < 0) {
                throw new IllegalArgumentException("Hours played cannot be negative");
            }

            int rating = Integer.parseInt(jComboBox1.getSelectedItem().toString());
            
            // For now, default to "Not Started" and null date
            // You can add UI components for these later
            String completionStatus = "Not Started";
            Date completionDate = null;

            return new Game(
                title,
                jTextField2.getText().trim(),
                jTextField3.getText().trim(),
                year,
                hours,
                completionStatus,
                completionDate,
                rating
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year and Hours must be numbers");
        }
    }



    public void showError(String msg) {
    JOptionPane.showMessageDialog(this, msg);
}

public String getTitleInput() {
    return jTextField1.getText().trim();
}

public String getPlatformInput() {
    return jTextField3.getText().trim();
}

public void initializeListeners() {
        // Add to Favorites button
        jButton6.addActionListener(e -> {
            String title = jTextField1.getText().trim();
            if (title.isEmpty()) {
                showError("Please enter a game title to add to favorites");
                return;
            }
            addToFavorites(title);
        });

        // Undo button
        jButton3.addActionListener(e -> {
            undoLastAction();
        });

        // Delete from Games button
        jButton4.addActionListener(e -> {
            // Get the title from the first text field
            String title = jTextField1.getText().trim();

            if (title.isEmpty()) {
                showError("Please enter a game title to delete");
                return;
            }

            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete '" + title + "'?\nThis will remove it from all lists.",
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = deleteGame(title);
                if (deleted) {
                    showError("Game deleted successfully: " + title);
                    clearInputFields();
                }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField7 = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(69, 69, 69));

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\rdsup\\Downloads\\image-2025-12-25-011908855.png")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("GameVault");

        jButton5.setBackground(new java.awt.Color(26, 26, 26));
        jButton5.setFont(new java.awt.Font("Yu Gothic Medium", 0, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Logout");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(305, 305, 305)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(51, 170, 216));

        jPanel3.setBackground(new java.awt.Color(51, 170, 216));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel3.setText("Title :");

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel4.setText("Genre: ");

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel5.setText("Platform: ");

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel6.setText("Release Year :");

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel7.setText("Hours Played :");

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel8.setText("Rating :");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title", "Genre", "Platform", "Release Year", "Hours Played", "Completion Status", "Completion Date", "Rating"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton1.setText("Add to Games");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton2.setText("Add to Wish List");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton3.setText("Undo");

        jButton4.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton4.setText("Delete from Games");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5" }));

        jButton6.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton6.setText("Add to Favorites");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton7.setText("Sort By Title");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton8.setText("Sort By Release Year");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jButton9.setText("Sort By Hours Played");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
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
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4)
                            .addComponent(jTextField5)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jButton8)
                            .addComponent(jButton9))))
                .addGap(42, 42, 42))
        );

        jTabbedPane1.addTab("Games", jPanel3);

        jPanel4.setBackground(new java.awt.Color(51, 170, 216));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title", "Genre", "Platform", "Release Year", "Hours Played", "Completion Status", "Completion Date", "Rating"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jButton11.setText("Search");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Sort By Title");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Sort By Release Year");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Sort By Hours Played");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(246, 246, 246)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton12)
                                .addGap(240, 240, 240)
                                .addComponent(jButton13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addGap(45, 45, 45))
        );

        jTabbedPane1.addTab("Favorite Games", jPanel4);

        jPanel5.setBackground(new java.awt.Color(51, 170, 216));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title", "Genre", "Platform", "Release Year", "Hours Played", "Completion Status", "Completion Date", "Rating"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jTabbedPane1.addTab("Recently Added", jPanel5);

        jPanel6.setBackground(new java.awt.Color(51, 170, 216));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title", "Genre", "Platform", "Release Year"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("WishList", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new MainFrame().setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
           try {
           Game game = getGameFromInput();

           // Add game for current user
           boolean added = addGameForCurrentUser(game);

           if (added) {
               clearInputFields();
               JOptionPane.showMessageDialog(this, "Game added successfully!");

               // Refresh all tables
               refreshGamesTable();
               refreshRecentlyAddedTable();
           }
       } catch (IllegalArgumentException e) {
           showError(e.getMessage());
       }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
            try {
            String title = getWishlistTitle();
            refreshWishlist(title);
            JOptionPane.showMessageDialog(this, "Added to wishlist: " + title);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        // Sort By Title (Selection Sort)
        if (currentUserId != null && userGames.containsKey(currentUserId)) {
        List<Game> userGameList = new ArrayList<>(userGames.get(currentUserId));
        selectionSortByTitle(userGameList);
        userGames.put(currentUserId, userGameList);
        refreshGamesTable();
        showError("Games sorted by Title (Selection Sort)");
    }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        // Sort By Release Year (Insertion Sort)
        if (currentUserId != null && userGames.containsKey(currentUserId)) {
        List<Game> userGameList = new ArrayList<>(userGames.get(currentUserId));
        insertionSortByReleaseYear(userGameList);
        userGames.put(currentUserId, userGameList);
        refreshGamesTable();
        showError("Games sorted by Release Year (Insertion Sort)");
    }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
         // Sort By Hours Played (Merge Sort)
        if (currentUserId != null && userGames.containsKey(currentUserId)) {
            List<Game> userGameList = new ArrayList<>(userGames.get(currentUserId));
            if (!userGameList.isEmpty()) {
                mergeSortByHoursPlayed(userGameList, 0, userGameList.size() - 1);
                userGames.put(currentUserId, userGameList);
                refreshGamesTable();
                showError("Games sorted by Hours Played (Merge Sort)");
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        // Search Games
        String searchTerm = jTextField6.getText().trim();
    
        if (searchTerm.isEmpty()) {
            // If search is empty, show all games
            refreshGamesTable();
            showError("Showing all games");
            return;
        }

        if (currentUserId != null && userGames.containsKey(currentUserId)) {
            List<Game> searchResults = linearSearchByTitle(
                userGames.get(currentUserId), searchTerm);

            if (searchResults.isEmpty()) {
                showError("No games found matching: " + searchTerm);
                // Keep showing all games if no results found
                refreshGamesTable();
            } else {
                // Create a temporary table model for search results
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);

                for (Game g : searchResults) {
                    String completionDateStr = (g.getCompletionDate() != null) ? 
                        dateFormat.format(g.getCompletionDate()) : "N/A";

                    model.addRow(new Object[]{
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
                showError("Found " + searchResults.size() + " game(s) matching: " + searchTerm);
            }
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        // Search Favorite Games
        String searchTerm = jTextField7.getText().trim();
    
        if (searchTerm.isEmpty()) {
            // If search is empty, show all favorite games
            refreshFavoriteGamesTable();
            showError("Showing all favorite games");
            return;
        }

        List<Game> searchResults = linearSearchByTitle(
            gameCollection.getFavoriteGames(), searchTerm);

        if (searchResults.isEmpty()) {
            showError("No favorite games found matching: " + searchTerm);
            // Show all favorite games if no results
            refreshFavoriteGamesTable();
        } else {
            // Create a temporary table model for search results
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            for (Game g : searchResults) {
                String completionDateStr = (g.getCompletionDate() != null) ? 
                    dateFormat.format(g.getCompletionDate()) : "N/A";

                model.addRow(new Object[]{
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
            showError("Found " + searchResults.size() + " favorite game(s) matching: " + searchTerm);
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        // Sort Favorite Games By Title (Selection Sort)
        List<Game> favoriteGamesList = new ArrayList<>(gameCollection.getFavoriteGames());
        if (!favoriteGamesList.isEmpty()) {
            selectionSortByTitle(favoriteGamesList);
            // Clear and re-add sorted games
            gameCollection.getFavoriteGames().clear();
            gameCollection.getFavoriteGames().addAll(favoriteGamesList);
            refreshFavoriteGamesTable();
            showError("Favorite Games sorted by Title (Selection Sort)");
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        // Sort Favorite Games By Release Year (Insertion Sort)
        List<Game> favoriteGamesList = new ArrayList<>(gameCollection.getFavoriteGames());
        if (!favoriteGamesList.isEmpty()) {
            insertionSortByReleaseYear(favoriteGamesList);
            // Clear and re-add sorted games
            gameCollection.getFavoriteGames().clear();
            gameCollection.getFavoriteGames().addAll(favoriteGamesList);
            refreshFavoriteGamesTable();
            showError("Favorite Games sorted by Release Year (Insertion Sort)");
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        // Sort Favorite Games By Hours Played (Merge Sort)
        List<Game> favoriteGamesList = new ArrayList<>(gameCollection.getFavoriteGames());
        if (!favoriteGamesList.isEmpty()) {
            mergeSortByHoursPlayed(favoriteGamesList, 0, favoriteGamesList.size() - 1);
            // Clear and re-add sorted games
            gameCollection.getFavoriteGames().clear();
            gameCollection.getFavoriteGames().addAll(favoriteGamesList);
            refreshFavoriteGamesTable();
            showError("Favorite Games sorted by Hours Played (Merge Sort)");
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
