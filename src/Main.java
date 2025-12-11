// JavaFX GUI to test FileHandler (uses email-based update/delete)
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.awt.Desktop;
import java.io.File;

import java.time.LocalDateTime;
import java.util.*;

public class Main extends Application {
    private final FileHandler fh = new FileHandler();

    private Stage primaryStage;
    private Scene loginScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Alzheimer Helper");

        loginScene = createLoginScene();

        primaryStage.setScene(loginScene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private Scene createLoginScene() {
        // Form controls and existing logic kept exactly the same
        Label lblTitle = new Label("Welcome to MemoraCare");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#124daa;");

        Label lblEmail = new Label("Email:");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("you@example.com");
        Label lblPassword = new Label("Password:");
        PasswordField pf = new PasswordField();
        pf.setPromptText("Enter password");

        // Keep the logical roleChoice but we'll present ToggleButtons that sync to it
        ChoiceBox<String> roleChoice = new ChoiceBox<>(FXCollections.observableArrayList("Caregiver", "Patient"));
        roleChoice.setValue("Caregiver");
        roleChoice.setVisible(false); roleChoice.setManaged(false);

        Button btnLogin = new Button("Login");
        Button btnRefresh = new Button("Reload accounts");
        Button btnSignUpCaregiver = new Button("Sign up (Caregiver)");
        Label status = new Label();

        // keep original button logic exactly
        btnLogin.setOnAction(e -> {
            String email = tfEmail.getText().trim();
            String pass = pf.getText();
            String role = roleChoice.getValue();
            if (email.isEmpty() || pass.isEmpty()) {
                status.setText("Email and password are required.");
                return;
            }
            User logged = findUserByCredentials(email, pass);
            if (logged == null) {
                status.setText("No matching account found.");
                return;
            }
            if (role.equals("Caregiver") && logged instanceof Caregiver) {
                status.setText("Entering caregiver dashboard...");
                showCaregiverDashboard((Caregiver) logged);
            } else if (role.equals("Patient") && logged instanceof Patient) {
                status.setText("Entering patient view...");
                showPatientView((Patient) logged);
            } else {
                status.setText("Role does not match account type.");
            }
        });

        btnRefresh.setOnAction(e -> {
            // simple feedback; FileHandler loads on demand
            status.setText("Accounts reloaded (on next action).");
        });

        btnSignUpCaregiver.setOnAction(e -> showCaregiverDashboard());

        // Build a polished UI: left image/branding and right card form
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(#f6fbff, #eaf3ff);");

        // Left branding area kept as a subtle panel (can be removed/tweaked)
        VBox leftBox = new VBox(12);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(10));
        leftBox.setStyle("-fx-background-color: transparent;");

        ImageView sideLogo = new ImageView();
        // Load the provided logo into the left branding panel and make it larger for balance
        try {
            String logoPath = "file:media/logo.jpg";
            javafx.scene.image.Image img = new javafx.scene.image.Image(logoPath);
            sideLogo.setImage(img);
            // slightly smaller for a more balanced layout
            sideLogo.setFitWidth(380);
            sideLogo.setPreserveRatio(true);
            sideLogo.setSmooth(true);
        } catch (Exception ignore) {
            // fallback sizing if image fails
            sideLogo.setFitWidth(300);
            sideLogo.setPreserveRatio(true);
        }
        leftBox.getChildren().addAll(sideLogo);

        // Right card form
        VBox card = new VBox(12);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0.2, 0, 4);");

        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#124daa;");
        Label subtitle = new Label("Choose Account Type");
        subtitle.setStyle("-fx-text-fill: #5b82c9; -fx-font-weight:600;");

        // Role toggle buttons (styled as bar buttons) that sync with roleChoice
        ToggleGroup tg = new ToggleGroup();
        ToggleButton tbCare = new ToggleButton("Caregiver");
        ToggleButton tbPatient = new ToggleButton("Patient");
        // Prepare ImageView placeholders so we can modify their effects on hover/select
        ImageView careIv = new ImageView();
        ImageView patientIv = new ImageView();
        // Load role icons and put them above the button text (TOP)
        try {
            String careIconPath = "file:media/caregiver-icon.png";
            javafx.scene.image.Image careImg = new javafx.scene.image.Image(careIconPath);
            careIv.setImage(careImg);
            careIv.setFitWidth(86);
            careIv.setPreserveRatio(true);
            careIv.setSmooth(true);
            tbCare.setGraphic(careIv);
            tbCare.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        } catch (Exception ignore) { /* ignore icon load */ }
        try {
            String patientIconPath = "file:media/patient-logo.png";;
            javafx.scene.image.Image patientImg = new javafx.scene.image.Image(patientIconPath);
            patientIv.setImage(patientImg);
            patientIv.setFitWidth(86);
            patientIv.setPreserveRatio(true);
            patientIv.setSmooth(true);
            tbPatient.setGraphic(patientIv);
            tbPatient.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        } catch (Exception ignore) { /* ignore icon load */ }
        tbCare.setToggleGroup(tg); tbPatient.setToggleGroup(tg);
        tbCare.setSelected(true);
        // selected / unselected styles (selected shows a highlighted border)
        String selStyle = "-fx-background-color: linear-gradient(#2b6ff6, #1f66d6); -fx-text-fill: white; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18; -fx-border-color:#1f66d6; -fx-border-width:2; -fx-border-radius:8;";
        String unselStyle = "-fx-background-color: white; -fx-border-color:#cfe4ff; -fx-text-fill:#2b6ff6; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18;";
        tbCare.setStyle(selStyle);
        tbPatient.setStyle(unselStyle);
        tbCare.setPrefWidth(150); tbPatient.setPrefWidth(150);

        // Create reusable effects for icon tinting
        javafx.scene.effect.DropShadow hoverShadowCare = new javafx.scene.effect.DropShadow(12, javafx.scene.paint.Color.web("#2b6ff6"));
        javafx.scene.effect.DropShadow hoverShadowPatient = new javafx.scene.effect.DropShadow(12, javafx.scene.paint.Color.web("#2b6ff6"));
        javafx.scene.effect.DropShadow selectedShadow = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web("#1f66d6"));
        // stronger blue tint on hover/selected: shift hue toward blue and increase saturation
        javafx.scene.effect.ColorAdjust hoverColorAdjust = new javafx.scene.effect.ColorAdjust();
        hoverColorAdjust.setHue(0.18);   // small hue shift toward blue
        hoverColorAdjust.setSaturation(0.45);
        hoverColorAdjust.setBrightness(0.02);
        // chain the color adjust under the drop shadow so the icon glows blue
        hoverShadowCare.setInput(hoverColorAdjust);
        hoverShadowPatient.setInput(hoverColorAdjust);
        selectedShadow.setInput(hoverColorAdjust);

