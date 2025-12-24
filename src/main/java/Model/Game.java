package Model;
import java.util.Date;

/** An abstract class information regarding the personal collection of games
 *
 * @author 24046585  Ilesh Maskey
 */
public class Game {
    /**Attributes for the games
     * private attributes can be accessed by the current class or through the getter and setter methods
     */
    private String title, genre, platform, completionStatus; //platform - PlayStation, PC, Xbox, completionStatus- Not Started, In Progress, Completed
    private int releaseYear, rating;
    private Date completionDate;

    public Game(String title, String genre, String platform, int releaseYear, int rating, String completionStatus, Date completionDate) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.releaseYear = releaseYear;
        this.completionStatus = completionStatus;
        this.rating = rating;
        this.completionDate = completionDate;
    }

    //Getter methods
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPlatform() { return platform; }
    public int getReleaseYear() { return releaseYear; }
    public int getRating() {return rating; }
    public String getCompletionStatus() { return completionStatus; }
    public Date getCompletionDate() { return completionDate; }

    //Setter Methods
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }

    //Checking for duplicate game ignoring title
    public boolean same(Game game) {
        return this.title.equalsIgnoreCase(game.title) && this.platform.equalsIgnoreCase(game.platform);
    }
}
