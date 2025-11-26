package fr.Floppa6237.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class settingWindow {

    /* ----------------------------------------
     *  Variables globales
     * ---------------------------------------- */

    private BorderPane root;

    /* ========================================================================
     *  FENÊTRE PRINCIPALE
     * ======================================================================== */
    public void show() {

        Stage stage = new Stage();
        stage.setTitle("Paramètres");

        root = new BorderPane();

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

        VBox BodyBar = buildBodyCompte();
        root.setCenter(BodyBar);
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
        btnCompte.setOnAction(e -> loadComptePage());
        Button btnLangage   = createLeftButton("Langage");
        btnLangage.setOnAction(e -> loadLangagePage());
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
     *  LEFT BAR UTILITAIRES
     * ======================================================================== */

    /** Crée un bouton pour la left bar */
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
        VBox CompteBodyBar = new VBox(2);
        CompteBodyBar.setId("body-bar-compte");
        CompteBodyBar.setPadding(new Insets(10));
        CompteBodyBar.setAlignment(Pos.TOP_LEFT);
        CompteBodyBar.setSpacing(20);
        CompteBodyBar.setFillWidth(true);

        //Première section de paramètre : Compte connecté
        VBox compte_param1 = new VBox();
        compte_param1.setPadding(new Insets(2));
        compte_param1.setSpacing(1);


        Label compte_param1_title = new Label("Mon compte");
        compte_param1_title.setId("compte-param-titre");
        compte_param1_title.setAlignment(Pos.TOP_LEFT);

        //TODO Avec l'api Microsoft mettre le nom du comptedans le label
        Label compte_param1_text = new Label("Accéder à votre compte Microsoft en appuyant sur le bouton ci-dessous :");
        compte_param1_text.setId("compte-param-text");
        compte_param1_text.setAlignment(Pos.CENTER_LEFT);

        Button compte_param1_button = new Button("Compte Microsoft");
        compte_param1_button.setId("compte-param1-button");
        compte_param1_button.setMaxWidth(150);
        compte_param1_button.setAlignment(Pos.CENTER);

        compte_param1.getChildren().add(compte_param1_title);
        compte_param1.getChildren().add(compte_param1_text);
        compte_param1.getChildren().add(compte_param1_button);
        CompteBodyBar.getChildren().addAll(compte_param1);


        //Deuxieme section de paramètre : Se déconnecter
        VBox compte_param2 = new VBox();
        compte_param2.setPadding(new Insets(2));
        compte_param2.setSpacing(1);

        Label compte_param2_title = new Label("Déconnexion");
        compte_param2_title.setId("compte-param-titre");
        compte_param2_title.setAlignment(Pos.TOP_LEFT);

        Label compte_param2_text = new Label("Se déconnecter vous renverra à la page de connexion :");
        compte_param2_text.setId("compte-param-text");
        compte_param2_text.setAlignment(Pos.CENTER_LEFT);

        Button compte_param2_button = new Button("Se déconnecter");
        compte_param2_button.setId("compte-param2-button");
        compte_param2_button.setMaxWidth(150);
        compte_param2_button.setAlignment(Pos.CENTER);

        compte_param2.getChildren().add(compte_param2_title);
        compte_param2.getChildren().add(compte_param2_text);
        compte_param2.getChildren().add(compte_param2_button);
        CompteBodyBar.getChildren().addAll(compte_param2);

        return CompteBodyBar;
    }

    private VBox buildBodyLangage(){
        VBox LangageBodyBar = new VBox(2);
        LangageBodyBar.setId("body-bar-langage");
        LangageBodyBar.setPadding(new Insets(10));
        LangageBodyBar.setAlignment(Pos.TOP_LEFT);
        LangageBodyBar.setSpacing(20);
        LangageBodyBar.setFillWidth(true);

        //Première section de paramètre : Changer de langue
        VBox langage_param1 = new VBox();
        langage_param1.setPadding(new Insets(2));
        langage_param1.setSpacing(1);


        Label langage_param1_title = new Label("Choix de la langue");
        langage_param1_title.setId("langage-param-titre");
        langage_param1_title.setAlignment(Pos.TOP_LEFT);

        Label langage_param1_text = new Label("Change la langue du launcher :");
        langage_param1_text.setId("langage-param-text");
        langage_param1_text.setAlignment(Pos.CENTER_LEFT);

        //TODO faire les logos des pays pour changer la langue

        langage_param1.getChildren().add(langage_param1_title);
        langage_param1.getChildren().add(langage_param1_text);

        LangageBodyBar.getChildren().addAll(langage_param1);

        return LangageBodyBar;
    }

    /* ========================================================================
     *  Fonctions
     * ======================================================================== */

    private void loadComptePage() {
        VBox compteBody = buildBodyCompte();
        root.setCenter(compteBody);
    }

    private void loadLangagePage() {
        VBox langageBody = buildBodyLangage();
        root.setCenter(langageBody);
    }
}
