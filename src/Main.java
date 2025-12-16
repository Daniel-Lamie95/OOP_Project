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
import javafx.scene.text.TextAlignment;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.scene.Node;
import java.awt.Desktop;
import java.io.File;
import java.util.function.Consumer;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;


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

        Label lblTitle = new Label("Welcome to MemoraCare");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#6b5146;");

        Label lblEmail = new Label("Email:");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("you@example.com");
        Label lblPassword = new Label("Password:");
        PasswordField pf = new PasswordField();
        pf.setPromptText("Enter password");


        ChoiceBox<String> roleChoice = new ChoiceBox<>(FXCollections.observableArrayList("Caregiver", "Patient"));
        roleChoice.setValue("Caregiver");
        roleChoice.setVisible(false); roleChoice.setManaged(false);

        Button btnLogin = new Button("Login");
        Button btnRefresh = new Button("Reload accounts");
        Button btnSignUpCaregiver = new Button("Sign up (Caregiver)");
        Label status = new Label();


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
            status.setText("Accounts reloaded (on next action).");
        });

        btnSignUpCaregiver.setOnAction(e -> showSignUpCaregiverDialog());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(#fbf8f6, #f2ebe6);");

        VBox leftBox = new VBox(12);
        leftBox.setAlignment(Pos.TOP_LEFT);
        leftBox.setPadding(new Insets(10, 0, 0, 0));
        leftBox.setStyle("-fx-background-color: transparent;");

        ImageView sideLogo = new ImageView();
        try {
            String logoPath = "file:media/logo.jpg";
            javafx.scene.image.Image img = new javafx.scene.image.Image(logoPath);
            sideLogo.setImage(img);
            sideLogo.setFitWidth(320);
            sideLogo.setPreserveRatio(true);
            sideLogo.setSmooth(true);
            sideLogo.setTranslateX(40);
            sideLogo.setTranslateY(0);
        } catch (Exception ignore) {
            sideLogo.setFitWidth(320);
            sideLogo.setPreserveRatio(true);
        }
        leftBox.setPrefWidth(260);

        VBox outerCard = new VBox();
        outerCard.setPadding(new Insets(12));
        outerCard.setAlignment(Pos.TOP_CENTER);
        outerCard.setStyle("-fx-background-color: white; -fx-border-radius:18; -fx-background-radius:18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0.12, 0, 6);");

        HBox inner = new HBox();
        inner.setAlignment(Pos.TOP_LEFT);

        Region innerLeft = new Region();
        innerLeft.setPrefWidth(140);

        VBox formPane = new VBox(12);
        formPane.setPadding(new Insets(18));
        formPane.setAlignment(Pos.TOP_CENTER);
        formPane.setStyle("-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12;");
        formPane.setPrefWidth(360);

        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:#6b5146;");
        Label subtitle = new Label("Choose Account Type");
        subtitle.setStyle("-fx-text-fill: #8b6a57; -fx-font-weight:600;");

        ToggleGroup tg = new ToggleGroup();
        ToggleButton tbCare = new ToggleButton("Caregiver");
        ToggleButton tbPatient = new ToggleButton("Patient");
        ImageView careIv = new ImageView();
        ImageView patientIv = new ImageView();
        try {
            String careIconPath = "file:media/caregiver-icon.png";
            javafx.scene.image.Image careImg = new javafx.scene.image.Image(careIconPath);
            careIv.setImage(careImg);
            careIv.setFitWidth(86);
            careIv.setPreserveRatio(true);
            careIv.setSmooth(true);
            tbCare.setGraphic(careIv);
            tbCare.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        } catch (Exception ignore) { }
        try {
            String patientIconPath = "file:media/patient-logo.png";
            javafx.scene.image.Image patientImg = new javafx.scene.image.Image(patientIconPath);
            patientIv.setImage(patientImg);
            patientIv.setFitWidth(86);
            patientIv.setPreserveRatio(true);
            patientIv.setSmooth(true);
            tbPatient.setGraphic(patientIv);
            tbPatient.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        } catch (Exception ignore) { }
        tbCare.setToggleGroup(tg); tbPatient.setToggleGroup(tg);
        tbCare.setSelected(true);
        String selStyle = "-fx-background-color: linear-gradient(#e6ddd6, #d1c6bd); -fx-text-fill: #2c2a29; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18; -fx-border-color:#a8846b; -fx-border-width:2; -fx-border-radius:8;";
        String unselStyle = "-fx-background-color: white; -fx-border-color:#efe6dd; -fx-text-fill:#6b5146; -fx-font-weight:600; -fx-background-radius:8; -fx-padding:10 18;";
        tbCare.setStyle(selStyle);
        tbPatient.setStyle(unselStyle);
        tbCare.setPrefWidth(150); tbPatient.setPrefWidth(150);

        javafx.scene.effect.DropShadow hoverShadowCare = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web("#a8846b"));
        javafx.scene.effect.DropShadow hoverShadowPatient = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.web("#a8846b"));
        javafx.scene.effect.DropShadow selectedShadow = new javafx.scene.effect.DropShadow(22, javafx.scene.paint.Color.web("#8b644a"));
        javafx.scene.effect.ColorAdjust hoverColorAdjust = new javafx.scene.effect.ColorAdjust();
        hoverColorAdjust.setHue(-0.08);
        hoverColorAdjust.setSaturation(0.18);
        hoverColorAdjust.setBrightness(0.02);
        hoverShadowCare.setInput(hoverColorAdjust);
        hoverShadowPatient.setInput(hoverColorAdjust);
        selectedShadow.setInput(hoverColorAdjust);

        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == tbCare) {
                roleChoice.setValue("Caregiver");
                tbCare.setStyle(selStyle);
                tbPatient.setStyle(unselStyle);
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

        loginPrimary.setOnAction(btnLogin.getOnAction());

        HBox footerRow = new HBox(12);
        footerRow.setAlignment(Pos.CENTER_LEFT);
        Label noAcc = new Label("No account?"); noAcc.setStyle("-fx-text-fill:#7e98c9;");
        footerRow.getChildren().addAll(noAcc, signupLink);

        signupLink.setOnAction(btnSignUpCaregiver.getOnAction());

        VBox hidden = new VBox(roleChoice); hidden.setVisible(false); hidden.setManaged(false);

        formPane.getChildren().addAll(lblTitle, subtitle, roleBar, emailLabel, tfEmail, passLabel, pf, loginPrimary, footerRow, status, hidden);

        inner.getChildren().addAll(innerLeft, formPane);

        outerCard.getChildren().add(inner);

        HBox base = new HBox(12);
        base.setAlignment(Pos.TOP_LEFT);
        base.getChildren().addAll(leftBox, outerCard);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(base, sideLogo);
        StackPane.setAlignment(sideLogo, Pos.CENTER_LEFT);
        sideLogo.setTranslateX(40);
        sideLogo.setTranslateY(0);

        HBox.setHgrow(leftBox, Priority.NEVER);
        outerCard.setPrefWidth(560);

        root.setCenter(stack);

        Label footer = new Label("© MemoraCare");
        footer.setStyle("-fx-text-fill: #6b5146;");
        BorderPane.setAlignment(footer, Pos.CENTER);
        root.setBottom(footer);

        Scene scene = new Scene(root);
        return scene;
    }

    private void showCaregiverDashboard(Caregiver caregiver) {

        BorderPane root = new BorderPane();

        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background:#E2DCC2; -fx-border-color:transparent;");
        root.setCenter(mainScroll);

        VBox page = new VBox(35);
        page.setPadding(new Insets(30));
        page.setStyle("-fx-background-color:#E2DCC2;");
        mainScroll.setContent(page);

        Scene scene = new Scene(root, 1100, 720);
        primaryStage.setScene(scene);

        VBox header = new VBox(15);
        header.setPadding(new Insets(22));
        header.setStyle(
                "-fx-background-color:#D8CBAE;" +
                        "-fx-background-radius:18;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.12),10,0,0,3);"
        );

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Caregiver Dashboard");
        title.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#3B3B3B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color:white;" +
                        "-fx-background-radius:16;" +
                        "-fx-border-color:#CFC1A3;" +
                        "-fx-border-radius:16;" +
                        "-fx-font-weight:bold;"
        );
        logoutBtn.setOnAction(e -> primaryStage.setScene(loginScene));

        topRow.getChildren().addAll(title, spacer, logoutBtn);

        Label nameLbl = new Label("Welcome, " + safeName(caregiver.getName()));
        nameLbl.setStyle("-fx-font-size:14px;-fx-text-fill:#4A4A4A;");

        header.getChildren().addAll(topRow, nameLbl);
        page.getChildren().add(header);

        VBox patientCard = new VBox(12);
        patientCard.setPadding(new Insets(18));
        patientCard.setStyle(
                "-fx-background-color:#F0E1B8;" +
                        "-fx-background-radius:18;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.1),6,0,0,2);"
        );

        Label patientTitle = new Label("Assigned Patient");
        patientTitle.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#3B3B3B;");

        VBox patientBox = new VBox(8);
        updatePatientBox(patientBox, caregiver);

        patientCard.getChildren().addAll(patientTitle, patientBox);
        page.getChildren().add(patientCard);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:transparent;");

        Tab tabRel = new Tab("Relatives");
        Tab tabMem = new Tab("Memories");
        Tab tabRem = new Tab("Reminders");

        tabRel.setContent(wrapCaregiverCard(createRelativesPane(caregiver, patientBox)));
        tabMem.setContent(wrapCaregiverCard(createMemoriesPane(caregiver, patientBox)));
        tabRem.setContent(wrapCaregiverCard(createRemindersPane(caregiver, patientBox)));

        tabs.getTabs().addAll(tabRel, tabMem, tabRem);
        page.getChildren().add(tabs);

        primaryStage.setTitle("Caregiver Dashboard - " + safeName(caregiver.getName()));
    }
    private VBox wrapCaregiverCard(Node content) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color:#D8CBAE;" +
                        "-fx-background-radius:18;" +
                        "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.12),8,0,0,3);"
        );
        card.getChildren().add(content);
        return card;
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

        TextField tfName = new TextField(); tfName.setPromptText("Name");
        TextField tfRel = new TextField(); tfRel.setPromptText("Relationship");
        TextField tfPhone = new TextField(); tfPhone.setPromptText("Phone");
        ChoiceBox<String> cbGender = new ChoiceBox<>(FXCollections.observableArrayList("", "Male", "Female"));
        cbGender.setValue("");
        TextField tfRelMediaPath = new TextField(); tfRelMediaPath.setPromptText("Media file path");
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
                Relative r = new Relative(
                        name,
                        rel.isEmpty()?"":rel,
                        "",
                        phone,
                        email,
                        (gender == null ? "" : gender),
                        address,
                        null,
                        birthday
                );
                try {
                    String path = tfRelMediaPath.getText().trim();
                    String type = cbRelMediaType.getValue();
                    String desc = tfRelMediaDesc.getText().trim();
                    if (!path.isEmpty() && type != null && !type.trim().isEmpty()) {
                        Media m = new Media(path, type, desc);
                        r.addMedia(m);
                        try {
                            if (type != null && type.equalsIgnoreCase("image") && (r.getPhotoPath() == null || r.getPhotoPath().trim().isEmpty())) {
                                r.setPhotoPath(path);
                            }
                        } catch (Exception ignoreSetPhoto) {
                        }
                    }
                } catch (Exception exMedia) {
                    status.setText("Relative added but media failed: " + exMedia.getMessage());
                }
                caregiver.addRelative(r);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                dumpPatientToConsole(caregiver.getPatient());
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
                syncPatientToAccounts(caregiver);
                refreshAssociateRelativesInUI(caregiver);
                status.setText("Deleted.");
                refresh.run();
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) { status.setText("Error: " + ex.getMessage()); }
        });

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
        ListView<Relative> relSelect = new ListView<>();
        relSelect.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Label status = new Label();
        Runnable refreshRelSelect = () -> {
            relSelect.getItems().clear();
            Patient p = caregiver.getPatient();
            if (p != null) relSelect.getItems().addAll(p.getRelatives());
        };
        refreshRelSelect.run();
        TextField tfMediaPath = new TextField(); tfMediaPath.setPromptText("Media file path");
        Button btnMemPick = new Button("Choose...");
        ChoiceBox<String> cbMediaType = new ChoiceBox<>(FXCollections.observableArrayList("image","audio","video","file")); cbMediaType.setValue("image");
        TextField tfMediaDesc = new TextField(); tfMediaDesc.setPromptText("Media description");
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
                Date dateVal = new Date();
                if (dp.getValue() != null) {
                    java.time.LocalDate ld = dp.getValue();
                    java.time.LocalDateTime ldt = ld.atStartOfDay();
                    dateVal = java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
                }
                List<Relative> selectedRels = new ArrayList<>();
                selectedRels.addAll(relSelect.getSelectionModel().getSelectedItems());
                List<Media> mediaForMemory = new ArrayList<>();
                mediaForMemory.addAll(memMediaObjects);
                try {
                    String curPath = tfMediaPath.getText().trim();
                    String curType = cbMediaType.getValue();
                    String curDesc = tfMediaDesc.getText().trim();
                    if (!curPath.isEmpty()) {
                        boolean exists = false;
                        for (Media _mm : mediaForMemory) if (_mm != null && curPath.equals(_mm.getMediaPath())) { exists = true; break; }
                        if (!exists) mediaForMemory.add(new Media(curPath, curType == null ? "file" : curType, curDesc));
                    }
                } catch (Exception ignore) { }
                Memory m = new Memory(java.util.UUID.randomUUID(), name, desc, dateVal, selectedRels, mediaForMemory);
                caregiver.addMemory(m);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                dumpPatientToConsole(caregiver.getPatient());
                refreshAssociateRelativesInUI(caregiver);
                tfName.clear(); tfMediaPath.clear(); tfMediaDesc.clear(); cbMediaType.setValue("image");
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

        GridPane formGrid = new GridPane();
        formGrid.setHgap(6); formGrid.setVgap(6);
        formGrid.add(new Label("Name:"), 0, 0); formGrid.add(tfName, 1, 0);
        formGrid.add(new Label("Description:"), 2, 0); formGrid.add(tfDesc, 3, 0);
        formGrid.add(new Label("Date:"), 0, 1); formGrid.add(dp, 1, 1);
        formGrid.add(new Label("Media path:"), 2, 1); formGrid.add(tfMediaPath, 3, 1); formGrid.add(btnMemPick, 0, 2);
        formGrid.add(new Label("Type:"), 0, 2); formGrid.add(cbMediaType, 1, 2);
        formGrid.add(new Label("Media desc:"), 2, 2); formGrid.add(tfMediaDesc, 3, 2);
        formGrid.add(new Label("Media list:"), 0, 3);
        formGrid.add(memMediaList, 1, 3, 3, 1);
        HBox mediaButtons = new HBox(6, btnAddMedia, btnRemoveMedia);
        formGrid.add(mediaButtons, 1, 4);
        HBox buttonsRow = new HBox(6, btnAdd);
        formGrid.add(buttonsRow, 0, 5, 4, 1);

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
        box.getChildren().addAll(lv, formGrid, btnDelete, status);
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
                        } catch (Exception ex) { }
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
                    stored.setName(p.getName());
                    stored.setPatientStage(p.getPatientStage());
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
            ArrayList<User> accounts = fh.loadAccounts();
            for (User u : accounts) if (u.getEmail() != null && u.getEmail().equals(email)) return;
            Patient p = new Patient(name, java.util.UUID.randomUUID(), email, pass, stage);
            fh.addAccount(p);
            try {
                caregiver.addPatient(p);
            } catch (Exception ex) {
            }
            fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
            syncPatientToAccounts(caregiver);
            updatePatientBox(patientBox, caregiver);
        }
    }

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
                ArrayList<User> accounts = fh.loadAccounts();
                for (User u : accounts) {
                    if (u instanceof Patient && ((Patient) u).getEmail() != null && ((Patient) u).getEmail().equals(p.getEmail())) {
                        ((Patient) u).setName(caregiver.getPatient().getName());
                        ((Patient) u).setPatientStage(caregiver.getPatient().getPatientStage());
                    }
                }
                fh.saveAccounts(accounts);
                fh.updateAccountByEmail(caregiver.getEmail(), caregiver);
                syncPatientToAccounts(caregiver);
                updatePatientBox(patientBox, caregiver);
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Error editing patient: " + ex.getMessage(), ButtonType.OK);
                a.showAndWait();
            }
        }
    }

    private void showSignUpCaregiverDialog() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Sign up - Caregiver");

        try {
            String logoPath = "file:/C:/Users/Mehrail Seddik10 24/IdeaProjects/OOP_Project/media/WhatsApp Image 2025-12-08 at 02.09.15_16f50d91.jpg";
            javafx.scene.image.Image lg = new javafx.scene.image.Image(logoPath);
            ImageView headerLogo = new ImageView(lg);
            headerLogo.setFitWidth(64);
            headerLogo.setPreserveRatio(true);
            dlg.getDialogPane().setGraphic(headerLogo);
        } catch (Exception ignore) { }

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

        for (javafx.scene.Node node : g.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill:#6b5146; -fx-font-weight:600;");
            }
        }
        tfName.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        tfEmail.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");
        pf.setStyle("-fx-border-color:#efe6dd; -fx-background-radius:6; -fx-border-radius:6; -fx-padding:6;");

        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.control.Button okBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dlg.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (okBtn != null) okBtn.setStyle("-fx-background-color: linear-gradient(#e6ddd6, #d1c6bd); -fx-text-fill: #2c2a29; -fx-background-radius:6; -fx-padding:6 14;");
        if (cancelBtn != null) cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill:#6b5146; -fx-padding:6 10; -fx-underline:true;");

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
            Caregiver c = new Caregiver(name,java.util.UUID.randomUUID(), email, pass);
            fh.addAccount(c);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Caregiver account created. You can now log in.", ButtonType.OK); a.showAndWait();
        }
    }

    private void showPatientView(Patient patient) {

        BorderPane root = new BorderPane();
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background: #E2DCC2; -fx-border-color: transparent;");
        root.setCenter(mainScroll);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);

        VBox page = new VBox(50);
        page.setPadding(new Insets(35));
        page.setStyle("-fx-background-color: #E2DCC2;");
        mainScroll.setContent(page);

        VBox headerBox = new VBox(20);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle(
                "-fx-background-color: #D8CBAE;" +
                        "-fx-background-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,3);"
        );

        HBox welcomeRow = new HBox();
        welcomeRow.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Hi, " + safeName(patient.getName()));
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #CFC1A3;" +
                        "-fx-border-radius: 16;" +
                        "-fx-padding: 6 16;" +
                        "-fx-font-weight: bold;"
        );
        logoutBtn.setOnAction(e -> primaryStage.setScene(loginScene));

        welcomeRow.getChildren().addAll(welcomeLabel, spacer, logoutBtn);

        Label stageLabel = new Label("Stage: " + safeString(patient.getPatientStage()));
        stageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A4A4A; -fx-font-weight: 500;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search memories, reminders, or relatives…");
        searchField.setMaxWidth(450);
        searchField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 12 18;" +
                        "-fx-border-color: #CFC1A3;" +
                        "-fx-border-radius: 20;" +
                        "-fx-font-size: 13px;"
        );

        headerBox.getChildren().addAll(welcomeRow, stageLabel, searchField);
        page.getChildren().add(headerBox);

        VBox remSection = new VBox(20);
        Label remTitle = new Label("Upcoming Reminders");
        remTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

        FlowPane remCards = new FlowPane(25, 25);
        remCards.setPrefWrapLength(950);
        remCards.setPadding(new Insets(10));
        remSection.getChildren().addAll(remTitle, remCards);
        page.getChildren().add(remSection);

        List<Node> allReminderCards = new ArrayList<>();
        List<Reminder> reminders = patient.getReminders() == null ? Collections.emptyList() : patient.getReminders();
        for (Reminder r : reminders) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(18));
            card.setPrefWidth(320);
            card.setStyle(
                    "-fx-background-color: #F0E1B8;" +
                            "-fx-background-radius: 18;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);"
            );

            Label rName = new Label(safeString(r.getName()));
            rName.setStyle("-fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

            Label rDesc = new Label(safeString(r.getDescription()));
            rDesc.setWrapText(true);
            rDesc.setStyle("-fx-text-fill: #4A4A4A;");

            Label rDate = new Label(r.getDate() != null ? r.getDate().toString() : "");
            rDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

            card.getChildren().addAll(rName, rDesc, rDate);
            remCards.getChildren().add(card);
            allReminderCards.add(card);
        }

        VBox memSection = new VBox(20);
        Label memTitle = new Label("Your Memories");
        memTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

        FlowPane memCards = new FlowPane(30, 25);
        memCards.setPrefWrapLength(950);
        memCards.setPadding(new Insets(10));
        memSection.getChildren().addAll(memTitle, memCards);
        page.getChildren().add(memSection);

        List<Node> allMemoryCards = new ArrayList<>();
        List<Memory> memories = patient.getMemories() == null ? Collections.emptyList() : patient.getMemories();
        for (Memory m : memories) {
            VBox card = new VBox(12);
            card.setPadding(new Insets(18));
            card.setPrefWidth(280);
            card.setStyle(
                    "-fx-background-color: #F0E1B8;" +
                            "-fx-background-radius: 18;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);"
            );
            card.setAlignment(Pos.CENTER);

            Node thumb = getMemoryThumbnail(m);
            card.getChildren().add(thumb);

            Label mName = new Label(safeString(m.getName()));
            mName.setStyle("-fx-font-weight: bold; -fx-text-fill: #3B3B3B;");
            mName.setAlignment(Pos.CENTER);

            Label mDesc = new Label(safeString(m.getDescription()));
            mDesc.setWrapText(true);
            mDesc.setStyle("-fx-text-fill: #4A4A4A;");
            mDesc.setAlignment(Pos.CENTER);
            mDesc.setTextAlignment(TextAlignment.CENTER);

            card.getChildren().addAll(mName, mDesc);
            card.setOnMouseClicked(e -> showMemoryDetailsDialog(m));

            memCards.getChildren().add(card);
            allMemoryCards.add(card);
        }

        VBox relSection = new VBox(20);
        Label relTitle = new Label("Closest Relatives");
        relTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

        FlowPane relCards = new FlowPane(25, 25);
        relCards.setPrefWrapLength(950);
        relCards.setPadding(new Insets(10));
        relSection.getChildren().addAll(relTitle, relCards);
        page.getChildren().add(relSection);

        List<Node> allRelativeCards = new ArrayList<>();
        List<Relative> relatives = patient.getRelatives() == null ? Collections.emptyList() : patient.getRelatives();
        for (Relative r : relatives) {
            VBox card = new VBox(12);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setPrefWidth(200);
            card.setStyle(
                    "-fx-background-color: #F0E1B8;" +
                            "-fx-background-radius: 18;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);"
            );

            ImageView iv = new ImageView();
            try {
                if (r.getPhotoPath() != null && new File(r.getPhotoPath()).exists()) {
                    Image img = new Image(new File(r.getPhotoPath()).toURI().toString(), 100, 100, true, true);
                    iv.setImage(img);
                }
            } catch (Exception ignored) {}
            iv.setFitWidth(100);
            iv.setFitHeight(100);
            iv.setClip(new Circle(50, 50, 50));
            card.getChildren().add(iv);

            Label rName = new Label(safeString(r.getName()));
            rName.setStyle("-fx-font-weight: bold; -fx-text-fill: #3B3B3B;");

            Label rRelation = new Label(safeString(r.getRelationship()));
            rRelation.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
            card.getChildren().addAll(rName, rRelation);

            card.setOnMouseClicked(e -> showRelativeDetailsDialog(r));

            relCards.getChildren().add(card);
            allRelativeCards.add(card);
        }

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            String q = newText.toLowerCase().trim();
            filterCards(allReminderCards, remCards, q);
            filterCards(allMemoryCards, memCards, q);
            filterCards(allRelativeCards, relCards, q);
        });

        primaryStage.setTitle("Patient Dashboard - " + safeName(patient.getName()));
    }

    private Node getMemoryThumbnail(Memory mem) {
        try {
            if (mem == null || mem.getMediaList() == null) {
                return makePlaceholder();
            }

            for (Object mmObj : mem.getMediaList()) {
                if (!(mmObj instanceof Media)) continue;

                Media mm = (Media) mmObj;
                String path = mm.getMediaPath();

                if (path != null) {
                    String p = path.toLowerCase();
                    if (p.endsWith(".png") || p.endsWith(".jpg") || p.endsWith(".jpeg")) {
                        File f = new File(path);
                        if (f.exists()) {
                            ImageView iv = new ImageView(new Image(f.toURI().toString(), 280, 160, true, true));
                            iv.setPreserveRatio(true);
                            return iv;
                        }
                    }
                }
            }

        } catch (Exception ignored) { }

        return makePlaceholder();
    }

    private Node makePlaceholder() {
        Label placeholder = new Label("No Image");
        placeholder.setPrefSize(280, 160);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #888; -fx-font-size: 14px; -fx-border-radius: 12; -fx-background-radius: 12;");
        return placeholder;
    }
    private void filterCards(List<Node> allCards, FlowPane container, String query) {
        container.getChildren().clear();
        for (Node card : allCards) {
            if (cardMatches(card, query)) container.getChildren().add(card);
        }
    }

    private boolean cardMatches(Node card, String query) {
        if (query.isEmpty()) return true;
        if (card instanceof VBox box) {
            for (Node n : box.getChildren()) {
                if (n instanceof Label lbl) {
                    if (lbl.getText().toLowerCase().contains(query)) return true;
                }
            }
        }
        return false;
    }

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

    @SuppressWarnings("unchecked")
    private void refreshAssociateRelativesInUI(Caregiver caregiver) {
        try {
            if (primaryStage == null) return;
            Scene sc = primaryStage.getScene();
            if (sc == null) return;
            javafx.scene.Parent root = sc.getRoot();
            if (root == null) return;
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
                                    } catch (Exception ignore) { }
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