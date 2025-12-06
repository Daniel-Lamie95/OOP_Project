// JavaFX GUI to test FileHandler (uses email-based update/delete)
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

public class Main extends Application {
    private final FileHandler fh = new FileHandler();
    private final ObservableList<User> usersObs = FXCollections.observableArrayList();
    private final ListView<User> usersList = new ListView<>(usersObs);

    private final ChoiceBox<String> typeChoice = new ChoiceBox<>();
    private final TextField tfName = new TextField();
    private final TextField tfEmail = new TextField();
    private final PasswordField tfPassword = new PasswordField();
    private final TextField tfStage = new TextField();
    private final Label status = new Label();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FileHandler GUI — Email-based operations");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Left: users list
        usersList.setPrefWidth(320);
        usersList.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " <" + item.getEmail() + "> [" + item.getClass().getSimpleName() + "]");
                }
            }
        });

        VBox leftBox = new VBox(8, new Label("Accounts"), usersList);
        leftBox.setPadding(new Insets(5));
        root.setLeft(leftBox);

        // Center: form
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(5));

        typeChoice.getItems().addAll("Patient", "Caregiver");
        typeChoice.setValue("Patient");

        form.add(new Label("Type:"), 0, 0);
        form.add(typeChoice, 1, 0);
        form.add(new Label("Name:"), 0, 1);
        form.add(tfName, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(tfEmail, 1, 2);
        form.add(new Label("Password:"), 0, 3);
        form.add(tfPassword, 1, 3);
        form.add(new Label("Patient Stage:"), 0, 4);
        form.add(tfStage, 1, 4);

        root.setCenter(form);

        // Bottom: buttons
        Button btnLoad = new Button("Load");
        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update (by email)");
        Button btnDelete = new Button("Delete (by email)");
        HBox btns = new HBox(8, btnLoad, btnAdd, btnUpdate, btnDelete);
        btns.setPadding(new Insets(8));

        VBox bottom = new VBox(6, btns, status);
        bottom.setPadding(new Insets(5));
        root.setBottom(bottom);

        // Actions
        btnLoad.setOnAction(e -> loadAccounts());
        btnAdd.setOnAction(e -> addAccount());
        btnUpdate.setOnAction(e -> updateByEmail());
        btnDelete.setOnAction(e -> deleteByEmail());

        usersList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) populateForm(newV);
        });

        loadAccounts();

        Scene scene = new Scene(root, 900, 420);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadAccounts() {
        usersObs.clear();
        List<User> loaded = fh.loadAccounts();
        usersObs.addAll(loaded);
        status.setText("Loaded " + loaded.size() + " accounts");
    }

    private void populateForm(User u) {
        tfName.setText(u.getName() == null ? "" : u.getName());
        tfEmail.setText(u.getEmail() == null ? "" : u.getEmail());
        tfPassword.setText(u.getPassword() == null ? "" : u.getPassword());
        if (u instanceof Patient) tfStage.setText(((Patient) u).getPatientStage() == null ? "" : ((Patient) u).getPatientStage());
        else tfStage.setText("");
        typeChoice.setValue(u instanceof Patient ? "Patient" : "Caregiver");
    }

    private void addAccount() {
        String type = typeChoice.getValue();
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String pass = tfPassword.getText();
        String stage = tfStage.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            status.setText("Name and email are required to add an account");
            return;
        }

        if (type.equals("Patient")) {
            Patient p = new Patient(name, UUID.randomUUID(), email, pass, stage);
            fh.addAccount(p);
            status.setText("Added Patient: " + email);
        } else {
            Caregiver c = new Caregiver(name, email, pass);
            fh.addAccount(c);
            status.setText("Added Caregiver: " + email);
        }
        loadAccounts();
    }

    private void updateByEmail() {
        String targetEmail = tfEmail.getText().trim();
        if (targetEmail.isEmpty()) { status.setText("Email is required to update"); return; }

        // find existing user by email
        List<User> users = fh.loadAccounts();
        User found = null;
        for (User u : users) if (u.getEmail() != null && u.getEmail().equals(targetEmail)) { found = u; break; }
        if (found == null) { status.setText("No account found with email: " + targetEmail); return; }

        String name = tfName.getText().trim();
        String pass = tfPassword.getText();
        String stage = tfStage.getText().trim();

        if (name.isEmpty()) name = found.getName();
        if (pass.isEmpty()) pass = found.getPassword();

        User updated;
        try {
            if (typeChoice.getValue().equals("Patient")) {
                UUID id;
                try { id = UUID.fromString(found.getId()); } catch (Exception ex) { id = UUID.randomUUID(); }
                if (stage.isEmpty() && found instanceof Patient) stage = ((Patient) found).getPatientStage();
                updated = new Patient(name, id, targetEmail, pass, stage);
            } else {
                UUID id;
                try { id = UUID.fromString(found.getId()); } catch (Exception ex) { id = UUID.randomUUID(); }
                updated = new Caregiver(name, id, targetEmail, pass);
            }
        } catch (IllegalArgumentException iae) {
            status.setText("Invalid id format: " + iae.getMessage());
            return;
        }

        boolean ok = fh.updateAccountByEmail(targetEmail, updated);
        status.setText(ok ? "Updated account: " + targetEmail : "Update failed for: " + targetEmail);
        loadAccounts();
    }

    private void deleteByEmail() {
        String email = tfEmail.getText().trim();
        if (email.isEmpty()) { status.setText("Email is required to delete"); return; }
        boolean ok = fh.deleteAccountByEmail(email);
        status.setText(ok ? "Deleted account: " + email : "Delete failed: not found");
        loadAccounts();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
