package org.example.jeudelavie;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.Random;

public class MaGrilleView extends Application {

    public GridPane gridPane = new GridPane();
    public Label populationLabel = new Label("Population: 0");
    public Label labelGeneration = new Label("Génération: ");
    public Text numeroGeneration = new Text("0");
    public Timeline timeline;
    public boolean execution = false;
    public int compteur = 0;
    public Button buttonReset, buttonStart, buttonPause;
    // Références au modèle et au contrôleur
    public JeuDeLaVieModel model = new JeuDeLaVieModel();
    public MaGrilleController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Création des cellules
        for (int i = 0; i < model.n; i++) {
            for (int j = 0; j < model.n; j++){
                Rectangle cell = creerRectangle(i, j);
                gridPane.add(cell, j, i);
            }
        }
        // Création de la zone d'information
        HBox boxGeneration = creerInfo();

        // Contrôle des boutons (Start, Pause, Reset) via la méthode controlBouton()
        HBox hBoxBouton = controlBouton();

        // Assemblage de l'interface de la partie du jeu
        VBox vBox = new VBox(20);
        vBox.setPrefWidth(780);
        vBox.getChildren().addAll(creerTitre(), boxGeneration, gridPane, hBoxBouton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: pink");

        // interface de tout le projet
        HBox root = new HBox(30, vBox, creerDescription());
        root.setStyle("-fx-background-color: BEIGE");
        Scene scene = new Scene(root, 600, 600);
        stage.setTitle("Jeu de la vie");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        // Instanciation du contrôleur et liaison avec la vue et le modèle
        controller = new MaGrilleController(model, this);
    }

