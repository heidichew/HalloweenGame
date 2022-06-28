package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy implements Actor
{

    public enum EnemyState {IDLE, ALIVE, ATTACK, DYING, DEAD, MOVE, JUMP, CHASE}

    //if you have a getter and setter method of each one it should become all private

    private Vector2 start_xy = null;  // The starting position of the enemy in the game world

    private Vector2 position = null;  // The current position of the enemy in the game world

    private Player targetPlayer = null;   // The player it can threaten in the game world

    private EnemyState state = null;      // The state of the enemy

    private int score = 0;    // The score of the enemy instance if the player kill the enemy

    private int patrol_range = 0;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     * @param player    The player that the enemy instance can kill in the game world
     * @param start_xy  The stating position to place the enemy instance
     */
    public Enemy(Player player, Vector2 start_xy, Vector2 position, int score, int patrol_range) {
        this.targetPlayer = player;
        this.start_xy = new Vector2(start_xy.x, start_xy.y);
        this.state = EnemyState.ALIVE;
        this.position = new Vector2(position);
        this.score = score;
        this.patrol_range = patrol_range;
    }

    @Override
    public void reset(){}

    @Override
    public void draw(SpriteBatch batch){
        //texture
    }

    @Override
    public void update(float delta){}

    @Override
    public void dispose(){}

    // Getters and Setters
    public Vector2 getStartPosition() { return start_xy; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }

    public Player getTargetPlayer() { return targetPlayer; }

    public EnemyState getState() { return state; }

    public void setState(EnemyState state) { this.state = state; }

    public int getScore() { return score; }

    public void setScore(int new_score)
    {
        if(new_score >=0)
        {
            this.score = new_score;
        }
    }

    public int getPatrolRange() {return  patrol_range;}
}
