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

    private boolean isJumpHeld, isRightHeld, isLeftHeld, isAttackHeld;

    public static final long LONG_JUMP_PRESS = 100l;
    private long jumpPressedTime;

    private TiledMapTileLayer tileLayer;


    // Player
    private Player player;

    public final static Vector2 CHECKPOINT_ONE = new Vector2(20, 600);
    public final static Vector2 CHECKPOINT_TWO = new Vector2(3800, 600);
    public final static float RESPAWN_TIME = 20;

    private boolean[][] collisionMap = new boolean[60][20];

    private float respawnTime;

    private boolean shouldMove = false;
    private boolean shouldFall = false;

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
        //player = new Player((int)(CHECKPOINT_ONE.x), (int)(CHECKPOINT_ONE.y)); // , y = 1100 (platform 2)
        player = new Player((int)(1500), (int)(600)); // , y = 1100 (platform 2)

        tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("BaseLayer");
        for (int y = 0; y < tileLayer.getHeight(); y++) {
            for (int x = 0; x < tileLayer.getWidth(); x ++) {
                this.collisionMap[x][y] = false;
                if (tileLayer.getCell(x,y) != null) {
                    this.collisionMap[x][y] = true;
                }
            }
        }

    }

    public void newGame(){

        //Translate camera to center of screen
        //camera.position.x = 16;
        //camera.position.y = 10;
        //camera.translate(player.getPosition().x, player.getPosition().y);

        super.newGame();

        isAttackHeld = false;
        isJumpHeld = false;
        isRightHeld = false;
        isLeftHeld = false;

        respawnTime = 0;
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

        System.out.println("x");
        System.out.println(player.getPosition().x);

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


                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || moveLeftButton.isDown) {

                    if(!isLeftHeld) isLeftHeld = true;

                    // Prevent player to move out from the tile
                    if(player.getPosition().x <= 0){
                        isLeftHeld = false;
                    }
                } else{
                    isLeftHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || moveRightButton.isDown) {

                    // Prevent pressing left and right on the same time
                    if(!isLeftHeld){
                        isRightHeld = true;
                    }else{
                        moveRightButton.isDown = false;
                        isRightHeld = false;
                    }

                }else{
                    isRightHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || attackButton.isDown) {
                    isAttackHeld = true;
                }else{
                    isAttackHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || jumpButton.isDown) {
                    if(player.getState() != Player.PlayerState.JUMPING && player.getState() != Player.PlayerState.JUMP_START){
                        isJumpHeld = true;
                        jumpPressedTime = System.currentTimeMillis();
                        player.setState(Player.PlayerState.JUMP_START);
                    }else{
                        if(isJumpHeld &&  ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)){
                            isJumpHeld = false;
                            jumpPressedTime = 0;
                        }else{
                            player.setState(Player.PlayerState.JUMP_START);
                        }
                    }
                }else{
                    if(isJumpHeld){
                        isJumpHeld = false;
                        jumpPressedTime = 0;
                    }
                }

                int x = 0, y = 0;


                if (player.getState() == Player.PlayerState.ALIVE) {

//                    if(isLeftHeld){
//                        x = Math.round((player.getPosition().x / 128)) + 1;
//                    }else{
//                        x = Math.round((player.getPosition().x / 128)) ;
//                    }
                    x = Math.round((player.getPosition().x / 128)) + 1;
                    y = Math.round((player.getPosition().y / 128)) - 1;

                    boolean shouldFall = false;
                    if(x >= 0 && x < 60 &&  y >= 0 && y < 20){
                        if(collisionMap[x][y] == true){
                            System.out.println("blocked fall");
                            shouldFall = false;
                        }else{
                            System.out.println("not blocked fall");
                            shouldFall = true;
                        }
                    }else{
                        shouldFall = false;
                    }

                    if(player.getState() != Player.PlayerState.DEAD || player.getState() != Player.PlayerState.DYING && health > 0){
                        if (shouldFall && !player.getIsOnGround()) {
                            player.setState(Player.PlayerState.FALL);
                        }else{
                            player.setState(Player.PlayerState.ALIVE);
                        }

                        if (player.getState() == Player.PlayerState.ALIVE){
                            if(isRightHeld){
                                player.setState(Player.PlayerState.MOVERIGHT);
                            }else if(isLeftHeld){
                                player.setState(Player.PlayerState.MOVELEFT);
                            }
//
//                            if(isJumpHeld){
//                                player.setIsJumpStart(true);
//                                player.setState(Player.PlayerState.JUMPING);
//                            }
                        }
                    }
                }

                if(player.getState() == Player.PlayerState.JUMPING){
                    x = Math.round((player.getPosition().x / 128));
                    y = Math.round((player.getPosition().y / 128));

//                    x = Math.round((player.getPosition().x + (player.PLAYER_WIDTH * 0.5f)) / 120);
//                    y = Math.round((player.getPosition().y - (player.PLAYER_HEIGHT * 0.35f) - 10) / 120);

                    boolean stopJump = false;
                    if(x >= 0 && x < 60 &&  y >= 0 && y < 20){
                        if(collisionMap[x][y] == true){
                            System.out.println("should stop jump");
                            stopJump = true;
                        }else{
                            stopJump = false;
                        }
                    }else{
                        stopJump = true;
                    }

                    if (stopJump) {
                        player.setState(Player.PlayerState.ALIVE);
                        isJumpHeld = false;
                        jumpPressedTime = 0;
                        player.setPosition(player.getPosition().x, player.getPosition().y + 40f);
                    }
                }
                if (player.getState() == Player.PlayerState.FALL || player.getState() == Player.PlayerState.FALLING) {

//                    if(player.getFacingDirection() == Player.PlayerDirection.LEFT){
//                        //x = Math.round((player.getPosition().x + (player.PLAYER_WIDTH * 0.3f) + 10) / 120);
//                        //x = Math.round((player.getPosition().x - (player.PLAYER_WIDTH * 0.35f) - 10) / 120);
//                    }else{
//                        //x = Math.round((player.getPosition().x - (player.PLAYER_WIDTH * 0.3f) - 10) / 120);
//                        x = Math.round((player.getPosition().x + (player.PLAYER_WIDTH * 0.35f) - 10) / 120);
//                    }
//                    x = Math.round(((player.getPosition().x) / 120));
//                    x += 1;
//                    y = Math.round((player.getPosition().y - (player.PLAYER_HEIGHT / 5f)) / 120);

                    if(player.getFacingDirection() == Player.PlayerDirection.LEFT){
                        x = Math.round(((player.getPosition().x) / 128));
                    }else{
                        x = Math.round((player.getPosition().x / 128)) + 1;
                    }

                    y = Math.round((player.getPosition().y / 128));

                    boolean fall = false;
                    if(x >= 0 && x < 60 &&  y >= 0 && y < 20){
                        if(collisionMap[x][y] == true){
                            System.out.println("should not fall");
                            fall = false;
                        }else{
                            if(player.getPosition().y <= 15f){
                                fall = false;
                            }else{
                                fall = true;
                            }
                        }
                    }else{
                        fall = false;
                    }

                    if(fall){
                        player.setState(Player.PlayerState.FALL);
                    }else if(!fall){
                        player.setState(Player.PlayerState.ALIVE);

                        if(player.getPosition().y <= 200){
                            player.setState(Player.PlayerState.FALL);
                        }else{
                            player.setPosition(player.getPosition().x, player.getPosition().y + 25);
                        }
//                        if(player.getPosition().y > 1000){
//                            player.setPosition(player.getPosition().x, player.getPosition().y + 30);
//                        }else{
//                            if(player.getPosition().y <= 300){
//                                player.setState(Player.PlayerState.FALL);
//                            }else{
//                                player.setPosition(player.getPosition().x, player.getPosition().y + 25);
//                            }
//                        }

                    }
                }
                if(player.getState() == Player.PlayerState.DEAD || player.getState() == Player.PlayerState.DYING){
                    if(health > 0){
                        respawnTime += 1;

                        if(respawnTime >= RESPAWN_TIME){
                            health -= 1;

                            // Reset camera position
                            camera.position.x = (Gdx.graphics.getWidth() / 2) + 300;
                            camera.position.y = player.getPosition().y + 600f;
                            camera.update();

                            if(player.getPosition().x <= CHECKPOINT_TWO.x){
                                player.setPosition(CHECKPOINT_ONE);
                            }else{
                                player.setPosition(CHECKPOINT_TWO);
                            }

                            player.setState(Player.PlayerState.ALIVE);
                            respawnTime = 0;
                        }
                    }else{
                        // restart button
                    }
                }

            }


            // Update player
            player.update(stateTime);

            // Move camera with the player
            if (player.getPosition().x > (Gdx.graphics.getWidth() / 2) - 600f) {
                camera.position.x = player.getPosition().x + 600f;

//                    if (camera.position.x >= Gdx.graphics.getWidth() - 190f) {
//                        camera.position.x = Gdx.graphics.getWidth() - 190;
//                    }
            }
            if (player.getPosition().y > (Gdx.graphics.getHeight() / 2) + 400f) {
                camera.position.y = player.getPosition().y;
            }else{
                camera.position.y = 715f;
            }
            camera.update();
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
