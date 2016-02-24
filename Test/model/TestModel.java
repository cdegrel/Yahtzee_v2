package model;

import Yahtzee.model.Model;

import org.junit.Assert;
import org.junit.Test;

public class TestModel {

	@Test
	/*
	* 3 dés identiques requis :
	* - pts = somme des 5 dés
	*/
	public void TestBrelan() {
		Model model = new Model();
		int[] des = new int[]{1, 2, 3, 2, 2};
		Assert.assertEquals(10, model.brelan(des));
		des = new int[]{1, 2, 2, 2, 3};
		Assert.assertEquals(10, model.brelan(des));
		des = new int[]{2, 2, 2, 1, 3};
		Assert.assertEquals(10, model.brelan(des));
	}

	@Test
	/*
	* 4 dés identiques, requis :
	* - pts = somme des 5 dés
	*/
	public void TestCarre() {
		Model model = new Model();
		int[] des = new int[]{2, 2, 3, 3, 2};
		Assert.assertEquals(0, model.carre(des));
		des = new int[]{2, 2, 3, 2, 2};
		Assert.assertEquals(11, model.carre(des));
		des = new int[]{2, 2, 2, 2, 3};
		Assert.assertEquals(11, model.carre(des));
		des = new int[]{3, 2, 2, 2, 2};
		Assert.assertEquals(11, model.carre(des));
	}

	@Test
	/*
	* 3 dés identiques + 2 dés identiques, requis :
	* - pts = 25pts
	*/
	public void TestFull() {
		Model model = new Model();
		int[] des = new int[]{2, 2, 2, 3, 1};
		Assert.assertEquals(0, model.full(des));
		des = new int[]{2, 2, 2, 3, 3};
		Assert.assertEquals(25, model.full(des));
		des = new int[]{3, 2, 2, 2, 3};
		Assert.assertEquals(25, model.full(des));
		des = new int[]{2, 3, 2, 3, 2};
		Assert.assertEquals(25, model.full(des));
	}

	@Test
	/*
	* 4 dés se suivent, requis :
	* - pts = 30pts
	*/
	public void TestPetiteSuite() {
		Model model = new Model();
		int[] des = new int[]{1, 2, 3, 5, 6};
		Assert.assertEquals(0, model.petiteSuite(des));
		des = new int[]{2, 3, 4, 5, 1};
		Assert.assertEquals(30, model.petiteSuite(des));
		des = new int[]{3, 2, 4, 5, 1};
		Assert.assertEquals(30, model.petiteSuite(des));
		des = new int[]{6, 5, 3, 4, 1};
		Assert.assertEquals(30, model.petiteSuite(des));
	}

	@Test
	/*
	* 5 dés se suivent, requis :
	* - pts = 40pts
	*/
	public void TestGrandeSuite() {
		Model model = new Model();
		int[] des = new int[]{1, 2, 3, 4, 6};
		Assert.assertEquals(0, model.grandeSuite(des));
		des = new int[]{1, 2, 3, 4, 5};
		Assert.assertEquals(40, model.grandeSuite(des));
		des = new int[]{2, 3, 4, 5, 6};
		Assert.assertEquals(40, model.grandeSuite(des));
		des = new int[]{3, 2, 5, 4, 6};
		Assert.assertEquals(40, model.grandeSuite(des));
		des = new int[]{5, 4, 3, 2, 1};
		Assert.assertEquals(40, model.grandeSuite(des));
	}

	@Test
	/*
	* 5 dés identiques, requis :
	* - pts = 50pts
	*/
	public void TestYahtzee() {
		Model model = new Model();
		int[] des = new int[]{2, 2, 2, 2, 5};
		Assert.assertEquals(0, model.yahtzee(des));
		des = new int[]{4, 4, 4, 4, 4};
		Assert.assertEquals(50, model.yahtzee(des));
		des = new int[]{4, 5, 5, 5, 5};
		Assert.assertEquals(0, model.yahtzee(des));
		des = new int[]{5, 5, 5, 5, 5};
		Assert.assertEquals(50, model.yahtzee(des));
	}

	@Test
	/*
	* pas de combinaison, requise :
	* - pts = somme des 5 dés
	*/
	public void TestChance() {
		Model model = new Model();
		int[] des = new int[]{1, 2, 3, 4, 5};
		Assert.assertEquals(15, model.chance(des));
		des = new int[]{4, 2, 3, 2, 5};
		Assert.assertEquals(16, model.chance(des));
		des = new int[]{2, 3, 1, 5, 1};
		Assert.assertEquals(12, model.chance(des));
	}
}
