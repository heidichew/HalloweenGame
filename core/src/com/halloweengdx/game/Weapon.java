package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Weapon {

    public enum WeaponState {ACTIVE, DEAD}
    public enum WeaponDirection {LEFT, RIGHT}

    public static final float MOVING_SPEED = 10f;     // The moving speed of the weapon projectile
    public static final float GRAVITY = 4f;       // The gravity to apply on this instance in the game world

    public static final float SIZE_WIDTH = 150f;
    public static final float SIZE_HEIGHT = 75f;

    public static final float INITIAL_VELOCITY_Y = 35;
    public static final float INITIAL_ROTATION = 45f;
    public static final float MAX_ROTATE_TIME = 3;

    public static final float WEAPON_VALID_TIME = 55;

    private WeaponState state = null;   // The state of the weapon projectile
    private Rectangle collider = null;  // The collider of the weapon projectile

    private Vector2 start_xy = null;    // The starting position to place this weapon projectile in the game world
    private Vector2 position = null;    // The position of the weapon projectile in the game world
    private Vector2 velocity;           // The velocity of the weapon projectile

    private Sprite weaponSprite;

    private Player owner = null;        // The player that create the weapon projectile instance

    private WeaponDirection direction;

    private boolean shouldDraw = false;

    private float rotateDegree = 0;
    private float rotateTime = 0;
    private float activeTime = 0;
    private float drawTime = 0;



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

    public void draw(SpriteBatch batch){

        weaponSprite.setRotation(rotateDegree);
        if(rotateTime > MAX_ROTATE_TIME){

            if(direction == WeaponDirection.RIGHT){
                if(rotateDegree >= 360){
                    rotateDegree = 0;
                }else{
                    rotateDegree -= INITIAL_ROTATION;
                }
            }else{
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

        if(activeTime > WEAPON_VALID_TIME - 15){
            drawTime += 1;
            if (shouldDraw) {
                weaponSprite.draw(batch);
                if(drawTime > 2) shouldDraw = false;
            }else{
                if(drawTime > 2) shouldDraw = true;
            }
        }else{
            weaponSprite.draw(batch);
        }

    }

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
