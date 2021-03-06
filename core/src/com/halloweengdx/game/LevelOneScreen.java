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

/**
 * This game class handles logics for level one gameplay
 */
public class LevelOneScreen extends GameScreen
{
    public final static Vector2 CHECKPOINT_ONE = new Vector2(20, 600);      // The first checkpoint to respawn the player if player's health is deducted
    public final static Vector2 CHECKPOINT_TWO = new Vector2(3800, 600);    // The second checkpoint to respawn the player if player's health is deducted

    public final static int MAP_WIDTH = 60;     // The map width for level 1
    public final static int MAP_HEIGHT = 20;    // The map height for level 1

    //assets
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    //tileMap Render
    private TiledMapRenderer tiledMapRenderer;

    //Tile map layer
    private TiledMapTileLayer tileLayer;

    // Player
    private Player player;

    //NPC
    private NPC npc;

    //Enemy
    private List<Enemy> enemies;

    // A variable to track if the player has passed through checkpoint 2, extra helpers has been spawned or not
    private boolean hasSpawnedHelper = false;

    /**
     * Level one constructor
     */
    public LevelOneScreen(HalloweenGdxGame game){
        super(game);
        this.game = game;

        // Initialise variables needed
        this.create();
        this.newGame();
    }

    /**
     *
     */
    public void create(){

        super.create();

        // Initialise
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(gameAssetsDB.tiledMap_L1);
        this.tileLayer = (TiledMapTileLayer) gameAssetsDB.tiledMap_L1.getLayers().get("BaseLayer");

        // Set the map width and height
        this.mapWidth = MAP_WIDTH;
        this.mapHeight = MAP_HEIGHT;

        // Initialise collision map
        this.collisionMap = new boolean[MAP_WIDTH][MAP_HEIGHT];
        for (int y = 0; y < this.tileLayer.getHeight(); y++) {
            for (int x = 0; x < this.tileLayer.getWidth(); x ++) {
                this.collisionMap[x][y] = false;
                if (this.tileLayer.getCell(x,y) != null) {
                    this.collisionMap[x][y] = true;
                }
            }
        }

        // Create player
        this.player = new Player((int)CHECKPOINT_ONE.x, (int)CHECKPOINT_ONE.y);

        // Create NPC
        this.npc = new NPC(this.player, (this.tileLayer.getTileWidth() * 45) - 20f, (this.tileLayer.getTileHeight()*13) + 50f, NPC.NPC_TYPE.Vampire);

        // Initialise the list to store all created enemy instances
        this.enemies = new ArrayList<Enemy>();
    }

