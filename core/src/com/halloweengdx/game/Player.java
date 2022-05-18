package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player implements Actor
{

    public enum PLAYER_STATE{ IDLE, MOVING, ATTACKING, JUMPING };
    public enum PLAYER_DIRECTION {LEFT, RIGHT};

    private float x;
    private float y;

    //texture

    public Player(float x, float y)
    {
        GameAssetsDB player_assets = GameAssetsDB.getInstance();

        //loading texture from db

    }


    public void render(SpriteBatch batch)
    {

    }

    public void update()
    {
        //update stuff

    }


}
