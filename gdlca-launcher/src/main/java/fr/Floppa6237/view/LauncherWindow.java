package fr.Floppa6237.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class LauncherWindow extends Application {
    @Override
    public void start(Stage MainStage) {

        //Frenetre Principale
        BorderPane MainPane = new BorderPane();

        Scene scene = new Scene(MainPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        MainStage.setTitle("GDLCA Launcher");
        MainStage.setResizable(false);
        MainStage.setScene(scene);
        MainStage.show();

        //TopBar
        HBox TopBar = new HBox(50);
        TopBar.setPrefHeight(75);
        TopBar.setId("top-bar");
        TopBar.setPadding(new Insets(10));
        TopBar.setAlignment(Pos.CENTER);

        Button AccueilButton = new Button("Accueil");
        AccueilButton.setId("top-bar-button");
        Button PatchNotesButton = new Button("Patch Notes");
        PatchNotesButton.setId("top-bar-button");
        Button InformationsButton = new Button("Informations");
        InformationsButton.setId("top-bar-button");
        Button SiteWebButton = new Button("Site Web");
        SiteWebButton.setId("top-bar-button");

        TopBar.getChildren().addAll(AccueilButton, PatchNotesButton, InformationsButton, SiteWebButton);

        MainPane.setTop(TopBar);

        //Left-Bar
        VBox LeftBar = new VBox(75);
        LeftBar.setPrefWidth(150);
        LeftBar.setId("left-bar");
        LeftBar.setPadding(new Insets(10));
        LeftBar.setAlignment(Pos.CENTER);

        //Minecraft de base
        Image minecraftLogo = new Image(getClass().getResource("/images/left-bar/minecraft.png").toExternalForm());
        ImageView logoView_minecraftLogo = new ImageView(minecraftLogo);
        logoView_minecraftLogo.setFitWidth(100);   // Taille (optionnel)
        logoView_minecraftLogo.setPreserveRatio(true);

        //GDLCA
        Image gdlcaLogo = new Image(getClass().getResource("/images/left-bar/gdlca.png").toExternalForm());
        ImageView logoView_gdlcaLogo = new ImageView(gdlcaLogo);
        logoView_gdlcaLogo.setFitWidth(100);
        logoView_gdlcaLogo.setPreserveRatio(true);

        //Elden RPG (MDR)
        Image eldenRPGLogo = new Image(getClass().getResource("/images/left-bar/gdlca.png").toExternalForm());
        ImageView logoView_eldenRPGLogo = new ImageView(eldenRPGLogo);
        logoView_eldenRPGLogo.setFitWidth(100);
        logoView_eldenRPGLogo.setPreserveRatio(true);

        LeftBar.getChildren().add(logoView_minecraftLogo);
        LeftBar.getChildren().add(logoView_gdlcaLogo);
        LeftBar.getChildren().add(logoView_eldenRPGLogo);

        MainPane.setLeft(LeftBar);

        // Body
        HBox BodyBar = new HBox();
        BodyBar.setId("body-bar");

        BodyBar.setPadding(new Insets(10));
        BodyBar.setAlignment(Pos.CENTER);

        BodyBar.setFillHeight(true);

        Image gdlcaScreen = new Image(
                getClass().getResource("/images/body/gdlca_screen.png").toExternalForm()
        );

        ImageView logoView_gdlca_screen = new ImageView(gdlcaScreen);
        logoView_gdlca_screen.setPreserveRatio(true);

        logoView_gdlca_screen.setFitWidth(1100);

        BodyBar.getChildren().add(logoView_gdlca_screen);

        MainPane.setCenter(BodyBar);
    }

    public static void main(String[] args) {
        launch();
    }
}