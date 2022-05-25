package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class GameScreen implements Screen {

    public enum GameState { PLAYING, PAUSE, WIN, FAIL}

    protected HalloweenGdxGame game;

    protected OrthographicCamera camera;
    protected Viewport viewport;

    protected float targetScreenHeight = 800;
    protected float targetScreenWidth;

    protected SpriteBatch batch;            // batch to draw the enemy instances, NPC and player
    protected SpriteBatch levelBatch;       // batch to draw instances in a level (e.g., Tilemap)
    protected SpriteBatch bgBatch;          // batch to draw background only
    protected SpriteBatch uiBatch;          // batch to draw buttons or UI related

    // Game clock
    protected float stateTime;

    protected Player player;

    protected GameState gameState;                 // The game state of this game level

    protected int gameScore;

    // Button here?

    public GameScreen(HalloweenGdxGame game) {

        this.game = game;
        gameScore = 0;

        create();
    }

    private void create(){
        //screen
        float screenRatio = (float)Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.targetScreenWidth = targetScreenHeight * screenRatio;

        //camera
        this.camera = new OrthographicCamera(this.targetScreenWidth, this.targetScreenHeight);
        this.camera.setToOrtho(false, this.targetScreenWidth , this.targetScreenHeight);
        this.camera.update();

        uiBatch = new SpriteBatch();
        batch = new SpriteBatch();
        levelBatch = new SpriteBatch();
        bgBatch = new SpriteBatch();
    }

    abstract public void update(float delta);

}
