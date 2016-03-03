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
		listCoup = new ArrayList<>();//(liste comprenant toutes les combi
		listCoup.add("chance");
		listCoup.add("un");
		listCoup.add("deux");
		listCoup.add("trois");
		listCoup.add("quatre");
		listCoup.add("cinq");
		listCoup.add("six");
		listCoup.add("brelan");listCoup.add("carre");
		listCoup.add("full");listCoup.add("petiteSuite");
		listCoup.add("grandeSuite");listCoup.add("yahtzee");
		
	}

	public IATabController getIAController() {
		return controller;
	}

	public boolean isIA() {
		return true;
	}

	public void jouer() {

		de.jette();
		Arrays.sort(de.DesGen);
	//verif des coupd bon d'est le premier lancer
		if(listCoup.size()!=0) {//verif si la liste est vide ou pas

			if (listCoup.contains("yahtzee") && model.yahtzee(de.DesGen) == 50) {

				this.setScoreSomme(50);//question con mest est ce que ça sa marche pour que l'IA est un score qui s'affiche?
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


					if(de.DesGen[0] == 1&&//verif des 1
							de.DesGen[0] == de.DesGen[1]&&
							de.DesGen[1] == de.DesGen[2]&&
							de.DesGen[2] == de.DesGen[3]&&
							de.DesGen[3] == de.DesGen[4]){

							this.setScoreSomme(model.calculBasic(1,de.DesGen));
							this.setscoreTotal(model.calculBasic(1,de.DesGen));
							listCoup.remove(1);}

				if(de.DesGen[0] == 2&&//verif des 2
						de.DesGen[0] == de.DesGen[1]&&
						de.DesGen[1] == de.DesGen[2]&&
						de.DesGen[2] == de.DesGen[3]&&
						de.DesGen[3] == de.DesGen[4]){

					this.setScoreSomme(model.calculBasic(2,de.DesGen));
					this.setscoreTotal(model.calculBasic(2,de.DesGen));
					listCoup.remove(2);}

				if(de.DesGen[0] == 3&&//verif des 3
						de.DesGen[0] == de.DesGen[1]&&
						de.DesGen[1] == de.DesGen[2]&&
						de.DesGen[2] == de.DesGen[3]&&
						de.DesGen[3] == de.DesGen[4]){

					this.setScoreSomme(model.calculBasic(3,de.DesGen));
					this.setscoreTotal(model.calculBasic(3,de.DesGen));
					listCoup.remove(3);}

				if(de.DesGen[0] == 4&&//verif des 4
						de.DesGen[0] == de.DesGen[1]&&
						de.DesGen[1] == de.DesGen[2]&&
						de.DesGen[2] == de.DesGen[3]&&
						de.DesGen[3] == de.DesGen[4]){

					this.setScoreSomme(model.calculBasic(4,de.DesGen));
					this.setscoreTotal(model.calculBasic(4,de.DesGen));
					listCoup.remove(4);}
				if(de.DesGen[0] == 5&&//verif des 5
						de.DesGen[0] == de.DesGen[1]&&
						de.DesGen[1] == de.DesGen[2]&&
						de.DesGen[2] == de.DesGen[3]&&
						de.DesGen[3] == de.DesGen[4]){

					this.setScoreSomme(model.calculBasic(5,de.DesGen));
					this.setscoreTotal(model.calculBasic(5,de.DesGen));
					listCoup.remove(5);}

				if(de.DesGen[0] == 6&&//verif des 6
						de.DesGen[0] == de.DesGen[1]&&
						de.DesGen[1] == de.DesGen[2]&&
						de.DesGen[2] == de.DesGen[3]&&
						de.DesGen[3] == de.DesGen[4]){

					this.setScoreSomme(model.calculBasic(6,de.DesGen));
					this.setscoreTotal(model.calculBasic(6,de.DesGen));
					listCoup.remove(6);}

			}else{System.out.println("Ia ne sait plus quoi faire");}

		}
	}


}