    // Méthode pour mettre à jour la grille
    public void MiseAJour(Color[][] grille, GridPane gridPane, int n) {
        Color[][] grilleSuivante = new Color[n][n];
        boolean stable = true;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int voisins = model.compterVoisin(grille, i, j, n);
                if (grille[i][j] == model.getCouleurVivante()) {
                    grilleSuivante[i][j] = (voisins < 2 || voisins > 3) ? model.getCouleurMorte() : model.getCouleurVivante();
                } else {
                    grilleSuivante[i][j] = (voisins == 3) ? model.getCouleurVivante() : model.getCouleurMorte();
                }
                if (!grilleSuivante[i][j].equals(grille[i][j])) {
                    stable = false;
                }
            }
        }

        if (stable) {
            timeline.stop();  // Arrêter la Timeline
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    grille[i][j] = grilleSuivante[i][j];
                    Rectangle cell = (Rectangle) gridPane.getChildren().get(i * n + j);
                    cell.setFill(grille[i][j]);
                }
            }
            int population = model.celluleVivante(grille, n);
            Platform.runLater(() -> populationLabel.setText("Population: " + population));
            compteur++;
            Platform.runLater(() -> numeroGeneration.setText(String.valueOf(compteur)));
        }
        for (int i = 0; i < n; i++) {
            System.arraycopy(grille[i], 0, model.precedenteGrille[i], 0, n);
        }
    }


    // Méthode pour créer un rectangle (cellule)
    public Rectangle creerRectangle(int i, int j) {
        Rectangle cell = new Rectangle(15, 15, model.grille[i][j]);
        cell.setStroke(Color.WHITE);
        final int row = i;
        final int col = j;
        cell.setOnMouseClicked(event -> {
            if (!execution) {
                if (model.grille[row][col] == model.getCouleurVivante()) {
                    model.grille[row][col] = model.getCouleurMorte();
                } else {
                    model.grille[row][col] = model.getCouleurVivante();
                }
                cell.setFill(model.grille[row][col]);
            }
        });
        return cell;
    }

    // Méthode pour créer le titre
    public Label creerTitre() {
        Label labelTitre = new Label("Jeu de la vie de John Conway");
        labelTitre.setFont(Font.font("serif", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 30));
        labelTitre.setAlignment(Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        return labelTitre;
    }


    // Méthode pour créer le ChoiceBox de sélection du motif
    public ChoiceBox<String> creerChoiceBox() {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        ObservableList<String> items = FXCollections.observableArrayList("Utilisateur", "Aléatoire");
        choiceBox.setValue("Utilisateur");
        choiceBox.setStyle("-fx-background-color: YELLOW");
        choiceBox.setItems(items);
        Random random = new Random();
        choiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {

            for (int i = 0; i < model.n; i++) {
                for (int j = 0; j < model.n; j++) {
                    model.grille[i][j] = model.getCouleurMorte();
                    Rectangle cell = (Rectangle) gridPane.getChildren().get(i * model.n + j);
                    cell.setFill(model.grille[i][j]);
                }
            }
            if (execution) {
                controller.getTimeline().stop();
                compteur = 0;
                execution = false;
            }
            switch (newVal) {
                case "Utilisateur":
                    // je l'ai par défaut (ligne 149).
                    break;
                case "Aléatoire":
                    for (int a = 0; a < model.n; a++) {
                        for (int b = 0; b < model.n; b++) {
                            if (random.nextBoolean()) {
                                model.grille[a][b] = model.getCouleurVivante();
                                Rectangle cell = (Rectangle) gridPane.getChildren().get(a * model.n + b);
                                cell.setFill(model.grille[a][b]);
                            }
                        }
                    }
                    break;
            }

        });
        return choiceBox;
    }

    // Méthode pour créer la zone d'information
    public HBox creerInfo() {
        HBox hboxGeneration = new HBox(5);
        hboxGeneration.getChildren().addAll(labelGeneration, numeroGeneration, populationLabel);
        hboxGeneration.setAlignment(Pos.CENTER);
        return hboxGeneration;
    }

    // Méthode pour créer la description du jeu
    public VBox creerDescription() {
        VBox description = new VBox(10);
        Label desc_Titre = new Label("Principe du Jeu");
        Label definition = new Label("C'est quoi le Jeu de la Vie ?");
        Label regle = new Label("Les règles du jeu");
        Text text_def = new Text("Le jeu de la vie (Game of life) est un automate cellulaire évoluant sur une grille composée de cases carrées appelées cellules qui ont un état binaire (1 pour vivante et 0 pour morte). Les cellules évoluent dans le temps en fonction de leur voisinage (chaque cellule a 8 cellules voisines), ce qui modifie la grille à chaque génération.");
        Text text_regle = new Text("** Une cellule morte possédant exactement trois cellules voisines vivantes devient vivante (elle naît) ;\n** Une cellule vivante ne possédant pas exactement deux ou trois cellules voisines vivantes meurt.");
        traiterObjet(desc_Titre, definition, regle, text_def, text_regle);
        //ajoute de l'image de conway
        URL imageURL = getClass().getResource("/org/example/jeudelavie/images/John_H_Conway.jpeg");
        ImageView imageView = new ImageView();
        if (imageURL != null) {
            Image image = new Image(imageURL.toExternalForm());
            imageView.setImage(image);
            imageView.setFitWidth(360);
            imageView.setFitHeight(230);
        } else {
            System.out.println("Image non trouvée !");
        }
        //methode de rediretion
        Hyperlink redirection = lienVersAuteur();
        description.getChildren().addAll(desc_Titre, definition, text_def, regle, text_regle, imageView, redirection);
        description.setAlignment(Pos.CENTER);
        return description;
    }

    //redirection vers le wikipédia de l'auteur
    public Hyperlink lienVersAuteur() {
        Hyperlink redirection = new Hyperlink("John Horton Conway");
        redirection.setStyle("-fx-font-size: 16px; -fx-text-fill: blue;");
        redirection.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://fr.wikipedia.org/wiki/John_Horton_Conway"));
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'ouvrir le lien !");
                alert.showAndWait();
            }
        });
        return redirection;
    }

    public static void traiterObjet(Object... objets) {
        for (Object objet : objets) {
            if (objet instanceof Label label) {
                label.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-text-fill: 'green';");
                label.setUnderline(true);
            } else if (objet instanceof Text text) {
                text.setStyle("-fx-font-size: 15px; -fx-font-family: 'Comic Sans MS';");
                text.setWrappingWidth(450);
            }
        }
    }


    // Méthode pour créer le HBox de contrôle boutons, slider et choiceBox
    public HBox controlBouton() {
        buttonStart = new Button("Start");
        buttonPause = new Button("Pause");
        buttonReset = new Button("Reset");
        URL startUrl = getClass().getResource("/org/example/jeudelavie/images/start.png");
        if (startUrl != null) {
            ImageView logoStart = new ImageView(startUrl.toString());
            logoStart.setFitWidth(30);
            logoStart.setFitHeight(30);
            logoStart.setPreserveRatio(true);
            buttonStart.setGraphic(logoStart);
        }
        buttonStart.setContentDisplay(ContentDisplay.RIGHT);
        buttonStart.setOnAction(event -> {
            timeline.play();
            execution = true;
        });

        URL pauseUrl = getClass().getResource("/org/example/jeudelavie/images/pause.png");
        if (pauseUrl != null) {
            ImageView logoPause = new ImageView(pauseUrl.toString());
            logoPause.setFitWidth(30);
            logoPause.setFitHeight(30);
            logoPause.setPreserveRatio(true);
            buttonPause.setGraphic(logoPause);
        }
        buttonPause.setContentDisplay(ContentDisplay.RIGHT);
        buttonPause.setOnAction(event -> timeline.pause());

        URL resetUrl = getClass().getResource("/org/example/jeudelavie/images/reset.png");
        if (resetUrl != null) {
            ImageView logoReset = new ImageView(resetUrl.toString());
            logoReset.setFitWidth(30);
            logoReset.setFitHeight(30);
            logoReset.setPreserveRatio(true);
            buttonReset.setGraphic(logoReset);
        }
        buttonReset.setContentDisplay(ContentDisplay.RIGHT);
        buttonReset.setOnAction(event -> {
            if (execution) {
                timeline.stop();
                execution = false;
            }
            for (int row = 0; row < model.n; row++) {
                for (int col = 0; col < model.n; col++) {
                    model.grille[row][col] = model.getCouleurMorte();
                    Rectangle cell = (Rectangle) gridPane.getChildren().get(row * model.n + col);
                    cell.setFill(model.grille[row][col]);
                }
            }
            compteur = 0;
            numeroGeneration.setText("0");
            populationLabel.setText("Population: " + 0);
        });

        HBox hBox = new HBox(20, creerChoiceBox(), buttonStart, buttonPause, buttonReset, creerSlider());
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    // Méthode pour créer le slider de vitesse
    public Slider creerSlider() {
        Slider slider = new Slider(1, 10, 1);
        slider.setShowTickLabels(true);
        slider.valueProperty().addListener((observableValue, ancien, nouveau) -> {
            double vitesse = nouveau.doubleValue();
            if (controller != null) {
                controller.getTimeline().setRate(vitesse);
            }
        });
        return slider;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
