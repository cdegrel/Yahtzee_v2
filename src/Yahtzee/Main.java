package Yahtzee;

import Yahtzee.model.Model;
import Yahtzee.view.InterfaceController;
import Yahtzee.view.PartieFinieController;
import Yahtzee.view.TabController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Stage primaryStage;
	private static Model model;
	private static InterfaceController interfaceController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		model = new Model();
		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Interface.fxml"));
			VBox rootLayout = loader.load();
			interfaceController = loader.getController();
			interfaceController.init_data(model);
			primaryStage.setTitle("Yahtzee");
			primaryStage.setScene(new Scene(rootLayout));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static TabController initTabLayout(Tab tab, int numJoueur) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Tab.fxml"));
			tab.setContent((Node) loader.load());
			TabController controller = loader.getController();
			controller.init_data(model, numJoueur, interfaceController);
			return controller;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void PopUp_about() {
		try {
			Stage stage = new Stage();
			Parent root;
			root = FXMLLoader.load(Main.class.getResource("view/PopUp_about.fxml"));
			stage.setScene(new Scene(root));
			stage.setTitle("À propos");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			//stage.initOwner(btn1.getScene().getWindow());
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void PopUp_partieFinie(Model model) {
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/PopUp_partieFinie.fxml"));
			Pane root = loader.load();
			PartieFinieController partieFinieController= loader.getController();
			partieFinieController.init_data(model);
			stage.setTitle("Partie Terminée");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}