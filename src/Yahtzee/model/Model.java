package Yahtzee.model;

import Yahtzee.util.De;
import Yahtzee.util.Joueur;
import javafx.scene.control.ToggleButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class Model {
	protected ArrayList<Joueur> joueurs;
	protected De Des;
	int joueurJoue;

	public Model() {
		joueurs = new ArrayList<>();
		Des = new De();
		initJoueurJoue();
	}

	public De getDes() {
		return Des;
	}

	public int getNbJoueurs() {
		return joueurs.size();
	}

	public ArrayList<Joueur> getJoueurs() {
		return joueurs;
	}

	public void deleteAllJoueurs() {
		joueurs.clear();
	}

	public int getJoueurJoue() {
		return joueurJoue;
	}

	public void initJoueurJoue() {
		joueurJoue = 0;
	}

	public void joueurJoueNext() {
		joueurJoue++;
		if (joueurJoue >= getNbJoueurs()) {
			initJoueurJoue();
		}
	}

	public int joueSomme(int valeur, ToggleButton[] des) {
		int nbDes = 0;
		for (ToggleButton de : des) {
			if (de.getText().equals(Integer.toString(valeur))) {
				nbDes++;
			}
		}
		return nbDes * valeur;
	}

	public int joueSpecial(String valeur, ToggleButton[] des) {

		int[] des_convert = new int[5];
		int i = 0;

		for (ToggleButton de : des) {
			des_convert[i] = Integer.parseInt(de.getText());
			i++;
		}

		try {
			Method method = getClass().getDeclaredMethod(valeur, int[].class);
			return (int) method.invoke(this, new Object[]{des_convert});
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int brelan(int t[]) {
		int result = -1;
		Arrays.sort(t);

		for (int i = 0; i <= 2; i++) {

			if (t[i] == t[i + 1] && t[i + 1] == t[i + 2]) {

				result = (t[0] + t[1] + t[2] + t[3] + t[4]);
				break;
			} else {
				result = 0;
			}
		}
		return result;
	}

	public int carre(int t[]) {
		int result = -1;
		Arrays.sort(t);

		for (int i = 0; i <= 1; i++) {

			if (t[i] == t[1 + 1] && t[i + 1] == t[i + 2] && t[i + 2] == t[i + 3]) {
				result = (t[0] + t[1] + t[2] + t[3] + t[4]);
				break;
			} else {
				result = 0;
			}
		}
		return result;
	}

	public int full(int t[]) {
		Arrays.sort(t);

		if (t[0] == t[1] && t[1] == t[2] && t[3] == t[4] ||//brelan + pair
				t[0] == t[1] && t[2] == t[3] && t[3] == t[4]) {//paire+brelan
			return 25;
		} else {
			return 0;
		}
	}

	public int petiteSuite(int t[]) {
		int result = 0;

		for (int i = 0; i < t.length - 1; i++) {
			if (t[i] == t[i + 1]) {
				t[i + 1] = 8;
			}
		}

		Arrays.sort(t);

		for (int i = 0; i < 2; i++) {
			if (t[i] == t[i + 1] - 1 && t[i + 1] == t[i + 2] - 1 && t[i + 2] == t[i + 3] - 1) {
				result = 30;
				break;
			} else {
				result = 0;
			}
		}
		return result;
	}

	public int grandeSuite(int t[]) {
		int result = -1;
		Arrays.sort(t);

		for (int i = 0; i < 1; i++) {
			if (t[i] == t[i + 1] - 1 && t[i + 1] == t[i + 2] - 1 && t[i + 2] == t[i + 3] - 1 && t[i + 3] == t[i + 4] - 1) {
				result = 40;
				break;
			} else {
				result = 0;
			}
		}
		return result;
	}

	public int yahtzee(int t[]) {
		int i = 0;

		if (t[i] == t[i + 1] && t[i + 1] == t[i + 2] && t[i + 2] == t[i + 3] && t[i + 3] == t[i + 4]) {
			return 50;
		} else {
			return 0;
		}
	}

	public int chance(int t[]) {
		int somme = 0;

		for (int p : t) {
			somme = somme + p;
		}
		return somme;
	}

}
