package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Actor
{
    public void reset(); //Setup something before draw

    public void draw(SpriteBatch batch);

    public void update(float delta);

    public void dispose();

    //getter and setter

    public Vector2 getStartPosition();

    public Vector2 getPosition();

    public void setPosition(Vector2 position);

}
