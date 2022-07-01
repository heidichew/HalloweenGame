package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class LevelTwoScreen extends GameScreen
{

    public final static Vector2 CHECKPOINT_ONE = new Vector2(20, 600);
    public final static Vector2 CHECKPOINT_TWO = new Vector2(3800, 600);

    public final static int MAP_WIDTH = 80;
    public final static int MAP_HEIGHT = 20;

    // The spike's y position after falling into a pit
    public final static float SPIKE_TRAP_Y = 15;

    //tileMap Render
    private TiledMapRenderer tiledMapRenderer;

    //Tile map layer
    private TiledMapTileLayer tileLayer;

    // Player
    private Player player;

    private boolean[][] collisionMap;

    //NPC
    private NPC npc;

    //Enemy
    private List<Enemy> enemies;

    public LevelTwoScreen(HalloweenGdxGame game)
    {
        super(game);
        newGame();
    }

    @Override
    public void create()
    {
        super.create();

        //initialise
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(GameAssetsDB.getInstance().tiledMap_L2);

        this.tileLayer = (TiledMapTileLayer) GameAssetsDB.getInstance().tiledMap_L2.getLayers().get("BaseLayer");

        // Create collision map
        this.collisionMap = new boolean[MAP_WIDTH][MAP_HEIGHT];
        for (int y = 0; y < this.tileLayer.getHeight(); y++) {
            for (int x = 0; x < this.tileLayer.getWidth(); x ++) {
                this.collisionMap[x][y] = false;
                if (this.tileLayer.getCell(x,y) != null) {
                    this.collisionMap[x][y] = true;
                }
            }
        }

        // real stuff
        // Create player
        // player = new Player(128 * 36,128 * 11); // 600
        this.player = new Player((int)CHECKPOINT_ONE.x, (int)CHECKPOINT_TWO.y);
        // player = new Player((int)(128*36), (int)(128*11)); // , y = 1100 (platform 2)

        this.npc = null;

        //Enemy
        this.enemies = new ArrayList<Enemy>();

        this.enemies.add(new NecromancerBoss(
                this.player, new Vector2(this.tileLayer.getTileWidth(), (this.tileLayer.getTileHeight()*18) - 25f), this.tileLayer, 200, 6, true));

        this.enemies.add(new SkullEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 36,  this.tileLayer.getTileHeight() * 6), this.tileLayer, 50,6, false));

        this.enemies.add(new SkullEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 55,  this.tileLayer.getTileHeight() * 7), this.tileLayer, 50,3, false));

        this.enemies.add(new BatEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 70, 30f + this.tileLayer.getTileHeight() * 17), this.tileLayer, 50 ,3, false));

        this.enemies.add(new BatEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 45, 30f + this.tileLayer.getTileHeight() * 18), this.tileLayer, 50 ,3, false));

        this.enemies.add(new SkullEnemy(this.player,
                new Vector2(0f + this.tileLayer.getTileWidth() * 29,  this.tileLayer.getTileHeight() * 15), this.tileLayer, 50,4, true));
    }

    public void newGame()
    {
        super.newGame();

        create();

        this.player.reset();
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
        super.bgBatch.draw(GameAssetsDB.getInstance().L2_background, 0,0, 80*128, 20*128);
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

        super.uiBatch.draw(GameAssetsDB.getInstance().lifeTexture, 50, Gdx.graphics.getHeight() - 150f);
        super.font.draw(super.uiBatch, Integer.toString(player.getHealth()), 200f, Gdx.graphics.getHeight() - 60f);

        if(super.gameState == GameState.PLAYING){
            super.moveLeftButton.draw(super.uiBatch);
            super.moveRightButton.draw(super.uiBatch);
            super.jumpButton.draw(super.uiBatch);
            super.attackButton.draw(super.uiBatch);

            super.pauseButton.draw(super.uiBatch);
        } else if(super.gameState == GameState.PAUSE) {
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
            super.exitButton.draw(super.uiBatch);
        }

        super.uiBatch.end();
    }

    @Override
    public void update()
    {

        if(super.gameState == GameState.PLAYING || super.gameState == GameState.WIN)
        {
            // Update player regardless the state
            player.update(stateTime);

            // Remove enemy regardless the state
            for(int i=this.enemies.size() -1; i>=0; i--)
            {
                this.enemies.get(i).update(Gdx.graphics.getDeltaTime());

                if(this.enemies.get(i).getState() == Enemy.EnemyState.DEAD)
                {
                    this.enemies.remove(i);
                }

            }

        }


        //Update Game State based on input
        switch (gameState) {

            case PAUSE:
            {
                GameAssetsDB.getInstance().l2_music.pause();

                // Check if the user press the resume button
                super.resumeButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.resumeButton.isDown){
                    super.resumePressed = true;
                    super.gameState = GameState.PLAYING;
                    GameAssetsDB.getInstance().l2_music.play();
                    super.resumePressed = false;
                }

                //else if(resumePressed){
//                    gameState = GameState.PLAYING;
//                    this.gameAssetsDB.l2_music.play();
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
                    //super.game.currentLevel += 1;
                    //super.game.setScreen(HalloweenGdxGame.gameLevels.get(super.game.currentLevel));
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

                GameAssetsDB.getInstance().l2_music.stop();
                GameAssetsDB.getInstance().game_over.play();

                super.restartButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.restartButton.isDown)
                {
                    GameAssetsDB.getInstance().game_over.stop();
                    newGame();
                }

                super.exitButton.update(Gdx.input.isTouched(),Gdx.input.getX(),Gdx.input.getY());
                if(super.exitButton.isDown)
                {
                    this.dispose();
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

                if(GameAssetsDB.getInstance().danger_zone_music.isPlaying())
                {
                    GameAssetsDB.getInstance().l2_music.pause();
                }
                else
                {
                    if(GameAssetsDB.getInstance().l2_music.isPlaying() == false)
                    {
                        GameAssetsDB.getInstance().l2_music.play();
                    }
                }

                // Check if the player weapon hit any enemy
                for(Enemy e:enemies){
                    for (Weapon w:player.getWeapons()){
                        if(e.getCollider() != null && (e.getState() != Enemy.EnemyState.DYING || e.getState() != Enemy.EnemyState.DEAD)){
                            if(w.getState() == Weapon.WeaponState.ACTIVE && w.getCollider().overlaps(e.getCollider()))
                            {

                                e.setState(Enemy.EnemyState.DYING);

                                super.gameScore += e.getScore();

                                //System.out.println("Enemy score:" + e.getScore());

                                // If the player kill the final boss, the player win
                                if(e.isFinalBoss() && e instanceof NecromancerBoss )
                                {
                                    NecromancerBoss tmp = (NecromancerBoss) e;

                                    if(tmp.getState() == Enemy.EnemyState.DYING && tmp.getLive() <= 1)
                                    {
                                        this.gameState = GameState.WIN;
                                    }
                                }

                                w.setState(Weapon.WeaponState.DEAD);
                            }
                        }
                    }
                }

                gameController();

                int x = 0, y = 0;

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
                        this.player.setState(Player.PlayerState.ALIVE);
                    }else{
                        player.setState(Player.PlayerState.DYING);
                    }

                }else if(this.player.getState() == Player.PlayerState.ALIVE) {

                    // Check if the player is walking on a platform
                    x = Math.round((this.player.getPosition().x)/ 128) + 1;
                    // System.out.println("ALIVEy: " + (player.getPosition().y));
                    y = (int)Math.floor((this.player.getPosition().y) / 128);
                    //y = Math.round((this.player.getPosition().y + 90) / 128) - 1;
                    //y = Math.round((this.player.getPosition().y) / 128) - 1;

                    boolean bottomBlocked = isBlocked(x, y);

                    System.out.println("AliveX: " + player.getPosition().x);

                    // Check if the left is blocked
                    x = Math.round((this.player.getPosition().x)/ 128);
                    y = Math.round((this.player.getPosition().y) / 128);
                    boolean isLeftBlocked = isBlocked(x, y);

                    // Check if the right is blocked
                    x = Math.round((this.player.getPosition().x)/ 128) + 1;
                    y = Math.round((this.player.getPosition().y) / 128);
                    boolean isRightBlocked = isBlocked(x, y);

                    if(this.player.getState() == Player.PlayerState.ALIVE){
                        // If the player is not walking on a platform
                        if (!bottomBlocked) {
                            this.player.setState(Player.PlayerState.FALL_START);
                            //this.player.setIsOnGround(false);
                        }else{
                            this.player.setState(Player.PlayerState.ALIVE);
                            //this.player.setIsOnGround(true);
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

                    boolean upperBlocked = isBlocked(x, y);

                    if (upperBlocked) {
                        this.isJumpHeld = false;
                        this.jumpPressedTime = 0;
                        this.player.setPosition(player.getPosition().x, player.getPosition().y + 25f);
                        this.player.setIsOnGround(true);
                        //player.setState(Player.PlayerState.ALIVE);
                    }else{
                        //this.player.setIsOnGround(false);
                        //this.player.setState(Player.PlayerState.FALL_START);
                    }
                    player.setState(Player.PlayerState.ALIVE);
                }else if (this.player.getState() == Player.PlayerState.FALLING) {

                    if(player.getPosition().y <= SPIKE_TRAP_Y){
                        player.setState(Player.PlayerState.HURT);
                    }

                    if(player.getState() != Player.PlayerState.HURT && player.getState() != Player.PlayerState.HURTING && player.getState() != Player.PlayerState.HURT_END){

                        if(this.player.getFacingDirection() == Player.PlayerDirection.LEFT){
                            x = Math.round(((this.player.getPosition().x) / 128));
                        }else{
                            x = Math.round((this.player.getPosition().x / 128)) + 1;
                        }

                        y = Math.round((this.player.getPosition().y) / 128);
                        //y = Math.round((this.player.getPosition().y - 90) / 128);
                        //y = (int)Math.floor((this.player.getPosition().y ) / 128);

                        boolean bottomBlocked = isBlocked(x, y);

//                        if(x >= 0 && x < MAP_WIDTH &&  y >= 0 && y < MAP_HEIGHT){
//                            if(collisionMap[x][y] == true){
//                                System.out.println("should not fall");
//                                normalFall = false;
//                            }else{
//                                if(player.getPosition().y <= 15f){
//                                    normalFall = false;
//                                }else{
//                                    normalFall = true;
//                                }
//                            }
//                        }

                        if(!bottomBlocked){
                            this.player.setState(Player.PlayerState.FALL_START);
                            this.player.setIsOnGround(false);
                        }else{
                            if(this.player.getPosition().y <= 200){
                                this.player.setState(Player.PlayerState.FALL_START);
                                //this.player.setIsOnGround(false);
                            }else{
                                this.player.setPosition(player.getPosition().x, player.getPosition().y + 20f);
                                //this.player.setIsOnGround(true);
                                this.player.setState(Player.PlayerState.ALIVE);
                            }
                        }
                    }

                }else if(this.player.getState() == Player.PlayerState.DEAD) {
                    GameAssetsDB.getInstance().satire.play();
                    super.gameState = GameState.FAIL;
                }

                // Double check this
                if(this.player.isRewarded())
                {
                    Reward tmp_reward = this.player.openReward();

                    if(tmp_reward.getRewardType() == Reward.RewardType.SCORE)
                    {
                        //super.gameScore += tmp_reward.getValue();
                    }
                    else
                    {
                        player.incrementHealth(tmp_reward.getValue());
                    }
                }
            }



//            // Move camera with bat
//            if (this.enemies.get(3).getPosition().x > (Gdx.graphics.getWidth() / 2) - 600) {
//                super.camera.position.x = this.enemies.get(3).getPosition().x + 600;
//
//                if(super.camera.position.x >= ((this.tileLayer.getWidth()*128) - Gdx.graphics.getWidth()/2))
//                {
//                    super.camera.position.x = ((this.tileLayer.getWidth()*128) - Gdx.graphics.getWidth()/2);
//                }
//                else if(super.camera.position.x <= 0)
//                {
//                    super.camera.position.x = 0;
//                }
//            }
//            if (this.enemies.get(3).getPosition().y > (Gdx.graphics.getHeight() / 2)) {
//                super.camera.position.y = this.enemies.get(3).getPosition().y; // can change
//
//                if(super.camera.position.y >= ((this.tileLayer.getHeight()*128) - Gdx.graphics.getHeight()/2))
//                {
//                    super.camera.position.y = ((this.tileLayer.getHeight()*128) - Gdx.graphics.getHeight()/2);
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

    /**
     * Check if the tile in x and y coordinate is blocked and not null
     * @param x The x coordinate of the tile which require for checking
     * @param y The y coordinate of the tile which require for checking
     * @return TRUE if the tile is blocked else false
     */
    private boolean isBlocked (int x, int y){
        if(x >= 0 && x < MAP_WIDTH &&  y >= 0 && y < MAP_HEIGHT){
            if(this.collisionMap[x][y] == true){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    /**
     * Game controller to detect game play control for playing the game
     * This function detect the following:
     * The move left button to control the player left movement
     * The move right button to control the player right movement
     * The attack button to control the player attack movement
     * The jump button to control the player jump movement depend on the player's current facing direction
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

            // Prevent player to go out from the screen
            if(player.getPosition().x < ((MAP_WIDTH * 128) - 240) ) this.isRightHeld = true;
        }else{

            this.isRightHeld = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) || super.attackButton.isDown) {
            this.isAttackHeld = true;
        }else{
            this.isAttackHeld = false;
        }

        // Double check this // need to fix
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

        // Cooldown attack
        if(attackPressedTime > ATTACK_PRESS_COOLDOWN){
            attackPressedTime = 0;
        }else if(attackPressedTime >= 1){
            attackPressedTime += 1;
        }
    }

    @Override
    public void show()
    {
        GameAssetsDB.getInstance().l2_music.play();
        newGame();
    }


    @Override
    public void hide()
    {
        GameAssetsDB.getInstance().l2_music.stop();
        this.gameScore = 0;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

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
