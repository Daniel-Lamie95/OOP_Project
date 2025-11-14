import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Memory {
    private static int id = 0;
    private String name;
    private String description;
    Date date = new Date();
    Relative[] relatives; 
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

    public Relative[] getRelatives() {
        return relatives;
    }

    public void setRelatives(Relative relative) {
        this.relatives[Relative.getNum_of_relatives()] = relative;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> newMediaList) {
    this.mediaList = new ArrayList<>(); // create a new list
    if (newMediaList != null) {
        for (int i = 0; i < newMediaList.size(); i++) {
            Media m = newMediaList.get(i);
            System.out.println("Adding media at index " + i);
            // Create a new Media object using correct constructor
            this.mediaList.add(new Media(m.getId(), m.getFilePath(), m.getMediaType()));
        }
    }
    }

    @Override
    public String toString() {
        return "Memory{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }

}
