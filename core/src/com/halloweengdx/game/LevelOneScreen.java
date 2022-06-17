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

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button jumpButton;
    Button attackButton;

    //Storage class for collision
    private Rectangle tileRectangle;

    private float stateTime;

    // Player
    private Player player;

    public LevelOneScreen(HalloweenGdxGame game){

        super(game);
        create();
        newGame();
    }

    private void create(){
        tiledMap = new TmxMapLoader().load("TileMap/tile_map_level01.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Create player
        //player = new Player(25,480);
        player = new Player(25,400);

        buttonSquareTexture = new Texture("button/button_square_blue.png");
        buttonSquareDownTexture = new Texture("button/button_square_beige.png");

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Buttons
        float buttonSize = h * 0.2f * 0.95f;
        moveLeftButton = new Button(0.0f, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button(buttonSize*2, buttonSize - 80f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        attackButton = new Button(w - 250f, buttonSize * 1.15f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        jumpButton = new Button(w - 500f, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);

        //Collision
//        tileRectangle = new Rectangle();
//        MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
//        TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;
//        tileRectangle.width = tileLayer.getTileWidth();
//        tileRectangle.height = tileLayer.getTileHeight();
    }

    private void newGame(){
        gameState = GameState.PLAYING;
        stateTime = 0;

        //Translate camera to center of screen
        //camera.position.x = 16;
        //camera.position.y = 10;
        //camera.translate(player.getPosition().x, player.getPosition().y);

        stateTime = 0f;
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

        camera.update();
        this.tiledMapRenderer.setView(camera);
        this.tiledMapRenderer.render();

        // Update the game state time
        stateTime += Gdx.graphics.getDeltaTime();
        update();

        //Apply camera to spritebatch and draw player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();


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
    public void update() {

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
//
//                player.update(stateTime);
//                player.getSprite().translate(playerDelta.x, playerDelta.y);

                TiledMapTileLayer tileLayer = (TiledMapTileLayer)tiledMap.getLayers().get("BaseLayer");
                //System.out.println(tileLayer.getCell(5, 15));

                if(moveX != 0 || moveY != 0){

//
//                    float tileWidth = ((TiledMapTileLayer) collisionLayer).getTileWidth();
//                    float tileHeight = ((TiledMapTileLayer) collisionLayer).getTileHeight();
//
//                    boolean collisonX = false, collisionY = false;
//                    TiledMapTileLayer.Cell  targetTile = null;
//

//                    //TODO Determine bounds to check within
//                    // Find top-right corner tile
//                    int right = (int) Math.ceil(Math.max(player.getPosition().x + (player.PLAYER_WIDTH * 0.6), player.getPosition().x + (player.PLAYER_WIDTH * 0.6) + playerDelta.x));
//                    int top = (int) Math.ceil(Math.max(player.getPosition().y + (player.PLAYER_HEIGHT * 0.6), player.getPosition().y + (player.PLAYER_HEIGHT * 0.6) + playerDelta.y));
//
//                    int left = (int) Math.floor(Math.min(player.getPosition().x, player.getPosition().x + playerDelta.x));
//                    int bottom = (int) Math.floor(Math.min(player.getPosition().y, player.getPosition().y + playerDelta.y));
//
//                    // Divide bounds by tile sizes to retrieve tile indices
//                    right /= tileLayer.getTileWidth();
//                    top /= tileLayer.getTileHeight();
//                    left /= tileLayer.getTileWidth();
//                    bottom /= tileLayer.getTileHeight();
//
//                    //TODO Loop through selected tiles and correct by each axis
//                    //EXTRA: Try counting down if moving left or down instead of counting up
//                    for (int y = bottom; y <= top; y++) {
//                        for (int x = left; x <= right; x++) {
//                            TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
//                            // If the cell is empty, ignore it
//                            if (targetCell == null) continue;
//
//                            if(targetCell != null){
//                                if(targetCell.getTile().getProperties().containsKey("blocked")){
//
////                                    Rectangle tileCollider = new Rectangle();
////                                    tileCollider.x = x * tileLayer.getTileWidth();
////                                    tileCollider.y = y * tileLayer.getTileHeight();
////
////                                    Rectangle playerCollider = new Rectangle();
////                                    playerCollider.x = player.getPosition().x + playerDelta.x;
////                                    playerCollider.y =  player.getPosition().y;
////
////                                    if (playerCollider.overlaps(tileCollider)) System.out.println("hey");
//
//
//                                    System.out.println(x + " " + y);
//                                    //System.out.println((player.getPosition().x + playerDelta.x) + " " + (playerDelta.y + player.getPosition().y ));
//                                    playerDelta.x = 0;
//                                    playerDelta.y = 0;
//                                }
//                            }
//                        }
//                    }

                    if(playerDelta.x > 0){
                        player.setState(Player.PlayerState.MOVERIGHT);
                    }else if(playerDelta.x < 0){
                        player.setState(Player.PlayerState.MOVELEFT);
                    }

                    // testing
                    TiledMapTileLayer.Cell cell =  new TiledMapTileLayer.Cell();
                    boolean blocked = false;
                    try {
                        cell = tileLayer.getCell((int) (Math.round(player.getPosition().x / tileLayer.getTileWidth())), (int) (player.getPosition().y/ tileLayer.getTileHeight()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.out.println("x: " + (player.getPosition().x / 7.1f));
                    //System.out.println("x: " + (Math.round(player.getPosition().x / tileLayer.getTileWidth())));
                    //System.out.println("y: " + ((player.getPosition().y / tileLayer.getTileHeight())*3));
                    if(cell != null){
                        if (cell.getTile().getProperties().containsKey("blocked")) {
                            blocked = true;
                        }
                    }else{
                        //System.out.println("null");
                    }

                    if(blocked){
                        System.out.println("yes");
                    }else{
                        System.out.println("no");
                    }


                }

                player.update(stateTime);
                camera.translate(playerDelta);

                //camera.position.x = player.getPosition().x;
                //camera.position.y = player.getPosition().y;
                //camera.update();
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
