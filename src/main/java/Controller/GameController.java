package Controller;

import Model.Game;
import Model.GameCollection;
import View.UserPanel;
import java.util.*;

public class GameController {

    private GameCollection model;
    private UserPanel view; 

    public GameController(GameCollection model, UserPanel view) {
        this.model = model;
        this.view = view;
        
        addSampleGames();
        initEvents();
    }
    
    private void addSampleGames() {
        Calendar cal = Calendar.getInstance();

        cal.set(2023, Calendar.OCTOBER, 15);
        model.addGame(new Game("Minecraft", "Sandbox", "PC", 2011, 250, "Completed", cal.getTime(), 5));

        cal.set(2023, Calendar.DECEMBER, 5);
        model.addGame(new Game("God of War", "Action", "PlayStation", 2018, 45, "Completed", cal.getTime(), 5));

        cal.set(2024, Calendar.FEBRUARY, 20);
        model.addGame(new Game("The Last of Us", "Survival", "PlayStation", 2013, 25, "Completed", cal.getTime(), 5));

        model.addGame(new Game("Red Dead Redemption 2", "Action-Adventure", "PlayStation", 2018, 80, "In Progress", null, 5));

        cal.set(2024, Calendar.APRIL, 15);
        model.addGame(new Game("Resident Evil 4", "Horror", "PC", 2023, 35, "Completed", cal.getTime(), 4));

        view.refreshGamesTable(model);
    }

    private void initEvents() {

        view.setAddGameListener(e -> {
            try {
                Game game = view.getGameFromInput();

                boolean added = model.addGame(game);
                if (!added) {
                    view.showError("Game already exists");
                    return;
                }

                view.refreshGamesTable(model);
                view.showError("Game added successfully");

            } catch (IllegalArgumentException ex) {
                view.showError(ex.getMessage());
            }
        });


        view.setWishlistListener(e -> {
            try {
                String title = view.getWishlistTitle();
                view.refreshWishlist(title);
                view.showError("Added to wishlist successfully");
            } catch (IllegalArgumentException ex) {
                view.showError(ex.getMessage());
            }
        });

        view.setDeleteGameListener(e -> {
            try {
                String title = view.getTitleInput();
                model.removeGameByTitle(title);
                view.refreshGamesTable(model);
                view.showError("Game deleted");
            } catch (IllegalArgumentException ex) {
                view.showError(ex.getMessage());
            }
        });
        
        view.setUndoListener(e -> {
            view.showError("Undo feature coming soon");
        });
    }
}
