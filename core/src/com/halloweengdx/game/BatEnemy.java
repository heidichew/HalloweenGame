package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BatEnemy extends Enemy{


    // Animations for bat enemy
    private Animation moveAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;

    // create collider

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     */
    public BatEnemy(Player player, Vector2 start_xy) {
        super(player, start_xy);
        score = 50;
        position = new Vector2(start_xy.x, start_xy.y);
        create();
    }

    private void create() {

    }

    @Override
    public void draw(SpriteBatch batch) {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

}
