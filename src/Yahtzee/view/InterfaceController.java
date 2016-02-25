package Yahtzee.view;

import Yahtzee.Main;
import Yahtzee.img.img;
import Yahtzee.model.Model;
import Yahtzee.util.IA;
import Yahtzee.util.Joueur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class InterfaceController {

	@FXML
	private MenuItem np_1_joueur, np_2_joueur, np_3_joueur, np_4_joueur,
			np_5_joueur, np_6_joueur, save, open, exit, about;          // Éléments du menu
	@FXML
	private TabPane tabpane;                                            // Barre d'onglets
	@FXML
	private GridPane tab_des;                                           // Grille contenant les dés
	private ToggleButton[] des;                                         // Tableau contenant les accès aux dés
	@FXML
	private ImageView fake_de_6, fake_de_7;                             // Faux dés pour l'affichage de "Yahtzee"
	@FXML
	private Button lancer;                                              // Bouton de lancer
	private Model model;                                                // Modèle

	/*@FXML
	void initialize() {
	}*/

	/**
	 * Initialisation des données
	 *
	 * @param model type model
	 */
	public void init_data(Model model) {
		this.model = model;
		des = arrayGenerate_des();
		newListe_joueur(2);            // Création d'un jeu de 2 joueurs par défaut, au lancement
	}

	/**
	 * ActionEvent appelé lors d'une interaction avec un élément du menu
	 * Déclenche les actions voulues
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void menu(ActionEvent event) {
		if (event.getSource() == np_1_joueur) {
			newListe_joueur(1);        // création d'un jeu avec 1 joueur + IA
		}
		if (event.getSource() == np_2_joueur) {
			newListe_joueur(2);
		}
		if (event.getSource() == np_3_joueur) {
			newListe_joueur(3);
		}
		if (event.getSource() == np_4_joueur) {
			newListe_joueur(4);
		}
		if (event.getSource() == np_5_joueur) {
			newListe_joueur(5);
		}
		if (event.getSource() == np_6_joueur) {
			newListe_joueur(6);
		}

		if (event.getSource() == save) {
			// Création d'un popup pour sauvegarde
		}
		if (event.getSource() == open) {
			// Création d'un popup pour choisir la sauvegarde à charger
		}

		if (event.getSource() == exit) {
			System.exit(0);
		}
		if (event.getSource() == about) {
			Main.PopUp_about();
		}
	}

	/**
	 * ActionEvent appelé lors de l'appui sur la toucher "Lancer"
	 * Lance les dés & gère le nombre de lancé
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void lancerDes_action(ActionEvent event) {
		model.getDes().jette();

		if (model.getDes().getLancer() == 0) {    // Au premier lancer, désélection de tous les dés
			for (int i = 0; i < 5; i++) {
				des[i].setSelected(false);
			}
			fake_de_6.setVisible(false);    // masque le faux dé n°6
			fake_de_7.setVisible(false);    // masque le faux dé n°7
		}

		for (int i = 0; i < 5; i++) {
			if (!des[i].isSelected()) {        // si dé sélectionné, pas de réassignation aléatoire
				setDe_face(des[i], model.getDes().getSortie(i), 0);
			}
		}

		if (model.getDes().getLancer() != 3) {
			model.getDes().incremLanceNum();
		}
		if (model.getDes().getLancer() == 3) {
			lancer.setDisable(true);
		}
	}

	/**
	 * Bascule vers l'onglet du joueur
	 *
	 * @param numJoueur type int
	 *                  numéro du joueur concerné
	 */
	public void basculeTab(int numJoueur) {
		tabpane.getSelectionModel().select(tabpane.getTabs().get(numJoueur));
	}

	/**
	 * Supprime (removeAll_Joueur())
	 * et crée une nouvelle liste de joueurs
	 *
	 * @param nbJoueur type int
	 *                 nombre de joueurs désiré(s)
	 */
	void newListe_joueur(int nbJoueur) {
		removeAll_Joueur();
		init_des();
		for (int i = 0; i < nbJoueur; i++) {
			Tab tab_joueur = new Tab("Joueur " + (tabpane.getTabs().size() + 1));
			tabpane.getTabs().add(tab_joueur);
			model.getJoueurs().add(new Joueur(tab_joueur, i));
		}
		if (nbJoueur == 1) {
			Tab tab_ia = new Tab("IA");
			tabpane.getTabs().add(tab_ia);
			model.getJoueurs().add(new IA(tab_ia, 1));
		}
		for (Joueur joueur : model.getJoueurs()) {
			joueur.getController().init_data_after_JoueurList();
		}
	}

	/**
	 * Supprime tous les joueurs
	 */
	void removeAll_Joueur() {
		tabpane.getTabs().remove(0, tabpane.getTabs().size());    // Supprimer tous les onglets
		model.initJoueurJoue();
		model.deleteAllJoueurs();
	}

	/**
	 * Disposition des dés en fin de partie
	 */
	public void fin_des() {
		init_des();
		lancer.setVisible(false);
	}

	/**
	 * (Ré)Initialise les dés
	 */
	void init_des() {
		model.getDes().initLancer();
		lancer.setDisable(false);
		lancer.setVisible(true);
		String[] lettre = new String[]{"Y", "A", "H", "T", "Z"};
		ImageView imv;
		for (int i = 0; i < 5; i++) {
			imv = new ImageView(new Image(img.class.getResourceAsStream(lettre[i] + ".png")));
			imv.setPreserveRatio(true);                             // Respect du ratio de l'image
			imv.fitWidthProperty().bind(des[i].widthProperty());    // Taille identique au bouton pour l'image
			des[i].setContentDisplay(ContentDisplay.CENTER);        // Centre le contenu (l'image)
			des[i].setGraphic(imv);                                 // Ajoute l'image
			des[i].setText("0");
		}
		fake_de_6.setVisible(true);        // affiche le faux dé n°6
		fake_de_7.setVisible(true);        // affiche le faux dé n°7
	}

	/**
	 * Changer le statut de la face actuelle du ToggleButton
	 *
	 * @param de     type ToggleButton
	 * @param statut type int
	 *               0 = non sélectionné
	 *               1 = appuie en cours
	 *               2 = sélectionné
	 */
	void setDe_statutFace(ToggleButton de, int statut) {
		setDe_face(de, Integer.parseInt(de.getText()), statut);
	}

	/**
	 * Appliquer une face de dé à un ToggleButton
	 *
	 * @param de     type ToggleButton
	 * @param num    type int
	 *               le n° de la face à associer (1 à 6)
	 * @param statut type int
	 *               0 = non sélectionné
	 *               1 = appuie en cours
	 *               2 = sélectionné
	 */
	void setDe_face(ToggleButton de, int num, int statut) {
		if (num == 0) return;

		Rectangle2D viewport;

		if (num == 1) {
			viewport = new Rectangle2D(10, 7, 102, 102);      // face n°1
		} else if (num == 2) {
			viewport = new Rectangle2D(9, 157, 102, 102);     // face n°2
		} else if (num == 3) {
			viewport = new Rectangle2D(160, 7, 102, 102);     // face n°3
		} else if (num == 4) {
			viewport = new Rectangle2D(160, 157, 102, 102);   // face n°4
		} else if (num == 5) {
			viewport = new Rectangle2D(309, 7, 102, 102);     // face n°5
		} else {
			viewport = new Rectangle2D(309, 157, 102, 102);   // face n°6
		}

		String img;
		if (statut == 0) {
			img = "des.png";
		} else if (statut == 1) {
			img = "des_selectionnes.png";
		} else {
			img = "des_enclenches.png";
		}

		de.setText(Integer.toString(num));    // définit au format texte le n° de la face associé

		Image imageDecline = new Image(img.class.getResourceAsStream(img));
		ImageView imv = new ImageView(imageDecline);
		imv.setPreserveRatio(true);                         // Respect du ratio de l'image
		imv.fitWidthProperty().bind(de.widthProperty());    // Taille identique au bouton pour l'image
		/*imv.setFitHeight(60); imv.setFitWidth(60);        // Taille de l'image manuel*/
		imv.setViewport(viewport);
		de.setContentDisplay(ContentDisplay.CENTER);        // Centre le contenu (l'image)
		de.setGraphic(imv);                                 // Ajoute l'image
	}

	/**
	 * Parcours la ligne du tableau contenant les dés
	 * et définis leurs accès via un tableau
	 *
	 * @return type ToggleButton[] : dés
	 */
	ToggleButton[] arrayGenerate_des() {
		ToggleButton[] Tb = new ToggleButton[5];
		int col_max = 7;
		int row = 1;
		int i = 0;
		for (Node node : tab_des.getChildren()) {
			if (GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == col_max) {
				break;
			}
			if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row) {
				Tb[i] = (ToggleButton) node;
				i++;
			}
		}
		return Tb;
	}

	/**
	 * ActionEvent appelé lors de l'appui sur un dé
	 * Change l'image du dé lors de sa sélection/désélection
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void actionPressed_Des(ActionEvent event) {
		if (((ToggleButton) event.getSource()).isSelected()) {
			setDe_statutFace(((ToggleButton) event.getSource()), 2);    // Sélectionné
		} else {
			setDe_statutFace(((ToggleButton) event.getSource()), 0);    // Non sélectionné
		}
	}

	/**
	 * MouseEvent appelé lors d'un maintien de la souris sur un dé
	 * Change l'image du dé au moment de l'appui
	 *
	 * @param event type ActionEvent
	 */
	@FXML
	void mousePressed_Des(MouseEvent event) {
		setDe_statutFace(((ToggleButton) event.getSource()), 1);
	}

	/**
	 * Retourne le tableau de dés
	 *
	 * @return type ToggleButton[] : dés
	 */
	public ToggleButton[] getDes() {
		return des;
	}
}
