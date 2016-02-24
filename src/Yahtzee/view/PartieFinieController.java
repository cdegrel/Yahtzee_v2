package Yahtzee.view;

import Yahtzee.model.Model;
import Yahtzee.util.Joueur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PartieFinieController {

	@FXML
	private Label message;
	private Model model; // Modèle

	/**
	 * Appelé à la création du PopUp
	 *
	 * @param model type Model
	 */
	public void init_data(Model model) {
		this.model = model;
		gagnant();
	}

	/**
	 * Détermine le joueur avec le plus de points
	 * et l'affiche dans le message
	 * /!\ Cette méthode ne gère pas les ex aequo
	 */
	void gagnant() {
		int numJoueur = 0;
		int score = 0;

		for (Joueur joueur : model.getJoueurs()) {
			if (joueur.getscoreTotal() > score) {
				numJoueur = joueur.getNumJoueur() + 1;
				score = joueur.getscoreTotal();
			}
		}

		message.setText("Félicitation au Joueur " + numJoueur + " qui remporte cette partie avec " + score + " pts.");
	}
}
