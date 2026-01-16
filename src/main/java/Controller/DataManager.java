package Controller;

import Model.Game;
import java.util.*;

public class DataManager {
    private static DataManager instance;
    private Map<String, String[]> userCredentials; // userId -> [username, password]
    private Map<String, List<Game>> userGames; // userId -> list of games
    private int userCounter = 5; // Start from 5 since we have U001-U004
    
    private DataManager() {
        userCredentials = new LinkedHashMap<>();
        userGames = new LinkedHashMap<>();
        initializeSampleData();
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        // Sample users
        userCredentials.put("U001", new String[]{"john_doe", "password123"});
        userCredentials.put("U002", new String[]{"jane_smith", "pass456"});
        userCredentials.put("U003", new String[]{"bob_wilson", "secret789"});
        userCredentials.put("U004", new String[]{"alice_brown", "admin123"});
        
        // Sample games with completion status and date
        Calendar cal = Calendar.getInstance();
        
        // U001 games
        cal.set(2023, Calendar.OCTOBER, 15);
        List<Game> user1Games = new ArrayList<>();
        user1Games.add(new Game("Minecraft", "Sandbox", "PC", 2011, 250, "Completed", cal.getTime(), 5));
        
        cal.set(2024, Calendar.JANUARY, 10);
        user1Games.add(new Game("Terraria", "Sandbox", "PC", 2011, 120, "In Progress", cal.getTime(), 4));
        
        userGames.put("U001", user1Games);
        
        // U002 games
        cal.set(2023, Calendar.DECEMBER, 5);
        List<Game> user2Games = new ArrayList<>();
        user2Games.add(new Game("God of War", "Action", "PlayStation", 2018, 45, "Completed", cal.getTime(), 5));
        
        cal.set(2024, Calendar.FEBRUARY, 20);
        user2Games.add(new Game("The Last of Us", "Survival", "PlayStation", 2013, 25, "Completed", cal.getTime(), 5));
        
        userGames.put("U002", user2Games);
        
        // U003 games
        cal.set(2024, Calendar.MARCH, 1);
        List<Game> user3Games = new ArrayList<>();
        user3Games.add(new Game("Red Dead Redemption 2", "Action-Adventure", "PlayStation", 2018, 80, "Not Started", null, 5));
        
        userGames.put("U003", user3Games);
        
        // U004 games
        cal.set(2024, Calendar.APRIL, 15);
        List<Game> user4Games = new ArrayList<>();
        user4Games.add(new Game("Resident Evil 4", "Horror", "PC", 2023, 35, "Completed", cal.getTime(), 4));
        
        userGames.put("U004", user4Games);
    }
    
    public String registerUser(String username, String password) {
        String userId = "U" + String.format("%03d", userCounter++);
        userCredentials.put(userId, new String[]{username, password});
        userGames.put(userId, new ArrayList<>());
        return userId;
    }
    
    public void addGameForUser(String userId, Game game) {
        List<Game> games = userGames.get(userId);
        if (games != null) {
            games.add(game);
        }
    }
    
    public Map<String, String[]> getUserCredentials() {
        return userCredentials;
    }
    
    public Map<String, List<Game>> getUserGames() {
        return userGames;
    }
    
    public boolean deleteUser(String userId) {
        userCredentials.remove(userId);
        userGames.remove(userId);
        return true;
    }
    
    // Helper method to find user ID by username
    public String findUserIdByUsername(String username) {
        for (Map.Entry<String, String[]> entry : userCredentials.entrySet()) {
            if (entry.getValue()[0].equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    // Helper method to validate login
    public boolean validateLogin(String username, String password) {
        for (String[] creds : userCredentials.values()) {
            if (creds[0].equals(username) && creds[1].equals(password)) {
                return true;
            }
        }
        return false;
    }
}