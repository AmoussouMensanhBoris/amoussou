package org.example.jeudelavie;

import javafx.scene.paint.Color;

public class JeuDeLaVieModel {
    public int n = 25;
    public Color[][] grille = new Color[n][n];
    private final Color couleurMorte = Color.BEIGE;
    private final Color couleurVivante = Color.GREEN;
    public final Color[][] precedenteGrille = new Color[n][n];


    // Constructeur – initialisation de la grille avec la couleur morte
    public JeuDeLaVieModel() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grille[i][j] = couleurMorte;
            }
        }
    }

    // Méthode de calcul des voisins
    public int compterVoisin(Color[][] grille, int i, int j, int n) {
        int voisins = 0;
        // Haut
        if (grille[i][(j - 1 + n) % n] == couleurVivante) voisins++;
        // Bas
        if (grille[i][(j + 1) % n] == couleurVivante) voisins++;
        // Gauche
        if (grille[(i - 1 + n) % n][j] == couleurVivante) voisins++;
        // Droite
        if (grille[(i + 1) % n][j] == couleurVivante) voisins++;
        // Haut-Gauche
        if (grille[(i - 1 + n) % n][(j - 1 + n) % n] == couleurVivante) voisins++;
        // Haut-Droite
        if (grille[(i + 1) % n][(j - 1 + n) % n] == couleurVivante) voisins++;
        // Bas-Gauche
        if (grille[(i - 1 + n) % n][(j + 1) % n] == couleurVivante) voisins++;
        // Bas-Droite
        if (grille[(i + 1) % n][(j + 1) % n] == couleurVivante) voisins++;
        return voisins;
    }

    // Méthode pour compter les cellules vivantes
    public int celluleVivante(Color[][] grille, int n) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grille[i][j] == couleurVivante) {
                    count++;
                }
            }
        }
        return count;
    }


    // Accesseurs pour les couleurs
    public Color getCouleurMorte() {
        return couleurMorte;
    }
    public Color getCouleurVivante() {
        return couleurVivante;
    }

}
