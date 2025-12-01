import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Memory {
    private static int nextId = 1;
    private int id;
    private String name;
    private String description;
    private Date date = new Date();

    private final List<Relative> relatives = new ArrayList<>();

    private final List<Media> mediaList = new ArrayList<>();

    public Memory(String name, Date date, String description) {
        if (name == null) {
            throw new IllegalArgumentException("Memory name must not be null.");
        }
        this.name = name;
        this.description = description;
        setDate(date);
        id = nextIdAndIncrement();
    }


    public static synchronized int nextIdAndIncrement() {
        return nextId++;
    }
    public int getId() {
        return id;
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

    // return a defensive copy to prevent callers from mutating internal Date
    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    // store a defensive copy; require a non-null past date (memories represent past events)
    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Memory date must not be null and must be in the past.");
        }
        Date toSet = new Date(date.getTime());
        long now = System.currentTimeMillis();
        if (toSet.getTime() >= now) {
            throw new IllegalArgumentException("Memory date must be in the past (no future dates allowed).");
        }
        this.date = toSet;
    }

    public List<Relative> getRelatives() {
        // return a defensive copy so callers cannot modify the internal list directly
        return new ArrayList<>(relatives);
    }

    public void addRelative(Relative relative) {
        if (relative != null && !this.relatives.contains(relative)) {
            this.relatives.add(relative);
        }
    }

    public boolean removeRelative(Relative relative) {
        if (relative == null) return false;
        return this.relatives.remove(relative);
    }

    public void clearRelatives() {
        this.relatives.clear();
    }

    public void setRelatives(List<Relative> newRelatives) {
        this.relatives.clear();
        if (newRelatives != null) {
            for (Relative r : newRelatives) {
                if (r != null && !this.relatives.contains(r)) {
                    this.relatives.add(r);
                }
            }
        }
    }

    public List<Media> getMediaList() {
        // return defensive copy to prevent external mutation of internal list
        return new ArrayList<>(mediaList);
    }

    // helper to detect media duplicates by file path
    private boolean mediaExists(String filePath) {
        if (filePath == null) return false;
        for (Media mm : mediaList) {
            if (filePath.equals(mm.getFilePath())) return true;
        }
        return false;
    }

    public void addMedia(Media media) {
        // ignore nulls and avoid duplicate media (by file path)
        if (media != null && !mediaExists(media.getFilePath())) {
            // store a defensive copy (consistent with setMediaList which creates new Media instances)
            this.mediaList.add(new Media(media.getId(),media.getFilePath(), media.getMediaType(), media.getDescription()));
        }
    }

    public boolean removeMedia(Media media) {
        if (media == null || media.getFilePath() == null) return false;
        String target = media.getFilePath();
        for (int i = 0; i < mediaList.size(); i++) {
            Media m = mediaList.get(i);
            if (target.equals(m.getFilePath())) {
                mediaList.remove(i);
                return true;
            }
        }
        return false;
    }

    public void clearMedia() {
        this.mediaList.clear();
    }

    public void setMediaList(List<Media> newMediaList) {
        this.mediaList.clear();
        if (newMediaList != null) {
            for (Media m : newMediaList) {
                if (m != null && !mediaExists(m.getFilePath())) {
                    this.mediaList.add(new Media(m.getId(),m.getFilePath(), m.getMediaType(), m.getDescription()));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Memory memory = (Memory) o;
        return id == memory.id;
    }

    // Ensure equals/hashCode contract: objects equal by 'id' must return same hash code.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Memory{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + (getDate()) +
                '}';
    }


}
