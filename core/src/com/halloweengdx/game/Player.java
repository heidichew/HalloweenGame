package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

public class Player implements Actor
{
    public enum PlayerState {ALIVE, ATTACK, ATTACKING, MOVELEFT, MOVERIGHT, JUMP_START, JUMPING, FALL_START, FALLING, HURT, HURTING, HURT_END, DYING, DEAD}

    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = 20f;            // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 20f;
    public static final float INITIAL_JUMP_SPEED = 3f;
    public static final float MAX_JUMP_SPEED = 20f;
    public static final float JUMP_X_SPEED = 10f;
    public static final int INITIAL_LIFE = 5;
    public static final float CEILING_HEIGHT = 2200f;   // The max height allow to jump to prevent jump over the ceiling

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

    private Vector2 start_position;         // The player's starting position when the game start
    protected Vector2 position;             // The player's current position
    private Vector2 velocity;               // The player's current velocity (for falling and jumping)

    private Weapon weapon  = null;          // The weapon instance (the player's weapon)

    // Float for animation
    private float jumpStartTime = 0f;
    private float dieTime = 0f;
    private float hurtTime = 0f;
    private float attackTime = 0;

    private float jumpYForce;               // record jump force to enable smooth falling

    private int health;

    private boolean isOnGround = false;
    private boolean isFlipLeft = false;

    public boolean isHurt; // not needed

    private Rectangle collider;

    private Reward receivedReward;

    private boolean isRewarded;

    private ArrayList<Weapon> weapons;

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

        isFlipLeft = false;

        receivedReward = null;
        isRewarded = false;

        health = INITIAL_LIFE;

