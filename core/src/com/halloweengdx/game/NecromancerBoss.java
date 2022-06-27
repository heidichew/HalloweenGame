package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class NecromancerBoss extends Enemy
{
    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    private Animation idleAnimation = null;
    private Animation attackGroundAnimation = null;
    private Animation hurtAnimation = null;
    private Animation dyingAnimation = null;

    private float idle_state;
    private float attack_sky_state;
    private float attack_ground_state;
    private float hurt_state;
    private float dying_state;

    //environment
    private TiledMapTileLayer environment;

    //w and h
    private int necromancerWidth;
    private int necromancerHeight;

    //scale
    private float scale;

    //life
    private int live;

    //child
    private NecromancerChild child;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player       The player that the enemy instance can kill in the game world
     * @param start_xy     The stating position to place the enemy instance
     * @param score
     * @param patrol_range
     */
    public NecromancerBoss(Player player, Vector2 start_xy, TiledMapTileLayer environment, int score, int patrol_range)
    {
        super(player, start_xy, start_xy, score, patrol_range);

        this.idleAnimation = new Animation(0.2f, this.texture_assets.necromancer_idle_texture);
        this.attackGroundAnimation = new Animation(0.2f, this.texture_assets.necromancer_attack_ground_texture);
        this.hurtAnimation = new Animation(0.03f, this.texture_assets.necromancer_hurt_texture);
        this.dyingAnimation = new Animation(0.03f, this.texture_assets.necromancer_dead_texture);

        this.idle_state = 0.0f;
        this.attack_sky_state = 0.0f;
        this.attack_ground_state = 0.0f;
        this.hurt_state = 0.0f;
        this.dying_state = 0.0f;

        this.environment = environment;

        this.live = 3;

        this.scale = 0.35f;

        this.necromancerWidth = Math.round(this.texture_assets.necromancer_idle_texture[0].getWidth() * this.scale);
        this.necromancerHeight = Math.round(this.texture_assets.necromancer_idle_texture[0].getHeight() * this.scale);

        super.setState(EnemyState.ATTACK);

        this.child = null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch)
    {

        switch (super.getState())
        {
            case IDLE:
                Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
                batch.draw(idle_texture, super.getPosition().x - this.necromancerWidth/2, super.getPosition().y - this.necromancerHeight/2, this.necromancerWidth, this.necromancerHeight);

                break;

            case ATTACK:
                Texture attack_texture = (Texture) this.attackGroundAnimation.getKeyFrame(this.attack_ground_state, false);
                batch.draw(attack_texture, super.getPosition().x - this.necromancerWidth/2, super.getPosition().y - this.necromancerHeight/2, this.necromancerWidth, this.necromancerHeight);

                break;


        }

        if(this.child != null)
        {
            this.child.draw(batch);
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
            if(this.child!=null)
            {
                this.child.update(delta);
            }

            switch (super.getState())
            {
                case IDLE:
                    this.idle_state += delta;

                case ATTACK:
                    this.attack_ground_state += delta;

                    if(this.attackGroundAnimation.isAnimationFinished(this.attack_ground_state))
                    {
                        this.attack_ground_state = 0.0f;
                        this.child = new NecromancerChild(this, super.getTargetPlayer(), new Vector2(super.getPosition().x + 350f, super.getPosition().y - 50f), 50);
                        super.setState(EnemyState.IDLE);
                    }
            }
        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
