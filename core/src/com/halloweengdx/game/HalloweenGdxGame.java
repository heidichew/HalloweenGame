package com.halloweengdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class HalloweenGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	float x;

	private OrthographicCamera camera;
	private float targetScreenHeight = 2160;
	private float targetScreenWidth; // don't set since different phone may have different width.

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		float screenRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		this.targetScreenWidth = this.targetScreenHeight * screenRatio;

		this.camera = new OrthographicCamera(this.targetScreenWidth, this.targetScreenHeight);
		this.camera.position.set(0,0,0);
		this.camera.update();


	}

	//target screen width set limitation
	@Override
	public void render () {
		x += 1;

		this.camera.position.set(x, 0, 0);
		this.camera.update();

		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.draw(img, x - img.getWidth() / 2, 0 - img.getHeight() / 2);
		batch.draw(img, -100, -0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
