package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class LevelOneScreen extends GameScreen
{
    //assets
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    //tileMap Render
    private TiledMapRenderer tiledMapRenderer;

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button jumpButton;
    Button attackButton;

    private float stateTime;

    public LevelOneScreen(HalloweenGdxGame game){

        super(game);
        create();
        newGame();
    }

    private void create(){
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(gameAssetsDB.tiledMap_L1);

        buttonSquareTexture = new Texture("button/button_square_blue.png");
        buttonSquareDownTexture = new Texture("button/button_square_beige.png");

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        //Buttons
        float buttonSize = h * 0.2f;
        moveLeftButton = new Button(0.0f, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        jumpButton = new Button(w - 350f, buttonSize*1.3f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(w - 300f, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
    }

    private void newGame(){
        gameState = GameState.PLAYING;
        stateTime = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //super.batch.setProjectionMatrix(super.camera.combined);
        this.tiledMapRenderer.setView(super.camera);
        this.tiledMapRenderer.render();

        // Update the game state time
        stateTime += Gdx.graphics.getDeltaTime();
        update(stateTime);

        uiBatch.begin();

        if(gameState == GameState.PLAYING){
            moveLeftButton.draw(uiBatch);
            moveRightButton.draw(uiBatch);
            jumpButton.draw(uiBatch);
            attackButton.draw(uiBatch);
        }else{

        }
        uiBatch.end();

    }

    @Override
    public void update(float delta) {

        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        //Update Game State based on input
        switch (gameState) {

            case PLAYING:
                //Poll user for input
                moveLeftButton.update(checkTouch, touchX, touchY);
                moveRightButton.update(checkTouch, touchX, touchY);
                attackButton.update(checkTouch, touchX, touchY);
                jumpButton.update(checkTouch, touchX, touchY);

                int moveX = 0;
                int moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
                    moveLeftButton.isDown = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
                    moveRightButton.isDown = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || attackButton.isDown) {
                    attackButton.isDown = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || jumpButton.isDown) {
                    jumpButton.isDown = true;
                }
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

    }

    @Override
    public void dispose() {
        buttonSquareTexture.dispose();
        buttonSquareDownTexture.dispose();
    }
}
