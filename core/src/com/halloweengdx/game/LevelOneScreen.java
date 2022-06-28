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

import java.util.ArrayList;
import java.util.List;

public class LevelOneScreen extends GameScreen
{
    public final static Vector2 CHECKPOINT_ONE = new Vector2(20, 600);
    public final static Vector2 CHECKPOINT_TWO = new Vector2(3800, 600);
    public final static float RESPAWN_TIME = 1000;

    //core Game
    private HalloweenGdxGame game;

    //assets
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    //tileMap Render
    private TiledMapRenderer tiledMapRenderer;

    //Tile map layer
    private TiledMapTileLayer tileLayer;

    // Player
    private Player player;

    private boolean isJumpHeld, isRightHeld, isLeftHeld, isAttackHeld;

    public static final long LONG_JUMP_PRESS = 200l;
    private long jumpPressedTime;

    private boolean[][] collisionMap;

    private long respawnTime;

    private boolean shouldMove;
    private boolean shouldFall;

    //NPC
    private NPC npc;

    //Enemy
    private List<Enemy> enemies;


    public LevelOneScreen(HalloweenGdxGame game){
        super(game);
        this.game = game;
        newGame();
    }

    public void create(){

        super.create();

        //initialise
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(gameAssetsDB.tiledMap_L1);

        this.tileLayer = (TiledMapTileLayer) gameAssetsDB.tiledMap_L1.getLayers().get("BaseLayer");

        this.shouldMove = false;
        this.shouldFall = false;

        this.collisionMap = new boolean[60][20];

        // real stuff
        // Create player
        // player = new Player(128 * 36,128 * 11); // 600
        this.player = new Player((int)(1500), (int)(600));
        // player = new Player((int)(128*36), (int)(128*11)); // , y = 1100 (platform 2)

        this.npc = new NPC(this.player, (this.tileLayer.getTileWidth() * 45) - 20f, (this.tileLayer.getTileHeight()*13) + 50f, NPC.NPC_TYPE.Vampire);

        for (int y = 0; y < this.tileLayer.getHeight(); y++) {
            for (int x = 0; x < this.tileLayer.getWidth(); x ++) {
                this.collisionMap[x][y] = false;
                if (this.tileLayer.getCell(x,y) != null) {
                    this.collisionMap[x][y] = true;
                }
            }
        }

        // work out tile height
        this.enemies = new ArrayList<Enemy>();

        this.enemies.add(new BatEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 35, 30f + this.tileLayer.getTileHeight() * 12), this.tileLayer, 3));

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 29,  (this.tileLayer.getHeight() - 5) * 128), this.tileLayer, 0));

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 54,  (this.tileLayer.getHeight() - 6) * 128), this.tileLayer, 0));

        this.enemies.add(new SkullEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 53,  this.tileLayer.getTileHeight() * 6), this.tileLayer, 5, SkullEnemy.Skull_TYPE.BOSS));

    }

    public void newGame(){

        //Translate camera to center of screen
        //camera.position.x = 16;
        //camera.position.y = 10;
        //camera.translate(player.getPosition().x, player.getPosition().y);

        super.newGame();

        this.isAttackHeld = false;
        this.isJumpHeld = false;
        this.isRightHeld = false;
        this.isLeftHeld = false;

        this.respawnTime = 0;

        if(this.gameAssetsDB.game_over.isPlaying())
        {
            this.gameAssetsDB.game_over.stop();
            this.gameAssetsDB.l1_music.play();
        }

        create();
    }

    @Override
    public void show() {
        this.gameAssetsDB.l1_music.play();
        this.gameAssetsDB.l1_music.setVolume(0.5f);

    }

    @Override
    public void render(float delta)
    {
        //Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.bgBatch.setProjectionMatrix(camera.combined);
        super.bgBatch.begin();
        super.bgBatch.draw(gameAssetsDB.L1_background, 0,0, 60*128, 20*128);
        super.bgBatch.end();

        if(super.camera != null){
            super.camera.update();
            this.tiledMapRenderer.setView(camera);
            this.tiledMapRenderer.render();
        }

        // Update the game state time
        super.stateTime += Gdx.graphics.getDeltaTime();
        update();


        //Apply camera to spritebatch and draw player
        super.batch.setProjectionMatrix(camera.combined);
        super.batch.begin();

        if(this.npc!=null)
        {
            this.npc.draw(batch);
        }

        for(Enemy e: this.enemies)
        {
            e.draw(super.batch);
        }

        this.player.draw(batch);

        super.batch.end();

        super.uiBatch.begin();

        // Render score
        super.font.draw(uiBatch, "Score: " + super.gameScore, Gdx.graphics.getWidth()/2 - 100f, Gdx.graphics.getHeight() - 60f);

        super.uiBatch.draw(this.gameAssetsDB.lifeTexture, 50, Gdx.graphics.getHeight() - 150f);
        super.font.draw(super.uiBatch, Integer.toString(health), 200f, Gdx.graphics.getHeight() - 60f);

        if(super.gameState == GameState.PLAYING){
            super.moveLeftButton.draw(super.uiBatch);
            super.moveRightButton.draw(super.uiBatch);
            super.jumpButton.draw(super.uiBatch);
            super.attackButton.draw(super.uiBatch);

            super.pauseButton.draw(super.uiBatch);
        }else if(super.gameState == GameState.PAUSE){

            super.resumeButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }
        else if(super.gameState == GameState.FAIL)
        {
            super.restartButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }
        else if(super.gameState == GameState.WIN)
        {
            super.newLevelButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }


        super.uiBatch.end();
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
                this.gameAssetsDB.l1_music.pause();

                // Check if the user press the resume button
                super.resumeButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.resumeButton.isDown){
                    super.resumePressed = true;
                    super.gameState = GameState.PLAYING;
                    this.gameAssetsDB.l1_music.play();
                    super.resumePressed = false;
                }

                //else if(resumePressed){
