package Yahtzee.util;

public class De {
	protected int lanceNum;
	protected int DesGen[];

	public De() {
		initLancer();
		DesGen = new int[6];
	}

	public void initLancer() {
		lanceNum = 0;
	}

	public void jette() {
		for (int i = 0; i < 5; i++) {
			DesGen[i] = (int) ((6.0 * Math.random()) + 1);
		}
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
