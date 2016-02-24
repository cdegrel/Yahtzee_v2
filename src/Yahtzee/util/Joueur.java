package Yahtzee.util;

import Yahtzee.Main;
import Yahtzee.view.TabController;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class Joueur {
	protected int numJoueur;
	protected int scoreSomme;
	protected int scoreSpecial;
	protected int scoreTotal;
	protected TabController controller;
	private Label[][] labels_somme;
	private Label[][] labels_special;

	public Joueur(Tab tab_joueur, int numJoueur) {
		this.numJoueur = numJoueur;
		controller = Main.initTabLayout(tab_joueur, numJoueur);
		scoreSomme = 0;
		scoreSpecial = 0;
		scoreTotal = 0;
	}

	public TabController getController() {
		return controller;
	}

	public int getNumJoueur() {
		return numJoueur;
	}

	public Label[][] getLabels_somme() {
		return labels_somme;
	}

	public Label[][] getLabels_special() {
		return labels_special;
	}

	public void setLabels_somme(Label[][] labels_somme) {
		this.labels_somme = labels_somme;
	}

	public void setLabels_special(Label[][] labels_special) {
		this.labels_special = labels_special;
	}

	/**
	 * Vérifie si le label n'est pas joué
	 *
	 * @param row type int
	 *            ligne concernée (valeur absolue/+1)
	 * @param col type boolean
	 *            colonne concernée
	 *            false : tab_somme
	 *            true : tab_special
	 * @return boolean
	 */
	public boolean label_isNotPlayed(int row, boolean col) {
		if (!col) {
			return labels_somme[numJoueur][row - 1].getText().equals("...");
		} else {
			return labels_special[numJoueur][row - 1].getText().equals("...");
		}
	}

	public int getScoreSomme() {
		return scoreSomme;
	}

	public void setScoreSomme(int scoreSomme) {
		this.scoreSomme = scoreSomme;
	}

	public int getscoreSpecial() {
		return scoreSpecial;
	}

	public void setscoreSpecial(int scoreSpecial) {
		this.scoreSpecial = scoreSpecial;
	}

	public int getscoreTotal() {
		return scoreTotal;
	}

	public void setscoreTotal(int scoreTotal) {
		this.scoreTotal = scoreTotal;
	}

}
