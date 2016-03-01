package Yahtzee.util;

import Yahtzee.model.Model;
import javafx.scene.control.Tab;

import java.util.Arrays;

public class IA extends Joueur {

	public De de;
	public Model model;
	public IA(Tab tab, int numIA) {

		super(tab, numIA);
		de = new De();
	}

	public void jouer(){
		de.jette();
		Arrays.sort(de.DesGen);

		if(model.yahtzee(de.DesGen)==50){

			this.setScoreSomme(50);
			this.setscoreTotal(50);

		}else if(model.grandeSuite(de.DesGen)==40){

			this.setScoreSomme(50);
			this.setscoreTotal(50);

		}else if(model.petiteSuite(de.DesGen)==30){

			this.setScoreSomme(30);
			this.setscoreTotal(30);

		}else if(model.full(de.DesGen)==25){

			this.setScoreSomme(25);
			this.setscoreTotal(25);

		}else if(model.carre(de.DesGen)!=0){

			this.setScoreSomme(model.carre(de.DesGen));
			this.setscoreTotal(model.carre(de.DesGen));

		}else if(model.brelan(de.DesGen)!=0){

			this.setScoreSomme(model.carre(de.DesGen));
			this.setscoreTotal(model.carre(de.DesGen));

		}



	}


}