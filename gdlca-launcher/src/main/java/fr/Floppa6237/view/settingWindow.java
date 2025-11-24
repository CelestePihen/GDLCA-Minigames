package fr.Floppa6237.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class settingWindow {

    public void show() {

        Stage stage = new Stage();
        stage.setTitle("Paramètres");

        BorderPane root = new BorderPane();

        Scene scene = new Scene(root, 916, 528);
        stage.setScene(scene);
        stage.setResizable(false);

        scene.getStylesheets().add(getClass().getResource("/css/style_setting.css").toExternalForm());

        stage.show();

        /* ----------------------------------------
         *  LEFT BAR
         * ---------------------------------------- */

        //Left BAR
        VBox LeftBar = new VBox(2);
        LeftBar.setPrefWidth(219);
        LeftBar.setId("left-bar");
        LeftBar.setPadding(new Insets(10));
        LeftBar.setAlignment(Pos.CENTER);

        root.setLeft(LeftBar);

        //Bouton LeftBAR

        Button Compte = new Button("Compte");
        Button Langage = new Button("Langage");
        Button Interface = new Button("Interface");
        Button MAJ = new Button("Mise à Jour");
        ImageView loutre = new ImageView(getClass().getResource("/images/setting-pane/left-bar/loutre.png").toExternalForm());

        Compte.setId("left-bar-button");
        Langage.setId("left-bar-button");
        Interface.setId("left-bar-button");
        MAJ.setId("left-bar-button");


        Compte.setMaxWidth(Double.MAX_VALUE);
        Langage.setMaxWidth(Double.MAX_VALUE);
        Interface.setMaxWidth(Double.MAX_VALUE);
        MAJ.setMaxWidth(Double.MAX_VALUE);


        LeftBar.setFillWidth(true);

        LeftBar.getChildren().addAll(Compte, Langage, Interface, MAJ, loutre);

        /* ----------------------------------------
         *  BOTTOM BAR
         * ---------------------------------------- */

        //TODO
    }

}