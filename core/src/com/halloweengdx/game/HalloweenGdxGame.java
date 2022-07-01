package com.halloweengdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class HalloweenGdxGame extends Game {


	// The class with the menu
	public static MenuScreen menuScreen;
	// The class with the game level
	private LevelOneScreen gameScreenOne;
	private LevelTwoScreen gameScreenTwo;

	public static ArrayList<GameScreen> gameLevels;

	public int currentLevel = 1;

	@Override
	public void create () {
		// Create main menu screen
		menuScreen = new MenuScreen(this);

		// Create game menu screen
		gameScreenOne = new LevelOneScreen(this);
		gameScreenTwo = new LevelTwoScreen(this);

		// Initialise the game level array
		gameLevels = new ArrayList<GameScreen>();

		// Add to array list
		gameLevels.add(gameScreenOne);
		gameLevels.add(gameScreenTwo);

		currentLevel = 0;

		// Change screens to the menu
		setScreen(menuScreen);
	}


	@Override
	public void dispose () {
		super.dispose();

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
