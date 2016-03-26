package Yahtzee.util;

import java.util.Random;

public class De {
	private int lanceNum;
	private int DesGen[];
	private static Random random;

	public De() {
		initLancer();
		DesGen = new int[6];
		random = new Random();
	}

	public void initLancer() {
		lanceNum = 0;
	}

	public void jette() {
		int min = 1, max = 6;
		for (int i = 0; i < 5; i++) {
			DesGen[i] = random.nextInt(max - min + 1) + min;
		}
	}

	public int[] getDes() {
		return DesGen;
	}

	public int getSortie(int i) {
		return DesGen[i];
	}

	public int getLancer() {
		return lanceNum;
	}

	public void incremLanceNum() {
		lanceNum++;
	}

}
