package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Skull enemy one of the ground enemy type
 * @author Adrian
 */
public class SkullEnemy extends Enemy
{
    /**
     * Game assets databse
     */
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    /**
     * Animation
     */
    private Animation idleAnimation;
    private Animation moveAnimation;
    private Animation dyingAnimation;
    private Animation attackAnimation;

    /**
     * State timer
     */
    private float idle_state;
    private float moving_state;
    private float dying_state;
    private float attack_state;

    /**
     * Moving Speed
     */
    private float moving_speed;

    /**
     * Timer that enemy stay in same position
     */
    private float stayTimer;

    /**
     * Render Environment
     */
    private TiledMapTileLayer environment;

    /**
     * Skull width and Height
     */
    private int skullWidth;
    private int skullHeight;

    /**
     * Render flip x
     */
    private boolean turn_head;

    /**
     * Render scale
     */
    private float scale;

    /**
     * Collider
     */
    private Rectangle collider;

    /**
     * The constructor to create the skull enemy with a specific starting position along with patrol range
     * @param player target player
     * @param start_xy starting location
     * @param environment rendering level
     * @param score score when get killed
     * @param patrol_range patrolling range
     * @param isFinalBoss  Final Boss of the level true or false
     */
    public SkullEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment,int score, int patrol_range, boolean isFinalBoss)
    {
        //variable initialise
        super(player, start_xy, start_xy, score, patrol_range, isFinalBoss);

        // loading different animation and texture in boss and normal
        if (super.isFinalBoss())
        {
            this.idleAnimation = new Animation(0.2f, gameAssetsDB.skull_boss_enemy_idle_texture);
            this.moveAnimation = new Animation(0.05f, gameAssetsDB.skull_boss_enemy_walking_texture);
            this.dyingAnimation = new Animation(0.12f, gameAssetsDB.skull_boss_enemy_dead_texture);
            this.attackAnimation = new Animation(0.05f, gameAssetsDB.skull_boss_enemy_attacking_texture);

            this.skullWidth = gameAssetsDB.skull_boss_enemy_walking_texture[0].getWidth();
            this.skullHeight = gameAssetsDB.skull_boss_enemy_walking_texture[0].getHeight();

            this.scale = 1.2f;
        }
        else
        {
            this.idleAnimation = new Animation(0.2f, gameAssetsDB.skull_enemy_idle_texture);
            this.moveAnimation = new Animation(0.05f, gameAssetsDB.skull_enemy_walking_texture);
            this.dyingAnimation = new Animation(0.12f, gameAssetsDB.skull_enemy_dead_texture);
            this.attackAnimation = new Animation(0.05f, gameAssetsDB.skull_enemy_attacking_texture);

            this.skullWidth = gameAssetsDB.skull_enemy_walking_texture[0].getWidth();
            this.skullHeight = gameAssetsDB.skull_enemy_walking_texture[0].getHeight();

            this.scale = 0.95f;
        }

        // state timer
        this.idle_state = 0;
        this.moving_state = 0;
        this.dying_state = 0;
        this.attack_state = 0;

        // render environment
        this.environment = environment;

        //moving speed
        this.moving_speed = 128f;

        // stay timer
        this.stayTimer = 0.0f;

        // the enemy turn right by default
        this.turn_head = false;

        // set the default state to move
        super.setState(EnemyState.MOVE);

        // collider
        collider = new Rectangle(super.getPosition().x - 200f, super.getPosition().y, skullWidth * this.scale, skullHeight * this.scale);
    }

    @Override
    public void reset()
    {
        // Nothing to reset

    }

    /**
     * Rendering Function for the skull enemy render with in different state
     * @param batch Sprite Batch
     */
    @Override
    public void draw(SpriteBatch batch)
    {

        switch (super.getState())
        {
            // move and chase animation will using the same moving texture
            case CHASE:
            case MOVE:
                Texture move_texture = (Texture)this.moveAnimation.getKeyFrame(this.moving_state, true);
                batch.draw(move_texture,
                        super.getPosition().x - (move_texture.getWidth() / 2.0f), super.getPosition().y - (move_texture.getHeight() / 2.0f),
                        0, 0,
                        move_texture.getWidth(),  move_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            // idle animation render
            case IDLE:
                Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f),
                        0, 0,
                        idle_texture.getWidth(),  idle_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)idle_texture.getWidth(), (int) idle_texture.getHeight(), turn_head, false);

                break;

            // attack animation render
            case ATTACK:
                Texture attacking_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
                batch.draw(attacking_texture,
                        super.getPosition().x - (attacking_texture.getWidth() / 2.0f), super.getPosition().y - (attacking_texture.getHeight() / 2.0f),
                        0, 0,
                        attacking_texture.getWidth(),  attacking_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)attacking_texture.getWidth(), (int) attacking_texture.getHeight(), turn_head, false);

                break;
            //dying animation render
            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(this.dying_state, true);
                batch.draw(dying_texture,
                        super.getPosition().x - (dying_texture.getWidth() / 2.0f), super.getPosition().y - (dying_texture.getHeight() / 2.0f) - 15f,
                        0, 0,
                        dying_texture.getWidth(),  dying_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);



        }

    }

    /**
     * Game loop updating function
     * update for the skull enemy position, stage and action.
     * @param delta Gdx.graphics.getDeltaTime
     */
    @Override
    public void update(float delta)
    {
        // When the enemy is dying or dead
        if(this.getState() == EnemyState.DYING || this.getState() == EnemyState.DEAD)
        {
            if(this.gameAssetsDB.danger_zone_music.isPlaying())
            {
                this.gameAssetsDB.danger_zone_music.stop();
            }

            this.gameAssetsDB.enemy_dead.play();

            this.dying_state += delta; // stage time for dying animation

            // set the enemy to dead when the dying animation is finish
            if(this.dyingAnimation.isAnimationFinished(dying_state))
            {
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else // When the enemy is not dying or dead
        {
            this.moving_state += delta; // state time control the moving animation
            this.idle_state += delta; // state time control the idle animation

            // movement x and y
            float distance_x = 0;
            float distance_y = 0;


            if (super.getPatrolRange() > 0)
            {
                // detect for the idle state the enemy will only stay in one position when idle
                if (super.getState() == EnemyState.IDLE)
                {
                    this.stayTimer += delta; // timer to control that the skull enemy stay in the same position

                    // skull enemy will not stay in one position more than 3 seconds
                    if (this.stayTimer >= 3) {
                        stayTimer = 0.0f;
                        super.setState(EnemyState.MOVE);
                    }
                }

                // if enemy not idle need to move
                if (super.getState() == EnemyState.MOVE || super.getState() == EnemyState.CHASE)
                {

                    if (turn_head)  //turning left
                    {
                        distance_x = -(this.moving_speed) * delta;
                    }
                    else //turning right
                    {
                        distance_x = this.moving_speed * delta;
                    }

                }

            }


            // Checking for few situation before setup the Chase state
            // Checking the player are not in the hurt adn dying stage
            // Make sure that the player are in the certain range of the x and y of the enemy
            if ((super.getTargetPlayer().getState() != Player.PlayerState.HURT
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURTING
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURT_END
                    && super.getTargetPlayer().getState() != Player.PlayerState.DEAD
                    && super.getTargetPlayer().getState() != Player.PlayerState.DYING)
                    && (super.getTargetPlayer().getPosition().x <= super.getStartPosition().x + (super.getPatrolRange() * 128))
                    && (super.getTargetPlayer().getPosition().x > super.getStartPosition().x - (super.getPatrolRange() * 128))
                    && super.getTargetPlayer().getPosition().y >= super.getStartPosition().y - this.skullHeight/2 - 100f
                    && super.getTargetPlayer().getPosition().y + super.getTargetPlayer().getSprite().getHeight() < super.getStartPosition().y + skullHeight)
            {
                super.setState(EnemyState.CHASE);
                this.gameAssetsDB.danger_zone_music.play();

            }
            else // the situation not suitable for Chase state anymore
            {
                // set the stage back to the move stage
                if (super.getState() == EnemyState.CHASE || super.getState() == EnemyState.ATTACK)
                {
                    super.setState(EnemyState.MOVE);
                    this.gameAssetsDB.danger_zone_music.stop();
                }
            }

            // In the Chase stage
            if (super.getState() == EnemyState.CHASE)
            {

                // the enemy moving double speed than usual
                distance_x *= 2;

                // the player are at the left of the enemy
                if (super.getPosition().x - super.getTargetPlayer().getPosition().x - ((super.getTargetPlayer().getSprite().getWidth() + 25f)) >= 0)
                {
                    turn_head = true;

                    // the player are at the right of the enemy
                } else if (super.getPosition().x - super.getTargetPlayer().getPosition().x + this.getTargetPlayer().getSprite().getWidth()/2 < 0) {
                    turn_head = false;

                }
                else // when the enemy are in the attack position
                {
                    distance_x = 0;
                    this.attack_state += delta; // attack state time to control attack animation
                    super.setState(EnemyState.ATTACK);

                    this.gameAssetsDB.skull_hit.play();

                    // when the attack animation finish the player will get hurt
                    if(this.attack_state >= this.attackAnimation.getAnimationDuration())
                    {
                        this.gameAssetsDB.skull_hit.stop();
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        this.attack_state = 0.0f;
                    }
                }
            }

            // map references for x coordinate
            int mapCurrentY = (int) (Math.round(super.getPosition().y / environment.getTileHeight()));
            int mapFutureX = (int) (Math.round((super.getPosition().x + distance_x) / environment.getTileWidth()));

            // make sure that the skull is moving
            if (distance_x != 0)
            {
                // map reference of the enemy start position
                int mapInitialX = (int) (Math.round(super.getStartPosition().x / environment.getTileHeight()));

                // detect for two situation
                // if the enemy are reach the patrolling range or it go off the screen reach 0
                if (super.getState() == EnemyState.MOVE && (Math.abs(mapInitialX - mapFutureX + 1) >= super.getPatrolRange() ||
                        super.getPosition().x + distance_x <= 0 + skullWidth / 2))
                {
                    distance_x = 0; // stop moving

                    // have 70% that it will being idle and stop at the position for 3 seconds
                    if (new Random().nextInt(10) + 1 > 3) {
                        super.setState(EnemyState.IDLE);
                    }
                    else // 30% that it will keep moving to the other side
                    {
                        turn_head = !turn_head;
                    }

                }
                else
                {
                    // this may not working since the enemy are not hitting left and right in game so cannot test
                    // but keep it in case needed

                    int xStep = (int) (Math.round(this.skullWidth / 128)) - 3; //cause by the background

                    TiledMapTileLayer.Cell leftCell = this.environment.getCell(mapFutureX, mapCurrentY); //left
                    TiledMapTileLayer.Cell rightCell = this.environment.getCell(mapFutureX + xStep, mapCurrentY); //right

                    if (leftCell != null || rightCell != null)
                    {
                        // hitting something turn left or right
                        distance_x = 0;
                        turn_head = !turn_head;
                    }
                }
            }

            // set the position of the skull enemy
            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }

        // Set the collider position
        this.collider.setPosition(new Vector2(super.getPosition().x - 200, super.getPosition().y));
    }


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Rectangle getCollider() {
        return collider;
    }
}