//                    gameState = GameState.PLAYING;
//                    this.gameAssetsDB.l1_music.play();
//                    resumePressed = false;
//                }
                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown)
                {
                    dispose();
                    Gdx.app.exit();
                    System.exit(-1);

                }
                return;
            }

            case WIN:
            {
                super.newLevelButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.newLevelButton.isDown)
                {
                    this.game.setScreen(HalloweenGdxGame.gameScreenTwo);
                }

                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown)
                {
                    dispose();
                    Gdx.app.exit();
                    System.exit(-1);

                }

                break;

            }

            case FAIL:
            {
                for(int i=this.enemies.size() -1; i>=0; i--){
                    this.enemies.get(i).update(Gdx.graphics.getDeltaTime());
                    if(this.enemies.get(i).getState() == Enemy.EnemyState.DEAD)
                    {
                        this.enemies.remove(i);
                    }
                }

                this.gameAssetsDB.l1_music.stop();
                this.gameAssetsDB.game_over.play();

                super.restartButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.restartButton.isDown)
                {
                    newGame();
                }

                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown)
                {
                    dispose();
                    Gdx.app.exit();
                    System.exit(-1);

                }
                return;
            }

            case PLAYING:
            {
                // Check if the user press the pause button
                super.pauseButton.update(Gdx.input.isTouched(), Gdx.input.getX(), Gdx.input.getY());
                if (super.pauseButton.isDown)
                {
                    super.gameState = GameState.PAUSE;
                }

                if(this.npc!=null && this.npc.getNpcState() == NPC.NPC_STATE.Mission_Complete)
                {
                    this.npc = null;
                }

                if(this.npc!=null)
                {
                    this.npc.update(Gdx.graphics.getDeltaTime());
                }

                for(int i=this.enemies.size() -1; i>=0; i--){
                    this.enemies.get(i).update(Gdx.graphics.getDeltaTime());
                    if(this.enemies.get(i).getState() == Enemy.EnemyState.DEAD)
                    {
                        super.gameScore+=this.enemies.get(i).getScore();
                        this.enemies.remove(i);
                    }
                }

                //Poll user for input
                super.moveLeftButton.update(checkTouch, touchX, touchY);
                super.moveRightButton.update(checkTouch, touchX, touchY);
                super.attackButton.update(checkTouch, touchX, touchY);
                super.jumpButton.update(checkTouch, touchX, touchY);

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || super.moveLeftButton.isDown) {

                    this.isLeftHeld = true;

                    // Prevent player to move out from the tile
                    if(this.player.getPosition().x <= 0){
                        this.isLeftHeld = false;
                    }
                } else{
                    this.isLeftHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || super.moveRightButton.isDown) {

                    this.isRightHeld = true;

                }else{
                    this.isRightHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || super.attackButton.isDown) {
                    this.isAttackHeld = true;
                }else{
                    this.isAttackHeld = false;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || super.jumpButton.isDown) {
                    if(this.player.getState() != Player.PlayerState.JUMPING && this.player.getState() != Player.PlayerState.JUMP_START){
                        this.isJumpHeld = true;
                        this.jumpPressedTime = System.currentTimeMillis();
                        this.player.setState(Player.PlayerState.JUMP_START);
                    }else{
                        if(this.isJumpHeld &&  ((System.currentTimeMillis() - this.jumpPressedTime) >= LONG_JUMP_PRESS)){
                            this.isJumpHeld = false;
                            this.jumpPressedTime = 0;
                        }else{
                            this.player.setState(Player.PlayerState.JUMP_START);
                        }
                    }
                }else{
                    if(this.isJumpHeld){
                        this.isJumpHeld = false;
                        this.jumpPressedTime = 0;
                    }
                }

                int x = 0, y = 0;


                if (this.player.getState() == Player.PlayerState.ALIVE) {

//                    if(isLeftHeld){
//                        x = Math.round((player.getPosition().x / 128)) + 1;
//                    }else{
//                        x = Math.round((player.getPosition().x / 128)) ;
//                    }
                    x = Math.round((this.player.getPosition().x)/ 128) + 1;

                   // y = (int)Math.floor(player.getPosition().y / 128f);

                    System.out.println("Here y");
                    System.out.println(player.getPosition().y);
                    y = Math.round((this.player.getPosition().y + 90f) / 128) - 1;


//                    if(player.getPosition().y < 1000 ){
//                        y = Math.round(((player.getPosition().y + 100) / 128)) - 1;
////                    }else if(player.getPosition().y > 1500 && player.getPosition().y < 1700){
////                        y = Math.round(((player.getPosition().y + 100) / 128)) - 1;
//                    }else if(player.getPosition().y >= 1700){
//                        // have to fix
//                        y = Math.round(((player.getPosition().y + 100) / 128)) - 1;
//                    }else{
//                        y = Math.round(player.getPosition().y / 128) - 1;
//                    }

                    boolean shouldFall = false;
                    if(x >= 0 && x < 60 &&  y >= 0 && y < 20){
                        if(this.collisionMap[x][y] == true){
                            System.out.println("blocked fall");
                            shouldFall = false;
                        }else{
                            System.out.println("not blocked fall");
                            shouldFall = true;
                        }
                    }

                    if(this.player.getState() != Player.PlayerState.DEAD || this.player.getState() != Player.PlayerState.DYING && health > 0){
                        if (shouldFall) {
                            this.player.setState(Player.PlayerState.FALL_START);
                            this.player.setIsOnGround(false);
                        }else{
                            this.player.setState(Player.PlayerState.ALIVE);
                            this.player.setIsOnGround(true);
                        }

                        if (this.player.getState() == Player.PlayerState.ALIVE){
                            if(this.isRightHeld){
                                this.player.setState(Player.PlayerState.MOVERIGHT);
                            }else if(this.isLeftHeld){
                                this.player.setState(Player.PlayerState.MOVELEFT);
                            }
//
//                            if(isJumpHeld){
//                                player.setIsJumpStart(true);
//                                player.setState(Player.PlayerState.JUMPING);
//                            }
                        }
                    }
                }

                if(this.player.getState() == Player.PlayerState.JUMPING){
                    x = Math.round((this.player.getPosition().x / 128));
                    y = Math.round((this.player.getPosition().y / 128));

//                    x = Math.round((player.getPosition().x + (player.PLAYER_WIDTH * 0.5f)) / 120);
//                    y = Math.round((player.getPosition().y - (player.PLAYER_HEIGHT * 0.35f) - 10) / 120);

                    boolean stopJump = false;
                    if(x >= 0 && x < 60 &&  y >= 0 && y < 20){
                        if(this.collisionMap[x][y] == true){
                            System.out.println("should stop jump");
                            stopJump = true;
                        }else{
                            stopJump = false;
                        }
                    }else{
                        stopJump = true;
                    }

                    if (stopJump) {
                        this.isJumpHeld = false;
                        this.jumpPressedTime = 0;
                        this.player.setPosition(player.getPosition().x, player.getPosition().y + 25f);
                        this.player.setIsOnGround(true);
                        //player.setState(Player.PlayerState.ALIVE);
                    }else{
                        this.player.setIsOnGround(false);
                    }
                    this.player.setState(Player.PlayerState.ALIVE);
                }

                if (this.player.getState() == Player.PlayerState.FALLING) {

                    if(this.player.getFacingDirection() == Player.PlayerDirection.LEFT){
                        x = Math.round(((this.player.getPosition().x) / 128));
                    }else{
                        x = Math.round((this.player.getPosition().x / 128)) + 1;
                    }

                    y = Math.round((this.player.getPosition().y / 128));

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
                    }

                    if(fall){
                        this.player.setState(Player.PlayerState.FALL_START);
                        this.player.setIsOnGround(false);
                    }else if(!fall){
                        if(this.player.getPosition().y <= 200){
                            this.player.setState(Player.PlayerState.FALL_START);
                            this.player.setIsOnGround(false);
                        }else{
                            this.player.setPosition(player.getPosition().x, player.getPosition().y + 25f);
                            this.player.setIsOnGround(true);
                            this.player.setState(Player.PlayerState.ALIVE);
                        }
                    }
                }

                if(this.player.getState() == Player.PlayerState.HURT || this.player.isHurt){
                    this.player.isHurt = false;
                    this.player.setState(Player.PlayerState.HURT);
                    this.gameAssetsDB.player_hurt.play();
                    if(super.health >= 1){
                        if (this.respawnTime + RESPAWN_TIME < System.currentTimeMillis()) {
                            super.health -= 1;

                            if(super.health >= 1){
                                // Reset camera position
                                super.camera.position.x = (Gdx.graphics.getWidth() / 2) - 100;
                                super.camera.position.y = player.getPosition().y + 600f;
                                super.camera.update();

                                if(this.player.getPosition().x <= CHECKPOINT_TWO.x){

                                    this.player.setPosition(CHECKPOINT_ONE);
                                }else{
                                    this.player.setPosition(CHECKPOINT_TWO);
                                }

                                this.player.setState(Player.PlayerState.ALIVE);
                                this.respawnTime = System.currentTimeMillis();
                            }
                        }
                    }else{
                        this.player.setState(Player.PlayerState.DYING);
                    }
                }

                if(this.player.getState() == Player.PlayerState.DEAD)
                {
                    this.gameAssetsDB.satire.play();
                    super.gameState = GameState.FAIL;

                }

                if(this.player.isRewarded())
                {
                    Reward tmp_reward = this.player.openReward();

                    if(tmp_reward.getRewardType() == Reward.RewardType.SCORE)
                    {
                        super.gameScore += tmp_reward.getValue();
                    }
                    else
                    {
                        super.health += tmp_reward.getValue();
                    }

                }

                if(super.gameScore >= 50)  //Need to change to higher
                {
                    super.gameState = GameState.WIN;
                }
            }


            // Update player
            this.player.update(stateTime);


