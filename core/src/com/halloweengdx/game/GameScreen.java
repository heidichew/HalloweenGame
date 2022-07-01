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

    protected OrthographicCamera camera;

    protected SpriteBatch batch;            // batch to draw the enemy instances, NPC and player
    protected SpriteBatch levelBatch;       // batch to draw instances in a level (e.g., Tilemap)
    protected SpriteBatch bgBatch;          // batch to draw background only
    protected SpriteBatch uiBatch;          // batch to draw buttons or UI related
    protected SpriteBatch enemyBatch;

    // Game clock
    protected float stateTime;

    protected GameState gameState;         // The game state of this game level

    protected int gameScore;               // The total score of the player win in the game

    //UI Buttons
    protected Button moveLeftButton;
    protected Button moveRightButton;
    protected Button jumpButton;
    protected Button attackButton;
    protected Button pauseButton;
    protected Button resumeButton;
    protected Button restartButton;
    protected Button newLevelButton;
    protected Button exitButton;

    protected BitmapFont font;

    protected boolean resumePressed;

    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();
    protected HalloweenGdxGame game;

    private float targetScreenHeight = 1920;
    private float targetScreenWidth;

    // Detect if the user press the gameplay controls
    protected boolean isJumpHeld, isRightHeld, isLeftHeld, isAttackHeld;

    protected long jumpPressedTime;
    protected float attackPressedTime = 0;

    public static final long LONG_JUMP_PRESS = 200l;
    public static final float ATTACK_PRESS_COOLDOWN = 40;
    public final static float RESPAWN_TIME = 100; // not using

    public GameScreen(HalloweenGdxGame game) {

        this.game = game;
        this.gameScore = 0;
        this.stateTime = 0f;
    }

    public void create(){
        // Screen
        float screenRatio = (float)Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.targetScreenWidth = targetScreenHeight * screenRatio;

        // Camera
        camera = new OrthographicCamera(this.targetScreenWidth, this.targetScreenHeight);
        camera.setToOrtho(false, this.targetScreenWidth/2, this.targetScreenHeight/2);
        camera.update();

        // Instantiate sprite batch
        uiBatch = new SpriteBatch();
        batch = new SpriteBatch();
        levelBatch = new SpriteBatch();
        bgBatch = new SpriteBatch();
        enemyBatch = new SpriteBatch();

        // Load button texture
        Texture buttonSquareTexture = this.gameAssetsDB.buttonSquareTexture;
        Texture buttonSquareDownTexture = this.gameAssetsDB.buttonSquareDownTexture;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Setup gameplay buttons
        float buttonSize = h * 0.2f * 0.95f;
        moveLeftButton = new Button(0.0f, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(w - 250f, buttonSize * 1.15f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        jumpButton = new Button(w - 500f, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);

        // Setup the pause button
        Texture pauseTexture = this.gameAssetsDB.pauseTexture;
        pauseButton = new Button(w - 150f, h - 150f, buttonSize * 0.7f, buttonSize * 0.7f, pauseTexture, pauseTexture);

        // Setup resume button
        resumeButton = new Button((w/2) - (buttonSize*3f)/2, h/2 - (buttonSize * 0.5f), buttonSize*3f, buttonSize, "Resume");

        //Restart and Exit
        restartButton = new Button((w/2) - (buttonSize*3f)/2, h/2 - (buttonSize * 0.5f) - 60f, buttonSize*3f, buttonSize, "Restart");
        exitButton = new Button((w/2) - (buttonSize*3f)/2, h/2 - (buttonSize * 0.5f) - 360f, buttonSize*3f, buttonSize, "Exit");

        //New Level
        this.newLevelButton = new Button((w/2) - (buttonSize*3f)/2, h/2 - (buttonSize * 0.5f) - 60f, buttonSize*3f, buttonSize, "Next Level ->");

        // Font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5, 5);
    }

    public void newGame(){
        gameState = GameState.PLAYING;
        stateTime = 0;
        gameScore = 0;
        attackPressedTime = 0;
        jumpPressedTime = 0;

        isAttackHeld = false;
        isJumpHeld = false;
        isRightHeld = false;
        isLeftHeld = false;
    }

    abstract public void update();

    abstract public void show();

    abstract public void hide();

    abstract public void resize(int width, int height);

    abstract public void pause();

    abstract public void resume();

    public void dispose()
    {
        this.gameAssetsDB.dispose();

        this.batch.dispose();
        this.bgBatch.dispose();
        this.uiBatch.dispose();
        this.levelBatch.dispose();
        this.enemyBatch.dispose();

        this.moveLeftButton.dispose();
        this.moveRightButton.dispose();
        this.jumpButton.dispose();
        this.attackButton.dispose();
        this.pauseButton.dispose();
        this.resumeButton.dispose();
        this.restartButton.dispose();
        this.newLevelButton.dispose();
        this.exitButton.dispose();

        this.font.dispose();

        this.game.dispose();
    };

}
