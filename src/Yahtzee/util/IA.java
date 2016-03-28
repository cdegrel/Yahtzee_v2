package Yahtzee.util;

import Yahtzee.Main;
import Yahtzee.model.Model;
import Yahtzee.view.IATabController;
import javafx.scene.control.Tab;

public class IA extends Joueur {

	public De de;
	public Model model;
	private IATabController controller;


	public IA(Tab tab, int numIA, Model model) {
		super(tab, numIA, true);
		this.model = model;
		controller = Main.initTabIALayout(tab, numIA);
	}

	public IATabController getIAController() {
		return controller;
	}

	public boolean isIA() {
		return true;
	}

	public void choixdeIA(int[] deIA, int[] choixIA) {
		//Recherche les combinaisons et vérifie si elles ont déjà été validé
		for (int i = 0; i < 6; i++) {
			int deidentique = 0;
			for (int j = 0; j < 5; j++) {
				if ((i + 1) == deIA[j]) {
					deidentique = deidentique + deIA[j];
				}
			}
			choixIA[i] = deidentique;
		}

		String[] func = new String[]{"brelan", "carre", "full", "petiteSuite", "grandeSuite", "yahtzee", "chance"};
		for (int i = 0; i < func.length; i++) {
			choixIA[i + 6] = model.joueSpecialIA(func[i], deIA);
		}
	}

	public int[] valeuruniquede(int[] scoreIAvalide, int[] scorezero, int[] deIA) {
		boolean valeurunique = true;
		int valeuruniqueselection = 0;

		for (int i = 6; i < 13; i++) {
			if (scoreIAvalide[i] == 0 && scorezero[i] == 0) {
				valeurunique = false;
			}
		}

		if (valeurunique) {
			for (int i = 5; i >= 0; i--) {
				if (valeuruniqueselection == 0 && scoreIAvalide[i] == 0 && scorezero[i] == 0) {
					valeuruniqueselection = i + 1;
				}
			}
			System.out.println("Valeur unique sélection " + valeuruniqueselection);
			for (int i = 0; i < 5; i++) {
				if (deIA[i] != valeuruniqueselection) {
					deIA[i] = model.getDes().getDes()[i];
				}
			}
		}
		return deIA;
	}

	public int[] rangedeintermediaire(int[] deIA) {
		for (int i = 0; i < 4; i++) {
			if (deIA[i] == deIA[i + 1]) {

				int intermediaire = deIA[i];
				System.arraycopy(deIA, i + 1, deIA, i, 4 - i);
				deIA[4] = intermediaire;
			}
		}
		return deIA;
	}

}