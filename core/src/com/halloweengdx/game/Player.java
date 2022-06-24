package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player implements Actor
{

    // Heidi Version
    public enum PlayerState {ALIVE, ATTACK, MOVELEFT, MOVERIGHT, JUMP_START, JUMPING, FALL, FALLING, HURT, DYING, DEAD}

    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = 3f;        // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 20f;
    public static final float MAX_JUMP_SPEED = 200f;
    public static final float JUMP_X_SPEED = 10f;

    public static final float PLAYER_WIDTH = 300f;
    public static final float PLAYER_HEIGHT = 300f;

    private PlayerState prevState;      // prev state of the player in game
    private PlayerState state;          // Current state of the player in game

    private PlayerDirection prevDirection;
    private PlayerDirection curDirection;

    // Texture
    private Texture playerTexture;
    private Sprite playerSprite;

    private Texture currentFrame;

    float tmp = 0.0f;

    // Animation
    private Animation idleAnimation = null;
    private Animation runAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;
    private Animation jumpStartAnimation = null;
    private Animation jumpAnimation = null;
    private Animation fallAnimation = null;
    private Animation hurtAnimation = null;

    private Vector2 start_position;     // The player's starting position when the game start
    protected Vector2 position;           // The player's current position
    private Vector2 velocity;           // The player's current velocity (for falling and jumping)

    private Weapon weapon  = null;      // The weapon instance (the player's weapon)

    private float jumpStartTime = 0f;
    private float dieTime = 0f;

    private boolean isOnGround = false;

    private Rectangle collider;

    /**
     * The constructor for creating an instance of this class
     * @param x The starting x position to place the player
     * @param y The starting y position to place the player
     */
    public Player (int x, int y) {

        start_position = new Vector2(x, y);
        position = new Vector2(x, y);
        velocity = new Vector2(0,0);

        prevState = PlayerState.ALIVE;
        state = PlayerState.ALIVE;

        curDirection = PlayerDirection.RIGHT;
        prevDirection = curDirection;

        create();
    }

    private void create(){
        playerTexture = new Texture("player/idle/idle_0.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(330, 330);
        playerSprite.translate(position.x, position.y);

        createAnimation();

        // Collider
        collider = new Rectangle(position.x, position.y, playerSprite.getWidth(), playerSprite.getHeight());
    }

    private void createAnimation()
    {
        if(GameAssetsDB.getInstance() != null){
            if(GameAssetsDB.getInstance().playerIdleTexture != null & idleAnimation == null)
                idleAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerIdleTexture);
            if(GameAssetsDB.getInstance().playerRunTexture != null & runAnimation == null )
                runAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerRunTexture);
            if(GameAssetsDB.getInstance().playerHurtTexture != null & hurtAnimation == null)
                hurtAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerHurtTexture);
            if(GameAssetsDB.getInstance().playerDieTexture != null & dieAnimation == null)
                dieAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerDieTexture);
            if(GameAssetsDB.getInstance().playerJumpStartTexture != null & jumpStartAnimation == null)
                jumpStartAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerJumpStartTexture);
            if(GameAssetsDB.getInstance().playerJumpLoopTexture != null & jumpAnimation == null)
                jumpAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerJumpLoopTexture);
            if(GameAssetsDB.getInstance().playerFallTexture != null & fallAnimation == null)
                fallAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerFallTexture);
            if(GameAssetsDB.getInstance().playerAttackTexture != null & attackAnimation == null)
                attackAnimation = new Animation(0.033f, GameAssetsDB.getInstance().playerAttackTexture);
        }
    }

    public void reset()
    {

    }

    public void draw (SpriteBatch batch){

//        if (weapon != null){
//            batch.draw(weapon.getTexture(), weapon.getPosition().x, weapon.getPosition().y);
//        }

        if(prevDirection != curDirection){
            playerSprite.flip(true, false);
            //batch.draw(, position.x, position.y, originX, originY, (int)PLAYER_WIDTH, (int)PLAYER_HEIGHT, 1, 1, 90);
        }

        playerSprite.draw(batch);


    }


    public void update(float delta)
    {

        currentFrame = null;
        createAnimation();

        if(state == PlayerState.ALIVE){
            isOnGround = true;
            if(idleAnimation != null) currentFrame = (Texture) idleAnimation.getKeyFrame(delta, true);
        }else{
            isOnGround = false;
        }

        if(state == PlayerState.MOVELEFT){
            moveLeft(delta);

            state = PlayerState.ALIVE;

            // Set direction
            prevDirection = curDirection;
            curDirection = PlayerDirection.LEFT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.MOVERIGHT){
            moveRight(delta);

            state = PlayerState.ALIVE;

            prevDirection = curDirection;
            curDirection = PlayerDirection.RIGHT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.JUMP_START){
            velocity.y = MAX_JUMP_SPEED;

            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }

            if(jumpStartAnimation != null)  currentFrame = (Texture) jumpStartAnimation.getKeyFrame(jumpStartTime);
            jumpStartTime += 0.03f;

            state = PlayerState.JUMPING;
        }else if(state == PlayerState.HURT){
            currentFrame = (Texture) hurtAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.FALL){
            if(position.y <= 15){
                position.y = 15;
                state = PlayerState.HURT;
            }else{
                fall(delta);
            }

            currentFrame = (Texture) fallAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.DYING) {

            // Make sure the animation only play once and stop at last frame of the animation
            if (dieAnimation.isAnimationFinished(dieTime)) {
                state = PlayerState.DEAD;
            } else {
                currentFrame = (Texture) dieAnimation.getKeyFrame(dieTime);
                dieTime += 0.03f;
            }
        }else if(state == PlayerState.DEAD){
            currentFrame = (Texture) dieAnimation.getKeyFrame(dieTime);
        }

        if(state == PlayerState.JUMPING){

            if(velocity.y < 20f && position.y < 2000f){
                jump(delta);
            }else{
                velocity.y = 0f;
            }

            // Make sure the animation only play once and stop at last frame of the animation
            if(jumpStartAnimation != null & jumpAnimation != null) {
                if (jumpStartAnimation.isAnimationFinished(jumpStartTime)) {
                    currentFrame = (Texture) jumpAnimation.getKeyFrame(delta, true);
                    jumpStartTime = 0f;
                }else{
                    currentFrame = (Texture) jumpStartAnimation.getKeyFrame(jumpStartTime);
                    jumpStartTime += 0.03f;
                }
            }

            state = PlayerState.ALIVE;
        }

        // Update animation frame
        if(currentFrame != null){
            playerSprite.setTexture(currentFrame);
        }else{
            playerSprite.setTexture(playerTexture);
        }

        playerSprite.setX(position.x);
        playerSprite.setY(position.y);

        // Update the collider position to follow the player instance
        collider.setPosition(position);
    }

    private void moveLeft(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x -= ( MOVING_SPEED);
    }

    private void moveRight(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += (MOVING_SPEED);
    }

    private void fall(float delta){
        // Safety measure to prevent the player movement after dead
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        float y = position.y - GRAVITY;
        if(y < position.y){
            position.y = y;
        }else if(position.y <= 15f){
            state = PlayerState.HURT;
        }
    }

    private void jump(float delta){
        tmp += delta;
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        float movement_y = 0;
        if(this.position.y < 2000f){
            movement_y += MAX_JUMP_SPEED  - (GRAVITY * tmp);
        }

        this.position.y += movement_y;
    }

    public void dispose(){

    }


    // Getters and Setters

    public Vector2 getStartPosition() { return this.start_position; }

    public Vector2 getPosition() { return position; }

    public void setPosition(float x, float y) {
        this.position = new Vector2(x, y);
        playerSprite.setX(position.x);
        playerSprite.setY(position.y);
    }

    public void setPosition(Vector2 position) {
        this.position = new Vector2(position.x, position.y);
    }

    public PlayerState getState() { return state; }

    public void setState(PlayerState state) {
        this.prevState = this.state; // record previous state
        this.state = state;
    }

    public Rectangle getCollider() { return collider; }

    public Weapon getWeapon(){ return weapon ; }

    public Sprite getSprite() { return playerSprite; }

    public PlayerDirection getFacingDirection(){ return curDirection; }

    public void setIsOnGround(boolean isOnGround) { this.isOnGround = isOnGround; }

    public boolean getIsOnGround() {return isOnGround; }

    public Vector2 getVelocity() { return velocity; }

    public void setVelocity(float x, float y) { this.velocity = new Vector2(x, y); }

}