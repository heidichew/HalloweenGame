package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class contain main logic for controlling the main player(the reaper man) in the game
 * In ALIVE state, the player can throw sword, move left, move right and jump.
 * When the player's is not hurt or die, the player can jump anytime.
 * In ATTACK state, the player can throw swords that can kill an enemy within a certain range.
 * The player can throw multiple swords; however, there is a cool down to prevent the player throw too many swords at a time.
 * When the player is hurting or dying, the player is unable to attack, move or jump.
 * The player is dead completely when its state is DEAD.
 * The PlayerDirection outline where the player is currently facing
 * The player will fall back to the ground after jumping due to gravity and keep falling if the player fail to stand on a platform
 * The state MOVELEFT will make the the player move to the left in the game world
 * The state MOVERIGHT will make the player move to the right in the game world
 */
public class Player implements Actor
{
    // The player state
    public enum PlayerState {
        ALIVE,      // The player is idle and stand on a platform
        ATTACK,     // The player begins to throw a weapon
        ATTACKING,  // The player is throwing a weapon
        MOVELEFT,   // The player is running to the left
        MOVERIGHT,  // The player is running to the right
        JUMP_START, // The player begins to jump
        JUMPING,    // The player is jumping in the air
        FALL_START, // The player begins to fall
        FALLING,    // The player is falling in the air
        HURT,       // The player begins to hurt
        HURTING,    // The player is hurting
        HURT_END,   // The player done received damage
        DYING,      // The player is in the dying process
        DEAD        // The player die completely
    }

    // The player facing direction
    public enum PlayerDirection {LEFT, RIGHT}

    public static final float GRAVITY = 20f;            // The gravity to apply to user after jumping
    public static final float MOVING_SPEED = 20f;       // The moving speed in x direction when the player state is MOVELEFT or MOVERIGHT
    public static final float INITIAL_JUMP_SPEED = 3f;  // The player starting jumping speed when the jump button is pressed
    public static final float MAX_JUMP_SPEED = 20f;     // The maximum jump speed to prevent the player to jump too high
    public static final float JUMP_X_SPEED = 10f;       // The speed in x direction when the player jump (e.g, increase the player position by x speed when jump)
    public static final int MAX_LIFE = 5;           // The initial health of the player
    public static final float CEILING_HEIGHT = 2200f;   // The max height allow to jump to prevent jump over the ceiling

    public static final float PLAYER_WIDTH = 300f;      // The player width
    public static final float PLAYER_HEIGHT = 300f;     // The player height

    private PlayerState prevState;                      // The previous state of the player in game
    private PlayerState state;                          // The current state of the player in game

    private PlayerDirection curDirection;               // The player facing direction (face left or right)

    // Texture
    private Texture playerTexture;
    private Sprite playerSprite;

    private Texture currentFrame;                       // The current frame to show on screen

    // Animation
    private Animation idleAnimation = null;
    private Animation runAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;
    private Animation jumpStartAnimation = null;
    private Animation jumpAnimation = null;
    private Animation fallAnimation = null;
    private Animation hurtAnimation = null;

    private Vector2 start_position;         // The player's starting position when the player instance is created
    protected Vector2 position;             // The player's current position
    private Vector2 velocity;               // The player's current velocity (for falling and jumping)

    // Float for tracking which frame animation should be played
    private float jumpStartTime = 0f;       // For jump start animation
    private float dieTime = 0f;             // For die animation
    private float hurtTime = 0f;            // For hurt animation
    private float attackTime = 0;           // For attack animation

    private float jumpYForce;               // Record jump force to enable smooth falling

    private int health;                     // Record the current health of the player

    private boolean isOnGround = false;         // !!! Not needed
    private boolean isFlipLeft = false;         // True if the sprite has been flipped to left
    private boolean hasSpawnWeapon = false;     // True if the weapon has been spawned
    private boolean shouldFallStraight = false; // !!! Not needed

    private Rectangle collider;                 // The player collider

    private Reward receivedReward;
    private boolean isRewarded;

    private ArrayList<Weapon> weapons;          // The list to store all swords that the player has threw

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

        isFlipLeft = false;

        receivedReward = null;
        isRewarded = false;

