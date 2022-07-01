package com.halloweengdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Main class of the game
 * This game class is used to create game levels and main menu screen for the game
 * @Author: Heidi Chew
 */
public class HalloweenGdxGame extends Game {


	// The class with the menu
	public static MenuScreen menuScreen;
	// The class with the game level
	private LevelOneScreen gameScreenOne;
	private LevelTwoScreen gameScreenTwo;

	public static ArrayList<GameScreen> gameLevels;
	public static ArrayList<Integer> levelScores;

	// The current level
	public int currentLevel;

	@Override
	public void create () {
		// Create main menu screen
		menuScreen = new MenuScreen(this);

		// Record score for each level
		levelScores = new ArrayList<Integer>();
		levelScores.add(0); // Level 1
		levelScores.add(0); // Level 2

		// Create game menu screen
		gameScreenOne = new LevelOneScreen(this);
		gameScreenTwo = new LevelTwoScreen(this);

		// Initialise the game level array
		gameLevels = new ArrayList<GameScreen>();

		// Add to array list
		gameLevels.add(gameScreenOne);
		gameLevels.add(gameScreenTwo);

		// Which level to start with
		currentLevel = 0;

		// Change screens to the menu
		setScreen(menuScreen);
	}


	@Override
	public void dispose () {
		super.dispose();

		GameAssetsDB.getInstance().dispose();

		for(GameScreen level: gameLevels){
			level.dispose();
		}

		menuScreen.dispose();
	}

	@Override
	// this method calls the super class render
	// which in turn calls the render of the actual screen being used
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

}
