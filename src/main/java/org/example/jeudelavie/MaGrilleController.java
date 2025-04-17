package org.example.jeudelavie;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class MaGrilleController {
    private final JeuDeLaVieModel model;
    private final MaGrilleView view;
    private final Timeline timeline;

    public MaGrilleController(JeuDeLaVieModel model, MaGrilleView view) {
        this.model = model;
        this.view = view;
        timeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> Jeu()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        view.timeline = timeline;
    }

    public void Jeu() {
        view.MiseAJour(model.grille, view.gridPane, model.n);
    }

    public Timeline getTimeline() {
        return timeline;
    }
}
