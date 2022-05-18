package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy
{

    public enum EnemyState {IDLE, ALIVE, ATTACK, DYING, DEAD}

    public enum ENEMY_STATE{ IDLE, MOVING, ATTACKING, JUMPING };
    public enum ENEMY_DIRECTION {LEFT, RIGHT};

    protected Vector2 start_xy = null;  // The starting position of the enemy in the game world

    protected Vector2 position = null;  // The current position of the enemy in the game world

    protected Player targetPlayer = null;   // The player it can threaten in the game world

    protected EnemyState state = null;      // The state of the enemy

    protected int score = 0;    // The score of the enemy instance if the player kill the enemy

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     * @param player    The player that the enemy instance can kill in the game world
     * @param start_xy  The stating position to place the enemy instance
     */
    public Enemy(Player player, Vector2 start_xy) {
        this.targetPlayer = player;
        this.start_xy = new Vector2(start_xy.x, start_xy.y);
        this.state = EnemyState.ALIVE;
    }

    abstract public void draw(SpriteBatch batch);

    abstract public void update(float delta);

    abstract public void dispose();

    // Getters and Setters
    public Vector2 getStartPosition() { return start_xy; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }

    public Player getTargetPlayer() { return targetPlayer; }

    public EnemyState getState() { return state; }

    public void setState(EnemyState state) { this.state = state; }

    public int getScore() { return score; }
}