        // Sync toggle selection with roleChoice value and update styles and icon effects
        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == tbCare) {
                roleChoice.setValue("Caregiver");
                tbCare.setStyle(selStyle);
                tbPatient.setStyle(unselStyle);
                // apply selected effect to care icon and clear patient icon effect
                careIv.setEffect(selectedShadow);
                patientIv.setEffect(null);
            } else if (newT == tbPatient) {
                roleChoice.setValue("Patient");
                tbPatient.setStyle(selStyle);
                tbCare.setStyle(unselStyle);
                patientIv.setEffect(selectedShadow);
                careIv.setEffect(null);
            }
        });

        // Hover behaviour: stronger visual feedback + blue tint glow on the icon
        tbCare.setOnMouseEntered(evt -> {
            if (!tbCare.isSelected()) {
                tbCare.setStyle(unselStyle + " -fx-effect: dropshadow(gaussian, rgba(47,111,246,0.22), 10, 0.2, 0, 3);");
                careIv.setEffect(hoverShadowCare);
            }
        });
        tbCare.setOnMouseExited(evt -> {
            if (!tbCare.isSelected()) {
                tbCare.setStyle(unselStyle);
                careIv.setEffect(null);
            }
        });
        tbPatient.setOnMouseEntered(evt -> {
            if (!tbPatient.isSelected()) {
                tbPatient.setStyle(unselStyle + " -fx-effect: dropshadow(gaussian, rgba(47,111,246,0.22), 10, 0.2, 0, 3);");
                patientIv.setEffect(hoverShadowPatient);
            }
        });
        tbPatient.setOnMouseExited(evt -> {
            if (!tbPatient.isSelected()) {
                tbPatient.setStyle(unselStyle);
                patientIv.setEffect(null);
            }
        });

        // Also ensure clicking the buttons themselves sets the toggle selection and styles (keeps logic consistent)
        tbCare.setOnAction(evt -> {
            tbCare.setSelected(true); roleChoice.setValue("Caregiver"); tbCare.setStyle(selStyle); tbPatient.setStyle(unselStyle);
            careIv.setEffect(selectedShadow); patientIv.setEffect(null);
        });
        tbPatient.setOnAction(evt -> {
            tbPatient.setSelected(true); roleChoice.setValue("Patient"); tbPatient.setStyle(selStyle); tbCare.setStyle(unselStyle);
            patientIv.setEffect(selectedShadow); careIv.setEffect(null);
        });

        HBox roleBar = new HBox(12, tbCare, tbPatient);
        roleBar.setAlignment(Pos.CENTER);

        // Inputs
        tfEmail.setPrefWidth(340);
        pf.setPrefWidth(340);
        tfEmail.setStyle("-fx-background-radius:6; -fx-border-radius:6; -fx-padding:8; -fx-border-color: #cfe4ff;");
        pf.setStyle("-fx-background-radius:6; -fx-border-radius:6; -fx-padding:8; -fx-border-color: #cfe4ff;");

        Label emailLabel = new Label("Email"); emailLabel.setStyle("-fx-text-fill:#6b87b7;");
        Label passLabel = new Label("Password"); passLabel.setStyle("-fx-text-fill:#6b87b7;");

        Button loginPrimary = new Button("Login");
        loginPrimary.setStyle("-fx-background-color: linear-gradient(#2b6ff6, #1f66d6); -fx-text-fill: white; -fx-padding:8 20; -fx-background-radius:8;");
        Button signupLink = new Button("Signup");
        signupLink.setStyle("-fx-background-color: transparent; -fx-text-fill:#2b6ff6; -fx-underline:true; -fx-padding:6 10; -fx-font-weight:600;");

        // wire our visible primary login button to the same logic as btnLogin
        loginPrimary.setOnAction(btnLogin.getOnAction());

        // We'll show the secondary controls in a compact footer row
        HBox footerRow = new HBox(12);
        footerRow.setAlignment(Pos.CENTER_LEFT);
        Label noAcc = new Label("No account?"); noAcc.setStyle("-fx-text-fill:#7e98c9;");
        footerRow.getChildren().addAll(noAcc, signupLink);

        // signupLink triggers the original sign-up dialog
        signupLink.setOnAction(btnSignUpCaregiver.getOnAction());

        // place original hidden roleChoice in scene graph so logic stays intact
        VBox hidden = new VBox(roleChoice); hidden.setVisible(false); hidden.setManaged(false);

        // Layout assemble: title, subtitle, roleBar, form fields, login button, and status
        // (logo is now shown on the left side for better balance)
        card.getChildren().addAll(lblTitle, subtitle, roleBar, emailLabel, tfEmail, passLabel, pf, loginPrimary, footerRow, status, hidden);

        // Layout: put left branding and right card side-by-side centered
        HBox center = new HBox(28);
        center.setAlignment(Pos.CENTER);
        center.getChildren().addAll(leftBox, card);

        HBox.setHgrow(leftBox, Priority.ALWAYS);
        leftBox.setPrefWidth(360);
        card.setPrefWidth(420);

        root.setCenter(center);

        // bottom small footer
        Label footer = new Label("© MemoraCare");
        footer.setStyle("-fx-text-fill: #666;");
        BorderPane.setAlignment(footer, Pos.CENTER);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        return scene;
    }

    private void showCaregiverDashboard(Caregiver caregiver) {
        // layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: header + logout
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("Caregiver: " + safeName(caregiver.getName()));
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> primaryStage.setScene(loginScene));
        top.getChildren().addAll(lbl, btnLogout);
        root.setTop(top);

        // Center: patient area (either assigned patient or create one)
        VBox center = new VBox(8);
        center.setPadding(new Insets(8));

        Label patientHeader = new Label("Assigned patient");
        patientHeader.setStyle("-fx-font-weight:bold;");
        center.getChildren().add(patientHeader);

        VBox patientBox = new VBox(6);
        updatePatientBox(patientBox, caregiver);
        center.getChildren().add(patientBox);

        root.setCenter(center);

        // Right: tabs for relatives, memories, reminders
        TabPane tabs = new TabPane();
        Tab tabRel = new Tab("Relatives");
        Tab tabMem = new Tab("Memories");
        Tab tabRem = new Tab("Reminders");

        tabRel.setContent(createRelativesPane(caregiver, patientBox));
        tabMem.setContent(createMemoriesPane(caregiver, patientBox));
        tabRem.setContent(createRemindersPane(caregiver, patientBox));

        tabs.getTabs().addAll(tabRel, tabMem, tabRem);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        root.setRight(tabs);

        Scene s = new Scene(root);
        primaryStage.setScene(s);
    }

    private void updatePatientBox(VBox patientBox, Caregiver caregiver) {
        patientBox.getChildren().clear();
        Patient p = caregiver.getPatient();
        if (p == null) {
            Label none = new Label("No patient assigned yet.");
            Button create = new Button("Create and assign patient");
            create.setOnAction(e -> showCreatePatientDialog(caregiver, patientBox));
            patientBox.getChildren().addAll(none, create);
        } else {
            Label name = new Label("Name: " + safeName(p.getName()));
            Label email = new Label("Email: " + safeName(p.getEmail()));
            Label stage = new Label("Stage: " + safeString(p.getPatientStage()));
            Button edit = new Button("Edit patient details");
            edit.setOnAction(e -> showEditPatientDialog(caregiver, patientBox));
            patientBox.getChildren().addAll(name, email, stage, edit);
        }
    }

    private Pane createRelativesPane(Caregiver caregiver, VBox patientBox) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));

        ListView<Relative> lv = new ListView<>();
        ObservableList<Relative> items = FXCollections.observableArrayList();
        lv.setItems(items);
        lv.setCellFactory(l -> new ListCell<Relative>() {
            @Override
            protected void updateItem(Relative r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null : r.getName() + " (" + r.getRelationship() + ")");
            }
        });

        Runnable refresh = () -> {
            items.clear();
            Patient p = caregiver.getPatient();
            if (p != null) items.addAll(p.getRelatives());
        };
        refresh.run();

        // form
        TextField tfName = new TextField(); tfName.setPromptText("Name");
        TextField tfRel = new TextField(); tfRel.setPromptText("Relationship");
        TextField tfPhone = new TextField(); tfPhone.setPromptText("Phone");
        ChoiceBox<String> cbGender = new ChoiceBox<>(FXCollections.observableArrayList("", "Male", "Female"));
        cbGender.setValue("");
        TextField tfRelMediaPath = new TextField(); tfRelMediaPath.setPromptText("Media file path");
        // new fields: email, address, birthday
        TextField tfRelEmail = new TextField(); tfRelEmail.setPromptText("Email");
        TextField tfRelAddress = new TextField(); tfRelAddress.setPromptText("Address");
        DatePicker dpBirthday = new DatePicker(); dpBirthday.setPromptText("Birthday");
        Button btnRelPick = new Button("Choose...");
        ChoiceBox<String> cbRelMediaType = new ChoiceBox<>(FXCollections.observableArrayList("image", "audio", "video", "file"));
        cbRelMediaType.setValue("image");
        TextField tfRelMediaDesc = new TextField(); tfRelMediaDesc.setPromptText("Media description (optional)");
        Button btnAdd = new Button("Add Relative");
        Label status = new Label();

        btnAdd.setOnAction(e -> {
            try {
                Patient p = caregiver.getPatient();
                if (p == null) { status.setText("Assign or create a patient first."); return; }
                String name = tfName.getText().trim();
                String rel = tfRel.getText().trim();
                String phone = tfPhone.getText().trim();
                String gender = cbGender.getValue();
                String email = tfRelEmail.getText().trim();
                String address = tfRelAddress.getText().trim();
                java.time.LocalDate birthday = dpBirthday.getValue();
                if (name.isEmpty()) { status.setText("Name required"); return; }
                // use the full constructor so email/address/birthday are stored
                Relative r = new Relative(
                        name,
                        rel.isEmpty()?"":rel,
                        "",              // description
                        phone,            // phoneNumber
                        email,            // email
                        (gender == null ? "" : gender), // gender
                        address,          // address
                        null,             // photoPath
                        birthday          // birthday
                );
                // attach single optional media from inline inputs (if provided)
                try {
                    String path = tfRelMediaPath.getText().trim();
                    String type = cbRelMediaType.getValue();
                    String desc = tfRelMediaDesc.getText().trim();
                    if (!path.isEmpty() && type != null && !type.trim().isEmpty()) {
                        Media m = new Media(path, type, desc);
                        r.addMedia(m);
                        // if this media is an image and the relative has no photo, set it as the profile photo
                        try {
                            if (type != null && type.equalsIgnoreCase("image") && (r.getPhotoPath() == null || r.getPhotoPath().trim().isEmpty())) {
                                r.setPhotoPath(path);
                            }
                        } catch (Exception ignoreSetPhoto) {
                            // non-fatal: leave media attached but photoPath unset if setPhotoPath refused
                        }
                    }
                } catch (Exception exMedia) {
                    // attach failed -> report but still add Relative
                    status.setText("Relative added but media failed: " + exMedia.getMessage());
                }
                caregiver.addRelative(r);
                // persist caregiver update
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                // ensure the separate Patient account (if exists) is also updated so patient view sees the new relative
                syncPatientToAccounts(caregiver);
                // debug: dump patient contents to console so we can verify media were attached and saved
                dumpPatientToConsole(caregiver.getPatient());
                // refresh any open 'Associate relatives' selector lists in the UI so Memories tab shows new relative
                refreshAssociateRelativesInUI(caregiver);
                tfName.clear(); tfRel.clear(); tfPhone.clear();
                cbGender.setValue("");
                tfRelEmail.clear(); tfRelAddress.clear(); dpBirthday.setValue(null);
                tfRelMediaPath.clear(); tfRelMediaDesc.clear(); cbRelMediaType.setValue("image");
                status.setText("Added relative.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setOnAction(e -> {
            Relative sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select relative to delete"); return; }
            try {
                caregiver.deleteRelative(sel.getName());
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                // sync patient as well
                syncPatientToAccounts(caregiver);
                // refresh memories' relative selectors so the deleted relative disappears
                refreshAssociateRelativesInUI(caregiver);
                status.setText("Deleted.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        // use a compact grid form so the media inputs are visible and aligned like other fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(6); formGrid.setVgap(6);
        formGrid.add(new Label("Name:"), 0, 0); formGrid.add(tfName, 1, 0);
        formGrid.add(new Label("Relationship:"), 2, 0); formGrid.add(tfRel, 3, 0);
        formGrid.add(new Label("Phone:"), 0, 1); formGrid.add(tfPhone, 1, 1);
        formGrid.add(new Label("Gender:"), 2, 1); formGrid.add(cbGender, 3, 1);
        formGrid.add(new Label("Media path:"), 0, 2); formGrid.add(tfRelMediaPath, 1, 2); formGrid.add(btnRelPick, 2, 2);
        formGrid.add(new Label("Type:"), 2, 2); formGrid.add(cbRelMediaType, 3, 2);
        formGrid.add(new Label("Media desc:"), 0, 3); formGrid.add(tfRelMediaDesc, 1, 3, 3, 1);
        formGrid.add(new Label("Email:"), 0, 4); formGrid.add(tfRelEmail, 1, 4);
        formGrid.add(new Label("Address:"), 2, 4); formGrid.add(tfRelAddress, 3, 4);
        formGrid.add(new Label("Birthday:"), 0, 5); formGrid.add(dpBirthday, 1, 5);
        HBox buttonsRow = new HBox(6, btnAdd);
        formGrid.add(buttonsRow, 0, 6, 4, 1);

        // File chooser for relatives media
        btnRelPick.setOnAction(evt -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose media file");
            File sel = fc.showOpenDialog(primaryStage);
            if (sel != null) {
                tfRelMediaPath.setText(sel.getAbsolutePath());
                String name = sel.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) cbRelMediaType.setValue("image");
                else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a")) cbRelMediaType.setValue("audio");
                else if (name.endsWith(".mp4") || name.endsWith(".mov") || name.endsWith(".avi")) cbRelMediaType.setValue("video");
                else cbRelMediaType.setValue("file");
            }
        });

        // show pending media list right under the form so it's clearly visible
        box.getChildren().addAll(lv, formGrid, btnDelete, status);
        return box;
    }

    private Pane createMemoriesPane(Caregiver caregiver, VBox patientBox) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));

        ListView<Memory> lv = new ListView<>();
        ObservableList<Memory> items = FXCollections.observableArrayList();
        lv.setItems(items);
        lv.setCellFactory(l -> new ListCell<Memory>() {
            @Override
            protected void updateItem(Memory m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : m.getName() + " - " + m.getDescription());
            }
        });

        Runnable refresh = () -> {
            items.clear();
            Patient p = caregiver.getPatient();
            if (p != null) items.addAll(p.getMemories());
        };
        refresh.run();

        TextField tfName = new TextField(); tfName.setPromptText("Memory name");
        TextField tfDesc = new TextField(); tfDesc.setPromptText("Description");
        DatePicker dp = new DatePicker();
        dp.setPromptText("Date");
        // allow selecting existing relatives to associate with the memory
        ListView<Relative> relSelect = new ListView<>();
        relSelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Label status = new Label();
        // populate relatives selection when refreshed
        Runnable refreshRelSelect = () -> {
            relSelect.getItems().clear();
            Patient p = caregiver.getPatient();
            if (p != null) relSelect.getItems().addAll(p.getRelatives());
        };
        refreshRelSelect.run();
        // Media inputs for memory (support multiple media items)
        TextField tfMediaPath = new TextField(); tfMediaPath.setPromptText("Media file path");
        Button btnMemPick = new Button("Choose...");
        ChoiceBox<String> cbMediaType = new ChoiceBox<>(FXCollections.observableArrayList("image","audio","video","file")); cbMediaType.setValue("image");
        TextField tfMediaDesc = new TextField(); tfMediaDesc.setPromptText("Media description");
        // list to hold multiple media for this memory
        ObservableList<Media> memMediaObjects = FXCollections.observableArrayList();
        ListView<Media> memMediaList = new ListView<>(memMediaObjects);
        memMediaList.setPrefHeight(100);
        memMediaList.setCellFactory(cell -> new ListCell<Media>() {
            @Override
            protected void updateItem(Media mm, boolean empty) {
                super.updateItem(mm, empty);
                if (empty || mm == null) setText(null);
                else setText((mm.getMediaPath()==null?"":mm.getMediaPath()) + " (" + (mm.getMediaType()==null?"":mm.getMediaType()) + ") " + (mm.getDescription()==null?"":mm.getDescription()));
            }
        });
        Button btnAddMedia = new Button("Add Media");
        Button btnRemoveMedia = new Button("Remove Media");
        Button btnAdd = new Button("Add Memory");

        btnAddMedia.setOnAction(ae -> {
            String path = tfMediaPath.getText().trim();
            String type = cbMediaType.getValue();
            String desc = tfMediaDesc.getText().trim();
            if (path.isEmpty()) { status.setText("Media path required"); return; }
            Media mm = new Media(path, type == null ? "file" : type, desc);
            memMediaObjects.add(mm);
            // clear inputs
            tfMediaPath.clear(); tfMediaDesc.clear(); cbMediaType.setValue("image");
            status.setText("Media added to list (" + mm.getMediaType() + ")");
        });

        btnRemoveMedia.setOnAction(ae -> {
            Media sel = memMediaList.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select media to remove"); return; }
            memMediaObjects.remove(sel);
            status.setText("Removed media.");
        });

        btnAdd.setOnAction(e -> {
            try {
                Patient p = caregiver.getPatient();
                if (p == null) { status.setText("Assign or create a patient first."); return; }
                String name = tfName.getText().trim();
                String desc = tfDesc.getText().trim();
                if (name.isEmpty()) { status.setText("Name required"); return; }
                // convert DatePicker value to java.util.Date (midnight)
                Date dateVal = new Date();
                if (dp.getValue() != null) {
                    java.time.LocalDate ld = dp.getValue();
                    java.time.LocalDateTime ldt = ld.atStartOfDay();
                    dateVal = java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
                }
                // gather selected relatives
                List<Relative> selectedRels = new ArrayList<>();
                selectedRels.addAll(relSelect.getSelectionModel().getSelectedItems());
                // gather media items (use memMediaObjects list). Also include current inputs as a fallback
                List<Media> mediaForMemory = new ArrayList<>();
                mediaForMemory.addAll(memMediaObjects);
                try {
                    String curPath = tfMediaPath.getText().trim();
                    String curType = cbMediaType.getValue();
                    String curDesc = tfMediaDesc.getText().trim();
                    if (!curPath.isEmpty()) {
                        // avoid duplicate by path
                        boolean exists = false;
                        for (Media _mm : mediaForMemory) if (_mm != null && curPath.equals(_mm.getMediaPath())) { exists = true; break; }
                        if (!exists) mediaForMemory.add(new Media(curPath, curType == null ? "file" : curType, curDesc));
                    }
                } catch (Exception ignore) { /* non-fatal */ }
                Memory m = new Memory(java.util.UUID.randomUUID(), name, desc, dateVal, selectedRels, mediaForMemory);
                caregiver.addMemory(m);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                // debug: dump patient contents so we can see saved memory and media
                dumpPatientToConsole(caregiver.getPatient());
                // also refresh any associate-relatives lists (in case new relative was just added elsewhere)
                refreshAssociateRelativesInUI(caregiver);
                tfName.clear(); tfMediaPath.clear(); tfMediaDesc.clear(); cbMediaType.setValue("image");
                // clear the media list after memory creation
                memMediaObjects.clear();
                status.setText("Added memory.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setOnAction(e -> {
            Memory sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select memory to delete"); return; }
            try {
                caregiver.deleteMemory(sel.getName());
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                status.setText("Deleted.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        // layout a compact grid form so media inputs are visible and align with other fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(6); formGrid.setVgap(6);
        formGrid.add(new Label("Name:"), 0, 0); formGrid.add(tfName, 1, 0);
        formGrid.add(new Label("Description:"), 2, 0); formGrid.add(tfDesc, 3, 0);
        formGrid.add(new Label("Date:"), 0, 1); formGrid.add(dp, 1, 1);
        formGrid.add(new Label("Media path:"), 2, 1); formGrid.add(tfMediaPath, 3, 1); formGrid.add(btnMemPick, 0, 2);
        formGrid.add(new Label("Type:"), 0, 2); formGrid.add(cbMediaType, 1, 2);
        formGrid.add(new Label("Media desc:"), 2, 2); formGrid.add(tfMediaDesc, 3, 2);
        // media list and controls
        formGrid.add(new Label("Media list:"), 0, 3);
        formGrid.add(memMediaList, 1, 3, 3, 1);
        HBox mediaButtons = new HBox(6, btnAddMedia, btnRemoveMedia);
        formGrid.add(mediaButtons, 1, 4);
        HBox buttonsRow = new HBox(6, btnAdd);
        formGrid.add(buttonsRow, 0, 5, 4, 1);

        // File chooser for memory media
        btnMemPick.setOnAction(evt -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose media file");
            File sel = fc.showOpenDialog(primaryStage);
            if (sel != null) {
                tfMediaPath.setText(sel.getAbsolutePath());
                String name = sel.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) cbMediaType.setValue("image");
                else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a")) cbMediaType.setValue("audio");
                else if (name.endsWith(".mp4") || name.endsWith(".mov") || name.endsWith(".avi")) cbMediaType.setValue("video");
                else cbMediaType.setValue("file");
            }
        });

        VBox right = new VBox(6, new Label("Associate relatives (select multiple):"), relSelect);
        // show pending media list right under the form so it's clearly visible
        box.getChildren().addAll(lv, formGrid, btnDelete, status);
        // ensure relSelect repopulates when patient changes
        // Note: refreshRelSelect is called at creation and whenever caregiver's patient changes via other actions
        return new VBox(8, box, new Label(""), right);
    }

    private Pane createRemindersPane(Caregiver caregiver, VBox patientBox) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));

        ListView<Reminder> lv = new ListView<>();
        ObservableList<Reminder> items = FXCollections.observableArrayList();
        lv.setItems(items);
        lv.setCellFactory(l -> new ListCell<Reminder>() {
            @Override
            protected void updateItem(Reminder r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null : r.getName() + " - " + r.getDescription());
            }
        });

        Runnable refresh = () -> {
            items.clear();
            Patient p = caregiver.getPatient();
            if (p != null) items.addAll(p.getReminders());
        };
        refresh.run();

        TextField tfName = new TextField(); tfName.setPromptText("Reminder name");
        TextField tfDesc = new TextField(); tfDesc.setPromptText("Description");
        DatePicker dp = new DatePicker(); dp.setPromptText("Date");
        TextField tfTime = new TextField(); tfTime.setPromptText("HH:mm (optional)");
        Button btnAdd = new Button("Add Reminder");
        Label status = new Label();

        btnAdd.setOnAction(e -> {
            try {
                Patient p = caregiver.getPatient();
                if (p == null) { status.setText("Assign or create a patient first."); return; }
                String name = tfName.getText().trim();
                String desc = tfDesc.getText().trim();
                if (name.isEmpty()) { status.setText("Name required"); return; }
                LocalDateTime when = LocalDateTime.now().plusDays(1);
                if (dp.getValue() != null) {
                    java.time.LocalDate date = dp.getValue();
                    java.time.LocalTime time = java.time.LocalTime.of(9, 0);
                    String timeText = tfTime.getText().trim();
                    if (!timeText.isEmpty()) {
                        try {
                            String[] parts = timeText.split(":");
                            int hh = Integer.parseInt(parts[0]);
                            int mm = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                            time = java.time.LocalTime.of(hh, mm);
                        } catch (Exception ex) { /* fall back to 09:00 */ }
                    }
                    when = LocalDateTime.of(date, time);
                }
                Reminder r = new Reminder(java.util.UUID.randomUUID(), name, desc, when);
                caregiver.addReminder(r);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                tfName.clear(); tfDesc.clear();
                dp.setValue(null); tfTime.clear();
                status.setText("Added reminder.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setOnAction(e -> {
            Reminder sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null) { status.setText("Select reminder to delete"); return; }
            try {
                caregiver.deleteReminder(sel.getName());
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                status.setText("Deleted.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

        HBox form = new HBox(6, tfName, tfDesc, dp, tfTime, btnAdd);
        box.getChildren().addAll(lv, form, btnDelete, status);
        return box;
    }

    // helper: when caregiver's patient object changes, copy it into the stored accounts so patient logins see updates
    private void syncPatientToAccounts(Caregiver caregiver) {
        if (caregiver == null) return;
        Patient p = caregiver.getPatient();
        if (p == null || p.getEmail() == null) return;
        ArrayList<User> accounts = fh.loadAccounts();
        boolean found = false;
        for (int i = 0; i < accounts.size(); i++) {
            User u = accounts.get(i);
            if (u instanceof Patient) {
                Patient stored = (Patient) u;
                if (stored.getEmail() != null && stored.getEmail().equals(p.getEmail())) {
                    // merge basic fields
                    stored.setName(p.getName());
                    stored.setPatientStage(p.getPatientStage());
                    // replace collections by clearing and adding
                    stored.getRelatives().clear();
                    stored.getRelatives().addAll(p.getRelatives());
                    stored.getMemories().clear();
                    stored.getMemories().addAll(p.getMemories());
                    stored.getReminders().clear();
                    stored.getReminders().addAll(p.getReminders());
                    accounts.set(i, stored);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            // add new patient account if missing
            accounts.add(p);
        }
        fh.saveAccounts(accounts);
    }

    private void showCreatePatientDialog(Caregiver caregiver, VBox patientBox) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Create Patient");

        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(8);
        TextField tfName = new TextField(); tfName.setPromptText("Name");
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");
        PasswordField pf = new PasswordField(); pf.setPromptText("Password");
        TextField tfStage = new TextField(); tfStage.setPromptText("Stage");
        g.add(new Label("Name:"),0,0); g.add(tfName,1,0);
        g.add(new Label("Email:"),0,1); g.add(tfEmail,1,1);
        g.add(new Label("Password:"),0,2); g.add(pf,1,2);
        g.add(new Label("Stage:"),0,3); g.add(tfStage,1,3);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> res = dlg.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            String pass = pf.getText();
            String stage = tfStage.getText().trim();
            if (name.isEmpty() || email.isEmpty() || pass.length() < 1) return;
            // check email uniqueness
            ArrayList<User> accounts = fh.loadAccounts();
            for (User u : accounts) if (u.getEmail() != null && u.getEmail().equals(email)) return;
            Patient p = new Patient(name, java.util.UUID.randomUUID(), email, pass, stage);
            fh.addAccount(p);
            try {
                caregiver.addPatient(p);
            } catch (Exception ex) {
                // already has a patient - ignore
            }
            fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
            // persist the patient copy so patient logins see the new patient contents
            syncPatientToAccounts(caregiver);
            updatePatientBox(patientBox, caregiver);
        }
    }

    // Add back the edit dialog that was referenced by the caregiver UI
    private void showEditPatientDialog(Caregiver caregiver, VBox patientBox) {
        Patient p = caregiver.getPatient();
        if (p == null) return;
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Patient");
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        TextField tfName = new TextField(p.getName());
        TextField tfStage = new TextField(p.getPatientStage());
        g.add(new Label("Name:"),0,0); g.add(tfName,1,0);
        g.add(new Label("Stage:"),0,1); g.add(tfStage,1,1);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = dlg.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                caregiver.editPatient(tfName.getText().trim(), tfStage.getText().trim());
                // Update stored patient account (if it exists) so saved accounts reflect edits
                ArrayList<User> accounts = fh.loadAccounts();
                for (User u : accounts) {
                    if (u instanceof Patient && ((Patient) u).getEmail() != null && ((Patient) u).getEmail().equals(p.getEmail())) {
                        ((Patient) u).setName(caregiver.getPatient().getName());
                        ((Patient) u).setPatientStage(caregiver.getPatient().getPatientStage());
                    }
                }
                fh.saveAccounts(accounts);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                // ensure stored patient account reflects edits too
                syncPatientToAccounts(caregiver);
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error editing patient: " + ex.getMessage(), ButtonType.OK);
                a.showAndWait();
            }
        }
    }

    // NEW: Sign-up dialogs
    private void showSignUpCaregiverDialog() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Sign up - Caregiver");

        // header graphic (small logo) to match login theme
        try {
            String logoPath = "file:/C:/Users/Mehrail Seddik10 24/IdeaProjects/OOP_Project/media/WhatsApp Image 2025-12-08 at 02.09.15_16f50d91.jpg";
            javafx.scene.image.Image lg = new javafx.scene.image.Image(logoPath);
            ImageView headerLogo = new ImageView(lg);
            headerLogo.setFitWidth(64);
            headerLogo.setPreserveRatio(true);
            dlg.getDialogPane().setGraphic(headerLogo);
        } catch (Exception ignore) { /* ignore image load problems */ }

        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        TextField tfName = new TextField(); tfName.setPromptText("Name");
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");
        PasswordField pf = new PasswordField(); pf.setPromptText("Password");
        g.add(new Label("Name:"),0,0); g.add(tfName,1,0);
        g.add(new Label("Email:"),0,1); g.add(tfEmail,1,1);
        g.add(new Label("Password:"),0,2); g.add(pf,1,2);

        // Apply theme styles to the dialog pane and inputs so it matches the login screen
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding:14;");
        dlg.getDialogPane().setPrefWidth(440);

        // color all labels inside the grid to match theme
        for (javafx.scene.Node node : g.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill:#6b87b7; -fx-font-weight:600;");
            }
        }
        // style inputs
        tfName.setStyle("-fx-border-color:#cfe4ff; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        tfEmail.setStyle("-fx-border-color:#cfe4ff; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        pf.setStyle("-fx-border-color:#cfe4ff; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");

        // Add the standard dialog buttons
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style the OK / Cancel buttons to match theme
        javafx.scene.control.Button okBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (okBtn != null) okBtn.setStyle("-fx-background-color: linear-gradient(#2b6ff6, #1f66d6); -fx-text-fill: white; -fx-background-radius:6; -fx-padding:6 14;");
        if (cancelBtn != null) cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill:#2b6ff6; -fx-padding:6 10; -fx-underline:true;");

        // Keep original OK handling logic exactly as before
        Optional<ButtonType> res = dlg.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            String pass = pf.getText();
            if (name.isEmpty() || email.isEmpty() || pass.length() < 6) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Invalid input: name/email required and password must be at least 6 characters.", ButtonType.OK);
                a.showAndWait();
                return;
            }
            ArrayList<User> accounts = fh.loadAccounts();
            for (User u : accounts) if (u.getEmail() != null && u.getEmail().equals(email)) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Email already in use.", ButtonType.OK); a.showAndWait(); return; }
            Caregiver c = new Caregiver(name, email, pass);
            fh.addAccount(c);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Caregiver account created. You can now log in.", ButtonType.OK); a.showAndWait();
        }
    }

    private void showPatientView(Patient patient) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(new Label("Patient: " + safeName(patient.getName())));
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> primaryStage.setScene(loginScene));
        top.getChildren().add(btnLogout);
        root.setTop(top);

        VBox center = new VBox(12); center.setPadding(new Insets(8));
        Label lblStage = new Label("Stage: " + safeString(patient.getPatientStage()));
        center.getChildren().add(lblStage);

        // --- RELATIVES ROW ---
        Label relHeader = new Label("Relatives");
        HBox relRow = new HBox(10);
        relRow.setPadding(new Insets(6));
        relRow.setPrefHeight(110);
        // details pane for selected relative
        VBox relDetails = new VBox(6);
        relDetails.setPadding(new Insets(6));
        relDetails.getChildren().add(new Label("Select a relative to see details"));

        List<Relative> patientRels = patient.getRelatives();
        for (Relative r : patientRels) {
            VBox card = new VBox(4);
            card.setPadding(new Insets(4));
            card.setStyle("-fx-border-color: #ccc; -fx-border-radius:4; -fx-padding:4;");

            ImageView iv = new ImageView();
            iv.setFitWidth(64); iv.setFitHeight(64); iv.setPreserveRatio(true);
            boolean imageLoaded = false;
            try {
                if (r.getPhotoPath() != null && !r.getPhotoPath().trim().isEmpty()) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + r.getPhotoPath(), 64, 64, true, true);
                    iv.setImage(img);
                    imageLoaded = true;
                }
            } catch (Exception ignored) { /* ignore image load errors */ }
            if (!imageLoaded) {
                // placeholder
                Label ph = new Label("No photo");
                ph.setPrefSize(64,64);
                ph.setStyle("-fx-border-color:#aaa; -fx-alignment:center; -fx-text-alignment:center;");
                card.getChildren().add(ph);
            } else {
                card.getChildren().add(iv);
            }
            Label nameLbl = new Label(safeName(r.getName()));
            card.getChildren().add(nameLbl);
            card.setOnMouseClicked(ev -> {
                relDetails.getChildren().clear();
                Label h = new Label("Relative: " + safeName(r.getName())); h.setStyle("-fx-font-weight:bold;");
                Label relLabel = new Label("Relationship: " + safeString(r.getRelationship()));
                Label phone = new Label("Phone: " + safeString(r.getPhoneNumber()));
                Label gender = new Label("Gender: " + safeString(r.getGender()));
                Label email = new Label("Email: " + safeString(r.getEmail()));
                Label addr = new Label("Address: " + safeString(r.getAddress()));
                Label bday = new Label("Birthday: " + (r.getBirthday()==null?"":r.getBirthday().toString()));
                relDetails.getChildren().addAll(h, relLabel, phone, gender, email, addr, bday, new Label("Media:"));
                ListView<String> mlist = new ListView<>();
                for (Media mm : r.getMediaList()) mlist.getItems().add(mm.getMediaPath() + " (" + mm.getMediaType() + ")");
                mlist.setPrefHeight(120);
                // make double-click open the media file with system default
                mlist.setOnMouseClicked(me -> {
                    if (me.getClickCount() == 2) {
                        String sel = mlist.getSelectionModel().getSelectedItem();
                        if (sel != null) {
                            // the list item format is "<path> (<type>)" - extract the path portion safely
                            String path;
                            int idx = sel.indexOf(" (");
                            if (idx > 0) path = sel.substring(0, idx); else path = sel;
                            try {
                                File f = new File(path);
                                if (f.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(f);
                            } catch (Exception ex) { /* ignore */ }
                        }
                    }
                });
                relDetails.getChildren().add(mlist);
            });
            relRow.getChildren().add(card);
        }

        HBox relBox = new HBox(12, relRow, relDetails);
        relBox.setAlignment(Pos.TOP_LEFT);

        // --- MEMORIES ROW ---
        Label memHeader = new Label("Memories");
        HBox memRow = new HBox(10);
        memRow.setPadding(new Insets(6));
        memRow.setPrefHeight(110);
        VBox memDetails = new VBox(6);
        memDetails.setPadding(new Insets(6));
        memDetails.getChildren().add(new Label("Select a memory to see details"));

        List<Memory> patientMems = patient.getMemories();
        for (Memory m : patientMems) {
            VBox card = new VBox(4); card.setPadding(new Insets(4));
            card.setStyle("-fx-border-color: #ccc; -fx-border-radius:4; -fx-padding:4;");

            // show thumbnails for all media attached to the memory (images as thumbnails, others as small labeled placeholders)
            HBox thumbs = new HBox(6);
            thumbs.setPadding(new Insets(2));
            for (Media mm : m.getMediaList()) {
                if (mm == null) continue;
                String path = mm.getMediaPath();
                String type = mm.getMediaType();
                if (type != null && type.equalsIgnoreCase("image") && path != null && !path.trim().isEmpty()) {
                    try {
                        javafx.scene.image.Image img = new javafx.scene.image.Image("file:" + path, 64, 64, true, true);
                        ImageView thumb = new ImageView(img);
                        thumb.setFitWidth(64); thumb.setFitHeight(64); thumb.setPreserveRatio(true);
                        // open on click
                        thumb.setOnMouseClicked(ev -> {
                            try { File f = new File(path); if (f.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(f); } catch (Exception ex) { /* ignore */ }
                        });
                        thumbs.getChildren().add(thumb);
                    } catch (Exception ignored) {
                        Label ph = new Label("img err"); ph.setPrefSize(64,64); ph.setStyle("-fx-border-color:#aaa; -fx-alignment:center;");
                        thumbs.getChildren().add(ph);
                    }
                } else {
                    // non-image media - show small clickable label with type
                    Label ph = new Label((type==null?"file":type));
                    ph.setPrefSize(64,64);
                    ph.setStyle("-fx-border-color:#aaa; -fx-alignment:center; -fx-text-alignment:center;");
                    String pth = path;
                    ph.setOnMouseClicked(ev -> {
                        try { if (pth != null) { File f = new File(pth); if (f.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(f); } } catch (Exception ex) { /* ignore */ }
                    });
                    thumbs.getChildren().add(ph);
                }
            }
            if (thumbs.getChildren().isEmpty()) {
                Label ph = new Label("No media"); ph.setPrefSize(64,64); ph.setStyle("-fx-border-color:#aaa; -fx-alignment:center; -fx-text-alignment:center;");
                card.getChildren().add(ph);
            } else {
                card.getChildren().add(thumbs);
            }

            Label title = new Label(safeString(m.getName()));
            Label when = new Label(m.getDate()==null?"":m.getDate().toString());
            card.getChildren().addAll(title, when);
            card.setOnMouseClicked(ev -> {
                memDetails.getChildren().clear();
                Label h = new Label("Memory: " + safeString(m.getName())); h.setStyle("-fx-font-weight:bold;");
                Label desc = new Label("Description: " + safeString(m.getDescription()));
                Label date = new Label("Date: " + (m.getDate()==null?"":m.getDate().toString()));
                memDetails.getChildren().addAll(h, desc, date, new Label("Relatives:"));
                ListView<String> rlist = new ListView<>();
                for (Relative rr : m.getRelatives()) rlist.getItems().add(rr.getName() + " (" + rr.getRelationship() + ")");
                rlist.setPrefHeight(80);
                memDetails.getChildren().add(rlist);
                memDetails.getChildren().add(new Label("Media:"));
                ListView<String> mlist = new ListView<>();
                for (Media mm : m.getMediaList()) mlist.getItems().add(mm.getMediaPath() + " (" + mm.getMediaType() + ")");
                mlist.setPrefHeight(100);
                mlist.setOnMouseClicked(me -> {
                    if (me.getClickCount() == 2) {
                        String sel = mlist.getSelectionModel().getSelectedItem();
                        if (sel != null) {
                            // the list item format is "<path> (<type>)" - extract the path portion safely
                            String path;
                            int idx = sel.indexOf(" (");
                            if (idx > 0) path = sel.substring(0, idx); else path = sel;
                            try { File f = new File(path); if (f.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(f); } catch (Exception ex) { /* ignore */ }
                        }
                    }
                });
                memDetails.getChildren().add(mlist);
            });
            memRow.getChildren().add(card);
        }
        HBox memBox = new HBox(12, memRow, memDetails);

        // --- REMINDERS ROW ---
        Label remHeader = new Label("Reminders");
        HBox remRow = new HBox(10);
        remRow.setPadding(new Insets(6));
        remRow.setPrefHeight(110);
        VBox remDetails = new VBox(6);
        remDetails.setPadding(new Insets(6));
        remDetails.getChildren().add(new Label("Select a reminder to see details"));

        List<Reminder> patientRems = patient.getReminders();
        for (Reminder r : patientRems) {
            VBox card = new VBox(4); card.setPadding(new Insets(4)); card.setStyle("-fx-border-color: #ccc; -fx-border-radius:4; -fx-padding:4;");
            Label title = new Label(safeString(r.getName()));
            String whenS = ""; try { if (r.getDate() != null) whenS = r.getDate().toString(); } catch (Exception ignored) {}
            final String whenSLocal = whenS;
            Label when = new Label(whenSLocal);
            card.getChildren().addAll(title, when);
            card.setOnMouseClicked(ev -> {
                remDetails.getChildren().clear();
                Label h = new Label("Reminder: " + safeString(r.getName())); h.setStyle("-fx-font-weight:bold;");
                Label desc = new Label("Description: " + safeString(r.getDescription()));
                Label date = new Label("When: " + whenSLocal);
                Label done = new Label("Done: " + String.valueOf(r.getDone()));
                remDetails.getChildren().addAll(h, desc, date, done);
            });
            remRow.getChildren().add(card);
        }
        HBox remBox = new HBox(12, remRow, remDetails);

        center.getChildren().addAll(relHeader, relBox, memHeader, memBox, remHeader, remBox);
        root.setCenter(center);

        Scene s = new Scene(root);
        primaryStage.setScene(s);
    }

    private User findUserByCredentials(String email, String password) {
        ArrayList<User> accounts = fh.loadAccounts();
        for (User u : accounts) {
            if (u != null && u.getEmail() != null && u.getPassword() != null && u.checkInfo(email, password)) return u;
        }
        return null;
    }

    private static String safeString(String s) { return s == null ? "" : s; }
    private static String safeName(String s) { return s == null ? "(no name)" : s; }

    // Debug helper: print patient contents (relatives/memories/reminders and attached media paths)
    private void dumpPatientToConsole(Patient p) {
        if (p == null) { System.out.println("dumpPatientToConsole: patient is null"); return; }
        System.out.println("--- DUMP PATIENT: " + p.getEmail() + " / " + p.getName() + " ---");
        System.out.println("Relatives: ");
        for (Relative r : p.getRelatives()) {
            System.out.println("  Relative: " + r.getName() + " photoPath=" + r.getPhotoPath());
            for (Media mm : r.getMediaList()) System.out.println("    Media: " + mm.getMediaPath() + " (" + mm.getMediaType() + ")");
        }
        System.out.println("Memories: ");
        for (Memory m : p.getMemories()) {
            System.out.println("  Memory: " + m.getName());
            for (Media mm : m.getMediaList()) System.out.println("    Media: " + mm.getMediaPath() + " (" + mm.getMediaType() + ")");
        }
        System.out.println("Reminders: " + p.getReminders().size());
        System.out.println("--- END DUMP ---");
    }

    // Walk the active scene and refresh any ListView used for 'Associate relatives' selectors inside the Memories tab
    @SuppressWarnings("unchecked")
    private void refreshAssociateRelativesInUI(Caregiver caregiver) {
        try {
            if (primaryStage == null) return;
            Scene sc = primaryStage.getScene();
            if (sc == null) return;
            javafx.scene.Parent root = sc.getRoot();
            if (root == null) return;
            // recursive traversal
            java.util.ArrayDeque<javafx.scene.Node> dq = new java.util.ArrayDeque<>();
            dq.add(root);
            while (!dq.isEmpty()) {
                javafx.scene.Node n = dq.removeFirst();
                if (n instanceof Label) {
                    String txt = ((Label) n).getText();
                    if (txt != null && txt.startsWith("Associate relatives")) {
                        javafx.scene.Parent parent = n.getParent();
                        if (parent instanceof javafx.scene.layout.VBox) {
                            for (javafx.scene.Node child : ((javafx.scene.layout.VBox) parent).getChildren()) {
                                if (child instanceof ListView) {
                                    @SuppressWarnings("rawtypes")
                                    ListView list = (ListView) child;
                                    try {
                                        list.getItems().clear();
                                        Patient p = caregiver == null ? null : caregiver.getPatient();
                                        if (p != null) for (Relative r : p.getRelatives()) list.getItems().add(r);
                                    } catch (Exception ignore) { /* ignore UI refresh issues */ }
                                }
                            }
                        }
                    }
                }
                if (n instanceof javafx.scene.Parent) {
                    for (javafx.scene.Node c : ((javafx.scene.Parent) n).getChildrenUnmodifiable()) dq.addLast(c);
                }
            }
        } catch (Exception ex) {
            System.out.println("refreshAssociateRelativesInUI failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
