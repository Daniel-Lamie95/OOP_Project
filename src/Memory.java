import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Memory {
    private static int id = 0;
    private String name;
    private String description;
    Date date = new Date();
    Relatives[] relatives; 
    private List<Media> mediaList = new ArrayList<>();

    public Memory(String name, Date date) {
        this.name = name;
        this.date = date;
        id++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Relatives[] getRelatives() {
        return relatives;
    }

    public void setRelatives(Relatives[] relatives) {
        this.relatives = relatives;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }

    

    

    


}
