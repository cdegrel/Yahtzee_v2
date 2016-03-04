package Yahtzee.view;

import Yahtzee.Main;
import Yahtzee.model.Model;
import Yahtzee.util.Joueur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Arrays;

public class IATabController {

	@FXML
	private GridPane tab_somme = new GridPane();        // Grille contenant boutons/labels sommes
	@FXML
	private GridPane tab_special = new GridPane();      // Grille contenant boutons/labels spéciaux
	private Button[] Bso;                               // Tableau contenant les accès aux boutons Sommes
	private Button[] Bsp;                               // Tableau contenant les accès aux boutons Spéciaux
	@FXML
	private Label Total_final_pts;                      // Label du score final
	private Model model;                                // Modèle
	private int numJoueur;                              // Numéro du joueur
	private Joueur joueur;                              // Raccourci du joueur depuis modèle
	private InterfaceController interfaceController;    // Contrôleur maître
	protected ArrayList<String> listCoup;
	/*@FXML
	void initialize() {
	}*/

	/**
	 * Appelé à la création du joueur
	 *
	 * @param model     type Model
	 * @param numJoueur type int
	 *                  numéro du joueur
	 */
	public void init_data(Model model, int numJoueur, InterfaceController interfaceController) {
		this.model = model;
		this.numJoueur = numJoueur;
		this.interfaceController = interfaceController;
		Bso = arrayGenerate_buttons(tab_somme, false);
		Bsp = arrayGenerate_buttons(tab_special, true);
		if (numJoueur != 0) disableAllButtons();
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

	/**
	 * Appelé après la création de tous les joueurs
	 */
	public void init_data_after_JoueurList() {
		joueur = model.getJoueurs().get(numJoueur);
		joueur.setLabels_somme(createLabel(tab_somme, false, model.getNbJoueurs()));
		joueur.setLabels_special(createLabel(tab_special, true, model.getNbJoueurs()));
	}

	/**
	 * ActionEvent appelé lors d'une interaction avec
	 * un bouton somme ou spécial
	 * Automatisme du jeu
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void ActionEvent(ActionEvent event) {
		String[] butPressed = ((Button) event.getSource()).getId().split("_");
		boolean col = !butPressed[0].equals("Bso");
		int row = Integer.parseInt(butPressed[1]);

		if (model.getDes().getLancer() != 0 && joueur.label_isNotPlayed(row, col)) {
			int pts;
			if (!col) {
				pts = model.joueSomme(row, interfaceController.getDes());
			} else {
				String[] func = new String[]{"brelan", "carre", "full", "petiteSuite", "grandeSuite", "yahtzee", "chance"};
				pts = model.joueSpecial(func[row - 1], interfaceController.getDes());
			}

			verif_yahtzee100();
			define_pts(pts, row, col);

			if (!col) {
				Somme_ScoreAndSousTotal();
			} else {
				Special_SousTotal();
			}

			Total_score();
			disableAllButtons();

			if (!verif_partieFinie()) {
				nextJoueur();
			}
		}
	}

	/**
	 * Donne la main au joueur suivant
	 * - Réinitialise les dés
	 * - Réactive les boutons d'actions (non joués)
	 * - Bascule la vue sur sa grille
	 */
	void nextJoueur() {
		interfaceController.init_des();
		model.joueurJoueNext();
		if (!model.getJoueurs().get(model.getJoueurJoue()).isIA()) {
			model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().activeNotPlayedButtons();
		}
		interfaceController.basculeTab(model.getJoueurJoue());
	}

	/**
	 * Vérifie si la partie est terminée
	 * et affiche le PopUp de fin en conséquence
	 *
	 * @return type boolean
	 */
	boolean verif_partieFinie() {
		for (int i = 0; i < joueur.getLabels_somme()[numJoueur].length - 3; i++) {
			if (joueur.label_isNotPlayed(i + 1, false)) {
				return false;
			}
		}

		for (int i = 0; i < joueur.getLabels_special()[numJoueur].length - 2; i++) {
			if (joueur.label_isNotPlayed(i + 1, true)) {
				return false;
			}
		}

		if (numJoueur == model.getNbJoueurs() - 1) {
			interfaceController.fin_des();
			Main.PopUp_partieFinie(model);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Mise à jour du score Total
	 * (calcul/sauvegarde/affichage)
	 */
	void Total_score() {
		joueur.setscoreTotal(joueur.getScoreSomme() + joueur.getscoreSpecial());
		Total_final_pts.setText(Integer.toString(joueur.getscoreTotal()) + " pts");
	}

	/**
	 * Calcul est met à jour en conséquence
	 * le sous-total de la partie spécial
	 */
	void Special_SousTotal() {
		int i = 0, score = 0;
		for (Label label : joueur.getLabels_special()[numJoueur]) {
			if (!joueur.label_isNotPlayed(i + 1, true) && i != 8) {
				score = score + Integer.parseInt(label.getText());
			} else if (i == 8) {
				define_pts(score, 9, true);
				joueur.setscoreSpecial(score);
			}
			i++;
		}
	}

	/**
	 * Calcul est met à jour en conséquence le score
	 * et sous-total de la partie somme
	 * Appel la vérification de la prime de 35 (verif_prime35())
	 */
	void Somme_ScoreAndSousTotal() {
		int i = 0, score = 0;
		for (Label label : joueur.getLabels_somme()[numJoueur]) {
			if (!joueur.label_isNotPlayed(i + 1, false) && i != 6 && i != 8) {
				score = score + Integer.parseInt(label.getText());
			} else if (i == 6) {
				define_pts(score, 7, false);
				verif_prime35(score);
			} else if (i == 8) {
				define_pts(score, 9, false);
				joueur.setScoreSomme(score);
			}
			i++;
		}
	}

	/**
	 * Accorde une prime (Yahtzee) de 100 si :
	 * - la case Yahtzee est déjà remplie
	 * - 5 dés identiques ont été de nouveau tirés
	 */
	void verif_yahtzee100() {
		if (joueur.label_isNotPlayed(8, true) && !joueur.label_isNotPlayed(6, true)
				&& !joueur.getLabels_special()[numJoueur][5].getText().equals("0")) {

			if (model.joueSpecial("yahtzee", interfaceController.getDes()) > 0) {
				define_pts(100, 8, true);
			}
		}
	}

	/**
	 * Accorde une prime de 35 si le score est supér/égal à 63pts
	 *
	 * @param score type int
	 */
	void verif_prime35(int score) {
		if (joueur.label_isNotPlayed(8, false) && score >= 63) {
			define_pts(35, 8, false);
		}
	}

	/**
	 * Définit le score du joueur dans SA grille et CELLE des autres
	 *
	 * @param pts type int
	 *            score à insérer
	 * @param row type int
	 *            ligne concernée (valeur absolue/+1)
	 * @param col type boolean
	 *            colonne concernée
	 *            false : tab_somme
	 *            true : tab_special
	 */
	void define_pts(int pts, int row, boolean col) {
		for (Joueur joueur : model.getJoueurs()) {
			if (!col) {
				joueur.getLabels_somme()[numJoueur][row - 1].setText(Integer.toString(pts));
			} else {
				joueur.getLabels_special()[numJoueur][row - 1].setText(Integer.toString(pts));
			}
		}
	}

	/**
	 * Active les boutons sommes/spéciaux encore non joués
	 */
	void activeNotPlayedButtons() {
		int i = 1;
		for (Button but : Bso) {
			if (joueur.label_isNotPlayed(i, false)) {
				but.setDisable(false);
			}
			i++;
		}

		i = 1;
		for (Button but : Bsp) {
			if (joueur.label_isNotPlayed(i, true)) {
				but.setDisable(false);
			}
			i++;
		}
	}

	/**
	 * Désactive tous les boutons sommes/spéciaux du joueur
	 */
	void disableAllButtons() {
		for (Button but : Bso) {
			but.setDisable(true);
		}

		for (Button but : Bsp) {
			but.setDisable(true);
		}
	}

	/**
	 * Insert dans les colonnes prévues, les labels destinés à accueillir les différents scores
	 * (score de CE joueur et également CEUX des autres)
	 *
	 * @param PaneHBox type GridPane
	 *                 Dans lequel ajouter les labels
	 * @param spe      type boolean
	 *                 false : pour 9 lignes max(incl) (requis pour tab_somme)
	 *                 true : pour 10 lignes max(incl) (requis pour tab_special)
	 * @return Label[][] associés
	 */
	Label[][] createLabel(GridPane PaneHBox, boolean spe, int nbJoueur) {
		int col = 1;
		int row_max = !spe ? 9 : 10;
		int i;
		Label[][] labels = new Label[nbJoueur][9];

		for (int j = 0; j < nbJoueur; j++) {
			i = 0;
			for (Node node : PaneHBox.getChildren()) {
				if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row_max) {
					break;
				}
				if (GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == col) {
					labels[j][i] = new Label("...");
					labels[j][i].getStyleClass().add("results_pts" + (numJoueur == j ? "" : "_autre"));
					((HBox) node).getChildren().add(labels[j][i]);
					i++;
				}
			}
		}
		return labels;
	}

	/**
	 * Récupère les boutons de sommes/spéciaux du joueur et les rassembles dans un tableau
	 *
	 * @param PaneHBox type GridPane
	 *                 Dans lequel récupérer les boutons
	 * @param spe      type boolean
	 *                 false : pour 6 lignes max(incl) (requis pour tab_somme)
	 *                 true : pour 7 lignes max(incl) (requis pour tab_special)
	 * @return Button[]
	 */
	Button[] arrayGenerate_buttons(GridPane PaneHBox, boolean spe) {
		//int col = 0; Différent car en JavaFX, par défaut la cellule 0 0 == null null
		int row_max = !spe ? 6 : 7;
		int i = 0;

		Button[] buttons = new Button[!spe ? 6 : 7];

		for (Node node : PaneHBox.getChildren()) {
			if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row_max) {
				break;
			}
			if (GridPane.getColumnIndex(node) == null) {
				buttons[i] = (Button) node;
				i++;
			}
		}
		return buttons;
	}
	public void jouer() {

		model.getDes().jette();

		Arrays.sort(model.getDes().getDe());
		//verif des coupd bon d'est le premier lancer
		if(listCoup.size()!=0) {//verif si la liste est vide ou pas

			if (listCoup.contains("yahtzee") && model.yahtzee(model.getDes().getDe()) == 50) {

				define_pts(50,6,true);
				listCoup.remove("yahtzee");//on retire de la liste

			} else if (listCoup.contains("grandeSuite") && model.grandeSuite(model.getDes().getDe()) == 40) {

				define_pts(40,5,true);
				listCoup.remove("grandeSuite");

			} else if (listCoup.contains("petiteSuite") && model.petiteSuite(model.getDes().getDe()) == 30) {

				define_pts(30,4,true);
				listCoup.remove("petiteSuite");

			} else if (listCoup.contains("full") && model.full(model.getDes().getDe()) == 25) {

				define_pts(25,3,true);
				listCoup.remove("full");

			} else if (listCoup.contains("carre") && model.carre(model.getDes().getDe()) != 0) {

				define_pts(model.carre(model.getDes().getDe()),2,true);
				listCoup.remove("carre");

			} else if (listCoup.contains("brelan") && model.brelan(model.getDes().getDe()) != 0) {

				define_pts(model.brelan(model.getDes().getDe()),1,true);
				listCoup.remove("brelan");

			} else if(listCoup.contains("un")||listCoup.contains("deux")||listCoup.contains("trois")//il me faut la methode pour le calcule du score
					||listCoup.contains("quatre")||listCoup.contains("cinq")||listCoup.contains("six")){


				if(model.getDes().getSortie(0) == 1&&//verif des 1
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(3,model.getDes().getDe()),1,false);
					listCoup.remove(1);}

				if(model.getDes().getSortie(0) == 2&&//verif des 2
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(2,model.getDes().getDe()),2,false);
					listCoup.remove(2);}

				if(model.getDes().getSortie(0) == 3&&//verif des 3
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(3,model.getDes().getDe()),3,false);
					listCoup.remove(3);}

				if(model.getDes().getSortie(0) == 4&&//verif des 4
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(4,model.getDes().getDe()),4,false);
					listCoup.remove(4);}

				if(model.getDes().getSortie(0) == 5&&//verif des 5
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(4,model.getDes().getDe()),5,false);
					listCoup.remove(5);}

				if(model.getDes().getSortie(0) == 6&&//verif des 6
						model.getDes().getSortie(0) == model.getDes().getSortie(1)&&
						model.getDes().getSortie(1) == model.getDes().getSortie(2)&&
						model.getDes().getSortie(2) == model.getDes().getSortie(3)&&
						model.getDes().getSortie(3) == model.getDes().getSortie(4)){

					define_pts(model.calculBasic(6,model.getDes().getDe()),6,false);
					listCoup.remove(6);}

			}else{System.out.println("Ia ne sait plus quoi faire");}

		}
	}


}
