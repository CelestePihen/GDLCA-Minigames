package fr.Floppa6237.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LauncherWindow extends Application {

    /* ----------------------------------------
     *  Variables globales
     * ---------------------------------------- */
    private ImageView selectedLogo = null;


    /* ========================================================================
     *  MÉTHODE PRINCIPALE : START()
     * ======================================================================== */
    @Override
    public void start(Stage MainStage) {

        /* ---- Chargement des polices ---- */
        Font.loadFont(getClass().getResource("/font/Jersey10-Regular.ttf").toExternalForm(), 10);

        /* ---- Fenêtre principale ---- */
        BorderPane MainPane = new BorderPane();
        Scene scene = new Scene(MainPane, 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("/css/style_main.css").toExternalForm()
        );

        MainStage.setTitle("GDLCA Launcher");
        MainStage.setResizable(false);
        MainStage.setScene(scene);
        MainStage.show();


        /* ========================================================================
         *  TOP BAR
         * ======================================================================== */
        HBox TopBar = buildTopBar(MainPane);


        /* ========================================================================
         *  BODY (zone centrale)
         * ======================================================================== */
        HBox BodyBar = buildBody(MainPane);


        /* ========================================================================
         *  LEFT BAR
         * ======================================================================== */
        VBox LeftBar = buildLeftBar(BodyBar);
        MainPane.setLeft(LeftBar);
    }



    /* ========================================================================
     *  TOP BAR BUILDER
     * ======================================================================== */
    private HBox buildTopBar(BorderPane MainPane) {

        HBox TopBar = new HBox(50);
        TopBar.setId("top-bar");
        TopBar.setPadding(new Insets(10));
        TopBar.setPrefHeight(75);

        // Sous-sections
        HBox SectionCompte = new HBox(50);
        SectionCompte.setId("section-compte");
        SectionCompte.setAlignment(Pos.CENTER_LEFT);
        SectionCompte.setPrefWidth(296);

        HBox SectionJeu = new HBox(50);
        SectionJeu.setAlignment(Pos.CENTER);

        HBox ParametreGlobal = new HBox(50);
        ParametreGlobal.setAlignment(Pos.CENTER_RIGHT);

        // Boutons top bar
        Button AccueilButton = new Button("Accueil");
        Button PatchNotesButton = new Button("Patch Notes");
        Button InformationsButton = new Button("Informations");
        Button SiteWebButton = new Button("Site Web");

        AccueilButton.setId("top-bar-button");
        PatchNotesButton.setId("top-bar-button");
        InformationsButton.setId("top-bar-button");
        SiteWebButton.setId("top-bar-button");

        SectionJeu.getChildren().addAll(
                AccueilButton,
                PatchNotesButton,
                InformationsButton,
                SiteWebButton
        );

        // Icône paramètre
        ImageView logoParametre = createTopbarImage("/images/main-pane/top-bar/parametre.png");
        ParametreGlobal.getChildren().add(logoParametre);

        logoParametre.setOnMouseClicked(e -> new settingWindow().show());

        // Assembler la top bar
        TopBar.getChildren().addAll(
                SectionCompte,
                SectionJeu,
                ParametreGlobal
        );

        MainPane.setTop(TopBar);
        return TopBar;
    }



    /* ========================================================================
     *  BODY BUILDER
     * ======================================================================== */
    private HBox buildBody(BorderPane MainPane) {

        HBox BodyBar = new HBox();
        BodyBar.setId("body-bar");
        BodyBar.setPadding(new Insets(10));
        BodyBar.setAlignment(Pos.CENTER);
        MainPane.setCenter(BodyBar);

        // Charge par défaut l’écran GDLCA
        loadScreen(BodyBar, "/images/main-pane/body/gdlca_screen.png");

        return BodyBar;
    }



    /* ========================================================================
     *  LEFT BAR BUILDER
     * ======================================================================== */
    private VBox buildLeftBar(HBox BodyBar) {

        VBox LeftBar = new VBox(75);
        LeftBar.setId("left-bar");
        LeftBar.setPadding(new Insets(10));
        LeftBar.setAlignment(Pos.CENTER);
        LeftBar.setPrefWidth(150);

        // Logos
        ImageView logoMinecraft = createSidebarImage("/images/main-pane/left-bar/minecraft.png");
        ImageView logoGDLCA    = createSidebarImage("/images/main-pane/left-bar/gdlca.png");
        ImageView logoElden    = createSidebarImage("/images/main-pane/left-bar/eldenRPG.png");

        // Actions clic
        logoMinecraft.setOnMouseClicked(e -> {
            selectLogo(logoMinecraft);
            loadScreen(BodyBar, "/images/main-pane/body/minecraft_screen.png");
        });

        logoGDLCA.setOnMouseClicked(e -> {
            selectLogo(logoGDLCA);
            loadScreen(BodyBar, "/images/main-pane/body/gdlca_screen.png");
        });

        logoElden.setOnMouseClicked(e -> {
            selectLogo(logoElden);
            loadScreen(BodyBar, "/images/main-pane/body/eldenRPG_screen.png");
        });

        // Logo sélectionné au démarrage
        selectLogo(logoGDLCA);

        LeftBar.getChildren().addAll(
                logoMinecraft,
                logoGDLCA,
                logoElden
        );

        return LeftBar;
    }



    /* ========================================================================
     *  UTILITAIRES
     * ======================================================================== */

    /** Charge une image au centre avec coins arrondis */
    private void loadScreen(HBox BodyBar, String resourcePath) {

        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView view = new ImageView(img);

        view.setFitWidth(1100);
        view.setPreserveRatio(true);

        Rectangle clip = new Rectangle(1100, 620);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        view.setClip(clip);

        BodyBar.getChildren().setAll(view);
    }


    /** Création d’un logo pour la sidebar */
    private ImageView createSidebarImage(String resourcePath) {

        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView view = new ImageView(img);

        view.setFitWidth(100);
        view.setPreserveRatio(true);
        view.setId("left-bar-image");

        // Opacité par défaut
        view.setOpacity(0.5);

        // Hover
        view.setOnMouseEntered(e -> {
            if (view != selectedLogo) view.setOpacity(0.8);
        });
        view.setOnMouseExited(e -> {
            if (view != selectedLogo) view.setOpacity(0.5);
        });

        return view;
    }


    /** Création des icônes top bar */
    private ImageView createTopbarImage(String resourcePath) {

        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView view = new ImageView(img);

        view.setPreserveRatio(true);
        view.setOpacity(0.6);
        view.setId("top-bar-image");

        view.setOnMouseEntered(e -> view.setOpacity(0.9));
        view.setOnMouseExited(e -> view.setOpacity(0.7));

        return view;
    }


    /** Gère le logo sélectionné */
    private void selectLogo(ImageView clickedLogo) {

        if (selectedLogo != null) {
            selectedLogo.setOpacity(0.5);
        }

        selectedLogo = clickedLogo;
        selectedLogo.setOpacity(1.0);
    }



    public static void main(String[] args) {
        launch();
    }
}
