package fr.Floppa6237.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class settingWindow {

    /* ----------------------------------------
     *  Variables globales
     * ---------------------------------------- */

    private Button selectedButton = null;
    /* ========================================================================
     *  FENÊTRE PRINCIPALE
     * ======================================================================== */
    public void show() {

        Stage stage = new Stage();
        stage.setTitle("Paramètres");

        BorderPane root = new BorderPane();

        Scene scene = new Scene(root, 916, 528);
        scene.getStylesheets().add(
                getClass().getResource("/css/style_setting.css").toExternalForm()
        );

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();


        /* ========================================================================
         *  LEFT BAR
         * ======================================================================== */
        VBox LeftBar = buildLeftBar();
        root.setLeft(LeftBar);


        /* ========================================================================
         *  BODY (Zone centrale)
         * ======================================================================== */

        // TODO : ajouter la body et dedans mettre en bas à droite les boutons de validations
    }



    /* ========================================================================
     *  LEFT BAR BUILDER
     * ======================================================================== */
    private VBox buildLeftBar() {

        VBox LeftBar = new VBox(2);
        LeftBar.setId("left-bar");
        LeftBar.setPrefWidth(219);
        LeftBar.setPadding(new Insets(10));
        LeftBar.setAlignment(Pos.CENTER);
        LeftBar.setFillWidth(true);

        // Boutons
        Button btnCompte    = createLeftButton("Compte");
        Button btnLangage   = createLeftButton("Langage");
        Button btnInterface = createLeftButton("Interface");
        Button btnMAJ       = createLeftButton("Mise à Jour");

        // Logo loutre
        ImageView loutre = new ImageView(
                getClass().getResource("/images/setting-pane/left-bar/loutre.png").toExternalForm()
        );

        // Ajout des éléments
        LeftBar.getChildren().addAll(
                btnCompte,
                btnLangage,
                btnInterface,
                btnMAJ,
                loutre
        );

        return LeftBar;
    }



    /* ========================================================================
     *  UTILITAIRES
     * ======================================================================== */

    /** Crée un bouton stylé pour la left bar */
    private Button createLeftButton(String text) {

        Button btn = new Button(text);
        btn.setId("left-bar-button");
        btn.setMaxWidth(Double.MAX_VALUE);

        return btn;
    }

    /* ========================================================================
     *  BODY BUILDER
     * ======================================================================== */

    private VBox buildBodyCompte(){
        VBox BodyBar = new VBox(2);
        BodyBar.setId("body-bar-compte");
        BodyBar.setPadding(new Insets(10));
        BodyBar.setAlignment(Pos.CENTER);
        BodyBar.setFillWidth(true);

        return BodyBar;
    }
}
