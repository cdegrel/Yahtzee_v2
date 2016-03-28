package Yahtzee.view;

import Yahtzee.Main;
import Yahtzee.model.Model;
import Yahtzee.util.IA;
import Yahtzee.util.Joueur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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
	private int numIA;                                  // Numéro du joueur
	private IA ia;                                      // Raccourci du joueur depuis modèle
	private InterfaceController interfaceController;    // Contrôleur maître

	private int validerChoixIA;
	private int deIA[] = new int[5];
	private int choixIA[] = new int[13];
	private int scoreIAvalide[] = new int[13];
	private int scorezero[] = new int[13];
	private int deInterval[] = new int[6];

	/*@FXML
	void initialize() {
	}*/

	/**
	 * Appelé à la création du joueur
	 *
	 * @param model type Model
	 * @param numIA type int
	 *              numéro du joueur
	 */
	public void init_data(Model model, int numIA, InterfaceController interfaceController) {
		System.out.println("MODE IA");
		this.model = model;
		this.numIA = numIA;
		this.interfaceController = interfaceController;
		Bso = arrayGenerate_buttons(tab_somme, false);
		Bsp = arrayGenerate_buttons(tab_special, true);
		if (numIA != 0) disableAllButtons();
	}

	/**
	 * Appelé après la création de tous les joueurs
	 */
	void init_data_after_JoueurList() {
		ia = (IA) model.getJoueurs().get(numIA);
		ia.setLabels_somme(createLabel(tab_somme, false, model.getNbJoueurs()));
		ia.setLabels_special(createLabel(tab_special, true, model.getNbJoueurs()));
	}

	/**
	 * ActionEvent appelé lors d'une interaction avec
	 * un bouton somme ou spécial
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void ActionEvent(ActionEvent event) {
	}

	/**
	 * Automatisme du jeu
	 */
	void tourIA() {
		System.out.print("-------- Tour IA --------");
		jouer();
		nextJoueur();
	}

	/**
	 * Donne la main au joueur suivant
	 * - Réinitialise les dés
	 * - Réactive les boutons d'actions (non joués)
	 * - Bascule la vue sur sa grille
	 */

	private void nextJoueur() {
		interfaceController.init_des();
		model.joueurJoueNext();
		model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().activeNotPlayedButtons();
		interfaceController.basculeTab(model.getJoueurJoue());
	}

	/**
	 * Vérifie si la partie est terminée
	 * et affiche le PopUp de fin en conséquence
	 *
	 * @return type boolean
	 */
	private boolean verif_partieFinie() {
		for (int i = 0; i < ia.getLabels_somme()[numIA].length - 3; i++) {
			if (ia.label_isNotPlayed(i + 1, false)) {
				return false;
			}
		}

		for (int i = 0; i < ia.getLabels_special()[numIA].length - 2; i++) {
			if (ia.label_isNotPlayed(i + 1, true)) {
				return false;
			}
		}

		if (numIA == model.getNbJoueurs() - 1) {
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
	private void Total_score() {
		ia.setscoreTotal(ia.getScoreSomme() + ia.getscoreSpecial());
		Total_final_pts.setText(Integer.toString(ia.getscoreTotal()) + " pts");
	}

	/**
	 * Calcul est met à jour en conséquence
	 * le sous-total de la partie spécial
	 */
	private void Special_SousTotal() {
		int i = 0, score = 0;
		for (Label label : ia.getLabels_special()[numIA]) {
			if (!ia.label_isNotPlayed(i + 1, true) && i != 8) {
				score = score + Integer.parseInt(label.getText());
			} else if (i == 8) {
				define_pts(score, 9, true);
				ia.setscoreSpecial(score);
			}
			i++;
		}
	}

	/**
	 * Calcul est met à jour en conséquence le score
	 * et sous-total de la partie somme
	 * Appel la vérification de la prime de 35 (verif_prime35())
	 */
	private void Somme_ScoreAndSousTotal() {
		int i = 0, score = 0;
		for (Label label : ia.getLabels_somme()[numIA]) {
			if (!ia.label_isNotPlayed(i + 1, false) && i != 6 && i != 8) {
				score = score + Integer.parseInt(label.getText());
			} else if (i == 6) {
				define_pts(score, 7, false);
				verif_prime35(score);
			} else if (i == 8) {
				define_pts(score, 9, false);
				ia.setScoreSomme(score);
			}
			i++;
		}
	}

	/**
	 * Accorde une prime (Yahtzee) de 100 si :
	 * - la case Yahtzee est déjà remplie
	 * - 5 dés identiques ont été de nouveau tirés
	 */
	private void verif_yahtzee100() {
		if (ia.label_isNotPlayed(8, true) && !ia.label_isNotPlayed(6, true)
				&& !ia.getLabels_special()[numIA][5].getText().equals("0")) {

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
	private void verif_prime35(int score) {
		if (ia.label_isNotPlayed(8, false) && score >= 63) {
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
	private void define_pts(int pts, int row, boolean col) {
		for (Joueur joueur : model.getJoueurs()) {
			if (!col) {
				joueur.getLabels_somme()[numIA][row - 1].setText(Integer.toString(pts));
			} else {
				joueur.getLabels_special()[numIA][row - 1].setText(Integer.toString(pts));
			}
		}
	}

	/**
	 * Applique un style css particulier au label désigné
	 *
	 * @param row type int
	 *            ligne concernée (valeur absolue/+1)
	 * @param col type boolean
	 *            colonne concernée
	 *            false : tab_somme
	 *            true : tab_special
	 */
	private void color_dernierCoup(int row, boolean col) {
		ia.setDernierCoup(new int[]{row, col ? 1 : 0});
		for (Joueur _joueur : model.getJoueurs()) {
			for (int i = 0; i < 7; i++) {
				_joueur.getLabels_somme()[numIA][i].getStyleClass().remove("coup_joue");
				_joueur.getLabels_special()[numIA][i].getStyleClass().remove("coup_joue");
			}
		}
		for (Joueur _joueur : model.getJoueurs()) {
			(!col ? _joueur.getLabels_somme() : _joueur.getLabels_special())[numIA][row - 1].getStyleClass().add("coup_joue");
		}
	}

	/**
	 * Active les boutons sommes/spéciaux encore non joués
	 */
	/*void activeNotPlayedButtons() {
		int i = 1;
		for (Button but : Bso) {
			if (ia.label_isNotPlayed(i, false)) {
				but.setDisable(false);
			}
			i++;
		}

		i = 1;
		for (Button but : Bsp) {
			if (ia.label_isNotPlayed(i, true)) {
				but.setDisable(false);
			}
			i++;
		}
	}*/

	/**
	 * Désactive tous les boutons sommes/spéciaux du joueur
	 */
	private void disableAllButtons() {
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
	@SuppressWarnings("Duplicates")
	private Label[][] createLabel(GridPane PaneHBox, boolean spe, int nbJoueur) {
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
					labels[j][i].getStyleClass().add("results_pts" + (numIA == j ? "" : "_autre"));
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
	@SuppressWarnings("Duplicates")
	private Button[] arrayGenerate_buttons(GridPane PaneHBox, boolean spe) {
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

	@SuppressWarnings("Duplicates")
	private void jouer() {
		System.out.println();
		model.getDes().jette();
		Arrays.sort(model.getDes().getDes());
		//Fonctionnement de l'IA visible e, détail dans le terminal

		model.getDes().jette();

		//Lance les dés et les tries
		System.arraycopy(model.getDes().getDes(), 0, deIA, 0, 5);
		Arrays.sort(deIA);

		for (int number : deIA) {
			System.out.print(number + "/");
		}
		System.out.println(" dés IA trié");

		Arrays.fill(choixIA, 0);

		while (model.getDes().getLancer() < 2) {

			model.getDes().jette();
			model.getDes().incremLanceNum();
			ia.choixdeIA(deIA, choixIA);

			if (choixIA[10] != 0) {
				System.out.println("Grande Suite");
				if (scoreIAvalide[10] == 0 && scorezero[10] == 0) {                        // si Grande suite pas fait alors enregistre Grande Suite
					System.out.println("Enregistre Grande Suite");

				} else if (scoreIAvalide[9] == 0 && scorezero[9] == 0) {                        // si Petite suite pas fait alors enregistre
					System.out.println("Enregistre Petite Suite");

				} else {
					System.out.println("Pas de possibilité");
					System.arraycopy(model.getDes().getDes(), 0, deIA, 0, 4);
				}

			} else if (choixIA[9] != 0) {
				System.out.println("Petite Suite");
				if (scoreIAvalide[10] == 0 && scorezero[10] == 0) {                        // si Grande suite pas fait alors relance dé
					System.out.println("Test Grande Suite");
					ia.rangedeintermediaire(deIA);
					if (deIA[0] != deIA[1] - 1) {
						deIA[0] = model.getDes().getDes()[0];
					} else {
						deIA[4] = model.getDes().getDes()[4];
					}

				} else if (scoreIAvalide[9] == 0 && scorezero[9] == 0) {                        // si Petite suite pas fait alors enregistre
					System.out.println("Enregistre Petite Suite");

				} else {
					System.out.println("Pas de possibilité");
					System.arraycopy(model.getDes().getDes(), 0, deIA, 0, 4);
				}

			} else if (choixIA[11] != 0) {  //si Yahtzee
				System.out.println("Yahtzee");

				if (scoreIAvalide[11] == 0 && scorezero[11] == 0) {  // si yahtzee pas fait alors enregistrer yahtzee
					System.out.println("Enregistre Yahtzee");

				} else if (scoreIAvalide[7] == 0 && scorezero[7] == 0) { // si carre pas fait alors enregistrer carre
					System.out.println("Enregistre Carre");

				} else if (scoreIAvalide[6] == 0 && scorezero[6] == 0) { // si brelan pas fait alors enregistrer brelan
					System.out.println("Enregistre Brelan");

				} else if (scoreIAvalide[8] == 0 && scorezero[8] == 0) {  //si full pas fait alors tente full
					System.out.println("Relance deux dés tente Full");
					deIA[0] = model.getDes().getDes()[0];
					deIA[1] = model.getDes().getDes()[1];

				} else if (scoreIAvalide[deIA[2]] == 0) { // si valeur unique pas fait alors enregistre
					System.out.println("Enregistre valeur dé : " + deIA[2]);
				} else {
					System.out.println("Pas de possibilité");
				}

			} else if (choixIA[7] != 0) {    // si carre
				System.out.println("Carre");

				if (scoreIAvalide[11] == 0 && scorezero[11] == 0) {  // si yahtzee pas fait alors rejeter un dé
					//rejeter unique dé différent
					System.out.println("Rejeter un dé pour Yahtzee");

					if (scoreIAvalide[deIA[2] - 1] != 0 && (scoreIAvalide[7] != 0 || scorezero[7] == 0)) {
						System.arraycopy(model.getDes().getDes(), 0, deIA, 0, 5);
					} else if (deIA[0] != deIA[1]) {
						deIA[0] = model.getDes().getDes()[0];
					} else {
						deIA[4] = model.getDes().getDes()[4];
					}

				} else if (scoreIAvalide[7] == 0 && scorezero[7] == 0) { // si carre pas fait alors enregistrer carre
					if (choixIA[7] >= 13) {
						System.out.println("Enregistre Carre");

					} else if (scoreIAvalide[deIA[2] - 1] == 0) {
						System.out.println("Enregistre " + (deIA[2] - 1));
						choixIA[deIA[2] - 1] = deIA[2] * 4;
					}

				} else if (scoreIAvalide[8] == 0 && scorezero[8] == 0) {        //si full pas fait alors tente full
					System.out.println("Relance un dé tente Full");
					deIA[2] = model.getDes().getDes()[2];

				} else if (scoreIAvalide[6] == 0 && scorezero[6] == 0) { // si brelan pas fait alors enregistrer brelan
					System.out.println("Enregistre Brelan");

				} else if (scoreIAvalide[deIA[2] - 1] == 0) { // si valeur unique pas fait alors enregistre
					if (deIA[0] != deIA[2]) {
						deIA[0] = model.getDes().getDes()[0];
					} else {
						deIA[4] = model.getDes().getDes()[4];
					}
				} else {
					System.out.println("Pas de possibilité");
				}

			} else if (choixIA[8] != 0) {    // si full
				System.out.println("Full");

				if (scoreIAvalide[8] == 0 && scorezero[8] == 0) {  //si full pas fait alors enregistre full
					System.out.println("Enregistre Full");

				} else if ((scoreIAvalide[11] == 0 && scorezero[11] == 0) || (scoreIAvalide[7] == 0 && scorezero[7] == 0)) {
					// si yahtzee ou carré pas fait alors rejeter dés
					//rejeter unique dé différent
					System.out.println("Rejeter deux dés pour Yahtzee");

					if (deIA[1] != deIA[2]) {
						deIA[0] = model.getDes().getDes()[0];
						deIA[1] = model.getDes().getDes()[1];
					} else {
						deIA[3] = model.getDes().getDes()[3];
						deIA[4] = model.getDes().getDes()[4];
					}

				} else if (scoreIAvalide[6] == 0 && scorezero[6] == 0) { // si brelan pas fait alors enregistrer brelan
					System.out.println("Enregistre Brelan");

				} else if (scoreIAvalide[deIA[2] - 1] == 0) { // si valeur unique pas fait alors enregistre
					ia.valeuruniquede(scoreIAvalide, scorezero, deIA);
				} else {
					System.out.println("Pas de possibilité");
				}

			} else if (choixIA[6] != 0) { // si brelan
				System.out.println("Brelan");

				if (scoreIAvalide[11] == 0 && scorezero[11] == 0) {  // si yahtzee pas fait alors rejeter un dé
					//rejeter unique dé différent
					System.out.println("Rejeter deux dés pour Yahtzee");
					for (int j = 0; j < 5; j++) {
						if (deIA[j] != deIA[2]) {
							deIA[j] = model.getDes().getDes()[j];
						}
					}

				} else if (scoreIAvalide[7] == 0 && scorezero[7] == 0) { // si carre pas fait alors enregistrer carre
					System.out.println("Rejeter deux dés pour Carre");
					for (int j = 0; j < 5; j++) {
						if (deIA[j] != deIA[2]) {
							deIA[j] = model.getDes().getDes()[j];
						}
					}

				} else if (scoreIAvalide[8] == 0 && scorezero[8] == 0) {  //si full pas fait alors enregistre full
					System.out.println("Enregistre Full");

				} else if (scoreIAvalide[6] == 0 && scorezero[6] == 0) { // si brelan pas fait alors enregistrer brelan
					System.out.println("Enregistre Brelan");

				} else if (scoreIAvalide[deIA[2] - 1] == 0) { // si valeur unique pas fait alors enregistre
					ia.valeuruniquede(scoreIAvalide, scorezero, deIA);

				} else {
					System.out.println("Pas de possibilité");
				}

			} else {    //si aucune combinaison possible
				ia.rangedeintermediaire(deIA);

				boolean suitepossible = false;
				boolean gsuitepossible = false;
				boolean fullpossible = false;
				int nombredouble = 0;
				int nombredouble2 = 0;

				// Test si suite et grande suite SERONT possible
				for (int i = 0; i < 2; i++) {
					if (deIA[i] == deIA[i + 1] - 1 && deIA[i + 1] == deIA[i + 2] - 1 && deIA[i + 2] == deIA[i + 3] - 1) {
						gsuitepossible = true;
					}
				}
				if (deIA[0] == 1 && deIA[1] == 2 && deIA[2] == 3 && deIA[3] == 5 && deIA[4] == 6) {
					gsuitepossible = true;
				}
				if (deIA[0] == 1 && deIA[1] == 2 && deIA[2] == 4 && deIA[3] == 5 && deIA[4] == 6) {
					gsuitepossible = true;
				}
				for (int i = 0; i < 3; i++) {
					if (deIA[i] == deIA[i + 1] - 1 && deIA[i + 1] == deIA[i + 2] - 1) {
						suitepossible = true;
					}
				}

				Arrays.sort(deIA);
				for (int i = 0; i < 4; i++) {
					if (deIA[i] == deIA[i + 1]) {
						nombredouble = deIA[i];
					}
				}
				for (int i = 0; i < 4; i++) {
					if (deIA[i] == deIA[i + 1] && deIA[i] != nombredouble) {
						nombredouble2 = deIA[i];
					}
				}

				System.out.println("Nombre double = " + nombredouble + " et " + nombredouble2);
				if (nombredouble != 0 && nombredouble2 != 0 && scoreIAvalide[8] == 0 && scorezero[8] == 0) {
					fullpossible = true;
				}

				if (fullpossible && (scoreIAvalide[8] != 0 || scorezero[8] == 1)) {
					Arrays.sort(deIA);
					for (int i = 0; i < 5; i++) {
						if (deIA[i] == nombredouble && scoreIAvalide[nombredouble - 1] != 0) {
							deIA[i] = model.getDes().getDes()[i];
						} else if (deIA[i] == nombredouble2 && scoreIAvalide[nombredouble2 - 1] != 0) {
							deIA[i] = model.getDes().getDes()[i];
						}
					}

				} else if (fullpossible) {   //test full
					Arrays.sort(deIA);
					System.out.println("Rejete dé Full possible");

					for (int i = 0; i < 5; i++) {
						if (scoreIAvalide[11] != 0 && scoreIAvalide[8] != 0 && scoreIAvalide[6] != 0 && deIA[nombredouble] != 0) {
							deIA[i] = model.getDes().getDes()[i];
						} else if (scoreIAvalide[11] != 0 && scoreIAvalide[8] != 0 && scoreIAvalide[6] != 0 && deIA[nombredouble2] != 0) {
							deIA[i] = model.getDes().getDes()[i];
						}
						if (deIA[i] != nombredouble && deIA[i] != nombredouble2) {
							deIA[i] = model.getDes().getDes()[i];
						}
					}

				} else if (gsuitepossible && scoreIAvalide[10] == 0 && scorezero[10] == 0) {       // test grande suite
					System.out.println("Grande Suite possibe");

					if (deIA[0] != deIA[1] - 1) {
						deIA[0] = model.getDes().getDes()[0];
					} else {
						deIA[4] = model.getDes().getDes()[4];
					}

				} else if (suitepossible && scoreIAvalide[9] == 0 && scorezero[9] == 0 && scoreIAvalide[10] != 0 && scorezero[10] == 0) {        //test suite ou grande suite
					System.out.println("Suite possibe");

					if ((deIA[0] == deIA[1] - 1) && (deIA[1] == deIA[2] - 1)) {
						deIA[3] = model.getDes().getDes()[3];
						deIA[4] = model.getDes().getDes()[4];
					} else if ((deIA[1] == deIA[2] - 1) && (deIA[2] == deIA[3] - 1)) {
						deIA[0] = model.getDes().getDes()[0];
						deIA[4] = model.getDes().getDes()[4];
					} else {
						deIA[0] = model.getDes().getDes()[0];
						deIA[1] = model.getDes().getDes()[1];
					}

				} else if (nombredouble != 0) {
					Arrays.sort(deIA);
					System.out.println("Relance car rien !");
					ia.valeuruniquede(scoreIAvalide, scorezero, deIA);

					if (scoreIAvalide[nombredouble - 1] != 0 && scorezero[nombredouble - 1] == 0 && scoreIAvalide[11] == 0 && scorezero[11] == 0 && scoreIAvalide[7] == 0 && scorezero[7] == 0
							&& scoreIAvalide[6] == 0 && scorezero[6] == 0 && scoreIAvalide[8] == 0 && scorezero[8] == 0) {
						System.arraycopy(model.getDes().getDes(), 0, deIA, 0, 5);

					} else {
						for (int i = 0; i < 5; i++) {
							if (deIA[i] != nombredouble) {
								deIA[i] = model.getDes().getDes()[i];
							}
						}
					}

				} else if (scoreIAvalide[10] != 0 && scoreIAvalide[9] != 0) {
					System.out.println("Rien à faire");
				}
			}

			ia.choixdeIA(deIA, choixIA);
			for (int number : deIA) {
				System.out.print(number + "/");
			}
			System.out.println(" dés IA trié 2");
		}

		for (int i = 0; i < 6; i++) {
			deInterval[i] = (choixIA[i] / (i + 1));
			System.out.print(deInterval[i] + "-");
		}
		System.out.println(" Dé plusieurs fois tiré");

		boolean validerchoix = false;
		int valeurIA = 0;
		ia.choixdeIA(deIA, choixIA);

		//Cherche la combinaison de la plus dur à plus simple
		if (scoreIAvalide[deIA[2] - 1] == 0 && scorezero[deIA[2] - 1] == 0 && choixIA[7] <= 13 && choixIA[7] != 0) {
			scoreIAvalide[deIA[2] - 1] = deIA[2] * 4;
			validerChoixIA = deIA[2];     //Choix de la combinaison validé
			valeurIA = scoreIAvalide[deIA[2] - 1];  //Valeur combinaison validé
			validerchoix = true;

		} else if (scoreIAvalide[7] == 0 && scorezero[7] == 0 && choixIA[7] != 0) {
			scoreIAvalide[7] = choixIA[7];
			validerChoixIA = 8;     //Choix de la combinaison validé
			valeurIA = choixIA[7];  //Valeur combinaison validé
			validerchoix = true;
		}

		for (int i = 11; i >= 6; i--) {
			if ((scoreIAvalide[i] == 0) && (choixIA[i] != 0) && (!validerchoix) && (scorezero[i] == 0)) {
				scoreIAvalide[i] = choixIA[i];
				validerChoixIA = i + 1;     //Choix de la combinaison validé
				valeurIA = scoreIAvalide[i];  //Valeur combinaison validé
				validerchoix = true;
			}
		}

		//Si pas de combinaison trouvé alors ajoute valeur à dé unique du plus petit au plus grand
		for (int i = 0; i < 6; i++) {
			if (!validerchoix && scoreIAvalide[i] == 0 && choixIA[i] != 0 && deInterval[i] > 1 && (scorezero[i] == 0)) { //sinon enregistre plus petit dé
				scoreIAvalide[i] = choixIA[i];
				validerChoixIA = i + 1;     //Choix de la combinaison validé
				valeurIA = scoreIAvalide[i];  //Valeur combinaison validé
				validerchoix = true;
			}
		}

		// Condition pour enregistrer en chance ou non
		for (int i = 0; i < 6; i++) {
			if (!validerchoix && scoreIAvalide[i] == 0 && choixIA[i] != 0 && scorezero[i] == 0) { //sinon enregistre plus petit dé
				if (choixIA[12] >= 15 && scoreIAvalide[12] == 0) {
					System.out.println(" Chance");
					scoreIAvalide[12] = choixIA[12];
					validerChoixIA = 13;     //Choix de la combinaison validé
					valeurIA = scoreIAvalide[12];  //Valeur combinaison validé
					validerchoix = true;
				} else if (choixIA[12] < 15 && choixIA[i] != 0 && scorezero[i] == 0) {
					scoreIAvalide[i] = choixIA[i];
					validerChoixIA = i + 1;     //Choix de la combinaison validé
					valeurIA = scoreIAvalide[i];  //Valeur combinaison validé
					validerchoix = true;
				} else if (scoreIAvalide[12] != 0) {
					scoreIAvalide[i] = choixIA[i];
					validerChoixIA = i + 1;     //Choix de la combinaison validé
					valeurIA = scoreIAvalide[i];  //Valeur combinaison validé
					validerchoix = true;
				}
			}
		}

		if (!validerchoix && (scoreIAvalide[12] != 0)) {
			if (scoreIAvalide[10] == 0 && scorezero[10] == 0 && scoreIAvalide[9] == 0 && scorezero[9] == 0) {
				validerChoixIA = 11;
				scorezero[10] = 1;
				scoreIAvalide[10] = 0;
				valeurIA = 0;
				validerchoix = true;
			}

			for (int i = 0; i < 6; i++) {
				if (!validerchoix && scoreIAvalide[i] == 0 && scorezero[i] == 0) {
					scorezero[i] = 1;
					validerChoixIA = i + 1;
					scoreIAvalide[i] = 0;
					valeurIA = 0;
					validerchoix = true;
				}
			}
			for (int i = 12; i >= 6; i--) {
				if (!validerchoix && scoreIAvalide[i] == 0 && scorezero[i] == 0) {
					scorezero[i] = 1;
					validerChoixIA = i + 1;
					scoreIAvalide[i] = 0;
					valeurIA = 0;
					validerchoix = true;
				}
			}
		}

		if (!validerchoix && (scoreIAvalide[12] == 0)) {
			scoreIAvalide[12] = choixIA[12];
			validerChoixIA = 13;     //Choix de la combinaison validé
			valeurIA = scoreIAvalide[12];  //Valeur combinaison validé
			validerchoix = true;
		}

		//Sélection du choix de l'IA
		for (int i = 0; i < 13; i++) {
			System.out.print(choixIA[i] + "/");
		}
		System.out.print(" -> Possibilité IA \n");

		for (int i = 0; i < 13; i++) {
			System.out.print(scoreIAvalide[i] + "/");
		}
		System.out.print(" -> Score IA déjà valide enregistré \n");

		// Enregistrement choix IA
		if (validerchoix) {
			System.out.println("Enregistrement de la combinaison " + validerChoixIA + " à la valeur " + valeurIA + ".");

			if (validerChoixIA < 7) {
				define_pts(valeurIA, validerChoixIA, false);
				color_dernierCoup(validerChoixIA, false);

			} else {
				validerChoixIA = validerChoixIA - 6;
				define_pts(valeurIA, validerChoixIA, true);
				color_dernierCoup(validerChoixIA, true);
			}
		}

		verif_yahtzee100();
		Special_SousTotal();
		Somme_ScoreAndSousTotal();
		Total_score();

		Total_final_pts.setText(Integer.toString(ia.getscoreTotal()) + " pts");
		System.out.println("IA fin \n");
		verif_partieFinie();

	}
}