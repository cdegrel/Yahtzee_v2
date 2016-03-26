package Yahtzee.util;

import Yahtzee.Main;
import Yahtzee.view.JoueurTabController;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.Serializable;

public class Joueur implements Serializable {
	private int numJoueur;
	private int scoreSomme;
	private int scoreSpecial;
	private int scoreTotal;
	private transient JoueurTabController controller;
	private transient Label[][] labels_somme;
	private transient Label[][] labels_special;
	public String[][] Sendlabels_somme;
	public String[][] Sendlabels_special;
	private int[] dernierCoup;

	public Joueur(Tab tab_joueur, int numJoueur, boolean ia) {
		this.numJoueur = numJoueur;
		if (!ia) controller = Main.initTabLayout(tab_joueur, numJoueur);
		scoreSomme = 0;
		scoreSpecial = 0;
		scoreTotal = 0;
	}

	public void appliLabelsToSend(String[][] Receive, boolean col) {
		Label[][] labels = !col ? labels_somme : labels_special;

		for (int i = 0; i < labels.length; i++) {
			for (int j = 0; j < labels[i].length; j++) {
				labels[i][j].setText(Receive[i][j]);
			}
		}
	}

	public void convertLabelsToSend(boolean col) {
		Label[][] labels = !col ? labels_somme : labels_special;
		String[][] send = !col ? Sendlabels_somme : Sendlabels_special;

		for (int i = 0; i < labels.length; i++) {
			for (int j = 0; j < labels[i].length; j++) {
				send[i][j] = labels[i][j].getText();
			}
		}
	}

	public void initLabelsToSend() {
		Sendlabels_somme = new String[labels_somme.length][9];
		Sendlabels_special = new String[labels_special.length][9];
	}

	public Joueur(Tab tab_joueur, int numJoueur) {
		this(tab_joueur, numJoueur, false);
	}

	public JoueurTabController getJoueurController() {
		return controller;
	}

	public boolean isIA() {
		return false;
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

	public int[] getDernierCoup() {
		return dernierCoup;
	}

	public void setDernierCoup(int[] dernierCoup) {
		if (dernierCoup != null) {
			this.dernierCoup = dernierCoup;
		}
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
