package Yahtzee.multi;

import Yahtzee.util.Joueur;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class ServerThread extends Thread {

	private int numJoueurActu;
	private Server server;
	private ObjectInputStream OisReception = null;
	private ObjectOutputStream OusEnvoi;

	ServerThread(Socket sockjoueur, int numJoueurActu, Server server) {
		this.numJoueurActu = numJoueurActu;
		this.server = server;
		try {
			sockjoueur.setKeepAlive(true);
			OusEnvoi = new ObjectOutputStream(sockjoueur.getOutputStream());
			OisReception = new ObjectInputStream(sockjoueur.getInputStream());
			server.addClient(OusEnvoi);
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			PrintConsole("Envoi du numéro de joueur");
			OusEnvoi.writeInt(numJoueurActu);
			OusEnvoi.flush();

			PrintConsole("Envoi du nombre total de joueur\n");
			OusEnvoi.writeInt(server.getModel().getNbJouMultiDist());
			OusEnvoi.flush();

			requestLoop();

		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			interrupt();
			/*e.printStackTrace();*/
		}
	}

	@SuppressWarnings({"unchecked", "InfiniteLoopStatement"})
	private void requestLoop() throws IOException, ClassNotFoundException, InterruptedException {

		while (true) {
			if (numJoueurActu == server.getModel().getJoueurJoue()) {
				PrintConsole("Attend le jeu du joueur " + (numJoueurActu + 1));
				ArrayList<Joueur> joueurList = (ArrayList<Joueur>) OisReception.readObject();
				PrintConsole("Jeu du joueur " + (numJoueurActu + 1) + " reçu");
				server.EnvoiAll(this, joueurList);
			}

			sleep(500);
		}
	}

	synchronized void PrintConsole(String message) {
		System.out.println("\033[31mServerThread (Joueur " + (numJoueurActu + 1) + ") -- " + message + "\u001B[0m");
	}

	/**
	 * Override de interrupt
	 * Stop la connexion du serveur
	 * et l'arrête
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		if (OisReception != null) {
			try {
				OusEnvoi.close(); // Fermeture du flux si l'interruption n'a pas fonctionné.
				OisReception.close(); // Fermeture du flux si l'interruption n'a pas fonctionné.
			} catch (IOException ignored) {
			}
		}
	}
}