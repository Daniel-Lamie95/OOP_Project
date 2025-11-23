import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Memory {
    private static int nextId = 1;
    private int id;
    private String name;
    private String description;
    private Date date = new Date();
    private List<Relative> relatives = new ArrayList<>();
    private List<Media> mediaList = new ArrayList<>();

    public Memory(String name, Date date) {
        this.name = name;
        this.date = date;
        id = nextId++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void addRelative(Relative relative) {
        this.relatives.add(relative);
    }

    public void setRelatives(List<Relative> newRelatives) {
        this.relatives = new ArrayList<>();
        if (newRelatives != null) {
            for (Relative r : newRelatives) {
                this.relatives.add(r);
            }
        }
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void addMedia(Media media) {
        this.mediaList.add(media);
    }

    public void setMediaList(List<Media> newMediaList) {
        this.mediaList = new ArrayList<>();
        if (newMediaList != null) {
            for (Media m : newMediaList) {
                this.mediaList.add(new Media(m.getFilePath(), m.getMediaType()));
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
