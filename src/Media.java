import java.util.Objects;

public class Media {
    private static int nextId = 1;
    private final int id;
    private String filePath;
    private String mediaType;
    private String description = "";

    public Media(String filePath, String mediaType, String description) {
        validatefilepath(filePath);
        validatemediatype(mediaType);
        this.id = nextIdAndIncrement();
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.description = description == null ? "" : description;
    }
    public Media(int id, String filePath, String mediaType, String description) {
        validateId(id);
        validatefilepath(filePath);
        validatemediatype(mediaType);
        this.id = id;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.description = description == null ? "" : description;
        synchronized (Media.class) {
            if (id >= nextId)  nextId = id + 1;
        }
    }

    private static synchronized int nextIdAndIncrement(){
        return nextId++;
    }

    public static synchronized int getNextId() {
        return nextId;
    }

    private void validatefilepath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath must not be null or empty");
        }
    }
    private void validatemediatype(String mediaType) {
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("mediaType must not be null or empty");
        }
    }

    private void validateId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be non-negative");
        }
    }
    
    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaType() {
        return mediaType;
    }

    // Setters (public to allow controlled mutation; validate where appropriate)
    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath must not be null or empty");
        }
        this.filePath = filePath;
    }

    public void setMediaType(String mediaType) {
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("mediaType must not be null or empty");
        }
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return id == media.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
