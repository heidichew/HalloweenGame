package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LevelOneScreen extends GameScreen
{
    //assets
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    //tileMap Render
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    // Player
    private Player player;

    public LevelOneScreen(HalloweenGdxGame game){
        super(game);
        create();
        newGame();
    }

    public void create(){
        super.create();

        tiledMap = new TmxMapLoader().load("TileMap/tile_map_level01.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Create player
        player = new Player(25,1100); // 600
    }

    public void newGame(){

        //Translate camera to center of screen
        //camera.position.x = 16;
        //camera.position.y = 10;
        //camera.translate(player.getPosition().x, player.getPosition().y);

        super.newGame();
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

        if(camera != null){
            camera.update();
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }

        // Update the game state time
        stateTime += Gdx.graphics.getDeltaTime();
        update();

        //Apply camera to spritebatch and draw player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();


        uiBatch.begin();

        // Render score
        font.draw(uiBatch, "Score: " + gameScore, Gdx.graphics.getWidth()/2 - 100f, Gdx.graphics.getHeight() - 60f);

        if(gameState == GameState.PLAYING){
            moveLeftButton.draw(uiBatch);
            moveRightButton.draw(uiBatch);
            jumpButton.draw(uiBatch);
            attackButton.draw(uiBatch);

            pauseButton.draw(uiBatch);
        }else if(gameState == GameState.PAUSE){

            resumeButton.draw(uiBatch);
        }

        uiBatch.draw(lifeTexture, 50, Gdx.graphics.getHeight() - 150f);
        font.draw(uiBatch, Integer.toString(health), 200f, Gdx.graphics.getHeight() - 60f);

        uiBatch.end();
    }

    @Override
    public void update() {

        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        //Update Game State based on input
        switch (gameState) {

            case PAUSE:
            {
                // Check if the user press the resume button
                resumeButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(resumeButton.isDown){
                    resumePressed = true;
                    //bgMusic.pause(); // pause music as well
                }else if(resumePressed){
                    gameState = GameState.PLAYING;
                    //bgMusic.play();
                    resumePressed = false;
                }
                return;
            }

            case PLAYING:
            {
                // Check if the user press the pause button
                pauseButton.update(Gdx.input.isTouched(), Gdx.input.getX(), Gdx.input.getY());
                if (pauseButton.isDown) {
                    gameState = GameState.PAUSE;
                }

                //Poll user for input
                moveLeftButton.update(checkTouch, touchX, touchY);
                moveRightButton.update(checkTouch, touchX, touchY);
                attackButton.update(checkTouch, touchX, touchY);
                jumpButton.update(checkTouch, touchX, touchY);

                int moveX = 0;
                int moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
                    //moveLeftButton.isDown = true;
                    moveX -= 1;
                    //player.setState(Player.PlayerState.MOVELEFT);
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
                    //moveRightButton.isDown = true;
                    moveX += 1;
                    //player.setState(Player.PlayerState.MOVERIGHT);
                } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || attackButton.isDown) {
                    //attackButton.isDown = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || jumpButton.isDown) {
                    //jumpButton.isDown = true;
                }

                Vector2 playerDelta = new Vector2(0, 0);
                playerDelta.x = moveX * player.MOVING_SPEED * stateTime;
                playerDelta.y = moveY * player.MOVING_SPEED * stateTime;

//                player.update(stateTime);
//                player.getSprite().translate(playerDelta.x, playerDelta.y);

                TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("BaseLayer");
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                boolean blocked = false;


                if (player.getState() == Player.PlayerState.ALIVE) {

                    if (moveX != 0 || moveY != 0) {

                        try {
                            cell = tileLayer.getCell((int) (Math.round((player.getPosition().x + playerDelta.x + (player.PLAYER_WIDTH * 0.25)) / tileLayer.getTileWidth())), (int) ((player.getPosition().y) / tileLayer.getTileHeight()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //System.out.println("y: " + ((player.getPosition().x + playerDelta.x + (player.PLAYER_WIDTH * 0.25)) / tileLayer.getTileWidth()));

                        if (cell != null) {
                            if (cell.getTile().getProperties().containsKey("blocked")) {
                                blocked = true;
                            }
                        }

                        if (blocked) {
                            //System.out.println("yes");
                        } else {
                            //System.out.println("no");
                            player.setState(Player.PlayerState.FALL);
                        }

                    }

                    if (player.getState() == Player.PlayerState.ALIVE) {
                        if (playerDelta.x > 0) {
                            player.setState(Player.PlayerState.MOVERIGHT);
                        } else if (playerDelta.x < 0) {
                            player.setState(Player.PlayerState.MOVELEFT);
                        }
                    }
                }

                if (player.getState() == Player.PlayerState.FALL || player.getState() == Player.PlayerState.FALLING) {
                    cell = tileLayer.getCell((int) (Math.round((player.getPosition().x + (player.PLAYER_WIDTH * 0.25)) / tileLayer.getTileWidth())), (int) ((player.getPosition().y + (player.PLAYER_WIDTH * 1.82f)) / tileLayer.getTileHeight()));

                    System.out.println("ypos: " + (player.getPosition().y));
                    System.out.println("y: " + ((player.getPosition().y + (player.PLAYER_HEIGHT * 2)) / tileLayer.getTileHeight()));
                    if (cell != null) {
                        if (cell.getTile().getProperties().containsKey("killed")) {
                            System.out.println("die");
                            //player.setState(Player.PlayerState.DEAD);
                        } else if (cell.getTile().getProperties().containsKey("blocked")) {
                            player.setState(Player.PlayerState.ALIVE);
                        }
                    } else {
                        System.out.println("null");
                    }
                }

                player.update(stateTime);

                // Move camera with the player
                if (player.getPosition().x > (Gdx.graphics.getWidth() / 2) - 600) {
                    camera.position.x = player.getPosition().x + 600;

                    if (camera.position.x >= Gdx.graphics.getWidth() - 190f) {
                        camera.position.x = Gdx.graphics.getWidth() - 190;
                    }
                }
                if (player.getPosition().y > (Gdx.graphics.getHeight() / 2)) {
                    camera.position.y = player.getPosition().y;
                }
                camera.update();
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
        lifeTexture.dispose();
        pauseTexture.dispose();

        batch.dispose();
        uiBatch.dispose();
        bgBatch.dispose();

        player.dispose();
    }
}
