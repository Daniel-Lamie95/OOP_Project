import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;


public class Media implements Serializable {
    private static final long serialVersionUID = 1L;
     private UUID id;
     private String mediaPath;
     private String mediaType;
     private String description = "";

     public Media(String filePath, String mediaType, String description) {

         validatefilepath(filePath);
         validatemediatype(mediaType);
         this.id = UUID.randomUUID();
         this.mediaPath = filePath;
         this.mediaType = mediaType;
         this.description = description == null ? "" : description;
     }

     public UUID getId() {
         return id;
     }
     public void setId(UUID id) {
         this.id = id;
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

     public String getMediaPath() {
         return mediaPath;
     }

     public String getDescription() {
         return description;
     }

     public String getMediaType() {
         return mediaType;
     }


     public void setDescription(String description) {
         this.description = description;
     }

     public void setFilePath(String mediaPath) {
         if (mediaPath == null || mediaPath.trim().isEmpty()) {
             throw new IllegalArgumentException("filePath must not be null or empty");
         }
         this.mediaPath = mediaPath;    }

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
                 ", mediaPath='" + mediaPath + '\'' +
                 ", mediaType='" + mediaType + '\'' +
                 ", description='" + description + '\'' +
                 '}';
     }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Media media = (Media) o;
         return Objects.equals(id, media.id);
     }

     @Override
     public int hashCode() {
         return Objects.hash(id);
     }
}
