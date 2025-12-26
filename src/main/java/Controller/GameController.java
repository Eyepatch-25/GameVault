package Controller;

import Model.Game;
import Model.GameCollection;
import View.UserPanel;

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
    model.addGame(new Game("Minecraft", "Sandbox", "PC", 2011, 250, 5));
    model.addGame(new Game("God of War", "Action", "PlayStation", 2018, 45, 5));
    model.addGame(new Game("The Last of Us", "Survival", "PlayStation", 2013, 25, 5));
    model.addGame(new Game("Red Dead Redemption 2", "Action-Adventure", "PlayStation", 2018, 80, 5));
    model.addGame(new Game("Resident Evil 4", "Horror", "PC", 2023, 35, 4));

    view.refreshGamesTable(model);
    }

    private void initEvents() {

        view.setAddGameListener(e -> {
            try {
                Game game = view.getGameFromInput();
                model.addGame(game);
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
