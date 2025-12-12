// JavaFX GUI to test FileHandler (uses email-based update/delete)
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.scene.Node;
import java.awt.Desktop;
import java.io.File;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Main extends Application {
    private final FileHandler fh = new FileHandler();

    private Stage primaryStage;
    private Scene loginScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MemoraCare");

        loginScene = createLoginScene();

        primaryStage.setScene(loginScene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private Scene createLoginScene() {
        // Form controls and existing logic kept exactly the same
        Label lblTitle = new Label("Welcome to MemoraCare");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#6b5146;");

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
        // use a warm, neutral background to match the logo's palette
        root.setStyle("-fx-background-color: linear-gradient(#fbf8f6, #f2ebe6);");

        // Left branding area: top-aligned so the logo lines up with the top of the login card
        VBox leftBox = new VBox(12);
        leftBox.setAlignment(Pos.TOP_LEFT);
        // reduce left box width a bit and remove bottom padding; we'll nudge the logo into the card area
        leftBox.setPadding(new Insets(10, 0, 0, 0));
        leftBox.setStyle("-fx-background-color: transparent;");

        ImageView sideLogo = new ImageView();
        // Load the provided logo into the left branding panel and make it larger for balance
        try {
            String logoPath = "file:media/logo.jpg";
            javafx.scene.image.Image img = new javafx.scene.image.Image(logoPath);
            sideLogo.setImage(img);
            // make the polaroid a bit larger per request (centered and slightly bigger)
            sideLogo.setFitWidth(320);
            sideLogo.setPreserveRatio(true);
            sideLogo.setSmooth(true);
            // we'll center it vertically and nudge right so about half sits over the card's inner empty area
            sideLogo.setTranslateX(40);
            sideLogo.setTranslateY(0);
        } catch (Exception ignore) {
            // fallback sizing if image fails
            sideLogo.setFitWidth(320);
            sideLogo.setPreserveRatio(true);
        }
        // don't add the image as a child of leftBox — we'll layer it above the card so it appears on top
        // reserve left box width so the layout keeps the same spacing
        leftBox.setPrefWidth(260);

        // Right card form (outer beige card). Inside it we'll place a left empty area and a white inner form pane
        VBox outerCard = new VBox();
        outerCard.setPadding(new Insets(12));
        outerCard.setAlignment(Pos.TOP_CENTER);
        outerCard.setStyle("-fx-background-color: white; -fx-border-radius:18; -fx-background-radius:18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0.12, 0, 6);");

        // Inner HBox: left empty pane (where logo should align) + right white form pane
        HBox inner = new HBox();
        inner.setAlignment(Pos.TOP_LEFT);

        // left empty area inside the card (transparent surface) - logo will visually sit over this area
        Region innerLeft = new Region();
        // set this so roughly half (or slightly less) of the logo sits on the empty side
        innerLeft.setPrefWidth(140);

        // white form pane that contains the inputs and controls
        VBox formPane = new VBox(12);
        formPane.setPadding(new Insets(18));
        formPane.setAlignment(Pos.TOP_CENTER);
        formPane.setStyle("-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12;");
        // make the white form slightly narrower to balance with the empty area
        formPane.setPrefWidth(360);

        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#6b5146;");
        Label subtitle = new Label("Choose Account Type");
        subtitle.setStyle("-fx-text-fill: #8b6a57; -fx-font-weight:600;");

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
            String patientIconPath = "file:media/patient-logo.png";
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
        // selected and unselected styles updated to warm beige/brown palette
        String selStyle = "-fx-background-color: linear-gradient(#e6ddd6, #d1c6bd); -fx-text-fill: #2c2a29; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18; -fx-border-color:#a8846b; -fx-border-width:2; -fx-border-radius:8;";
        String unselStyle = "-fx-background-color: white; -fx-border-color:#efe6dd; -fx-text-fill:#6b5146; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18;";
        tbCare.setStyle(selStyle);
        tbPatient.setStyle(unselStyle);
        tbCare.setPrefWidth(150); tbPatient.setPrefWidth(150);

        // Create reusable effects for icon tinting
        javafx.scene.effect.DropShadow hoverShadowCare = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web("#a8846b"));
        javafx.scene.effect.DropShadow hoverShadowPatient = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web("#a8846b"));
        javafx.scene.effect.DropShadow selectedShadow = new javafx.scene.effect.DropShadow(22, javafx.scene.paint.Color.web("#8b644a"));
        // stronger blue tint on hover/selected: shift hue toward blue and increase saturation
        javafx.scene.effect.ColorAdjust hoverColorAdjust = new javafx.scene.effect.ColorAdjust();
        // subtle warm tint adjustments for a cohesive look with the logo
        hoverColorAdjust.setHue(-0.08);
        hoverColorAdjust.setSaturation(0.18);
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
                tbCare.setStyle(unselStyle + " -fx-effect: dropshadow(gaussian, rgba(168,132,107,0.30), 16, 0.2, 0, 5);");
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
                tbPatient.setStyle(unselStyle + " -fx-effect: dropshadow(gaussian, rgba(168,132,107,0.30), 16, 0.2, 0, 5);");
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

        // Inputs (formPane inner width reduced so it looks balanced)
        tfEmail.setPrefWidth(320);
        pf.setPrefWidth(320);
        tfEmail.setStyle("-fx-background-radius:6; -fx-border-radius:6; -fx-padding:8; -fx-border-color: #efe6dd;");
        pf.setStyle("-fx-background-radius:6; -fx-border-radius:6; -fx-padding:8; -fx-border-color: #efe6dd;");

        Label emailLabel = new Label("Email"); emailLabel.setStyle("-fx-text-fill:#6b5146;");
        Label passLabel = new Label("Password"); passLabel.setStyle("-fx-text-fill:#6b5146;");

        Button loginPrimary = new Button("Login");
        loginPrimary.setStyle("-fx-background-color: linear-gradient(#e6ddd6, #d1c6bd); -fx-text-fill: #2c2a29; -fx-padding:8 20; -fx-background-radius:8;");
        Button signupLink = new Button("Signup");
        signupLink.setStyle("-fx-background-color: transparent; -fx-text-fill:#6b5146; -fx-underline:true; -fx-padding:6 10; -fx-font-weight:600;");

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

        // assemble formPane (white box)
        formPane.getChildren().addAll(lblTitle, subtitle, roleBar, emailLabel, tfEmail, passLabel, pf, loginPrimary, footerRow, status, hidden);

        // add left empty area and the white form pane into inner HBox
        inner.getChildren().addAll(innerLeft, formPane);

        // put inner into outerCard
        outerCard.getChildren().add(inner);

        // Layout: build a base HBox (left spacer + outer card), then layer the logo on top using a StackPane
        HBox base = new HBox(12);
        base.setAlignment(Pos.TOP_LEFT);
        base.getChildren().addAll(leftBox, outerCard);

        // create a StackPane so the logo can be placed above the card (not behind it)
        StackPane stack = new StackPane();
        stack.getChildren().addAll(base, sideLogo);
        // align logo centered vertically on the left side of the layout (not under the card) and nudge horizontally
        StackPane.setAlignment(sideLogo, Pos.CENTER_LEFT);
        sideLogo.setTranslateX(40);
        sideLogo.setTranslateY(0);

        HBox.setHgrow(leftBox, Priority.NEVER);
        outerCard.setPrefWidth(560);

        root.setCenter(stack);

        // bottom small footer
        Label footer = new Label("© MemoraCare");
        footer.setStyle("-fx-text-fill: #6b5146;");
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
        DatePicker dp = new DatePicker();
        dp.setPromptText("Date");
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

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding:14;");
        dlg.getDialogPane().setPrefWidth(440);

        // color all labels inside the grid to match theme
        for (javafx.scene.Node node : g.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill:#6b5146; -fx-font-weight:600;");
            }
        }
        // style inputs
        tfName.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        tfEmail.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        pf.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");

        // Add the standard dialog buttons
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style the OK / Cancel buttons to match theme
        javafx.scene.control.Button okBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (okBtn != null) okBtn.setStyle("-fx-background-color: linear-gradient(#e6ddd6, #d1c6bd); -fx-text-fill: #2c2a29; -fx-background-radius:6; -fx-padding:6 14;");
        if (cancelBtn != null) cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill:#6b5146; -fx-padding:6 10; -fx-underline:true;");

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

    // java
    private void showPatientView(Patient patient) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);

        // Main content: stacked sections
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setStyle("-fx-background-color: white;");
        root.setCenter(centerContent);

        // Header (beige)
        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle("-fx-background-color: #F5F5DC;");
        root.setTop(headerBox);

        // Line 1: Welcome + Logout
        HBox welcomeRow = new HBox(300);
        Label welcomeLabel = new Label("Hi, " + safeName(patient.getName()));
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Region hdrSpacer = new Region();
        HBox.setHgrow(hdrSpacer, Priority.ALWAYS);
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> primaryStage.setScene(loginScene));
        welcomeRow.getChildren().addAll(welcomeLabel, hdrSpacer, btnLogout);
        welcomeRow.setAlignment(Pos.CENTER_LEFT);

        // Line 2: Stage
        Label stageLabel = new Label("Stage: " + safeString(patient.getPatientStage()));
        stageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        // Line 3: Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search memories or relatives...");
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10 15; -fx-border-color: #ddd;");
        searchField.setMaxWidth(400);

        headerBox.getChildren().addAll(welcomeRow, stageLabel, searchField);

        // --- REMINDERS SECTION ---
        VBox remindersSection = new VBox(10);
        Label remTitle = new Label("Upcoming Reminders");
        remTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox remindersHBox = new HBox(12);
        remindersHBox.setPadding(new Insets(8));
        remindersHBox.setAlignment(Pos.CENTER_LEFT);

        ScrollPane remScrollPane = new ScrollPane(remindersHBox);
        remScrollPane.setFitToHeight(true);
        remScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        remScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        remScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // populate reminders (sorted by date/time if available)
        List<Reminder> remList = patient.getReminders() == null ? Collections.emptyList() : new ArrayList<>(patient.getReminders());
        // normalize to epoch millis to handle java.util.Date or java.time.LocalDateTime safely
        remList.sort(Comparator.comparingLong(r -> {
            if (r == null) return Long.MAX_VALUE;
            try {
                // try primary getter
                Object dateObj = null;
                try { dateObj = r.getDate(); } catch (Exception ignored) {}
                if (dateObj instanceof java.util.Date) return ((java.util.Date) dateObj).getTime();
                if (dateObj instanceof java.time.LocalDateTime) {
                    return ((java.time.LocalDateTime) dateObj).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }
                // fallback to an alternative getter if available
                try {
                    Object dateObj2 = r.getDate();
                    if (dateObj2 instanceof java.util.Date) return ((java.util.Date) dateObj2).getTime();
                    if (dateObj2 instanceof java.time.LocalDateTime) {
                        return ((java.time.LocalDateTime) dateObj2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    }
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
            return Long.MAX_VALUE;
        }));

        for (Reminder r : remList) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(12));
            card.setPrefWidth(300);
            card.setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 8;");
            Label name = new Label(r == null ? "Reminder" : safeString(r.getName()));
            name.setStyle("-fx-font-weight: bold;");
            Label desc = new Label(r == null ? "" : safeString(r.getDescription()));
            desc.setWrapText(true);
            String when = "";
            try {
                if (r != null) {
                    Object d = null;
                    try { d = r.getDate(); } catch (Exception ignored) {}
                    if (d == null) try { d = r.getDate(); } catch (Exception ignored) {}
                    when = d == null ? "" : String.valueOf(d);
                }
            } catch (Exception ignored) {}
            Label whenLbl = new Label(when);
            whenLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
            card.getChildren().addAll(name, desc, whenLbl);
            remindersHBox.getChildren().add(card);
        }
        remindersSection.getChildren().addAll(remTitle, remScrollPane);
        centerContent.getChildren().add(remindersSection);

        // --- MEMORIES SECTION ---
        VBox memoriesSection = new VBox(10);
        Label memTitle = new Label("Your Memories");
        memTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox memoriesHBox = new HBox(12);
        memoriesHBox.setPadding(new Insets(8));
        memoriesHBox.setAlignment(Pos.CENTER_LEFT);

        ScrollPane memScrollPane = new ScrollPane(memoriesHBox);
        memScrollPane.setFitToHeight(true);
        memScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        memScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        memScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        List<Memory> memList = patient.getMemories() == null ? Collections.emptyList() : new ArrayList<>(patient.getMemories());
        for (Memory m : memList) {
            final Memory mem = m; // final copy for lambda usage
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setPrefWidth(300);
            card.setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 8;");

            // find thumbnail image among media (best-effort)
            Node imageRegion;
            try {
                String imagePath = null;
                try {
                    if (mem != null && mem.getMediaList() != null) {
                        for (Object mmObj : mem.getMediaList()) {
                            if (mmObj == null) continue;
                            try {
                                Media mm = (Media) mmObj;
                                String path = mm.getMediaPath();
                                String type = null;
                                try { type = mm.getMediaType(); } catch (Exception ignored) {}
                                if (path != null) {
                                    String pLower = path.toLowerCase();
                                    if ((type != null && type.toLowerCase().contains("image")) ||
                                            pLower.endsWith(".png") || pLower.endsWith(".jpg") || pLower.endsWith(".jpeg") ||
                                            pLower.endsWith(".gif") || pLower.endsWith(".bmp")) {
                                        imagePath = path;
                                        break;
                                    }
                                }
                            } catch (ClassCastException ignored) {
                                // ignore non-Media objects
                            }
                        }
                    }
                } catch (Exception ignored) {}

                if (imagePath != null && new File(imagePath).exists()) {
                    Image img = new Image(new File(imagePath).toURI().toString(), 280, 160, true, true);
                    ImageView iv = new ImageView(img);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    imageRegion = iv;
                } else {
                    StackPane placeholder = new StackPane();
                    placeholder.setPrefSize(280, 160);
                    placeholder.setStyle("-fx-background-color: #EFEAD8; -fx-background-radius:6;");
                    Label pLbl = new Label("No Image");
                    pLbl.setStyle("-fx-text-fill:#777;");
                    placeholder.getChildren().add(pLbl);
                    imageRegion = placeholder;
                }
            } catch (Exception ex) {
                StackPane placeholder = new StackPane();
                placeholder.setPrefSize(280, 160);
                placeholder.setStyle("-fx-background-color: #EFEAD8;");
                placeholder.getChildren().add(new Label("No Image"));
                imageRegion = placeholder;
            }

            Label name = new Label(mem == null ? "Memory" : safeString(mem.getName()));
            name.setStyle("-fx-font-weight: bold;");
            Label desc = new Label(mem == null ? "" : safeString(mem.getDescription()));
            desc.setWrapText(true);

            card.getChildren().addAll(imageRegion, name, desc);

            // click to open details dialog with media list and Open buttons
            card.setOnMouseClicked(evt -> {
                showMemoryDetailsDialog(mem);
            });

            memoriesHBox.getChildren().add(card);
        }
        memoriesSection.getChildren().addAll(memTitle, memScrollPane);
        centerContent.getChildren().add(memoriesSection);

        // --- RELATIVES SECTION ---
        VBox relativesSection = new VBox(10);
        Label relTitle = new Label("Closest Relatives");
        relTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox relativesHBox = new HBox(16);
        relativesHBox.setPadding(new Insets(8));
        relativesHBox.setAlignment(Pos.CENTER_LEFT);

        ScrollPane relScrollPane = new ScrollPane(relativesHBox);
        relScrollPane.setFitToHeight(true);
        relScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        relScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        relScrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        List<Relative> relList = patient.getRelatives() == null ? Collections.emptyList() : new ArrayList<>(patient.getRelatives());
        for (Relative r : relList) {
            final Relative rel = r; // final copy for lambda usage
             VBox card = new VBox(6);
             card.setPadding(new Insets(10));
             card.setPrefWidth(160);
             card.setAlignment(Pos.TOP_CENTER);
             card.setStyle("-fx-background-color: #F5F5DC; -fx-background-radius: 8;");
 
             ImageView iv = new ImageView();
             try {
                String p = null;
                try { p = rel.getPhotoPath(); } catch (Exception ignored) {}
                if (p != null && new File(p).exists()) {
                    Image img = new Image(new File(p).toURI().toString(), 80, 80, true, true);
                    iv.setImage(img);
                }
             } catch (Exception ignore) {}
             iv.setFitWidth(80);
             iv.setFitHeight(80);
             Circle clip = new Circle(40, 40, 40);
             iv.setClip(clip);
 
             Label name = new Label(rel == null ? "Relative" : safeString(rel.getName()));
             name.setStyle("-fx-font-weight: bold;");
             Label relation = new Label(rel == null ? "" : safeString(rel.getRelationship()));
             relation.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
 
             card.getChildren().addAll(iv, name, relation);
 
             // show full contact info popup
             card.setOnMouseClicked(evt -> {
                 showRelativeDetailsDialog(rel);
             });
 
             relativesHBox.getChildren().add(card);
         }
        relativesSection.getChildren().addAll(relTitle, relScrollPane);
        centerContent.getChildren().add(relativesSection);

        primaryStage.setTitle("Patient Dashboard - " + safeName(patient.getName()));
        // ensure size applies
        primaryStage.setWidth(scene.getWidth());
        primaryStage.setHeight(scene.getHeight());
    }

    // Show a memory details dialog including list of attached media and ability to open files
    private void showMemoryDetailsDialog(Memory mem) {
        if (mem == null) return;
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        VBox content = new VBox(10);
        content.setPadding(new Insets(12));
        Label title = new Label(safeString(mem.getName()));
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
        Label desc = new Label(safeString(mem.getDescription()));
        desc.setWrapText(true);
        content.getChildren().addAll(title, desc);

        List<Media> mediaList = Collections.emptyList();
        try { mediaList = mem.getMediaList() == null ? Collections.emptyList() : mem.getMediaList(); } catch (Exception ignored) {}
        for (Media mm : mediaList) {
            if (mm == null) continue;
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            String path = null;
            try { path = mm.getMediaPath(); } catch (Exception ignored) {}
            Label fileLabel = new Label(path == null ? "(unknown)" : new File(path).getName());
            Button openBtn = new Button("Open");
            String finalPath = path;
            openBtn.setOnAction(ae -> {
                try {
                    if (finalPath != null && new File(finalPath).exists() && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(finalPath));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            row.getChildren().addAll(fileLabel, openBtn);
            content.getChildren().add(row);
        }

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        Scene s = new Scene(sp, 560, 420);
        dlg.setScene(s);
        dlg.showAndWait();
    }

    // Show a relative details dialog with contact info
    private void showRelativeDetailsDialog(Relative rel) {
        if (rel == null) return;
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        VBox content = new VBox(8);
        content.setPadding(new Insets(12));
        Label name = new Label(safeString(rel.getName()));
        name.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");
        Label phone = new Label("Phone: " + safeString(rel.getPhoneNumber()));
        Label email = new Label("Email: " + safeString(rel.getEmail()));
        Label address = new Label("Address: " + safeString(rel.getAddress()));
        Label bday = new Label("Birthday: " + (rel.getBirthday() == null ? "" : String.valueOf(rel.getBirthday())));
        content.getChildren().addAll(name, phone, email, address, bday);
        Scene s = new Scene(content, 360, 220);
        dlg.setScene(s);
        dlg.showAndWait();
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

