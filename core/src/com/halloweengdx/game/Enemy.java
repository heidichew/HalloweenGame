package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract Class of Enemy that implements from Actor Interface
 * @author Adrian Yi Kiat Teo
 */
public abstract class Enemy implements Actor
{

    /**
     * Different State of Enemy
     * IDLE - Standing at one place
     * MOVE - Moving left or right
     * JUMP - Jump down the level
     * CHASE - Chasing player
     * ALIVE - Not Dead
     * DYING - Prepare for dead
     * DEAD - Dead and will get remove from the game
     */
    public enum EnemyState {IDLE, MOVE, JUMP, CHASE, ATTACK, ALIVE, DYING, DEAD,}

    /**
     * Starting position of the Enemy
     */
    private Vector2 start_xy;  // The starting position of the enemy in the game world

    /**
     * Updating position of the Enemy
     */
    private Vector2 position;  // The current position of the enemy in the game world

    /**
     * Player that play the game
     */
    private Player targetPlayer;   // The player it can threaten in the game world

    /**
     * The updating state of the enemy
     */
    private EnemyState state;      // The state of the enemy

    /**
     * Enemy have their own score
     */
    private int score;    // The score of the enemy instance if the player kill the enemy

    /**
     * Patrolling range of the enemy
     */
    private int patrol_range;

    /**
     * True if the enemy is a final boss of a level
     * False if not
     */
    private boolean isFinalBoss;

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     * @param player    The player that the enemy instance can kill in the game world
     * @param start_xy  The stating position to place the enemy instance
     * @param position  The allocated position of the enemy
     * @param score     The score of the enemy
     * @param patrol_range  The patrolling range of the enemy
     * @param isFinalBoss    The enemy is a final boss of a level or not
     */
    public Enemy(Player player, Vector2 start_xy, Vector2 position, int score, int patrol_range, boolean isFinalBoss) {
        this.targetPlayer = player;
        this.start_xy = new Vector2(start_xy.x, start_xy.y);
        this.state = EnemyState.ALIVE;
        this.position = new Vector2(position);
        this.score = score;  // this is not working
        this.patrol_range = patrol_range;
        this.isFinalBoss = isFinalBoss;
    }

    /**
     * Implement from interface
     */
    @Override
    public void reset()
    {
        //Do nothing
    }

    /**
     * Implement from interface
     * render function of the enemy
     */
    @Override
    public void draw(SpriteBatch batch)
    {
        //Do nothing
    }

    /**
     * Implement from interface
     * update game loop of the enemy
     */
    @Override
    public void update(float delta)
    {
        // Do nothing
    }

    /**
     * Implement from interface
     * Clean up function of enemy when exit
     */
    @Override
    public void dispose()
    {
        //Do nothing
    }

    /**
     * Abstract getter function for collider
     * @return Rectangle collider box
     */
    abstract public Rectangle getCollider();

    /**
     * Getter function of enemy start position
     * @return Vector2
     */
    public Vector2 getStartPosition()
    {
        return this.start_xy;
    }

    /**
     * Getter function of enemy position
     * @return Vector2
     */
    public Vector2 getPosition()
    {

        return this.position;
    }

    /**
     * Setter function of the enemy position
     * @param position new enemy position
     */
    public void setPosition(Vector2 position)
    {
        this.position = position;
    }

    /**
     * Getter function of the enemies' target player
     * @return Player
     */
    public Player getTargetPlayer()
    {
        return this.targetPlayer;
    }

    /**
     * Getter function of enemy current state
     * @return ENEMY STATE ENUM
     */
    public EnemyState getState()
    {
        return this.state;
    }

    /**
     * Setter function of enemy state
     * @param state  new state of the enemy
     */
    public void setState(EnemyState state)
    {
        this.state = state;
    }

    /**
     * Getter function of enemy score
     * @return int
     */
    public int getScore()
    {
        return this.score;
    }

    /**
     * Setter fucntion of the enemy score
     * @param new_score must >= 0
     */
    public void setScore(int new_score)
    {
        if(new_score >= 0 )
        {
            this.score = new_score;
        }
    }

    /**
     * Getter function of enemy patrolling range
     * @return int
     */
    public int getPatrolRange()
    {
        return this.patrol_range;
    }

    /**
     * Getter function of enemy
     * @return true is the enemy are final boss of the level
     * @return false if not
     */
    public boolean isFinalBoss()
    {
        return this.isFinalBoss;
    }

}
