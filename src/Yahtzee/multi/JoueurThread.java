package Yahtzee.multi;

import Yahtzee.model.Model;
import Yahtzee.util.Joueur;
import Yahtzee.view.InterfaceController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class JoueurThread extends Thread {

	private Model model;
	private InterfaceController Interface;
	private ObjectInputStream OisReception = null;
	private ObjectOutputStream OusEnvoi;
	private ArrayList<Joueur> joueurList;

	private int numJoueur;
	private boolean coup_effectue = false, initialisationJoueur = false;

	public JoueurThread(Model model, InterfaceController Interface) {
		this.model = model;
		this.Interface = Interface;
		try {
			Socket connect = new Socket(model.getIp_address(), model.getPort());
			OisReception = new ObjectInputStream(connect.getInputStream());
			OusEnvoi = new ObjectOutputStream(connect.getOutputStream());
			model.initJoueurJoue();
			PrintConsole("Connexion au serveur établie");
			start();
		} catch (IOException e) {
			PrintConsole("Impossible de se connecter au serveur : " + model.getIp_address() + ":" + model.getPort());
			interrupt();
			/*e.printStackTrace();*/
		}
	}

	public void run() {
		try {
			numJoueur = OisReception.readInt();
			PrintConsole("Joueur actuel n°" + (numJoueur + 1));

			final int nbJoueurTotal = OisReception.readInt();
			PrintConsole("Partie de " + nbJoueurTotal + " joueurs\n");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Interface.newListe_joueur(nbJoueurTotal, numJoueur);
				}
			});

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					model.getJoueurs().get(0).getJoueurController().disableAllButtons();
					Interface.DisableLancer(true);
					Interface.basculeTab(numJoueur);
				}
			});

			if (numJoueur == 0) {
				PrintConsole("Attend la connexion de tous les joueurs");
				if (OisReception.readBoolean()) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Interface.init_des();
							model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().activeNotPlayedButtons();
							Interface.basculeTab(model.getJoueurJoue());
						}
					});
				}
			}

			requestLoop();

		} catch (InterruptedException | IOException | ClassNotFoundException e) {
			interrupt();
			/*e.printStackTrace();*/
		}
	}

	@SuppressWarnings({"unchecked", "InfiniteLoopStatement"})
	private void requestLoop() throws IOException, ClassNotFoundException, InterruptedException {
		while (true) {
			if (coup_effectue) {
				PrintConsole("Le joueur a joué");
				OusEnvoi.reset();                       // Une des choses qui font que Java est merdique
				OusEnvoi.writeObject(model.getJoueurs());
				OusEnvoi.flush();
				PrintConsole("Jeu envoyé au ServerThread (Joueur " + (numJoueur + 1) + ")\n");
				coup_effectue = false;
				initialisationJoueur = false;
				model.joueurJoueNext();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().verif_partieFinie()) {
							interrupt();
						} else {
							Interface.init_des();
							Interface.DisableLancer(true);
						}
					}
				});
			}

			if (numJoueur != model.getJoueurJoue()) {
				PrintConsole("Attend le jeu du joueur " + (model.getJoueurJoue() + 1));
				joueurList = (ArrayList<Joueur>) OisReception.readObject();
				PrintConsole("Jeu reçu\n");

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						for (Joueur joueur : joueurList) {
							model.getJoueurs().get(joueur.getNumJoueur()).appliLabelsToSend(joueur.Sendlabels_somme, false);
							model.getJoueurs().get(joueur.getNumJoueur()).appliLabelsToSend(joueur.Sendlabels_special, true);

							model.getJoueurs().get(joueur.getNumJoueur()).setScoreSomme(joueur.getScoreSomme());
							model.getJoueurs().get(joueur.getNumJoueur()).setscoreSpecial(joueur.getscoreSpecial());
							model.getJoueurs().get(joueur.getNumJoueur()).setscoreTotal(joueur.getscoreTotal());

							model.getJoueurs().get(joueur.getNumJoueur()).getJoueurController().Somme_ScoreAndSousTotal();
							model.getJoueurs().get(joueur.getNumJoueur()).getJoueurController().Special_SousTotal();
							model.getJoueurs().get(joueur.getNumJoueur()).getJoueurController().Total_score();
						}

						if (model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().verif_partieFinie()) {
							interrupt();
						}
						model.joueurJoueNext();
					}
				});
			}

			if (numJoueur == model.getJoueurJoue() && !initialisationJoueur) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Interface.init_des();
						model.getJoueurs().get(model.getJoueurJoue()).getJoueurController().activeNotPlayedButtons();
						Interface.basculeTab(model.getJoueurJoue());
					}
				});
				initialisationJoueur = true;
				PrintConsole("Initialisation du jeu effectué, attend que le joueur " + (numJoueur + 1) + " joue");
			}

			sleep(500);
		}
	}

	private synchronized void PrintConsole(String message) {
		System.out.println("\033[33m- JoueurThread (Joueur " + (numJoueur + 1) + ") : " + message + "\u001B[0m");
	}

	public void aJoue() {
		coup_effectue = true;
	}

	/**
	 * Override de interrupt
	 * Stop la connexion du serveur
	 * et l'arrête
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		model.setMultiDistant(false);
		if (OisReception != null) {
			try {
				OusEnvoi.close(); // Fermeture du flux si l'interruption n'a pas fonctionné.
				OisReception.close(); // Fermeture du flux si l'interruption n'a pas fonctionné.
			} catch (IOException ignored) {
			}
		}
	}
}
