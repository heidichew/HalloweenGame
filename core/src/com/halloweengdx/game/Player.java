package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player implements Actor
{

    // Adrian Version
    public enum PLAYER_STATE{ IDLE, MOVING, ATTACKING, JUMPING };
    public enum PLAYER_DIRECTION {LEFT, RIGHT};

    private float x;
    private float y;

    // Heidi Version
    public enum PlayerState {ALIVE, ATTACK, MOVELEFT, MOVERIGHT, MOVEUP, MOVEDOWN, DYING, DEAD}

    private static final float GRAVITY = -0.25f;        // The gravity to apply to user after jumping
    private static final float MOVING_SPEED = 0.02f;

    private PlayerState state;          // State of the player in game

    private Vector2 start_position;     // The player's starting position when the game start
    private Vector2 position;           // The player's current position
    private Vector2 velocity;           // The player's current velocity

    private Weapon weapon  = null;      // The weapon instance (the player's weapon)


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

        create();
    }

    // Adrian version
    public Player(float x, float y)
    {
        GameAssetsDB player_assets = GameAssetsDB.getInstance();

        //loading texture from db

    }

    public void create(){
        // texture and collider
    }

    public void draw (SpriteBatch batch){
//        batch.draw(currentFrame, position.x, position.y);
//        if (weapon != null){
//            batch.draw(weapon.getTexture(), weapon.getPosition().x, weapon.getPosition().y);
//        }
    }


    public void update()
    {
        //update stuff
    }

    public void reset(){

    }

    public void dispose(){

    }

    // Getters and Setters
    public PlayerState getState() { return state; }

    public void setState(PlayerState state) { this.state = state; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }

    //public Rectangle getCollider() { return collider; }

    public Weapon getWeapon(){ return weapon ; }


}
