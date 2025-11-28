import java.util.Objects;

public class Media {
    private static int nextId = 1;
    private final int id;
    private String filePath;    // path to photo or video file
    private String mediaType;  // "image", "video"
    private String description = ""; // optional description

    // primary constructor with validation and normalization
    public Media(String filePath, String mediaType, String description) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath must not be null or empty");
        }
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("mediaType must not be null or empty");
        }
        this.id = nextId++;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.description = description == null ? "" : description;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public static int getNextId() {
        return nextId;
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