        health = MAX_LIFE;

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
    }

    /**
     * Create animations for player different states
     */
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

    /**
     * Reset the player
     */
    public void reset()
    {
        this.health = MAX_LIFE;
        this.position = new Vector2(start_position.x, start_position.y);
        this.velocity = new Vector2(0,0);

        this.prevState = PlayerState.ALIVE;
        this.state = PlayerState.ALIVE;

        this.curDirection = PlayerDirection.RIGHT;

        this.receivedReward = null;
        this.isRewarded = false;
    }

    /**
     * Draw the player and its weapon(s)
     * @param batch for drawing the player and weapon
     */
    public void draw (SpriteBatch batch){

        // Draw each weapon that is active
        for(Weapon w:weapons){
            if(w.getState() == Weapon.WeaponState.ACTIVE){
                w.draw(batch);
            }
        }

        // Check where the player is currently facing
        // if the sprite has flipped before and flip the sprite
        // to make sure it match the current facing direction
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

        // If for somewhat reason the animation is not created when the player instance is created
        // Then, create animation
        createAnimation();

        // Remove inactive weapon
        removeInvalidWeapon();

        // Update weapon state
        for(Weapon w:weapons){
            w.update(delta);
        }

        if(state == PlayerState.ALIVE){
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

            setState(PlayerState.ALIVE);

            velocity.x = -MOVING_SPEED;

            // Set direction
            curDirection = PlayerDirection.LEFT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.MOVERIGHT){
            moveRight(delta);

            setState(PlayerState.ALIVE);

            velocity.x = MOVING_SPEED;

            curDirection = PlayerDirection.RIGHT;

            if(runAnimation != null) currentFrame = (Texture) runAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.JUMP_START){
            velocity.y = INITIAL_JUMP_SPEED;

            // Set the player x velocity based on the player's facing direction
            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }

            // Make a smooth curve when the delta value is too small
            if(delta < 5){
                velocity.x = velocity.x/2;
            }

            if(jumpStartAnimation != null)  currentFrame = (Texture) jumpStartAnimation.getKeyFrame(jumpStartTime);
            jumpStartTime += 0.03f;

            // Change state to jumping
            setState(PlayerState.JUMPING);
        }else if(state == PlayerState.FALL_START){

            // Set the player x velocity based on the player's facing direction
            if(curDirection == PlayerDirection.LEFT){
                velocity.x = -JUMP_X_SPEED;
            }else{
                velocity.x = JUMP_X_SPEED;
            }
            setState(PlayerState.FALLING);
        }else if(state == PlayerState.HURT){
            hurtTime = 0;
            setState(PlayerState.HURTING);
            // Play the voice when the player start to hurt
            GameAssetsDB.getInstance().player_hurt.play();
        }else if(state == PlayerState.DYING) {

            // Make sure the animation only play once and stop at last frame of the animation
            if (dieAnimation.isAnimationFinished(dieTime)) {
                GameAssetsDB.getInstance().player_fall_down.play();
                setState(PlayerState.DEAD);
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
            setState(PlayerState.ATTACKING);
        }


        if(state == PlayerState.JUMPING){

            // Prevent the player to jump too much and jump over the map
            if(velocity.y < 20f && position.y < CEILING_HEIGHT){
                jump(delta);
            }else{
                velocity.y = 0f;
            }

            jumpStartTime = 0f;
            currentFrame = (Texture) jumpAnimation.getKeyFrame(delta, true);

        }else if(state == PlayerState.FALLING){

            fall(delta);
            currentFrame = (Texture) fallAnimation.getKeyFrame(delta, true);
        }else if(state == PlayerState.HURTING){
            currentFrame = (Texture) hurtAnimation.getKeyFrame(hurtTime);

            if (hurtAnimation.isAnimationFinished(hurtTime)) {
                // Reduce the player health if the hurt animation is done
                if(health > 0) {
                    health -= 1;
                    setState(PlayerState.HURT_END);
                }else{
                    setState(PlayerState.DYING);
                }
            } else {
                hurtTime += 0.01f;
            }
        }else if(state == PlayerState.ATTACKING){
            currentFrame = (Texture) attackAnimation.getKeyFrame(attackTime);
            if (attackAnimation.isAnimationFinished(attackTime)) {
                attackTime = 0;
                hasSpawnWeapon = false;
                setState(PlayerState.ALIVE);
            }else{
                attackTime += 0.03f;
                if(attackTime >= 0.12 && !hasSpawnWeapon){
                    // Only create weapon at a certain animation frame
                    // and create the weapon once only
                    spawnWeapon();
                    hasSpawnWeapon = true;
                }
            }
        }

        // Update animation frame
        if(currentFrame != null){
            playerSprite.setTexture(currentFrame);
        }else{
            playerSprite.setTexture(playerTexture);
        }

        // Update the sprite
        playerSprite.setX(position.x);
        playerSprite.setY(position.y);
    }

    /**
     * Function to create a new sword based on the player currently facing direction
     */
    private void spawnWeapon(){
        Weapon newWeapon = null;

        if(curDirection == PlayerDirection.LEFT){
            newWeapon = new Weapon(this, position.x - 200f, position.y + 50f, Weapon.WeaponDirection.LEFT);
        }else{
            newWeapon = new Weapon(this, position.x + 200f, position.y + 50f, Weapon.WeaponDirection.RIGHT);
        }

        // Add the newly created weapon into the weapon list
        if(newWeapon != null) weapons.add(newWeapon);
    }

    /**
     * Function to increase the player x position in left direction
     * @param delta
     */
    private void moveLeft(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += velocity.x;
    }

    /**
     * Function to increase the player x position in right direction
     * @param delta
     */
    private void moveRight(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        position.x += velocity.x;
    }

    /**
     * Function to apply gravity and make the player fall
     * @param delta
     */
    private void fall(float delta){
        // Safety measure to prevent the player movement after dead
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        // Make a smooth fall based on how much previously the player has jumped
        // (how much y the player has jumped)
        float y = 0;
        if(jumpYForce <= 0){
            y = position.y - GRAVITY;
        }else{
            y = position.y - jumpYForce;
        }
        jumpYForce = 0;

        if(y < position.y){
            position.y = y;
        }

        // If the player fall into a trap
        // Then the player should fall straight
        float x = velocity.x + position.x;
        if(position.y > 400){
            position.x = x;
        }
    }

    /**
     * Function to enable the player jump
     * @param delta
     */
    private void jump(float delta){
        if (state == PlayerState.DEAD || state == PlayerState.DYING){
            return;
        }

        float y = velocity.y * delta;
        // Prevent the player to jump too high
        if(y > MAX_JUMP_SPEED){
            y = MAX_JUMP_SPEED;
        }
        // Set how much y the player has jumped and used for ensure smooth failing
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
                position = new Vector2(position.x + velocity.x, CEILING_HEIGHT);
            }else{
                position = new Vector2(0, CEILING_HEIGHT);
            }
        }

        // Prevent the player to jump over the top of the map
        if(position.y > CEILING_HEIGHT){
            position.y = CEILING_HEIGHT;
        }
    }

    /**
     * Remove the weapon from the weapon list where its state is DEAD
     */
    private void removeInvalidWeapon()
    {
        for(int i = weapons.size() - 1; i>=0; i--)
        {
            if(weapons.get(i).getState() == Weapon.WeaponState.DEAD){
                weapons.remove(i);
            }
        }
    }

    /**
     * Function to enable the player to receive a health reward
     * @param reward
     * @return
     */
    public boolean receiveReward(Reward reward)
    {
        this.receivedReward = reward;

        if(!this.isRewarded) {
            // Prevent the health go over maximum health
            if(health < MAX_LIFE) health += 1;
            // Record that the player has received a health reward
            this.isRewarded = true;
        }
        return true;
    }

    public void dispose(){
        playerTexture.dispose();
    }

    // Getters and Setters
    public boolean isRewarded()
    {
        return this.isRewarded;
    }

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
        this.prevState = this.state;    // Record previous state
        this.state = state;
    }

    public Sprite getSprite() { return this.playerSprite; }

    public PlayerDirection getFacingDirection(){ return this.curDirection; }

    public void setFacingDirection(PlayerDirection direction) { this.curDirection = direction; }

    public Vector2 getVelocity() { return this.velocity; }

    public void setVelocity(float x, float y) { this.velocity = new Vector2(x, y); }

    public PlayerState getPreviousState(){ return this.prevState; }

    public int getHealth() { return this.health; }

    public ArrayList<Weapon> getWeapons(){ return weapons; }

}