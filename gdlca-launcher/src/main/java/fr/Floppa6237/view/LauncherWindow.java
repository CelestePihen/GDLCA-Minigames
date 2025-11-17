package fr.Floppa6237.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;



public class LauncherWindow extends Application {
    @Override
    public void start(Stage MainStage) {

        //Frenetre Principale
        BorderPane MainPane = new BorderPane();

        Scene scene = new Scene(MainPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        MainStage.setTitle("GDLCA Launcher");
        MainStage.setResizable(true);
        MainStage.setScene(scene);
        MainStage.show();

        //TopBar
        HBox TopBar = new HBox(75);
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
        HBox LeftBar = new HBox(75);
        LeftBar.setPrefWidth(150);
        LeftBar.setId("left-bar");
        LeftBar.setPadding(new Insets(10));
        LeftBar.setAlignment(Pos.CENTER);

        MainPane.setLeft(LeftBar);
    }

    public static void main(String[] args) {
        launch();
    }
}