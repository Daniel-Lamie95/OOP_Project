import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;

public class Main extends Application {
    private List<Relative> relatives = new ArrayList<>();
    private List<Memory> memories = new ArrayList<>();
    private ListView<String> memoriesListView;
    private ListView<String> relativesListView;
    private TextArea detailsArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Patient Memory System");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-size: 12;");

        // Top Menu Bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Left Panel - Relatives and Memories
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // Center Panel - Details and Add Forms
        VBox centerPanel = createCenterPanel();
        root.setCenter(centerPanel);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu editMenu = new Menu("Edit");
        MenuItem addRelativeItem = new MenuItem("Add Relative");
        addRelativeItem.setOnAction(e -> showAddRelativeDialog());
        MenuItem addMemoryItem = new MenuItem("Add Memory");
        addMemoryItem.setOnAction(e -> showAddMemoryDialog());
        editMenu.getItems().addAll(addRelativeItem, addMemoryItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);
        return menuBar;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        leftPanel.setPrefWidth(300);

        // Relatives Section
        Label relativesLabel = new Label("Relatives");
        relativesLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        relativesListView = new ListView<>();
        relativesListView.setPrefHeight(250);
        relativesListView.setOnMouseClicked(e -> showRelativeDetails());

        Button addRelBtn = new Button("+ Add Relative");
        addRelBtn.setMaxWidth(Double.MAX_VALUE);
        addRelBtn.setOnAction(e -> showAddRelativeDialog());

        // Memories Section
        Label memoriesLabel = new Label("Memories");
        memoriesLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        memoriesListView = new ListView<>();
        memoriesListView.setPrefHeight(250);
        memoriesListView.setOnMouseClicked(e -> showMemoryDetails());

        Button addMemBtn = new Button("+ Add Memory");
        addMemBtn.setMaxWidth(Double.MAX_VALUE);
        addMemBtn.setOnAction(e -> showAddMemoryDialog());

        leftPanel.getChildren().addAll(
                relativesLabel, relativesListView, addRelBtn,
                new Separator(),
                memoriesLabel, memoriesListView, addMemBtn);

        return leftPanel;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        Label detailsLabel = new Label("Details");
        detailsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        detailsArea = new TextArea();
        detailsArea.setWrapText(true);
        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(20);

        centerPanel.getChildren().addAll(detailsLabel, detailsArea);
        VBox.setVgrow(detailsArea, javafx.scene.layout.Priority.ALWAYS);

        return centerPanel;
    }

    private void showAddRelativeDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Relative");
        dialog.setHeaderText("Enter Relative Information");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField relationshipField = new TextField();
        relationshipField.setPromptText("Relationship (e.g., Son, Daughter)");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("M", "F");
        genderCombo.setPromptText("Gender");

        Label photoLabel = new Label("No photo selected");
        Button photoBtn = new Button("Upload Photo");
        final File[] selectedPhoto = {null};
        
        photoBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Relative Photo");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedPhoto[0] = file;
                photoLabel.setText("Selected: " + file.getName());
            }
        });

        content.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Relationship:"), relationshipField,
                new Label("Phone:"), phoneField,
                new Label("Gender:"), genderCombo,
                new Label("Photo:"), new HBox(10, photoBtn, photoLabel));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setOnCloseRequest(e -> {
            if (dialog.getResult() == null)
                return;
            String name = nameField.getText();
            String relationship = relationshipField.getText();
            String phone = phoneField.getText();
            String gender = genderCombo.getValue();

            if (!name.isEmpty() && !relationship.isEmpty() && !phone.isEmpty() && gender != null) {
                int id = relatives.size() + 1;
                Relative newRelative = new Relative(id, name, relationship, phone, gender);
                if (selectedPhoto[0] != null) {
                    newRelative.setPhotoPath(selectedPhoto[0].getAbsolutePath());
                }
                relatives.add(newRelative);
                updateRelativesList();
                detailsArea.setText("✓ Relative '" + name + "' added successfully!");
            }
        });

        dialog.showAndWait();
    }

    private void showAddMemoryDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Memory");
        dialog.setHeaderText("Enter Memory Information");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        TextField nameField = new TextField();
        nameField.setPromptText("Memory Title");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Memory Description");
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setWrapText(true);

        ListView<String> mediaListView = new ListView<>();
        mediaListView.setPrefHeight(100);
        final List<File> mediaFiles = new ArrayList<>();

        Button addMediaBtn = new Button("+ Add Media File");
        Button removeMediaBtn = new Button("- Remove Selected");
        removeMediaBtn.setOnAction(e -> {
            int idx = mediaListView.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                mediaListView.getItems().remove(idx);
                mediaFiles.remove(idx);
            }
        });

        addMediaBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Media File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Media", "*.jpg", "*.png", "*.mp4", "*.avi", "*.mov", "*.gif"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov"));
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                mediaFiles.add(file);
                mediaListView.getItems().add(file.getName() + " (" + getMediaType(file) + ")");
            }
        });

        HBox mediaButtonBox = new HBox(10, addMediaBtn, removeMediaBtn);

        content.getChildren().addAll(
                new Label("Title:"), nameField,
                new Label("Description:"), descriptionArea,
                new Label("Media Files:"), mediaListView,
                mediaButtonBox);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setOnCloseRequest(e -> {
            if (dialog.getResult() == null)
                return;
            String name = nameField.getText();
            String description = descriptionArea.getText();

            if (!name.isEmpty()) {
                Memory newMemory = new Memory(name, new Date());
                newMemory.setDescription(description);
                
                // Add media files to the memory
                for (File mediaFile : mediaFiles) {
                    String mediaType = getMediaType(mediaFile);
                    Media media = new Media(mediaFile.getAbsolutePath(), mediaType);
                    media.setDescription(mediaFile.getName());
                    newMemory.addMedia(media);
                }
                
                memories.add(newMemory);
                updateMemoriesList();
                detailsArea.setText("✓ Memory '" + name + "' added successfully with " + mediaFiles.size() + " media file(s)!");
            }
        });

        dialog.showAndWait();
    }

    private String getMediaType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
            return "photo";
        } else if (fileName.matches(".*\\.(mp4|avi|mov|mkv|wmv)$")) {
            return "video";
        } else if (fileName.matches(".*\\.(mp3|wav|aac|flac)$")) {
            return "audio";
        }
        return "file";
    }

    private void showRelativeDetails() {
        int selectedIndex = relativesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < relatives.size()) {
            Relative relative = relatives.get(selectedIndex);
            StringBuilder details = new StringBuilder();
            details.append("--- RELATIVE DETAILS ---\n\n");
            details.append("ID: ").append(relative.getRel_id()).append("\n");
            details.append("Name: ").append(relative.getName()).append("\n");
            details.append("Relationship: ").append(relative.getRelationship()).append("\n");
            details.append("Phone: ").append(relative.getPhoneNumber()).append("\n");
            details.append("Gender: ").append(relative.getGender()).append("\n");
            details.append("Photo: ").append(relative.getPhotoPath() != null ? relative.getPhotoPath() : "No photo");
            detailsArea.setText(details.toString());
        }
    }

    private void showMemoryDetails() {
        int selectedIndex = memoriesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < memories.size()) {
            Memory memory = memories.get(selectedIndex);
            StringBuilder details = new StringBuilder();
            details.append("--- MEMORY DETAILS ---\n\n");
            details.append("ID: ").append(memory.getId()).append("\n");
            details.append("Title: ").append(memory.getName()).append("\n");
            details.append("Description: ").append(memory.getDescription()).append("\n");
            details.append("Date: ").append(memory.getDate()).append("\n\n");

            details.append("--- LINKED RELATIVES ---\n");
            List<Relative> relList = memory.getRelatives();
            if (relList.isEmpty()) {
                details.append("No relatives linked to this memory.\n");
            } else {
                for (Relative r : relList) {
                    details.append("• ").append(r.getName()).append(" (").append(r.getRelationship()).append(")\n");
                }
            }

            details.append("\n--- LINKED MEDIA ---\n");
            List<Media> mediaList = memory.getMediaList();
            if (mediaList.isEmpty()) {
                details.append("No media linked to this memory.\n");
            } else {
                for (Media m : mediaList) {
                    details.append("• ").append(m.getFilePath()).append(" [").append(m.getMediaType()).append("]\n");
                }
            }

            detailsArea.setText(details.toString());
        }
    }

    private void updateRelativesList() {
        relativesListView.getItems().clear();
        for (Relative r : relatives) {
            relativesListView.getItems().add(r.getName() + " (" + r.getRelationship() + ")");
        }
    }

    private void updateMemoriesList() {
        memoriesListView.getItems().clear();
        for (Memory m : memories) {
            memoriesListView.getItems().add(m.getName() + " - " + m.getDate());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
