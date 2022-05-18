package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Actor
{
    public void create();

    public void draw(SpriteBatch batch);

    public void update();
}
