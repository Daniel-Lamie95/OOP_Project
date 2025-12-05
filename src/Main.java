import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Main extends Application {
    private final ListView<String> relativesListView = new ListView<>();
    private final ListView<String> memoriesListView = new ListView<>();
    private final ListView<String> remindersListView = new ListView<>();
    private final TextArea detailsArea = new TextArea();

    // keep actual objects so entered data can be inspected and edited
    private final java.util.List<Relative> relatives = new java.util.ArrayList<>();
    private final java.util.List<Memory> memories = new java.util.ArrayList<>();
    private final java.util.List<Reminder> reminders = new java.util.ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Project Test Harness - JavaFX");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Left column: lists
        VBox left = new VBox(10);
        left.setPadding(new Insets(5));
        Label relLabel = new Label("Relatives");
        Label memLabel = new Label("Memories");
        Label remLabel = new Label("Reminders");
        relativesListView.setPrefWidth(220);
        memoriesListView.setPrefWidth(220);
        remindersListView.setPrefWidth(220);
        left.getChildren().addAll(relLabel, relativesListView, memLabel, memoriesListView, remLabel, remindersListView);

        // Center: details
        VBox center = new VBox(10);
        center.setPadding(new Insets(5));
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefWidth(400);
        center.getChildren().addAll(new Label("Details / Logs"), detailsArea);
        VBox.setVgrow(detailsArea, Priority.ALWAYS);

        // Bottom: buttons to create sample items, add entries manually, and run checks
        HBox bottom = new HBox(8);
        bottom.setPadding(new Insets(5));
        Button btnAddSample = new Button("Add Sample Data");
        Button btnAddRelative = new Button("Add Relative");
        Button btnAddMemory = new Button("Add Memory");
        Button btnAddReminder = new Button("Add Reminder");
        Button btnRunChecks = new Button("Run Runtime Checks");
        Button btnClear = new Button("Clear All");
        bottom.getChildren().addAll(btnAddSample, btnAddRelative, btnAddMemory, btnAddReminder, btnRunChecks, btnClear);

        // Wire actions
        btnAddSample.setOnAction(e -> addSampleData());
        btnAddRelative.setOnAction(e -> showAddRelativeDialog());
        btnAddMemory.setOnAction(e -> showAddMemoryDialog());
        btnAddReminder.setOnAction(e -> showAddReminderDialog());
        btnClear.setOnAction(e -> clearAll());
        btnRunChecks.setOnAction(e -> runChecks());

        // Selection listeners show details
        relativesListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> showRelativeDetails(newV.intValue()));
        memoriesListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> showMemoryDetails(newV.intValue()));
        remindersListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> showReminderDetails(newV.intValue()));

        root.setLeft(left);
        root.setCenter(center);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        log("Ready: use 'Add Sample Data' to populate lists and 'Run Runtime Checks' to exercise behaviours.");
    }

    // Add a set of sample objects that exercise constructors and relations
    private void addSampleData() {
        try {
            // Create relatives
            Relative r1 = new Relative(1, "John Doe", "Son", "0123456789", "M");
            Relative r2 = new Relative(2, "Sarah Smith", "Daughter", "0987654321", "F");

            // Add to in-memory lists and update UI
            relatives.clear();
            relatives.add(r1);
            relatives.add(r2);
            updateRelativesList();

            // Create memory with media
            Memory m1 = new Memory(UUID.randomUUID(),"Beach Trip");
            m1.setDescription("A happy day at the beach");
            m1.setDate(new Date());
            Media photo = new Media("/path/to/photo.jpg", "photo", "beach.jpg");
            m1.addMedia(photo);
            m1.addRelative(r1);

            Memory m2 = new Memory(UUID.randomUUID(), "Birthday");
            m2.setDescription("Surprise party");
            m2.setDate(new Date());
            m2.addRelative(r2);

            memories.clear();
            memories.add(m1);
            memories.add(m2);
            updateMemoriesList();

            // Create reminders
            Reminder rem1 = new Reminder(UUID.randomUUID(), "Doctor Visit", "Annual checkup", LocalDateTime.now().plusDays(1));
            Reminder rem2 = new Reminder(UUID.randomUUID(), "Medication", "Take medicine", LocalDateTime.now().plusHours(2));

            reminders.clear();
            reminders.add(rem1);
            reminders.add(rem2);
            updateRemindersList();

            log("Sample data added: 2 relatives, 2 memories, 2 reminders.");
        } catch (Exception ex) {
            log("Error adding sample data: " + ex.toString());
        }
    }

    private void clearAll() {
        relativesListView.getItems().clear();
        memoriesListView.getItems().clear();
        remindersListView.getItems().clear();
        detailsArea.clear();
        relatives.clear();
        memories.clear();
        reminders.clear();
        log("Cleared all lists and logs.");
    }

    private void runChecks() {
        StringBuilder sb = new StringBuilder();
        sb.append("Running runtime checks...\n");
        // 1) Memory.setDate with null
        try {
            Memory test = new Memory(UUID.randomUUID(), "NullDateTest");
            test.setDescription("desc");
            test.setDate(null);
            sb.append("Memory created with null date -> date used = ").append(test.getDate()).append("\n");
        } catch (Exception ex) {
            sb.append("Memory(null) threw: ").append(ex).append("\n");
        }

        // 2) Adding invalid media
        try {
            Memory m = new Memory(UUID.randomUUID(), "MediaTest");
            m.setDescription("desc");
            m.setDate(new Date());
            m.addMedia(null); // should be ignored safely
            sb.append("Adding null media did not crash (expected). Current media count = ").append(m.getMediaList().size()).append("\n");
            try {
                Media invalid = new Media("", "photo", "");
                m.addMedia(invalid);
                sb.append("Unexpected: created invalid media and added it.\n");
            } catch (Exception e) {
                sb.append("Creating invalid Media(\"\") correctly threw: ").append(e.getClass().getSimpleName()).append("\n");
            }
        } catch (Exception ex) {
            sb.append("Media test failed: ").append(ex).append("\n");
        }

        // 3) equals/hashCode for Memory
        try {
            Memory a = new Memory(UUID.randomUUID(), "A");
            a.setDescription("d");
            a.setDate(new Date());
            Memory b = new Memory(a.getId(), a.getName());
            b.setDescription(a.getDescription());
            b.setDate(a.getDate());
            sb.append("Memory equality by id: a.equals(b) = ").append(a.equals(b)).append("\n");
        } catch (Exception ex) {
            sb.append("Equals/hash test failed: ").append(ex).append("\n");
        }

        // 4) Reminder notify check
        try {
            Reminder r = new Reminder(UUID.randomUUID(), "TestRem", "desc", LocalDateTime.now().minusMinutes(1));
            r.setDone(false);
            sb.append("Reminder due? (should trigger notify): \n");
            r.notifyIfDue(); // this prints to stdout in current implementation
            sb.append("Called notifyIfDue() — check console output for notification text.\n");
        } catch (Exception ex) {
            sb.append("Reminder check failed: ").append(ex).append("\n");
        }

        log(sb.toString());
    }

    // --- UI helpers ---
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

    private void updateRemindersList() {
        remindersListView.getItems().clear();
        for (Reminder r : reminders) {
            remindersListView.getItems().add(r.getName() + " @ " + (r.getDate() == null ? "no date" : r.getDate()));
        }
    }

    // --- Add dialogs ---
    private void showAddRelativeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Relative");
        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        TextField name = new TextField(); name.setPromptText("Name");
        TextField rel = new TextField(); rel.setPromptText("Relationship");
        TextField phone = new TextField(); phone.setPromptText("Phone");
        TextField gender = new TextField(); gender.setPromptText("Gender (M/F)");
        content.getChildren().addAll(new Label("Name:"), name, new Label("Relationship:"), rel, new Label("Phone:"), phone, new Label("Gender:"), gender);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(bt -> bt == ButtonType.OK ? ButtonType.OK : null);
        var res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            int id = relatives.size() + 1;
            Relative rObj = new Relative(id, name.getText().trim(), rel.getText().trim(), phone.getText().trim(), gender.getText().trim());
            relatives.add(rObj);
            updateRelativesList();
            log("Added Relative: " + rObj.getName());
        }
    }

    private void showAddMemoryDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Memory");
        VBox content = new VBox(8); content.setPadding(new Insets(10));
        TextField title = new TextField(); title.setPromptText("Title");
        TextArea desc = new TextArea(); desc.setPromptText("Description"); desc.setPrefRowCount(4);
        content.getChildren().addAll(new Label("Title:"), title, new Label("Description:"), desc);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        var res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            Memory m = new Memory(UUID.randomUUID(), title.getText().trim());
            m.setDescription(desc.getText().trim());
            m.setDate(new Date());
            memories.add(m);
            updateMemoriesList();
            log("Added Memory: " + m.getName());
        }
    }

    private void showAddReminderDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        VBox content = new VBox(8); content.setPadding(new Insets(10));
        TextField title = new TextField(); title.setPromptText("Title");
        TextField desc = new TextField(); desc.setPromptText("Description");
        TextField minutes = new TextField(); minutes.setPromptText("Minutes from now (e.g. 60)");
        content.getChildren().addAll(new Label("Title:"), title, new Label("Description:"), desc, new Label("Due in minutes:"), minutes);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        var res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            int mins;
            try { mins = Integer.parseInt(minutes.getText().trim()); } catch (NumberFormatException ex) { mins = 0; }
            Reminder r = new Reminder(UUID.randomUUID(), title.getText().trim(), desc.getText().trim(), LocalDateTime.now().plusMinutes(Math.max(0, mins)));
            reminders.add(r);
            updateRemindersList();
            log("Added Reminder: " + r.getName());
        }
    }

    private void showRelativeDetails(int index) {
        if (index < 0 || index >= relatives.size()) return;
        Relative r = relatives.get(index);
        StringBuilder sb = new StringBuilder();
        sb.append("--- RELATIVE ---\n");
        sb.append("ID: ").append(r.getRel_id()).append("\n");
        sb.append("Name: ").append(r.getName()).append("\n");
        sb.append("Relationship: ").append(r.getRelationship()).append("\n");
        sb.append("Phone: ").append(r.getPhoneNumber()).append("\n");
        sb.append("Gender: ").append(r.getGender()).append("\n");
        detailsArea.setText(sb.toString());
    }

    private void showMemoryDetails(int index) {
        if (index < 0 || index >= memories.size()) return;
        Memory m = memories.get(index);
        StringBuilder sb = new StringBuilder();
        sb.append("--- MEMORY ---\n");
        sb.append("ID: ").append(m.getId()).append("\n");
        sb.append("Title: ").append(m.getName()).append("\n");
        sb.append("Description: ").append(m.getDescription()).append("\n");
        sb.append("Date: ").append(m.getDate()).append("\n\n");
        sb.append("Linked Relatives:\n");
        for (Relative r : m.getRelatives()) sb.append(" - ").append(r.getName()).append(" (").append(r.getRelationship()).append(")\n");
        sb.append("\nMedia:\n");
        for (Media md : m.getMediaList()) sb.append(" - ").append(md.getMediaPath()).append(" [").append(md.getMediaType()).append("]\n");
        detailsArea.setText(sb.toString());
    }

    private void showReminderDetails(int index) {
        if (index < 0 || index >= reminders.size()) return;
        Reminder r = reminders.get(index);
        StringBuilder sb = new StringBuilder();
        sb.append("--- REMINDER ---\n");
        sb.append("ID: ").append(r.getId()).append("\n");
        sb.append("Title: ").append(r.getName()).append("\n");
        sb.append("Description: ").append(r.getDescription()).append("\n");
        sb.append("Date: ").append(r.getDate()).append("\n");
        sb.append("Done: ").append(r.getDone()).append("\n");
        detailsArea.setText(sb.toString());
    }

    private void log(String s) {
        detailsArea.appendText(s + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