        create();
    }

    private void create(){
        playerTexture = new Texture("player/idle/idle_0.png");

        //Player
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(330, 330);
        playerSprite.translate(position.x, position.y);

        createAnimation();

        weapons = new ArrayList<Weapon>();

        //this.isHurt = false; // not needed

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
                attackAnimation = new Animation(0.01f, GameAssetsDB.getInstance().playerAttackTexture);
        }
    }

    // Reset the player state when the restart button is clicked
    public void reset()
    {
        health = INITIAL_LIFE;
        position = new Vector2(start_position.x, start_position.y);
        velocity = new Vector2(0,0);

        prevState = PlayerState.ALIVE;
        state = PlayerState.ALIVE;

        curDirection = PlayerDirection.RIGHT;
        prevDirection = curDirection;

        isFlipLeft = false;

        receivedReward = null;
        isRewarded = false;
    }

    public void draw (SpriteBatch batch){

        for(Weapon w:weapons){
            if(w.getState() == Weapon.WeaponState.ACTIVE){
                w.draw(batch);
            }
        }

        if(curDirection == PlayerDirection.LEFT && !isFlipLeft){
            playerSprite.flip(true, false);
            isFlipLeft = true;
        }else if(curDirection == PlayerDirection.RIGHT && isFlipLeft){
            playerSprite.flip(true, false);
            isFlipLeft = false;
        }

        playerSprite.draw(batch);
    }


    public void update(float delta)
    {
        currentFrame = null;
        createAnimation();

        // Remove inactive weapon
        removeInvalidWeapon();
        // Update weapon state
        for(Weapon w:weapons){
            w.update(delta);
        }

        if(state == PlayerState.ALIVE){
            //isOnGround = true;
            velocity.x = 0;
            if(idleAnimation != null) currentFrame = (Texture) idleAnimation.getKeyFrame(delta, true);
        }else{
            isOnGround = false;
        }

        if(this.isRewarded == false)
        {
            if(this.receivedReward != null)
            {
                this.receivedReward = null;
            }
        }

        if(state == PlayerState.MOVELEFT){
            moveLeft(delta);

            prevState = state;
            state = PlayerState.ALIVE;

            velocity.x = -MOVING_SPEED;

            // Set direction
            curDirection = PlayerDirection.LEFT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.MOVERIGHT){
            moveRight(delta);

            prevState = state;
            state = PlayerState.ALIVE;

            velocity.x = MOVING_SPEED;

            curDirection = PlayerDirection.RIGHT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.JUMP_START){
            velocity.y = INITIAL_JUMP_SPEED;

            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }

            if(jumpStartAnimation != null)  currentFrame = (Texture) jumpStartAnimation.getKeyFrame(jumpStartTime);
            jumpStartTime += 0.03f;

            prevState = state;
            state = PlayerState.JUMPING;

        }else if(state == PlayerState.FALL_START){
            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }

            prevState = state;
            state = PlayerState.FALLING;
        }else if(state == PlayerState.HURT){
            hurtTime = 0;
            state = PlayerState.HURTING;
        }else if(state == PlayerState.DYING) {

            // Make sure the animation only play once and stop at last frame of the animation
            if (dieAnimation.isAnimationFinished(dieTime)) {
                GameAssetsDB.getInstance().player_fall_down.play();
                prevState = state;
                state = PlayerState.DEAD;
            } else {
                currentFrame = (Texture) dieAnimation.getKeyFrame(dieTime);
                dieTime += 0.03f;
            }
        }else if(state == PlayerState.DEAD){
            currentFrame = (Texture) dieAnimation.getKeyFrame(dieTime);
        }else if(state == PlayerState.HURT_END){
            currentFrame = (Texture) hurtAnimation.getKeyFrame(hurtTime);
        }else if(state == PlayerState.ATTACK){
            GameAssetsDB.getInstance().player_attack.play();
            state = PlayerState.ATTACKING;
        }

        if(state == PlayerState.JUMPING){

            if(velocity.y < 20f && position.y < CEILING_HEIGHT){
                jump(delta);
            }else{
                velocity.y = 0f;
            }

//            // Make sure the animation only play once and stop at last frame of the animation
//            if(jumpStartAnimation != null & jumpAnimation != null) {
//                if (jumpStartAnimation.isAnimationFinished(jumpStartTime)) {
//                    currentFrame = (Texture) jumpAnimation.getKeyFrame(delta, true);
//                    jumpStartTime = 0f;
//                }else{
//                    currentFrame = (Texture) jumpStartAnimation.getKeyFrame(jumpStartTime);
//                    jumpStartTime += 0.03f;
//                }
//            }

            jumpStartTime = 0f;
            currentFrame = (Texture) jumpAnimation.getKeyFrame(delta, true);

            prevState = state;
            //state = PlayerState.ALIVE;
        }else if(state == PlayerState.FALLING){
//            if(position.y <= 15){
//                position.y = 15;
//
//                prevState = state;
//                state = PlayerState.HURT;
//            }else{
//                fall(delta);
//            }

            fall(delta);
            currentFrame = (Texture) fallAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.HURTING){
            if (hurtAnimation.isAnimationFinished(hurtTime)) {
                prevState = state;
                if(health > 0) {
                    health -= 1;
                    state = PlayerState.HURT_END;
                }else{
                    state = PlayerState.DYING;
                }
                currentFrame = (Texture) hurtAnimation.getKeyFrame(hurtTime);
            } else {
                GameAssetsDB.getInstance().player_hurt.play();
                currentFrame = (Texture) hurtAnimation.getKeyFrame(hurtTime);
                hurtTime += 0.01f;
            }
        }else if(state == PlayerState.ATTACKING){
            currentFrame = (Texture) attackAnimation.getKeyFrame(attackTime);
            if (attackAnimation.isAnimationFinished(attackTime)) {
                attackTime = 0;
                spawnWeapon();
                state = PlayerState.ALIVE;
            }else{
                attackTime += 0.01f;
            }
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

    private void spawnWeapon(){
        Weapon newWeapon = null;

        if(curDirection == PlayerDirection.LEFT){
            newWeapon = new Weapon(this, position.x - 200f, position.y + 50f, Weapon.WeaponDirection.LEFT);
        }else{
            newWeapon = new Weapon(this, position.x + 200f, position.y + 50f, Weapon.WeaponDirection.RIGHT);
        }
        if(newWeapon != null) weapons.add(newWeapon);
        System.out.println("creating weapon");
    }

    private void moveLeft(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += velocity.x;
    }

    private void moveRight(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += velocity.x;
    }

    private void fall(float delta){
        // Safety measure to prevent the player movement after dead
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        float y = 0;
        if(jumpYForce <= 0){
            y = position.y - GRAVITY;
        }else{
            y = position.y - jumpYForce;
        }
        jumpYForce = 0;

        if(y < position.y){
            position.y = y;
        }else if(position.y <= 15f){
            state = PlayerState.HURT;
        }

        float x = velocity.x + position.x;
        if(position.y > 400){
            position.x = x;
        }
    }

    private void jump(float delta){
        tmp += delta;
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

//        float y = position.y;
//        y += GRAVITY * 0.6f;
//
//        if(y < CEILING_HEIGHT){
//            this.position.y += y;
//        }

        float y = velocity.y * delta;
        if(y > MAX_JUMP_SPEED){
            y = MAX_JUMP_SPEED;
        }
        jumpYForce = y;
        y += position.y;

        float x = velocity.x + position.x;

        if(y < CEILING_HEIGHT){
            if(x > 0){
                position = new Vector2(position.x + velocity.x, y);
            }else{
                position = new Vector2(0, y);
            }
        }else{
            if(x > 0){
                position = new Vector2(position.x + velocity.x, 2300);
            }else{
                position = new Vector2(0, 2300);
            }
        }

        if(position.y > 2300){
            position.y = 2300;
        }
    }

    private void removeInvalidWeapon()
    {
        for(int i = weapons.size() - 1; i>=0; i--)
        {
            if(weapons.get(i).getState() == Weapon.WeaponState.DEAD){
                weapons.remove(i);
            }
        }
    }

    public boolean receiveReward(Reward reward)
    {
        this.receivedReward = reward;
        this.isRewarded = true;
        return true;
    }

    public Reward openReward()
    {
        Reward tmpReward = this.receivedReward;
        if(this.receivedReward!=null)
        {
            this.isRewarded = false;
            return tmpReward;
        }
        return null;
    }

    public boolean isRewarded()
    {
        return this.isRewarded;
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

    public Weapon getWeapon(){ return weapon;}

    public Sprite getSprite() { return playerSprite; }

    public PlayerDirection getFacingDirection(){ return curDirection; }

    public void setIsOnGround(boolean isOnGround) { this.isOnGround = isOnGround; }

    public boolean getIsOnGround() {return isOnGround; }

    public Vector2 getVelocity() { return velocity; }

    public void setVelocity(float x, float y) { this.velocity = new Vector2(x, y); }

    public PlayerState getPreviousState(){ return prevState; }

    public int getHealth() { return health; }

    // Increase the player health
    public boolean incrementHealth(int newHealth)
    {
        health += newHealth;
        return true;
    }

    public ArrayList<Weapon> getWeapons(){ return weapons; }
}