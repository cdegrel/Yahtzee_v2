package Yahtzee.view;

import Yahtzee.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class configServerController {

	@FXML
	private TextField ip, port;
	@FXML
	private ChoiceBox<Integer> nbJoueurs;

	private Model model; // Modèle
	private Stage stage;
	private boolean mode;

	/**
	 * Appelé à la création du PopUp
	 *
	 * @param model type Model
	 * @param mode  type boolean
	 *              true : hébergé
	 *              false : joindre
	 * @param stage type Stage
	 */
	public void init_data(Model model, boolean mode, Stage stage) {
		this.model = model;
		this.stage = stage;
		this.mode = mode;

		if (mode) {
			ip.setText(getIPlocal(true));
			ip.setDisable(true);
		} else {
			ip.setText(getIPlocal(false));
			nbJoueurs.setDisable(true);
		}
		initChoiceBox();
	}

	/**
	 * ActionEvent appelé lors de l'appuie
	 * sur "Valider"
	 */
	@FXML
	void valide() {
		if (!ip.getText().equals("") && !port.getText().equals("")) {
			model.configMultiDistant(!mode ? ip.getText() : "127.0.0.1", Integer.parseInt(port.getText()), nbJoueurs.getSelectionModel().getSelectedItem());
			stage.close();
		}
	}

	/**
	 * Initialisation des options de la ChoiceBox
	 * (Joueurs de 2 à 8)
	 */
	void initChoiceBox() {
		nbJoueurs.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
		nbJoueurs.getSelectionModel().selectFirst();
	}

	/**
	 * Récupère l'adresse ip actuel
	 *
	 * @param ipEntier type boolean
	 *                 true : retourne l'adresse entière
	 *                 false : retourne les 3 premiers groupes
	 * @return type String : adresse ip
	 */
	String getIPlocal(boolean ipEntier) {
		try {
			String ip_adress = InetAddress.getLocalHost().getHostAddress();
			if (ipEntier) {
				return ip_adress;
			} else {
				return ip_adress.split("\\d+$")[0];
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
