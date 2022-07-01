package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Necromancer child one of the enemy type
 * @author Adrian
 */
public class NecromancerChild extends Enemy
{
    /**
     * Game assets database
     */
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    /**
     * Animation
     */
    private Animation moveAnimation;
    private Animation attackAnimation;
    private Animation dyingAnimation;

    /**
     * State timer
     */
    private float move_state;
    private float attack_state;
    private float dying_state;

    /**
     * Necromancer child width and height
     */
    private int necromancerChild_Width;
    private int necromancerChild_Height;

    /**
     * Render scale
     */
    private float scale;

    /**
     * Moving speed
     */
    float movementSpeed;

    /**
     * Parent enmey spawn the child
     */
    private NecromancerBoss parent;

    /**
     * Attack position
     */
    private Vector2 attack_pos;

    /**
     * Render flip x
     */
    private boolean turn_head;


    /**
     * The constructor to create the necromancer child enemy with a specific starting position along with patrol range
     * @param player target player
     * @param start_xy starting location
     * @param score score when get killed
     */
    public NecromancerChild(NecromancerBoss necromancerBoss, Player player, Vector2 start_xy, int score)
    {
        // variable and field initialise
        super(player, start_xy, start_xy, score, 0, false);

        // animation
        this.moveAnimation = new Animation(0.07f, this.gameAssetsDB.necromancer_child_move_texture);
        this.attackAnimation = new Animation(0.1f, this.gameAssetsDB.necromancer_child_attack_texture);
        this.dyingAnimation = new Animation(0.2f, this.gameAssetsDB.necromancer_child_dying_texture);

        // state timer for animation
        this.move_state = 0.0f;
        this.attack_state = 0.0f;
        this.dying_state = 0.0f;

        // render scale
        this.scale = 0.36f;

        // moving speed
        this.movementSpeed = 350f;

        // necromancer child width and height
        this.necromancerChild_Width = Math.round(this.gameAssetsDB.necromancer_child_move_texture[0].getWidth() * this.scale);
        this.necromancerChild_Height = Math.round(this.gameAssetsDB.necromancer_child_move_texture[0].getHeight() * this.scale);

        // spawn parent
        this.parent = necromancerBoss;

        // attack position
        this.attack_pos = null;

        // set teh default state to move
        super.setState(EnemyState.MOVE);
    }

    @Override
    public void reset()
    {
        // Nothing to reset

    }

    /**
     * Rendering Function for the necromancer child enemy render with in different state
     * @param batch Sprite Batch
     */
    @Override
    public void draw(SpriteBatch batch)
    {

        switch (super.getState())
        {
            // chase and move are using the same texture to render
            case CHASE:
            case MOVE:
                Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.move_state, true);
                batch.draw(move_texture,
                        super.getPosition().x- this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        move_texture.getWidth() , move_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            // render attack
            case ATTACK:
                Texture attack_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
                batch.draw(attack_texture,
                        super.getPosition().x - this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        attack_texture.getWidth() , attack_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) attack_texture.getWidth(), (int) attack_texture.getHeight(), turn_head, false);
                break;

            // render dying
            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(this.dying_state, true);

                batch.draw(dying_texture,
                        super.getPosition().x- this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        dying_texture.getWidth() , dying_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);
                break;

        }

    }

    /**
     * Game loop updating function
     * update for the necromancer child enemy position, stage and action.
     * @param delta Gdx.graphics.getDeltaTime
     */
    @Override
    public void update(float delta)
    {

        // triggering for the dying and dead state
        if(super.getState() == EnemyState.DYING || super.getState() == EnemyState.DEAD)
        {
            this.dying_state += delta;
            if(this.dying_state >= this.dyingAnimation.getAnimationDuration())
            {
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD); // set to dead after the dying animation end
            }

        }
        else
        {

            if(this.parent.getLive() < 1)
            {
                super.setState(EnemyState.DYING);
            }

            switch (super.getState())
            {
                // update for move state
                case MOVE:

                    this.move_state += delta;

                    // player on the left
                    if(super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth() - 200f < super.getStartPosition().x)
                    {
                        this.turn_head = true;
                    }
                    else // player on the right
                    {
                        this.turn_head = false;
                    }

                    // if player are in the attacking range
                    if (super.getTargetPlayer().getPosition().dst(super.getStartPosition()) < 600f)
                    {
                        super.setState(EnemyState.CHASE); // start to chasing player

                    }

                    break;

                // update chase state
                case CHASE:

                    // movement x and y
                    float distance_x = 0.0f;
                    float distance_y = 0.0f;

                    this.move_state += delta;

                    // move the enemy close to the player
                    if(super.getPosition().x + (this.necromancerChild_Width/2 - 280f) < super.getTargetPlayer().getPosition().x)
                    {
                        distance_x += (this.movementSpeed * delta);
                    }
                    else if(super.getPosition().x + (this.necromancerChild_Width/2 - 280f) > super.getTargetPlayer().getPosition().x)
                    {
                        // when it move close enough to the player
                        // record about the current player location and set the state to attck
                        this.attack_pos = new Vector2(super.getTargetPlayer().getPosition());
                        super.setState(EnemyState.ATTACK);
                    }

                    // move the enemy up to the player head position
                    if(super.getPosition().y + this.necromancerChild_Height/2 - 150f < super.getTargetPlayer().getPosition().y + super.getTargetPlayer().getSprite().getHeight())
                    {
                        distance_y += (this.movementSpeed * delta);
                    }

                    // set the child position
                    super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));

                    // if the child chase the player and go far away from it parent it will dead
                    if(super.getPosition().dst(this.parent.getPosition()) > 1000f)
                    {
                        super.setState(EnemyState.DYING);
                    }

                    break;


                // update for attack
                case ATTACK:

                    this.attack_state += delta;

                    // detect for the attack animation end
                    if(this.attackAnimation.isAnimationFinished(this.attack_state))
                    {
                        this.attack_state = 0.0f;

                        // if the player still stand not far away from the attack triggered position the player will dead
                        if(this.attack_pos != null && super.getTargetPlayer().getPosition().dst(attack_pos) < 100f)
                        {
                            super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        }

                        // the enemy will dead after attack
                        super.setState(EnemyState.DYING);
                    }

                    break;
            }


        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Rectangle getCollider() {
        return null;
    }
}