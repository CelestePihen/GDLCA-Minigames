package fr.Floppa6237.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LauncherWindow extends Application {

    // Logo sélectionné
    private ImageView selectedLogo = null;

    @Override
    public void start(Stage MainStage) {

        /* ----------------------------------------
         *  Fenêtre principale
         * ---------------------------------------- */
        BorderPane MainPane = new BorderPane();

        Scene scene = new Scene(MainPane, 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );

        MainStage.setTitle("GDLCA Launcher");
        MainStage.setResizable(false);
        MainStage.setScene(scene);
        MainStage.show();


        /* ----------------------------------------
         *  TOP BAR
         * ---------------------------------------- */
        HBox TopBar = new HBox(50);
        TopBar.setPrefHeight(75);
        TopBar.setId("top-bar");
        TopBar.setPadding(new Insets(10));

        HBox SectionCompte = new HBox(50);
        SectionCompte.setId("section-compte");
        SectionCompte.setAlignment(Pos.CENTER_LEFT);
        SectionCompte.setPrefWidth(296);

        HBox SectionJeu = new HBox(50);
        SectionJeu.setAlignment(Pos.CENTER);


        HBox ParametreGlobal = new HBox(50);
        ParametreGlobal.setAlignment(Pos.CENTER_RIGHT);



        Button AccueilButton = new Button("Accueil");
        Button PatchNotesButton = new Button("Patch Notes");
        Button InformationsButton = new Button("Informations");
        Button SiteWebButton = new Button("Site Web");

        AccueilButton.setId("top-bar-button");
        PatchNotesButton.setId("top-bar-button");
        InformationsButton.setId("top-bar-button");
        SiteWebButton.setId("top-bar-button");
        ImageView logoParametre = createTopbarImage("/images/top-bar/parametre.png");

        SectionJeu.getChildren().addAll(
                AccueilButton,
                PatchNotesButton,
                InformationsButton,
                SiteWebButton
        );

        ParametreGlobal.getChildren().addAll(
                logoParametre
        );

        TopBar.getChildren().addAll(
                SectionCompte,
                SectionJeu,
                ParametreGlobal
        );

        MainPane.setTop(TopBar);


        /* ----------------------------------------
         *  BODY (zone centrale)
         * ---------------------------------------- */
        HBox BodyBar = new HBox();
        BodyBar.setId("body-bar");

        BodyBar.setPadding(new Insets(10));
        BodyBar.setAlignment(Pos.CENTER);

        MainPane.setCenter(BodyBar);


        /* ----------------------------------------
         *  MÉTHODES DE CHANGEMENT D'ÉCRAN
         * ---------------------------------------- */

        Runnable loadGDLCA = () -> {
            Image img = new Image(
                    getClass().getResource("/images/body/gdlca_screen.png").toExternalForm()
            );
            ImageView view = new ImageView(img);
            view.setFitWidth(1100);
            view.setPreserveRatio(true);
            BodyBar.getChildren().setAll(view);
        };

        Runnable loadMinecraft = () -> {
            Image img = new Image(
                    getClass().getResource("/images/body/minecraft_screen.png").toExternalForm()
            );
            ImageView view = new ImageView(img);
            view.setFitWidth(1100);
            view.setPreserveRatio(true);
            BodyBar.getChildren().setAll(view);
        };

        Runnable loadElden = () -> {
            Image img = new Image(
                    getClass().getResource("/images/body/eldenRPG_screen.png").toExternalForm()
            );
            ImageView view = new ImageView(img);
            view.setFitWidth(1100);
            view.setPreserveRatio(true);
            BodyBar.getChildren().setAll(view);
        };

        // Chargement par défaut
        loadGDLCA.run();


        /* ----------------------------------------
         *  LEFT BAR
         * ---------------------------------------- */
        VBox LeftBar = new VBox(75);
        LeftBar.setPrefWidth(150);
        LeftBar.setAlignment(Pos.CENTER);
        LeftBar.setPadding(new Insets(10));
        LeftBar.setId("left-bar");


        // Logos
        ImageView logoMinecraft = createSidebarImage("/images/left-bar/minecraft.png");
        ImageView logoGDLCA    = createSidebarImage("/images/left-bar/gdlca.png");
        ImageView logoElden    = createSidebarImage("/images/left-bar/eldenRPG.png");

        // Actions
        logoMinecraft.setOnMouseClicked(e -> {
            selectLogo(logoMinecraft);
            loadMinecraft.run();
        });

        logoGDLCA.setOnMouseClicked(e -> {
            selectLogo(logoGDLCA);
            loadGDLCA.run();
        });

        logoElden.setOnMouseClicked(e -> {
            selectLogo(logoElden);
            loadElden.run();
        });

        // Par défaut : GDLCA sélectionné
        selectLogo(logoGDLCA);

        LeftBar.getChildren().addAll(
                logoMinecraft,
                logoGDLCA,
                logoElden
        );

        MainPane.setLeft(LeftBar);
    }


    /* ----------------------------------------
     *  Méthode utilitaire pour les logos
     * ---------------------------------------- */
    private ImageView createSidebarImage(String resourcePath) {
        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView view = new ImageView(img);

        view.setFitWidth(100);
        view.setPreserveRatio(true);
        view.setId("left-bar-image");

        // Opacité par défaut
        view.setOpacity(0.5);

        // Animation de survol
        view.setOnMouseEntered(e -> {
            if (view != selectedLogo)
                view.setOpacity(0.8);
        });

        view.setOnMouseExited(e -> {
            if (view != selectedLogo)
                view.setOpacity(0.5);
        });

        return view;
    }

    private ImageView createTopbarImage(String resourcePath) {
        Image img = new Image(getClass().getResource(resourcePath).toExternalForm());
        ImageView view = new ImageView(img);

        view.setPreserveRatio(true);
        view.setId("top-bar-image");

        // Opacité par défaut
        view.setOpacity(0.6);

        // Animation de survol
        view.setOnMouseEntered(e -> {
            if (view != selectedLogo)
                view.setOpacity(0.9);
        });

        view.setOnMouseExited(e -> {
            if (view != selectedLogo)
                view.setOpacity(0.7);
        });

        return view;
    }



    /* ----------------------------------------
     *  Sélection visuelle d'un logo
     * ---------------------------------------- */
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
