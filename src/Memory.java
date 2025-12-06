import java.io.Serializable;
import java.util.*;

public class Memory extends PatientInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    private String description;
    private Date date = new Date();
    private List<Relative> relatives = new ArrayList<>();
    private List<Media> mediaList = new ArrayList<>();

    public Memory(UUID id, String name, String description, Date date, List<Relative> relatives, List<Media> mediaList) {
        super(id, name);
        this.description = description == null ? "" : description;
        setDate(date);
        this.relatives = relatives;
        this.mediaList = mediaList;
    }

    @Override
    public String getInfoSummary() {
        return getName() + " on " + getDate().toString();
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

    // store a defensive copy; accept null by defaulting to now and avoid throwing on future dates
    public void setDate(Date date) {
        // defensively default null to current time
        Date toSet = (date == null) ? new Date() : new Date(date.getTime());
        long now = System.currentTimeMillis();
        // If caller passed a future date, clamp to now instead of throwing to avoid crashes
        if (toSet.getTime() > now) {
            toSet = new Date();
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
            if (filePath.equals(mm.getMediaPath())) return true;
        }
        return false;
    }

    public void addMedia(Media media) {
        // ignore nulls and avoid duplicate or invalid media (by file path)
        if (media == null) return;
        String path = media.getMediaPath();
        if (path == null || path.isBlank()) return; // skip invalid media
        if (mediaExists(path)) return; // already added
        String type = media.getMediaType() == null ? "file" : media.getMediaType();
        // store a defensive copy
        this.mediaList.add(new Media(path, type, media.getDescription()));
    }

    public boolean removeMedia(Media media) {
        if (media == null || media.getMediaPath() == null) return false;
        String target = media.getMediaPath();
        for (int i = 0; i < mediaList.size(); i++) {
            Media m = mediaList.get(i);
            if (target.equals(m.getMediaPath())) {
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
                if (m != null && !mediaExists(m.getMediaPath())) {
                    this.mediaList.add(new Media(m.getMediaPath(), m.getMediaType(), m.getDescription()));
                }
            }
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Memory memory = (Memory) o;
        return Objects.equals(getId(), memory.getId());
    }

    // Ensure equals/hashCode contract: objects equal by 'id' must return same hash code.
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Memory{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", relatives=" + relatives +
                ", mediaList=" + mediaList +
                '}';
    }



}