    /**
     * Create and spawn enemy instances in the game world
     */
    private void spawnEnemyInMap(){
        this.enemies.add(new BatEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 35, 30f + this.tileLayer.getTileHeight() * 12), this.tileLayer, 50 ,3, false));

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 29,  (this.tileLayer.getHeight() - 5) * 128), this.tileLayer, 50, 0, false));

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 54,  (this.tileLayer.getHeight() - 6) * 128), this.tileLayer, 50, 0, false));

        this.enemies.add(new SkullEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 53,  this.tileLayer.getTileHeight() * 6), this.tileLayer, 100,5, true));

    }

    /**
     * Reset everything for every new game
     */
    public void newGame()
    {
        super.newGame();

        if(GameAssetsDB.getInstance().game_over.isPlaying())
        {
            GameAssetsDB.getInstance().game_over.stop();
            GameAssetsDB.getInstance().l1_music.play();
        }

        hasSpawnedHelper = false;

        // Remove the old enemies for every restart
        this.enemies.clear();
        // Respawn enemy every new game
        spawnEnemyInMap();

        // Reset NPC
        if(npc != null) npc.reset();

        // Reset player
        if(player != null) player.reset();

        // Reset the camera position
        super.camera.position.x = (Gdx.graphics.getWidth() / 2) - 100;
        super.camera.position.y = player.getPosition().y + 600f;
        super.camera.update();
    }

    /**
     * If this level is shown on screen, play the music that is specific for this level
     */
    @Override
    public void show() {
        GameAssetsDB.getInstance().l1_music.play();
        GameAssetsDB.getInstance().l1_music.setVolume(0.5f);
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

        // Draw NPC
        if(this.npc != null) this.npc.draw(batch);

        // Draw enemies
        for(Enemy e: this.enemies){
            e.draw(super.batch);
        }

        this.player.draw(batch);

        super.batch.end();

        super.uiBatch.begin();

        // Render score
        super.font.draw(uiBatch, "Score: " + super.gameScore, Gdx.graphics.getWidth()/2 - 100f, Gdx.graphics.getHeight() - 60f);

        super.uiBatch.draw(GameAssetsDB.getInstance().lifeTexture, 50, Gdx.graphics.getHeight() - 150f);
        super.font.draw(super.uiBatch, Integer.toString(player.getHealth()), 200f, Gdx.graphics.getHeight() - 60f);

        if(super.gameState == GameState.PLAYING){
            super.moveLeftButton.draw(super.uiBatch);
            super.moveRightButton.draw(super.uiBatch);
            super.jumpButton.draw(super.uiBatch);
            super.attackButton.draw(super.uiBatch);

            super.pauseButton.draw(super.uiBatch);
        }else if(super.gameState == GameState.PAUSE){
            super.resumeButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }else if(super.gameState == GameState.FAIL){
            super.restartButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }else if(super.gameState == GameState.WIN){
            super.newLevelButton.draw(super.uiBatch);
            super.exitButton.draw(super.uiBatch);
        }

        super.uiBatch.end();
    }

    @Override
    public void update() {

        // Update player regardless the state
        player.update(stateTime);

        if(super.gameState == GameState.PLAYING || super.gameState == GameState.WIN)
        {
            // Remove enemy
            for(int i=this.enemies.size() -1; i>=0; i--)
            {
                this.enemies.get(i).update(Gdx.graphics.getDeltaTime());

                if(this.enemies.get(i).getState() == Enemy.EnemyState.DEAD)
                {
                    // Add score if the enemy is killed
                    // Special for the licking enemy type (as it can be killed by jumping over it)
                    super.gameScore += this.enemies.get(i).getScore();
                    this.enemies.remove(i);
                }
            }
        }

        //Update Game State based on input
        switch (gameState) {

            case PAUSE:
            {
                GameAssetsDB.getInstance().l1_music.pause();

                // Check if the user presses the resume button
                super.resumeButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.resumeButton.isDown){
                    super.resumePressed = true;
                    super.gameState = GameState.PLAYING;
                    GameAssetsDB.getInstance().l1_music.play();
                    super.resumePressed = false;
                }

                // If the player presses the exit button
                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown){
                    dispose();
                    Gdx.app.exit();
                    System.exit(-1);
                }
                return;
            }
            case WIN:
            {
                super.newLevelButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.newLevelButton.isDown){
                    // Save the game score to retrieve again for next level
                    this.game.levelScores.set(this.game.currentLevel, this.gameScore);
                    this.game.currentLevel += 1;
                    this.game.setScreen(this.game.gameLevels.get(game.currentLevel));
                }

                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown){
                    dispose();
                    Gdx.app.exit();
                    System.exit(-1);
                }

                break;

            }
            case FAIL:
            {
                GameAssetsDB.getInstance().l1_music.stop();
                GameAssetsDB.getInstance().game_over.play();

                // If the player press restart
                super.restartButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.restartButton.isDown){
                    newGame();
                }

                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown){
                    this.game.dispose();
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

                // Update NPC state
                if(this.npc!=null) this.npc.update(Gdx.graphics.getDeltaTime());

                // Play music
                if(GameAssetsDB.getInstance().danger_zone_music.isPlaying()){
                    GameAssetsDB.getInstance().l1_music.pause();
                }else{
                    if(GameAssetsDB.getInstance().l1_music.isPlaying() == false){
                        GameAssetsDB.getInstance().l1_music.play();
                    }
                }

                // Spawn more licking enemies to increase difficulty when the player is close to the final boss
                if(player.getPosition().x > ((mapWidth - 17) * 128f)){
                    spawnBossHelperEnemy();
                }

                // Detect if the player press any game buttons that control the player movement
                gameController();

                int x = 0, y = 0;

                // Weird method to prevent the player stuck in a platform
                // Skip checking falling on a platform if the player is not stand on a platform
                if(player.getPreviousState() == Player.PlayerState.FALLING && player.getState() == Player.PlayerState.ALIVE && aliveFall == 0){
                    aliveFall += 1;
                }

                if(aliveFall >= 1) aliveFall += 1;

                if(aliveFall > 2 && aliveFall < 5){
                    skipCheckState = true;
                }else if(aliveFall >= 5){
                    aliveFall = 0;
                    skipCheckState = false;
                }

                // Allow the player to jump when the player state is in jump, alive or even fall
                if(this.player.getState() != Player.PlayerState.HURT && this.player.getState() != Player.PlayerState.HURTING && this.player.getState() != Player.PlayerState.HURT_END && this.player.getState() != Player.PlayerState.DEAD && this.player.getState() != Player.PlayerState.DYING){

                    // For game score
                    // Check if the player weapon hit any enemy
                    for(Enemy e:enemies){
                        for (Weapon w:player.getWeapons()){
                            if(e.getCollider() != null && (e.getState() != Enemy.EnemyState.DYING || e.getState() != Enemy.EnemyState.DEAD)){
                                if(w.getState() == Weapon.WeaponState.ACTIVE && w.getCollider().overlaps(e.getCollider()))
                                {
                                    // Kill the enemy
                                    e.setState(Enemy.EnemyState.DYING);

                                    // Since not all player can be killed by weapon
                                    // After adding score for those that can be killed by weapon
                                    // Set score to 0
                                    super.gameScore += e.getScore();
                                    e.setScore(0);

                                    // If the player kill the final boss, the player win
                                    if(e.isFinalBoss())
                                    {
                                        gameState = GameState.WIN;
                                    }
                                    w.setState(Weapon.WeaponState.DEAD);
                                }
                            }
                        }
                    }

                    // If the user press jump
                    if(isJumpHeld){
                        player.setState(Player.PlayerState.JUMP_START);
                    }

                    // If the player somewhat jump out from the tilemap
                    // Reset the position
                    if(player.getPosition().x <= 0){
                        player.setPosition(0, player.getPosition().y);
                    }else if(player.getPosition().x >= ((MAP_WIDTH * 128) - 240)){
                        player.setPosition(((MAP_WIDTH * 128) - 240), player.getPosition().y);
                    }
                }

                if(this.player.getState() == Player.PlayerState.HURT_END){

                    if(player.getHealth() > 0){
                        // Reset camera position
                        super.camera.position.x = (Gdx.graphics.getWidth() / 2) - 100;
                        super.camera.position.y = player.getPosition().y + 600f;
                        super.camera.update();

                        if(this.player.getPosition().x <= CHECKPOINT_TWO.x){
                            this.player.setPosition(CHECKPOINT_ONE);
                        }else{
                            this.player.setPosition(CHECKPOINT_TWO);
                        }
                        this.player.setFacingDirection(Player.PlayerDirection.RIGHT);
                        this.player.setState(Player.PlayerState.ALIVE);
                    }else{
                        player.setState(Player.PlayerState.DYING);
                    }
                }else if(this.player.getState() == Player.PlayerState.ALIVE) {
                    // Check if the player is walking on a platform
                    x = Math.round((this.player.getPosition().x)/ 128) + 1;
                    y = (int)Math.floor((this.player.getPosition().y) / 128);
                    boolean bottomBlocked = isBlocked(x, y);

                    // Check if the player is standing in a platform
                    x = Math.round((this.player.getPosition().x)/ 128);
                    y = Math.round((this.player.getPosition().y) / 128);
                    boolean middleBlocked = isBlocked(x, y);

                    // Check if the left is blocked
                    x = Math.round((this.player.getPosition().x)/ 128);
                    y = Math.round((this.player.getPosition().y + 60f) / 128);
                    boolean isLeftBlocked = isBlocked(x, y);

                    // Check if the right is blocked
                    x = Math.round((this.player.getPosition().x)/ 128) + 1;
                    y = Math.round((this.player.getPosition().y + 60f) / 128);
                    boolean isRightBlocked = isBlocked(x, y);

                    if(this.player.getState() == Player.PlayerState.ALIVE){
                        // If the player is not walking on a platform
                        if (!bottomBlocked || middleBlocked) {
                            this.player.setState(Player.PlayerState.FALL_START);
                        }else if(bottomBlocked){
                            this.player.setState(Player.PlayerState.ALIVE);
                        }

                        if (this.player.getState() == Player.PlayerState.ALIVE){
                            // If the right button is held
                            if(this.isRightHeld && !isRightBlocked){
                                this.player.setState(Player.PlayerState.MOVERIGHT);
                                isRightHeld = false;
                            }else{
                                isRightHeld = false;
                            }

                            // If the left button is held
                            if(this.isLeftHeld && !isLeftBlocked){
                                this.player.setState(Player.PlayerState.MOVELEFT);
                                isLeftHeld = false;
                            }else{
                                isLeftHeld = false;
                            }

                            // If the attack button is held
                            if(this.isAttackHeld){
                                player.setState(Player.PlayerState.ATTACK);
                                isAttackHeld = false;
                            }
                        }
                    }
                } else if(this.player.getState() == Player.PlayerState.JUMPING){
                    x = Math.round((this.player.getPosition().x / 128));
                    y = Math.round((this.player.getPosition().y / 128));
                    boolean middleBlocked = isBlocked(x, y);

                    x = Math.round((this.player.getPosition().x)/ 128) + 1;
                    y = Math.round((this.player.getPosition().y - 250) / 128);
                    boolean bottomBlocked = isBlocked(x, y);

                    if (!middleBlocked && bottomBlocked) {
                        this.player.setPosition(player.getPosition().x, player.getPosition().y + 25f);
                        player.setState(Player.PlayerState.ALIVE);
                    }else{
                        this.player.setState(Player.PlayerState.FALL_START);
                    }
                }else if (this.player.getState() == Player.PlayerState.FALLING) {

                    if(player.getPosition().y <= 15){
                        player.setState(Player.PlayerState.HURT);
                    }

                    if(player.getState() != Player.PlayerState.HURT && player.getState() != Player.PlayerState.HURTING && player.getState() != Player.PlayerState.HURT_END){

                        if(this.player.getFacingDirection() == Player.PlayerDirection.LEFT){
                            x = Math.round(((this.player.getPosition().x + 50f)/128));
                        }else{
                            x = Math.round((this.player.getPosition().x / 128)) + 1;
                        }
                        y = Math.round((this.player.getPosition().y) / 128);
                        boolean bottomBlocked = isBlocked(x, y);

                        if(!bottomBlocked || skipCheckState){
                            this.player.setState(Player.PlayerState.FALL_START);
                        }else{
                            if(this.player.getPosition().y <= 200){
                                this.player.setState(Player.PlayerState.FALL_START);
                            }else{
                                this.player.setPosition(player.getPosition().x, player.getPosition().y + 20f);
                                this.player.setState(Player.PlayerState.ALIVE);
                            }
                        }
                    }
                }else if(this.player.getState() == Player.PlayerState.DEAD) {
                    GameAssetsDB.getInstance().satire.play();
                    super.gameState = GameState.FAIL;
                }

            }

            moveCameraToFollowPlayer();

            return;
        }
    }

    /**
     * Spawn more licking enemies to increase the difficulty for killing the final boss
     * Spawn only once
     */
    private void spawnBossHelperEnemy()
    {
        if(hasSpawnedHelper){
            return;
        }

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 50, (5 * 128f) - 40f), this.tileLayer, 50, 0, false));

        this.enemies.add(new LickingEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 56,  (5 * 128f) - 40f), this.tileLayer, 50, 0, false));

        hasSpawnedHelper = true;
    }

    /**
     * Move camera with the player
     */
    private void moveCameraToFollowPlayer(){
        // Set camera x
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

        // Set camera y
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
    }

    /**
     * Game controller to detect game play control for playing the game
     * This function detect the following:
     * The move left button to control the player left movement
     * The move right button to control the player right movement
     * The attack button to control the player attack movement
     * The jump button to control the player jump movement depend on the player's current facing direction
     * All buttons to control the player's movement will be frozen if the player is hurting, dying or dead
     */
    private void gameController(){
        // Disable the user to press any during these states
        if(player.getState() == Player.PlayerState.HURT || player.getState() == Player.PlayerState.HURTING || player.getState() == Player.PlayerState.DYING || player.getState() == Player.PlayerState.DEAD){
            return;
        }

        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        // Poll user for input
        super.moveLeftButton.update(checkTouch, touchX, touchY);
        super.moveRightButton.update(checkTouch, touchX, touchY);
        super.attackButton.update(checkTouch, touchX, touchY);
        super.jumpButton.update(checkTouch, touchX, touchY);

        // Detect if the the left button is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) || super.moveLeftButton.isDown) {

            this.isLeftHeld = true;

            // Prevent player to move out from the tile
            if(this.player.getPosition().x <= 0){
                this.isLeftHeld = false;
            }
        } else{
            this.isLeftHeld = false;
        }

        // Detect if the the left right is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) || super.moveRightButton.isDown) {
            // Prevent player to go out from the screen
            if(player.getPosition().x < ((MAP_WIDTH * 128) - 240) ) this.isRightHeld = true;
        }else{
            this.isRightHeld = false;
        }

        // Detect if the the jump button is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) || super.jumpButton.isDown) {
            if(this.jumpPressedTime == 0){
                this.isJumpHeld = true;
                this.jumpPressedTime += 1f;
            }else if(this.jumpPressedTime < (JUMP_PRESS_COOLDOWN/4)){
                this.isJumpHeld = true;
            }
            else{
                this.isJumpHeld = false;
            }
        }else{
            this.isJumpHeld = false;
            this.jumpPressedTime = 0;
        }

        // Cool down jump press
        if(this.jumpPressedTime > JUMP_PRESS_COOLDOWN){
            this.jumpPressedTime = 0;
        }else if(this.jumpPressedTime >= 1){
            this.jumpPressedTime += 1;
        }

        // Detect if the the attack button is pressed
        if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) ||attackButton.isDown){
            if(attackPressedTime == 0) {
                isAttackHeld = true;
                attackPressedTime += 1;
            }else{
                isAttackHeld = false;
            }
        }else{
            isAttackHeld = false;
        }

        // Cool down attack press
        if(attackPressedTime > ATTACK_PRESS_COOLDOWN){
            attackPressedTime = 0;
        }else if(attackPressedTime >= 1){
            attackPressedTime += 1;
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
        GameAssetsDB.getInstance().l1_music.stop();
        GameAssetsDB.getInstance().danger_zone_music.stop();
        GameAssetsDB.getInstance().game_over.stop();
    }

    @Override
    public void dispose()
    {
        this.player.dispose();

        for(Enemy e: enemies)
        {
            e.dispose();
        }

    }
}
