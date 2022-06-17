package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class GameScreen implements Screen {

    public enum GameState { PLAYING, PAUSE, WIN, FAIL}

    public static final int HEALTH = 5;

    protected HalloweenGdxGame game;

    protected OrthographicCamera camera;

    protected float targetScreenHeight = 1400;
    protected float targetScreenWidth;

    protected SpriteBatch batch;            // batch to draw the enemy instances, NPC and player
    protected SpriteBatch levelBatch;       // batch to draw instances in a level (e.g., Tilemap)
    protected SpriteBatch bgBatch;          // batch to draw background only
    protected SpriteBatch uiBatch;          // batch to draw buttons or UI related

    // Game clock
    protected float stateTime;

    protected Player player;

    protected GameState gameState;         // The game state of this game level

    protected int health;                  // The total health or life that the player have
    protected int gameScore;               // The total score of the player win in the game

    //UI textures
    protected Texture buttonSquareTexture;
    protected Texture buttonSquareDownTexture;
    protected Texture lifeTexture;
    protected Texture pauseTexture;
    protected Texture buttonLongTexture;
    protected Texture buttonLongDownTexture;

    //UI Buttons
    protected Button moveLeftButton;
    protected Button moveRightButton;
    protected Button jumpButton;
    protected Button attackButton;
    protected Button pauseButton;
    protected Button resumeButton;

    protected BitmapFont font;

    protected boolean resumePressed;

    public GameScreen(HalloweenGdxGame game) {

        this.game = game;
        this.gameScore = 0;
        this.stateTime = 0f;
        this.health = HEALTH;
    }

    public void create(){
        // Screen
        float screenRatio = (float)Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.targetScreenWidth = targetScreenHeight * screenRatio;

        // Camera
        camera = new OrthographicCamera(this.targetScreenWidth, this.targetScreenHeight);
        camera.setToOrtho(false, this.targetScreenWidth , this.targetScreenHeight);
        camera.update();

        // Instantiate sprite batch
        uiBatch = new SpriteBatch();
        batch = new SpriteBatch();
        levelBatch = new SpriteBatch();
        bgBatch = new SpriteBatch();

        // Load button texture
        buttonSquareTexture = new Texture("button/button_square_blue.png");
        buttonSquareDownTexture = new Texture("button/button_square_beige.png");
        buttonLongTexture = new Texture("button/button_long_blue.png");
        buttonLongDownTexture = new Texture("button/button_long_beige.png");

        // Load health or life texture
        lifeTexture = new Texture("ui/life.png");

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Setup gameplay buttons
        float buttonSize = h * 0.2f * 0.95f;
        moveLeftButton = new Button(0.0f, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(w - 250f, buttonSize * 1.15f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        jumpButton = new Button(w - 500f, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);

        // Setup the pause button
        pauseTexture = new Texture(Gdx.files.internal("button/pause_button_round.png"));
        pauseButton = new Button(w - 150f, h - 150f, buttonSize * 0.7f, buttonSize * 0.7f, pauseTexture, pauseTexture);

        // Setup resume button
        resumeButton = new Button((w/2) - (buttonSize*3f)/2, h/2 - (buttonSize * 0.5f), buttonSize*3f, buttonSize, "Resume");

        // Font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5, 5);
    }

    public void newGame(){
        gameState = GameState.PLAYING;
        stateTime = 0;
        gameScore = 0;
        health = HEALTH;
    }

    abstract public void update();

}
