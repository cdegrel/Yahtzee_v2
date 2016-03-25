package Yahtzee;

import Yahtzee.model.Model;
import Yahtzee.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					interfaceController.stopThreads();
					Platform.exit();
					System.exit(0);
				}
			});
			primaryStage.setScene(new Scene(rootLayout));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JoueurTabController initTabLayout(Tab tab, int numJoueur) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/TabJoueur.fxml"));
			tab.setContent((Node) loader.load());
			JoueurTabController controller = loader.getController();
			controller.init_data(model, numJoueur, interfaceController);
			return controller;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IATabController initTabIALayout(Tab tab, int numIA) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/TabIA.fxml"));
			tab.setContent((Node) loader.load());
			IATabController controller = loader.getController();
			controller.init_data(model, numIA, interfaceController);
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
			PartieFinieController partieFinieController = loader.getController();
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

	public static boolean PopUp_configServer(final Model model, boolean mode) {
		final boolean[] confOk = {true};
		try {
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/PopUp_configServer.fxml"));
			Pane root = loader.load();
			configServerController configServController = loader.getController();
			configServController.init_data(model, mode, stage);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					confOk[0] = false;
				}
			});
			stage.setTitle("Paramètres du Yahtzee réseau");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return confOk[0];
	}
}