//            // Move camera with bat
//            if (this.enemies.get(2).getPosition().x > (Gdx.graphics.getWidth() / 2) - 600) {
//                super.camera.position.x = this.enemies.get(2).getPosition().x + 600;
//
//                if(super.camera.position.x >= ((this.layer.getWidth()*128) - Gdx.graphics.getWidth()/2))
//                {
//                    super.camera.position.x = ((this.layer.getWidth()*128) - Gdx.graphics.getWidth()/2);
//                }
//                else if(super.camera.position.x <= 0)
//                {
//                    super.camera.position.x = 0;
//                }
//            }
//            if (this.enemies.get(2).getPosition().y > (Gdx.graphics.getHeight() / 2)) {
//                super.camera.position.y = this.enemies.get(2).getPosition().y; // can change
//
//                if(super.camera.position.y >= ((this.layer.getHeight()*128) - Gdx.graphics.getHeight()/2))
//                {
//                    super.camera.position.y = ((this.layer.getHeight()*128) - Gdx.graphics.getHeight()/2);
//                }
//                else if(super.camera.position.y <= 0)
//                {
//                    super.camera.position.y = 0;
//                }
//            }
//            super.camera.update();

//            if (this.npc.getPosition().x > (Gdx.graphics.getWidth() / 2) - 600) {
//                super.camera.position.x = this.npc.getPosition().x + 600;
//
//                if(super.camera.position.x >= ((this.layer.getWidth()*128) - Gdx.graphics.getWidth()/2))
//                {
//                    super.camera.position.x = ((this.layer.getWidth()*128) - Gdx.graphics.getWidth()/2);
//                }
//                else if(super.camera.position.x <= 0)
//                {
//                    super.camera.position.x = 0;
//                }
//            }
//
//            if (this.npc.getPosition().y > (Gdx.graphics.getHeight() / 2)) {
//                super.camera.position.y = this.npc.getPosition().y; // can change
//
//                if(super.camera.position.y >= ((this.layer.getHeight()*128) - Gdx.graphics.getHeight()/2))
//                {
//                    super.camera.position.y = ((this.layer.getHeight()*128) - Gdx.graphics.getHeight()/2);
//                }
//                else if(super.camera.position.y <= 0)
//                {
//                    super.camera.position.y = 0;
//                }
//            }
//            super.camera.update();

            // Move camera with the player
            if (this.player.getPosition().x > (Gdx.graphics.getWidth() / 2) - 600f) {
                super.camera.position.x = player.getPosition().x + 200f;

                if(super.camera.position.x >= ((this.tileLayer.getWidth()*128) - Gdx.graphics.getWidth()/2) + 100f)
                {
                    super.camera.position.x = ((this.tileLayer.getWidth()*128) - Gdx.graphics.getWidth()/2 + 100f);
                }
                else if(super.camera.position.x <= 0 + Gdx.graphics.getWidth()/2 - 100f)
                {
                    super.camera.position.x = 0 + Gdx.graphics.getWidth()/2 - 100f ;
                }

            }

            if (this.player.getPosition().y > (Gdx.graphics.getHeight() / 2) - 400f) {
                super.camera.position.y = player.getPosition().y - 60f;
            }else{
                if(super.camera.position.y >= ((this.tileLayer.getHeight()*128) - Gdx.graphics.getHeight()/2))
                {
                    super.camera.position.y = ((this.tileLayer.getHeight()*128) - Gdx.graphics.getHeight()/2);
                }
                else if(super.camera.position.y <= 0)
                {
                    super.camera.position.y = 0;
                }
                else
                {
                    super.camera.position.y = 715f;
                }
            }
            super.camera.update();


            return;
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
    public void hide()
    {
        this.gameAssetsDB.l1_music.stop();
        this.gameAssetsDB.danger_zone_music.stop();
        this.gameAssetsDB.game_over.stop();

    }

    @Override
    public void dispose()
    {
        super.dispose();

        this.player.dispose();

        for(Enemy e: enemies)
        {
            e.dispose();
        }

    }
}
