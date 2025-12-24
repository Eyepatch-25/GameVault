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
    private Stack<String> undo = new Stack<>();
}