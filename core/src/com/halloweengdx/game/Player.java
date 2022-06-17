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
    public enum PlayerState {ALIVE, ATTACK, MOVELEFT, MOVERIGHT, JUMP, JUMPING, FALL, FALLING, DYING, DEAD}

    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = 6f;        // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 1f;

    public static final float PLAYER_WIDTH = 300f;
    public static final float PLAYER_HEIGHT = 300f;

    private PlayerState state;          // State of the player in game

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

    //texture

    /**
     * The constructor for creating an instance of this class
     * @param x The starting x position to place the player
     * @param y The starting y position to place the player
     */
    public Player (int x, int y) {

        start_position = new Vector2(x, y);
        position = new Vector2(x, y);
        velocity = new Vector2(0,0);

        state = PlayerState.ALIVE;
        curDirection = PlayerDirection.RIGHT;
        prevDirection = curDirection;

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

        //int originX = (int) ((position.x + PLAYER_WIDTH)/2);
        //int originY =  (int) (position.y + PLAYER_HEIGHT)/2;

        if(prevDirection != curDirection){
            playerSprite.flip(true, false);
            //batch.draw(, position.x, position.y, originX, originY, (int)PLAYER_WIDTH, (int)PLAYER_HEIGHT, 1, 1, 90);
        }

        playerSprite.draw(batch);
    }


    public void update(float delta)
    {
        //update stuff
        if(state == PlayerState.MOVELEFT){
            moveLeft(delta);
            state = PlayerState.ALIVE;

            // Set direction
            prevDirection = curDirection;
            curDirection = PlayerDirection.LEFT;
        }

        if(state == PlayerState.MOVERIGHT){
            moveRight(delta);
            state = PlayerState.ALIVE;

            // Set direction
            prevDirection = curDirection;
            curDirection = PlayerDirection.RIGHT;
        }

        if(state == PlayerState.FALL){
            fall(delta);

        }

        playerSprite.setX(position.x);
        playerSprite.setY(position.y);
    }

    private void moveLeft(float delta){
        position.x -= 1 * delta * MOVING_SPEED;
    }

    private void moveRight(float delta){
        position.x += 1 * delta * MOVING_SPEED;
    }

    private void fall(float delta){
        // Safety measure to prevent the player movement after dead
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }
        position.y -= 1 * delta * GRAVITY;
//        velocity.sub(0, GRAVITY);
//        velocity.scl(delta);
//        position.sub(0, velocity.y);
//        velocity.scl(1/delta);
    }

    public void dispose(){

    }


    // Getters and Setters

    public Vector2 getStartPosition() { return this.start_position; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }

    public PlayerState getState() { return state; }

    public void setState(PlayerState state) { this.state = state; }

    //public Rectangle getCollider() { return collider; }

    public Weapon getWeapon(){ return weapon ; }

    public Sprite getSprite() { return playerSprite; }

    //public PlayerDirection getFacingDirection(){}
}
