package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GameCollection {

    private ArrayList<Game> games = new ArrayList<>();
    private LinkedList<Game> favoriteGames = new LinkedList<>();
    private Queue<Game> recentlyAdded = new LinkedList<>();
    private LinkedList<String> wishlist = new LinkedList<>();
    private Stack<Game> undo = new Stack<>();
    
    public boolean addGame(Game game) {
        
        for (Game g : games) {
            if (g.getTitle().equalsIgnoreCase(game.getTitle()) &&
                    g.getPlatform().equalsIgnoreCase(game.getPlatform())) {
                return false;
            }
        }

        games.add(game);
        
        if (recentlyAdded.size() == 5) {
            recentlyAdded.poll();
        }
        recentlyAdded.add(game);

        return true;
    }
    
    public void addToWishlist(String title) {
        wishlist.add(title);
    }


    public void pushUndo(Game game) {
        undo.push(game);
    }

    public Game popUndo() {
        if (undo.isEmpty()) return null;
        return undo.pop();
    }


    public ArrayList<Game> getGames() {
        return games;
    }

    public Queue<Game> getRecentlyAdded() {
        return recentlyAdded;
    }

    public LinkedList<String> getWishlist() {
        return wishlist;
    }

    public LinkedList<Game> getFavoriteGames() {
        return favoriteGames;
    }
    
    public boolean removeGame(String title, String platform) {
        return games.removeIf(g ->
            g.getTitle().equalsIgnoreCase(title) &&
            g.getPlatform().equalsIgnoreCase(platform)
        );
    }
    
    public boolean removeGameByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }

        Game toRemove = null;
        for (Game g : games) {
            if (g.getTitle().equalsIgnoreCase(title.trim())) {
                toRemove = g;
                break;
            }
        }

        if (toRemove != null) {
            games.remove(toRemove);
            return true;
        }
        return false;
    }
    
    // Add these missing methods to GameCollection.java
    public void addToFavorites(Game game) {
        if (!favoriteGames.contains(game)) {
            favoriteGames.add(game);
        }
    }

    public boolean removeFromFavorites(Game game) {
        return favoriteGames.remove(game);
    }
    
    public boolean containsGame(Game game) {
        for (Game g : games) {
            if (g.getTitle().equalsIgnoreCase(game.getTitle()) &&
                g.getPlatform().equalsIgnoreCase(game.getPlatform())) {
                return true;
            }
        }
        return false;
    }
    
    
}