package Yahtzee.util;

import Yahtzee.Main;
import Yahtzee.model.Model;
import Yahtzee.view.IATabController;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.Arrays;

public class IA extends Joueur {

	public De de;
	public Model model;
	protected IATabController controller;
	protected ArrayList<String> listCoup;

	public IA(Tab tab, int numIA) {
		super(tab, numIA, true);
		controller = Main.initTabIALayout(tab, numIA);
		listCoup = new ArrayList<>();
		listCoup.add("chance");
		listCoup.add("un");
		listCoup.add("deux");
		listCoup.add("trois");
		listCoup.add("quatre");
		listCoup.add("cinq");
		listCoup.add("six");
		listCoup.add("brelan");listCoup.add("carre");
		listCoup.add("full");listCoup.add("petiteSuite");
		listCoup.add("grandeSuite");listCoup.add("yahtzee");listCoup.add("chance");



	}

	public IATabController getIAController() {
		return controller;
	}

	public boolean isIA() {
		return true;
	}

	public void jouer() {
		int score=0;
		int nouvScore=0;
		int indice = 0;

		de.jette();
		Arrays.sort(de.DesGen);

		if(listCoup.size()!=0) {

			if (listCoup.contains("yahtzee") && model.yahtzee(de.DesGen) == 50) {

				this.setScoreSomme(50);
				this.setscoreTotal(50);
				listCoup.remove("yahtzee");

			} else if (listCoup.contains("grandeSuite") && model.grandeSuite(de.DesGen) == 40) {

				this.setScoreSomme(50);
				this.setscoreTotal(50);
				listCoup.remove("grandeSuite");

			} else if (listCoup.contains("petiteSuite") && model.petiteSuite(de.DesGen) == 30) {

				this.setScoreSomme(30);
				this.setscoreTotal(30);
				listCoup.remove("petiteSuite");

			} else if (listCoup.contains("full") && model.full(de.DesGen) == 25) {

				this.setScoreSomme(25);
				this.setscoreTotal(25);
				listCoup.remove("full");

			} else if (listCoup.contains("carre") && model.carre(de.DesGen) != 0) {

				this.setScoreSomme(model.carre(de.DesGen));
				this.setscoreTotal(model.carre(de.DesGen));
				listCoup.remove("carre");

			} else if (listCoup.contains("brelan") && model.brelan(de.DesGen) != 0) {

				this.setScoreSomme(model.carre(de.DesGen));
				this.setscoreTotal(model.carre(de.DesGen));
				listCoup.remove("brelan");

			} else if(listCoup.contains("un")||listCoup.contains("deux")||listCoup.contains("trois")//il me faut la methode pour le calcule du score
					||listCoup.contains("quatre")||listCoup.contains("cinq")||listCoup.contains("six")){

				for (int i = 0; i < de.DesGen.length; i++) {

					nouvScore = de.DesGen[i];

						if(nouvScore>score){
							score = nouvScore;
							indice = i;
						}

				}
				listCoup.remove(indice);

			}else{System.out.println("Ia ne sait plus quoi faire");}

		}
	}


}