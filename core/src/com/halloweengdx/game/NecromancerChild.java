package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class NecromancerChild extends Enemy
{
    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    private Animation moveAnimation;
    private Animation attackAnimation;
    private Animation dyingAnimation;

    private float move_state;
    private float attack_state;
    private float dying_state;


    //w and h
    private int necromancerChild_Width;
    private int necromancerChild_Height;

    //scale
    private float scale;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player       The player that the enemy instance can kill in the game world
     * @param start_xy     The stating position to place the enemy instance
     * @param score
     */
    public NecromancerChild(NecromancerBoss necromancerBoss, Player player, Vector2 start_xy, int score)
    {
        super(player, start_xy, start_xy, score, 0);

        this.moveAnimation = new Animation(0.07f, this.texture_assets.necromancer_child_move_texture);
        this.attackAnimation = new Animation(0.1f, this.texture_assets.necromancer_child_attack_texture);
        this.dyingAnimation = new Animation(0.03f, this.texture_assets.necromancer_child_dying_texture);

        this.move_state = 0.0f;
        this.attack_state = 0.0f;
        this.dying_state = 0.0f;

        this.scale = 0.36f;

        this.necromancerChild_Width = Math.round(this.texture_assets.necromancer_child_move_texture[0].getWidth() * this.scale);
        this.necromancerChild_Height = Math.round(this.texture_assets.necromancer_child_move_texture[0].getHeight() * this.scale);

        super.setState(EnemyState.MOVE);
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        switch (super.getState())
        {
            case MOVE:
                Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.move_state, true);
                batch.draw(move_texture, super.getPosition().x - this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2, this.necromancerChild_Width, this.necromancerChild_Height);

                break;

            case ATTACK:
                Texture attack_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, false);
                batch.draw(attack_texture, super.getPosition().x - this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2, this.necromancerChild_Width, this.necromancerChild_Height);

                break;

        }

    }

    @Override
    public void update(float delta)
    {

        if(this.getState() == EnemyState.DYING || this.getState() == EnemyState.DEAD)
        {
            this.dying_state += delta;
            if(this.dying_state >= this.dyingAnimation.getAnimationDuration())
            {
                this.texture_assets.enemy_dead.play();
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else
        {
            switch (super.getState())
            {
                case MOVE:
                    this.move_state += delta;

                case ATTACK:
                    this.attack_state += delta;
            }

        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
