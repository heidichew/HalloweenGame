package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * NecromancerBoss the boss of the level 2 one type of the enemy
 * @author Adrian
 */
public class NecromancerBoss extends Enemy
{
    /**
     * Game assets database
     */
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    /**
     * Attack cold down timer
     */
    private final int ATTACK_COLDOWN = 3;

    /**
     * Animation
     */
    private Animation idleAnimation;
    private Animation attackGroundAnimation;
    private Animation attackGroundAnimation_2;
    private Animation hurtAnimation;
    private Animation dyingAnimation;

    /**
     * State timer
     */
    private float idle_state;
    private float attack_ground_state_2;
    private float attack_ground_state;
    private float hurt_state;
    private float dying_state;

    /**
     * Attack method
     */
    private boolean shortRange;

    /**
     * Render environment
     */
    private TiledMapTileLayer environment;

    /**
     * Width and Height of the texture
     */
    private int necromancerWidth;
    private int necromancerHeight;

    /**
     * Render scale
     */
    private float scale;

    /**
     * live to survive
     */
    private int live;

    /**
     * Long range attack spawn for the his child
     */
    private NecromancerChild child;

    /**
     * Attack timer
     */
    private float attack_timer;

    /**
     * Collider
     */
    private Rectangle collider;



    /**
     * The constructor to create the necromancer enemy with a specific starting position along with patrol range
     * @param player target player
     * @param start_xy starting location
     * @param environment rendering level
     * @param score score when get killed
     * @param patrol_range patrolling range
     * @param isFinalBoss  Final Boss of the level true or false
     */
    public NecromancerBoss(Player player, Vector2 start_xy, TiledMapTileLayer environment, int score, int patrol_range, boolean isFinalBoss)
    {
        // variable / field initialize

        super(player, start_xy, start_xy, score, patrol_range, isFinalBoss);

        // animation
        this.idleAnimation = new Animation(0.1f, this.gameAssetsDB.necromancer_idle_texture);
        this.attackGroundAnimation = new Animation(0.2f, this.gameAssetsDB.necromancer_attack_ground_texture);
        this.attackGroundAnimation_2 = new Animation(0.1f, this.gameAssetsDB.necromancer_attack_ground_texture_2);
        this.hurtAnimation = new Animation(0.08f, this.gameAssetsDB.necromancer_hurt_texture);
        this.dyingAnimation = new Animation(0.15f, this.gameAssetsDB.necromancer_dead_texture);

        // state timer
        this.idle_state = 0.0f;
        this.attack_ground_state = 0.0f;
        this.attack_ground_state_2 = 0.0f;
        this.hurt_state = 0.0f;
        this.dying_state = 0.0f;

        // tile layer
        this.environment = environment;

        // live
        this.live = 3;

        // scale
        this.scale = 0.35f;

        // texture width and height
        this.necromancerWidth = Math.round(this.gameAssetsDB.necromancer_idle_texture[0].getWidth() * this.scale);
        this.necromancerHeight = Math.round(this.gameAssetsDB.necromancer_idle_texture[0].getHeight() * this.scale);

        // set the default state to idle
        super.setState(EnemyState.IDLE);

        // startup two second of attack
        this.attack_timer = 2.0f;

        // child set to null since no player are in the range at the beginning
        this.child = null;

        // player always come from far right so the short range will be false
        this.shortRange = false;

        // collider initialise
        collider = new Rectangle(super.getPosition().x - (necromancerWidth / 2.0f), super.getPosition().y - (necromancerHeight / 2.0f), this.necromancerWidth * this.scale, this.necromancerHeight * this.scale);
    }

    @Override
    public void reset()
    {
        // Nothing to reset

    }

    /**
     * Rendering Function for the necromancer enemy render with in different state
     * @param batch Sprite Batch
     */
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

                Texture attack_texture;

                if(shortRange == false)
                {
                    attack_texture = (Texture) this.attackGroundAnimation.getKeyFrame(this.attack_ground_state, false);
                }
                else
                {
                    attack_texture = (Texture) this.attackGroundAnimation_2.getKeyFrame(this.attack_ground_state_2, false);
                }

                batch.draw(attack_texture, super.getPosition().x - this.necromancerWidth/2, super.getPosition().y - this.necromancerHeight/2, this.necromancerWidth, this.necromancerHeight);

                break;

            case DYING:

                Texture dying_texture;

