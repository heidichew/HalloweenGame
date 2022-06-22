package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player implements Actor
{

    // Heidi Version
    public enum PlayerState {ALIVE, ATTACK, MOVELEFT, MOVERIGHT, JUMP_START, JUMPING, JUMPING_STOP, FALL, FALLING, DYING, DEAD}

    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = 20f;        // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 1.5f;
    public static final float MAX_JUMP_SPEED = 6f;
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

    private Vector2 start_position;     // The player's starting position when the game start
    protected Vector2 position;           // The player's current position
    private Vector2 velocity;           // The player's current velocity (for falling and jumping)

    private Weapon weapon  = null;      // The weapon instance (the player's weapon)

    private Rectangle collider;

    private boolean isOnGround;

    private boolean isJumpStart;

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

        isJumpStart = false;

        create();
    }

    private void create(){
        playerTexture = new Texture("player/reaper_man_static.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(300, 300);
        playerSprite.translate(position.x, position.y);

        // Collider
        collider = new Rectangle(position.x, position.y, playerSprite.getWidth(), playerSprite.getHeight());
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
        if(state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        if(state == PlayerState.ALIVE){
            isOnGround = true;
        }else{
            isOnGround = false;
        }

        if(state == PlayerState.MOVELEFT){
            moveLeft(delta);

            state = PlayerState.ALIVE;

            // Set direction
            prevDirection = curDirection;
            curDirection = PlayerDirection.LEFT;
        }else if(state == PlayerState.MOVERIGHT){
            moveRight(delta);

            state = PlayerState.ALIVE;

            prevDirection = curDirection;
            curDirection = PlayerDirection.RIGHT;
        }else if(state == PlayerState.JUMP_START){
            velocity.y = MAX_JUMP_SPEED;

            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }
            state = PlayerState.JUMPING;
        }

        if(state == PlayerState.JUMPING){

            if(velocity.y < 20f && position.y < 2000f){
                jump(delta);
            }else{
                velocity.y = 0f;
            }

            state = PlayerState.ALIVE;
        }else if(state == PlayerState.FALL){
            if(position.y <= 15){
                position.y = 15;
                state = PlayerState.DEAD;
            }else{
                fall(delta);
            }
        }

        playerSprite.setX(position.x);
        playerSprite.setY(position.y);
    }

    private void moveLeft(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x -= 1 * delta * MOVING_SPEED;
    }

    private void moveRight(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += 1 * delta * MOVING_SPEED;
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
            state = PlayerState.DEAD;
        }
    }

    private void jump(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

//        if(curDirection == PlayerDirection.LEFT){
//            velocity.x = -JUMP_X_SPEED;
//        }else{
//            velocity.x = JUMP_X_SPEED;
//        }

        float y = position.y + velocity.y * delta;
        float x = position.x + velocity.x * delta;
        if(y < 2000f){
            if(x > 0){
                position = new Vector2(position.x + velocity.x, y);
            }else{
                position = new Vector2(0, y);
            }
        }else {
            if(x > 1){
                position = new Vector2(position.x + velocity.x, 2000);
            }else{
                position = new Vector2(0, 2000);
            }
        }

        if(position.y > 2000){
            position.y = 2000;
        }
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

    public void setIsJumpStart(boolean isJumpStart) { this.isJumpStart = isJumpStart; }

}
