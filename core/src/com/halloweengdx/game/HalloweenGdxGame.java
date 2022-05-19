package com.halloweengdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class HalloweenGdxGame extends Game {


	// The class with the menu
	public static MenuScreen menuScreen;
	// The class with the game level
	public static LevelOneScreen gameScreenOne;
	//public static LevelTwoScreen gameScreenTwo;


	@Override
	public void create () {
		gameScreenOne = new LevelOneScreen(this);
		//gameScreenTwo = new LevelTwoScreen(this);
		menuScreen = new MenuScreen(this);
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
