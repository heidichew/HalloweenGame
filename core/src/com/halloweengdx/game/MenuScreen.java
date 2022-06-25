package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScreen implements Screen {

    HalloweenGdxGame game;

    private SpriteBatch uiBatch;    // Sprite batch to render objects on the screen
    private BitmapFont font;

    private Texture backgroundImage = null;

    // UI Buttons
    private Button playButton = null;
    private Button exitButton = null;

    // Music
    private Music bgMusic = GameAssetsDB.getInstance().menu_music;

    public MenuScreen(HalloweenGdxGame game) {
        this.game = game;
    }

    public void create(){
        uiBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        font.getData().setScale(10, 10);

        backgroundImage = new Texture(Gdx.files.internal("background/menu_background.png"));
        Texture playTexture = new Texture(Gdx.files.internal("button/play_button.png"));
        Texture exitTexture = new Texture(Gdx.files.internal("button/exit_button.png"));


        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        float buttonSize = h * 0.2f;
        playButton = new Button(w/2 - ((buttonSize * 3f)/2), h/2 - (buttonSize * 0.5f), buttonSize*3f, buttonSize, "Play", playTexture, playTexture);
        exitButton = new Button(w/2 - ((buttonSize * 3f)/2), h/2 - (buttonSize * 2.0f), buttonSize*3f, buttonSize, "Exit", exitTexture, exitTexture);
    }

    @Override
    public void show() {
        create();

        bgMusic.play();
        bgMusic.setVolume(0.5f);
    }

    @Override
    public void render(float delta) {
        //Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiBatch.begin();
        uiBatch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.draw(uiBatch, "Halloween Party", Gdx.graphics.getWidth()/2 - 500f, Gdx.graphics.getHeight() - 150f);
        playButton.draw(uiBatch);
        exitButton.draw(uiBatch);
        uiBatch.end();

        update();
    }

    private void update(){
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        playButton.update(checkTouch, touchX, touchY);
        exitButton.update(checkTouch, touchX, touchY);

        if (exitButton.isDown){
            dispose();
            Gdx.app.exit();
            System.exit(-1);
        }

        if (playButton.isDown){
            game.setScreen(HalloweenGdxGame.gameScreenOne);
            System.out.println("pressed play");
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        bgMusic.stop();
    }

    @Override
    public void dispose() {
        uiBatch.dispose();
        font.dispose();
        playButton.dispose();
        exitButton.dispose();
    }
}
