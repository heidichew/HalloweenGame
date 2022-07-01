package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Licking enemy class one of the enemy type that blocking player
 * @author Adrian
 */
public class LickingEnemy extends Enemy
{

    /**
     * Game assets database
     */
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    /**
     * Animation
     */
    private Animation moveAnimation;
    private Animation dyingAnimation;
    private Animation jumpingAnimation;

    /**
     * State time use to control animation
     */
    private float moving_state;
    private float jumping_state;
    private float dying_state;

    /**
     * Moving speed
     */
    private float moving_speed;

    /**
     * The tile layer or the render environment
     */
    private TiledMapTileLayer environment;

    /**
     * The width and height
     */
    private int lickingWidth;
    private int lickingHeight;

    /**
     * Render flip x
     */
    private boolean turn_head;

    /**
     * Render scale
     */
    private float scale;

    /**
     * Create collider
     */
    private Rectangle collider;

    /**
     * The constructor to create an licking enemy that place the enemy at a specific starting position
     * @param player target player
     * @param start_xy starting location
     * @param environment rendering level
     * @param score score when get killed
     * @param patrol_range patrolling range
     * @param isFinalBoss  Final Boss of the level true or false
     */
    public LickingEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment,int score, int patrol_range, boolean isFinalBoss)
    {
        // variable / field initialise

        super(player, start_xy, start_xy, score, patrol_range, isFinalBoss);

        // animation
        this.moveAnimation = new Animation(0.03f, gameAssetsDB.licking_enemy_walking);
        this.jumpingAnimation = new Animation(0.2f, gameAssetsDB.licking_enemy_jumping);
        this.dyingAnimation = new Animation(0.05f, gameAssetsDB.enemy_dead_texture);

        // state time
        this.moving_state = 0;
        this.jumping_state = 0;
        this.dying_state = 0;

        // moving speed
        this.moving_speed = 350f;

        // render environment
        this.environment = environment;

        // width and height of the enemy
        this.lickingWidth = (int)Math.round(gameAssetsDB.licking_enemy_walking[0].getWidth());
        this.lickingHeight = (int) Math.round(gameAssetsDB.licking_enemy_walking[0].getHeight());

        // enemy turn left by default
        this.turn_head = false;

        // rendering scale
        this.scale = 0.5f;

        // set the default stage to move
        super.setState(EnemyState.MOVE);

        // collider initialise
        collider = new Rectangle(super.getPosition().x, super.getPosition().y, this.lickingWidth * this.scale, this.lickingHeight * this.scale);

    }

    @Override
    public void reset()
    {
        // Nothing to reset
    }

    /**
     * Rendering function for the licking enemy in different state
     * @param batch Sprite Batch
     */
    @Override
    public void draw(SpriteBatch batch)
    {
        switch (super.getState())
        {
            // rendering in move state
            case MOVE:
                Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.moving_state, true);
                batch.draw(move_texture,
                        super.getPosition().x, super.getPosition().y,
                        0,0,
                        move_texture.getWidth() , move_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            // rendering in jump state
            case JUMP:
                Texture jump_texture = (Texture) this.jumpingAnimation.getKeyFrame(this.jumping_state, true);
                batch.draw(jump_texture,
                        super.getPosition().x, super.getPosition().y,
                        0, 0,
                        jump_texture.getWidth(), jump_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) jump_texture.getWidth(), (int) jump_texture.getHeight(), turn_head, false);

                break;

            // rendering in dying state
            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(dying_state, false);
                batch.draw(dying_texture,
                        super.getPosition().x, super.getPosition().y,
                        0, 0,
                        dying_texture.getWidth(), dying_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);

                break;
        }

    }

    /**
     * Game loop updating function
     * update for the licking enemy position, stage and action.
     * @param delta Gdx.graphics.getDeltaTime
     */
    @Override
    public void update(float delta)
    {

        this.moving_state += delta; // state time use to control the moving animation
        this.jumping_state += delta; // state time use to control the jumping animation


        // Movement x an y
        float distance_x;
        float distance_y;

        // Trigger for the dying and dead stage
        if(super.getState() == EnemyState.DYING || super.getState() == EnemyState.DEAD)
        {
            if((super.getTargetPlayer().getState() != Player.PlayerState.HURT
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURTING
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURT_END
                    && super.getTargetPlayer().getState() != Player.PlayerState.DEAD
                    && super.getTargetPlayer().getState() != Player.PlayerState.DYING)
                    && super.getPosition().y > this.environment.getTileHeight()*2)
            {
                this.gameAssetsDB.enemy_dead.play();
            }

            this.dying_state += delta; // state time use to control the dying animation

            // set the enemy to dead when the dying animation is finished
            if(this.dyingAnimation.isAnimationFinished(dying_state))
            {
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else // when the enemy is not dead or dying
        {
            // detecting that the player are in the same y level in certain range
            if ((super.getTargetPlayer().getPosition().y >= super.getPosition().y -128f)
                    &&(super.getTargetPlayer().getPosition().y < super.getPosition().y + (lickingHeight * scale)))
            {
                // detecting that the player are at the middle of the enemy either in the left or right side
                if (super.getPosition().x + (lickingWidth * scale) - 160f > super.getTargetPlayer().getPosition().x &&
                        super.getPosition().x + (lickingWidth * scale) <= super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth())
                {

                    // detecting for jump kill
                    // if player go over the enemy and it was stating to falling after jump
                    if ((super.getTargetPlayer().getState() == Player.PlayerState.FALLING)
                            && (super.getTargetPlayer().getPosition().y >= super.getPosition().y + (lickingHeight * scale) - 150f))
                    {
                        // set the enemy to dying
                        super.setState(EnemyState.DYING);

                    }
                    else // when the enemy hit the player
                    {

                        // set the score to 0 and the enemy and the player dying together
                        super.setScore(0);
                        this.gameAssetsDB.licking_hit.play();
                        super.setState(EnemyState.DYING);
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                    }
                }
            }

        }


        // When the enemy is not dying or dead
        if(super.getState() != EnemyState.DYING && super.getState()!= EnemyState.DEAD)
        {

            if (this.turn_head)  // turning right
            {
                distance_x = this.moving_speed * delta;
            }
            else // turning left
            {
                distance_x = -(this.moving_speed) * delta;
            }

            distance_y = 0;


            // Checking for a collision above or below the character.

            // What map reference are we going to be at after this frame?
            int mapFutureY;
            int mapCurrentX;

            // Only make it fall if it is on land. So lok to see if we are on the ground.
            boolean onGround = false;

            // This is to track the width. A character could be on 1, 2 or 3 blocks, so
            // more than one block may need to be checked. This tracks how far into the width
            // we are
            float tempX = 0;

            // Keep checking for collisions until the go past the width
            while (tempX < this.lickingWidth * this.scale) {
                // Work out the x map reference.
                mapCurrentX = (int) (Math.floor((super.getPosition().x + tempX) / environment.getTileWidth()));

                // Work out the y map reference for the bottom
                mapFutureY = (int) (Math.floor((super.getPosition().y + distance_y) / environment.getTileHeight()));

                // Are we hitting a block from above?
                if (this.environment.getCell(mapCurrentX, mapFutureY) != null) {
                    // We have a hit. Can't go down, and need to move up to the top of the block if not already there.
                    distance_y = 0;
                    super.setPosition(new Vector2(super.getPosition().x, (mapFutureY + 1) * 128));
                }
                // If not, is there a block underneath?
                else if (this.environment.getCell(mapCurrentX, mapFutureY - 1) != null) {
                    ;
                    onGround = true;
                }

                // Work out the map y for the top of this thing
                mapFutureY = (int) (Math.floor((super.getPosition().y + distance_y + (this.lickingHeight * this.scale) / environment.getTileHeight())));

                // Are we hitting a block from below? Not that this can't be tested until jumping is added
                // so it might need a change.
                if (distance_y > 0 && this.environment.getCell(mapCurrentX, mapFutureY) != null) {
                    // We have a hit. Can't go up after all.
                    distance_y = 0;
                }

                tempX += 128;
            }

            // Do we need to fall?
            if (!onGround || super.getPosition().y % 128 != 0) {
                // We need to fall
                super.setState(EnemyState.JUMP);
                distance_y = (float) (-(this.moving_speed) * 2.5 * delta);

            } else {

                // if the enemy are dropping down to the bottom
                if(super.getPosition().y < this.environment.getTileHeight()*2)
                {
                    // the score will set to 0 and it will dying
                    super.setScore(0);
                    super.setState(EnemyState.DYING);
                }
                else
                {
                    super.setState(EnemyState.MOVE);
                }

            }


            // Checking for collision on X

            // Map reference of the left
            int mapCurrentY;
            int mapFutureX;

            float tempY = 0;

            // enemy width and height are larger that the map 128 so need to detect for multiple tile
            while (tempY < this.lickingHeight * this.scale) {

                mapCurrentY = (int) (Math.floor((super.getPosition().y + tempY) / environment.getTileHeight()));
                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                // Are we hitting a block to the left?
                if (this.environment.getCell(mapFutureX, mapCurrentY) != null) {
                    // We have a hit. Can't go left, and need to move up to the right of the block if not already there.
                    distance_x = 0;
                    super.setPosition(new Vector2((mapFutureX + 1) * 128, super.getPosition().y));
                    this.turn_head = true;
                }

                // map references of the right
                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x + (this.lickingWidth * this.scale)) / environment.getTileWidth()));

                // Are we hitting a block to the right?
                if (this.environment.getCell(mapFutureX, mapCurrentY) != null) {
                    // We have a hit. Can't go right, and need to move to the left of the block if not already there.
                    distance_x = 0;
                    super.setPosition(new Vector2(mapFutureX * 128 - (this.lickingWidth * this.scale) - 5, super.getPosition().y));
                    this.turn_head = false;
                }
                tempY += 128;
            }

            // make sure that the enemy don't go over the screen in x
            if (super.getPosition().x + distance_x >= (this.environment.getWidth() * 128) - (lickingWidth / 2))
            {
                this.turn_head = false;

            }
            else if(super.getPosition().x + distance_x <= (0 + (lickingWidth / 2) - 120f)) // using graphic pixel)
            {
                this.turn_head = true;
            }

            // set the location of the enemy after update
            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }

        // collider update
        this.collider.setPosition(super.getPosition());
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }
}