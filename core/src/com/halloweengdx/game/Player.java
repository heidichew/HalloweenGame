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
    public enum PlayerState {ALIVE, ATTACK, MOVELEFT, MOVERIGHT, JUMP, FALL, DYING, DEAD}

    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = -0.25f;        // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 0.5f;

    public static final float PLAYER_WIDTH = 300f;
    public static final float PLAYER_HEIGHT = 300f;

    private PlayerState state;          // State of the player in game
    private PlayerDirection direction;

    // Texture
    private Texture playerTexture;
    private Sprite playerSprite;

    private Vector2 start_position;     // The player's starting position when the game start
    protected Vector2 position;           // The player's current position
    private Vector2 velocity;           // The player's current velocity

    private Weapon weapon  = null;      // The weapon instance (the player's weapon)

    Rectangle collider;

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

        this.state = PlayerState.ALIVE;
        this.direction = PlayerDirection.RIGHT;

        create();
    }

    private void create(){
        playerTexture = new Texture("player/reaper_man_static.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(300, 300);
        playerSprite.translate(position.x, position.y);
        collider = new Rectangle(position.x, position.y, playerSprite.getWidth(), playerSprite.getHeight());
    }

    public void reset()
    {

    }

    public void draw (SpriteBatch batch){
//        batch.draw(currentFrame, position.x, position.y);
//        if (weapon != null){
//            batch.draw(weapon.getTexture(), weapon.getPosition().x, weapon.getPosition().y);
//        }
        //playerSprite.draw(batch);
        batch.draw(playerTexture, position.x, position.y, 300, 300);
    }


    public void update(float delta)
    {
        //update stuff
        if(state == PlayerState.MOVELEFT){
            moveLeft(delta);
            state = PlayerState.ALIVE;
        }

        if(state == PlayerState.MOVERIGHT){
            moveRight(delta);
            state = PlayerState.ALIVE;
        }
    }

    private void moveLeft(float delta){
        position.x -= 1 * delta * MOVING_SPEED;
    }

    private void moveRight(float delta){
        position.x += 1 * delta * MOVING_SPEED;
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
}
