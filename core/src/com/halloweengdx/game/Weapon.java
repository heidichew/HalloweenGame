package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * This class defines how should the weapon (i.e. the sword) behaves in the game
 * Only the player class should instantiate this instance (only player instance can own this instance),
 * and the player class is responsible to call the update and draw functions of this class
 * The weapon has two valid states: ACTIVE and DEAD
 * When the state is ALIVE, it can kill the enemy within a certain range
 * The weapon only active and valid for a certain time, it will disappear after a certain time
 * The weapon will blink (appear and disappear) when it is about to disappear
 * The weapon will move in x axis (e.g., move to left is the weapon is created to face left) and y axis (move up and then drop)
 * @Author: Heidi Chew
 */
public class Weapon {

    public enum WeaponState {ACTIVE, DEAD}
    public enum WeaponDirection {LEFT, RIGHT}           // The weapon direction

    public static final float MOVING_SPEED = 10;        // The moving speed of the weapon projectile
    public static final float GRAVITY = 4;              // The gravity to apply on this instance in the game world

    public static final float SIZE_WIDTH = 150;         // The width for drawing the weapon on screen
    public static final float SIZE_HEIGHT = 75;         // The height for drawing the weapon on screen

    public static final float INITIAL_VELOCITY_Y = 35;  // The height to throw the weapon up to before falling due to gravity
    public static final float INITIAL_ROTATION = 45;    // How many degree to add every interval of MAX_ROTATE_TIME
    public static final float MAX_ROTATE_TIME = 3;      // Change the angle of the weapon drawn on screen every interval

    public static final float WEAPON_VALID_TIME = 55;   // The weapon valid time

    private WeaponState state = null;                   // The state of the weapon
    private Rectangle collider = null;                  // The collider of the weapon

    private Vector2 start_xy = null;                    // The starting position to place this weapon in the game world
    private Vector2 position = null;                    // The position of the weapon in the game world
    private Vector2 velocity;                           // The velocity of the weapon

    private Sprite weaponSprite;                        // The weapon sprite

    private Player owner = null;                        // The player that create and own this weapon instance

    private WeaponDirection direction;                  // The weapon direction (normally follow the player's facing direction)

    private boolean shouldDraw = false;                 // The boolean variable to create blinking effect to show that the weapon is about to disappear

    private float rotateDegree = 0;                     // The current rotate angle of the weapon
    private float rotateTime = 0;                       // Track the rotation time, to use for how long to change rotate angle
    private float activeTime = 0;                       // How long the weapon has active for since created
    private float drawTime = 0;                         // Variable to toggle draw and not draw for creating blinking effect
                                                        // (such as draw for two seconds and not draw for another two)


    /**
     * The constructor to create this instance
     * @param player    The player that own the weapon projectile
     */
    public Weapon(Player player, float startX, float startY, WeaponDirection direction){
        this.owner = player;
        this.state = WeaponState.ACTIVE;

        this.start_xy = new Vector2(startX, startY);
        this.position = new Vector2(startX, startY);

        this.velocity = new Vector2(0,0);
        this.velocity.x = MOVING_SPEED;
        this.velocity.y = INITIAL_VELOCITY_Y;

        this.direction = direction;

        create();
    }

    /**
     * Create the texture and collider for the weapon projectile
     */
    private void create(){
        //Player
        weaponSprite = new Sprite(GameAssetsDB.getInstance().weaponTexture);
        weaponSprite.setSize(SIZE_WIDTH, SIZE_HEIGHT);
        weaponSprite.translate(position.x, position.y);

        // Starting degree to throw the weapon at based on the weapon direction
        if(direction == WeaponDirection.LEFT){
            weaponSprite.flip(true, false);
            rotateDegree = 270;
        }else{
            rotateDegree = 0;
        }

        collider = new Rectangle(position.x, position.y, weaponSprite.getWidth(), weaponSprite.getHeight());
    }

    /**
     * Update the state of the weapon projectile and its collider's position
     * @param delta The game delta time
     */
    public void update(float delta){

        if(state == WeaponState.DEAD){
            return;
        }

        activeTime += 1;
        if(activeTime > WEAPON_VALID_TIME){
            state = WeaponState.DEAD;
        }

        if(direction == WeaponDirection.LEFT){
            moveLeft(delta);
        }else{
            moveRight(delta);
        }

        weaponSprite.setX(position.x);
        weaponSprite.setY(position.y);
        collider.setPosition(position);
    }

    /**
     * Draw this instance on the screen
     * @param batch
     */
    public void draw(SpriteBatch batch){

        // Rotate the sprite to create throwing effect
        weaponSprite.setRotation(rotateDegree);
        if(rotateTime > MAX_ROTATE_TIME){

            if(direction == WeaponDirection.RIGHT){
                // If the weapon face right
                if(rotateDegree >= 360){
                    rotateDegree = 0;
                }else{
                    rotateDegree -= INITIAL_ROTATION;
                }
            }else{
                // If the weapon face left
                if(rotateDegree >= 360){
                    rotateDegree = 0;
                }else{
                    rotateDegree += INITIAL_ROTATION;
                }
            }
            rotateTime = 0;
        }else{
            rotateTime += 1;
        }

        // Create blinking effect to indicate if the weapon is about to disappear
        if(activeTime > WEAPON_VALID_TIME - 15){
            drawTime += 1;

            if(shouldDraw){
                weaponSprite.draw(batch);
            }
            if(drawTime > 2) {
                if(shouldDraw) {
                    shouldDraw = false;
                }else{
                    shouldDraw = true;
                }
                drawTime = 0;
            }
        }else{
            weaponSprite.draw(batch);
        }

    }

    /**
     * Move the weapon in left direction
     * Also apply gravity gradually
     * Such that the weapon is throw up to a highest point before falling
     * @param delta
     */
    private void moveLeft(float delta){
        float x = velocity.x - MOVING_SPEED;
        if(x > -20){
            velocity.x = x;
        }

        velocity.y += -(GRAVITY);
        if(velocity.y < -10){
            velocity.y = 0;
        }
        position.add(velocity.x, velocity.y);
    }

    /**
     * Move the weapon in right direction
     * Also apply gravity gradually
     * Such that the weapon is throw up to a highest point before falling
     * @param delta
     */
    private void moveRight(float delta){

        float x = velocity.x + MOVING_SPEED;
        if(x < 20){
            velocity.x = x;
        }

        velocity.y += -(GRAVITY);
        if(velocity.y < -10){
            velocity.y = 0;
        }
        position.add(velocity.x, velocity.y);
    }

    public void dispose(){

    }

    // Getters and Setters
    public WeaponState getState() { return state; }

    public void setState(WeaponState state) { this.state = state; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }

    public Player getOwner() { return owner; }

    public Rectangle getCollider(){ return collider; }

    public WeaponDirection getDirection() {
        return direction;
    }

    public void setDirection(WeaponDirection direction) {
        direction = direction;
    }


}
