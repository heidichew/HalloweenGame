package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Enemy implements Actor
{

    public enum ENEMY_STATE{ IDLE, MOVING, ATTACKING, JUMPING };
    public enum ENEMY_DIRECTION {LEFT, RIGHT};

    private float x;
    private float y;

    //texture

    public Enemy(float x, float y)
    {
        GameAssetsDB player_assets = GameAssetsDB.getInstance();

        //loading texture from db

    }


    public void render(SpriteBatch batch)
    {

    }

    public void update()
    {

    }
}