                if(this.live <= 1)
                {
                    // play dying animation if last live being deducted
                    dying_texture = (Texture) this.dyingAnimation.getKeyFrame(this.dying_state, false);
                }
                else
                {
                    // play hurt if live more than 1
                    dying_texture = (Texture) this.hurtAnimation.getKeyFrame(this.hurt_state, false);
                }

                batch.draw(dying_texture, super.getPosition().x - this.necromancerWidth/2, super.getPosition().y - this.necromancerHeight/2, this.necromancerWidth, this.necromancerHeight);

                break;


        }

        // render the child as well if it is being spawn
        if(this.child != null)
        {
            this.child.draw(batch);
        }

    }

    /**
     * Game loop updating function
     * update for the necromancer enemy position, stage and action.
     * @param delta Gdx.graphics.getDeltaTime
     */
    @Override
    public void update(float delta)
    {
        // detecting for the dying and dead state
        if(this.getState() == EnemyState.DYING || this.getState() == EnemyState.DEAD)
        {

            if(live <= 1) // when the enemy only have last live
            {
                // control the dying animation
                this.dying_state += delta;
                this.gameAssetsDB.enemy_dead.play();
                if(this.dyingAnimation.isAnimationFinished(dying_state))
                {
                    this.live -= 1;
                    this.dying_state = 0.0f;
                    super.setState(EnemyState.DEAD);
                }
            }
            else // when the enemy have liver more than 1
            {
                // control the hurt animation
                this.hurt_state += delta;
                if(this.hurtAnimation.isAnimationFinished(hurt_state))
                {
                    this.live -= 1;
                    this.hurt_state = 0.0f;
                    super.setState(EnemyState.IDLE);
                }

            }


        }
        else // if enemy not dying or dead
        {
            // update the child if it alive
            if(this.child!=null)
            {
                this.child.update(delta);

                // if the child is in dead state after update
                if(this.child.getState() == EnemyState.DEAD)
                {
                    // delete the child and set to null
                    this.child = null;
                }
            }

            switch (super.getState())
            {
                // update in idle state
                case IDLE:

                    this.idle_state += delta; // idle state timer for animation

                    // detecting for the distance of the enemy and the player
                    if(super.getPosition().dst(super.getTargetPlayer().getPosition()) <= this.environment.getTileWidth() * getPatrolRange())
                    {
                        // player in patrol range
                        // by default use long range attack calling for cild
                        this.shortRange = false;

                        // if the player too close the enemy
                        if(super.getPosition().dst(super.getTargetPlayer().getPosition()) < 400f)
                        {
                            // use short range attack
                            shortRange = true;
                        }

                        this.attack_timer +=delta; // attack state timer for animation

                        // every three second set to the attack state
                        if(this.attack_timer >= ATTACK_COLDOWN )
                        {
                            super.setState(EnemyState.ATTACK);
                            this.attack_timer = 0;
                        }


                    }

                    break;

                // update in attack state
                case ATTACK:

                    // if not short range attack calling for child
                    if(this.shortRange == false)
                    {
                        this.attack_ground_state += delta; // long range attack animation timer calling child

                        if(this.attackGroundAnimation.isAnimationFinished(this.attack_ground_state))
                        {
                            this.attack_ground_state = 0.0f;
                            this.child = new NecromancerChild(this, super.getTargetPlayer(), new Vector2(super.getPosition().x + 350f, super.getPosition().y - 50f), 0);
                            super.setState(EnemyState.IDLE); // set back to idle when attack animation finish
                        }
                    }
                    else // short range attack
                    {

                        this.attack_ground_state_2 += delta; // short range attack timer for animation

                        // when the animation finish
                        if(this.attackGroundAnimation_2.isAnimationFinished(this.attack_ground_state_2))
                        {
                            // trigger that whether the player still in the short range attack's range
                            if(super.getPosition().dst(super.getTargetPlayer().getPosition()) < 400f)
                            {
                                // kill player if yes
                                super.getTargetPlayer().setState(Player.PlayerState.HURT);
                            }

                            this.attack_ground_state_2 = 0.0f;

                            // set back to idle after attack
                            super.setState(EnemyState.IDLE);
                        }
                    }
            }
        }

        // Update the collider
        this.collider.setPosition(new Vector2(super.getPosition().x - (necromancerWidth / 2.0f), super.getPosition().y - (necromancerHeight / 2.0f)));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Rectangle getCollider() {
        return this.collider;
    }

    public int getLive()
    {
        return this.live;
    }
}
