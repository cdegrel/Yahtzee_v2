package Yahtzee.multi;

import Yahtzee.model.Model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Vector;

public class Server extends Thread {

	private Model model;
	private static ArrayList<ServerThread> ThreadJoueurs;
	private Vector<ObjectOutputStream> _ServClientsout = new Vector<>();
	private ServerSocket connect = null;
	private boolean shutdown = false;

	public Server(Model model) {
		model.initJoueurJoue();
		this.model = model;
		ThreadJoueurs = new ArrayList<>();
		try {
			connect = new ServerSocket(model.getPort());
			PrintConsole("Lancement parties multiDistantes");
			start();
		} catch (IOException | RuntimeException e) {
			PrintConsole("Impossible de lancer le serveur sur le port " + model.getPort());
			interrupt();
			/*e.printStackTrace();*/
		}
	}

	public void run() {
		while (ThreadJoueurs.size() < model.getNbJoueurs() && !shutdown) {
			try {
				PrintConsole("Attente de connexion du joueur " + (ThreadJoueurs.size() + 1) + "\n");
				ThreadJoueurs.add(new ServerThread(connect.accept(), ThreadJoueurs.size(), this));

				if (ThreadJoueurs.size() == model.getNbJoueurs()) {
					PrintConsole("Tous les joueurs sont connectés, début de la partie\n");
					_ServClientsout.get(0).writeBoolean(true);
					_ServClientsout.get(0).flush();
				}
			} catch (IOException e) {
				shutdown();
				/*e.printStackTrace();*/
			}
		}
	}

	private synchronized void PrintConsole(String message) {
		System.out.println("\033[36mServer - " + message + "\u001B[0m");
	}

	synchronized public Model getModel() {
		return model;
	}

	/**
	 * Ajoute à la liste _ServClientsout un flux sortant
	 *
	 * @param out type ObjectOutputStream
	 *            flux sortant
	 */
	synchronized void addClient(ObjectOutputStream out) {
		_ServClientsout.addElement(out);
	}

	/**
	 * Arrête tous les ServerThread
	 * en appellant leur méthode interrupt
	 * Puis arrête le Server actuel via interrupt
	 */
	public void shutdown() {
		shutdown = true;
		for (ServerThread thread : ThreadJoueurs) {
			if (thread != null) {
				thread.interrupt();
			}
		}
		interrupt();
	}

	/**
	 * Envoi un objet à tous les threads démarrés
	 * sauf à celui qui l'appel
	 *
	 * @param lui     type ServerThread
	 *                le Thread qui appel
	 * @param joueurs type Object
	 *                l'objet à envoyer
	 */
	synchronized void EnvoiAll(ServerThread lui, Object joueurs) {
		ObjectOutputStream out;
		for (int i = 0; i < _ServClientsout.size(); i++) {
			out = _ServClientsout.elementAt(i);
			if ((out != null) && (ThreadJoueurs.get(i) != lui)) {
				lui.PrintConsole("Envoi du jeu au joueur " + (i + 1) + "\n");
				try {
					out.writeObject(joueurs);
					out.flush();
				} catch (IOException e) {
					/*e.printStackTrace();*/
				}
			}
		}
	}

	/**
	 * Override de interrupt
	 * Stop la connexion du serveur
	 * et l'arrête
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		if (connect != null) {
			try {
				connect.close();
			} catch (IOException e) {
				/*e.printStackTrace();*/
			}
		}
	}
}