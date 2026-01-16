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
    private int releaseYear, rating, hoursPlayed;
    private Date completionDate;

    public Game ( String title, String genre, String platform, int releaseYear, int hoursPlayed, String completionStatus, Date completionDate, int rating ) {
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.releaseYear = releaseYear;
        this.hoursPlayed = hoursPlayed;
        this.completionStatus = completionStatus;
        this.completionDate = completionDate;
        this.rating = rating;
    }   

    //Getter methods
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPlatform() { return platform; }
    public int getReleaseYear() { return releaseYear; }
    public int getHoursPlayed() { return hoursPlayed; }
    public String getCompletionStatus() { return completionStatus; }
    public Date getCompletionDate() { return completionDate; }
    public int getRating() { return rating; }

    //Setter Methods
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setHoursPlayed (int hoursPlayed) { this.hoursPlayed = hoursPlayed; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    public void setRating(int rating) { this.rating = rating; }

    //Checking for duplicate game ignoring title
    public boolean same(Game game) {
        return this.title.equalsIgnoreCase(game.title) && this.platform.equalsIgnoreCase(game.platform);
    }
}