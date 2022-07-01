package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Bat Enemy, one of the flying enemy type.
 * @author Adrian
 */
public class BatEnemy extends Enemy
{

    /**
     * Texture Database
     */
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    /**
     * All the bat animation
     */
    private Animation moveAnimation;
    private Animation idleAnimation;
    private Animation dyingAnimation;
    private Animation attackAnimation;

    /**
     * The state time for the animation
     */
    private float moving_state;
    private float idle_state;
    private float attack_state;
    private float dying_state;

    /**
     * The moving speed of the bat
     */
    private float moving_speed;

    /**
     * Timer for the bat to hold when it fly
     */
    private float holdTimer;
    private float onHoldTimer;
    private boolean onHold;

    /**
     * Tile Map layer
     */
    private TiledMapTileLayer environment;


    /**
     * The width and heigh of the Bat texture
     */
    private int batWidth;
    private int batHeight;

    /**
     * Rise for flying up or down
     * false fly down
     * true fly up
     */
    private boolean rise;

    /**
     * Turn for turn left or right
     * false turn right
     * true turn left
     */
    private boolean turn;

    /**
     * Scale for rendering
     */
    private float scale;

    /**
     * Collider box
     */
    private Rectangle collider;

    /**
     * The constructor to create the bat enemy with a specific starting position along with patrol range
     * @param player target player
     * @param start_xy starting location
     * @param environment rendering level
     * @param score score when get killed
     * @param patrol_range patrolling range
     * @param isFinalBoss  Final Boss of the level true or false
     */
    public BatEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int score, int patrol_range, boolean isFinalBoss)
    {
        // variable/field initialise

        super(player, start_xy, start_xy, score, patrol_range, isFinalBoss);

        // animation
        this.moveAnimation = new Animation(0.05f,this.gameAssetsDB.bat_enemy_flying_texture);
        this.idleAnimation = new Animation(0.1f, this.gameAssetsDB.bat_enemy_idle_texture);
        this.attackAnimation = new Animation(0.03f, this.gameAssetsDB.bat_enemy_attacking_texture);
        this.dyingAnimation = new Animation(0.05f, this.gameAssetsDB.enemy_dead_texture);

        // state
        this.moving_state = 0.0f;
        this.idle_state = 0.0f;
        this.attack_state = 0.0f;

        // moving speed
        this.moving_speed = 128f;

        // flying hold timer
        this.holdTimer = 0.0f;
        this.onHoldTimer = 0.0f;
        this.onHold = false;

        // current layer
        this.environment = environment;

        // bat height and width
        this.batWidth = this.gameAssetsDB.bat_enemy_idle_texture[0].getWidth();
        this.batHeight = this.gameAssetsDB.bat_enemy_idle_texture[0].getHeight();

        // bat default beginning should be flying down and turn right
        this.rise = false;
        this.turn = false;

        // initialise the scale
        this.scale = 0.7f;

        // Create collider
        collider = new Rectangle(super.getPosition().x - (batWidth / 2.0f), super.getPosition().y - (batHeight / 2.0f), this.batWidth * this.scale, this.batHeight * this.scale);

        // Set teh default state to move
        super.setState(EnemyState.MOVE);

    }

    @Override
    public void reset()
    {
        // Nothing to reset

    }

    /**
     * Rendering Function for the bat enemy render with in different state
     * @param batch Sprite Batch
     */
    @Override
    public void draw(SpriteBatch batch)
    {

        switch (super.getState())
        {
            // move and chase animation will using the same moving texture
            case MOVE:
            case CHASE:

                Texture move_texture = (Texture)this.moveAnimation.getKeyFrame(this.moving_state, true);

                batch.draw(move_texture,
                        super.getPosition().x - (move_texture.getWidth() / 2.0f), super.getPosition().y - (move_texture.getHeight() / 2.0f),
                        0, 0,
                        move_texture.getWidth(), move_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)move_texture.getWidth(), (int) move_texture.getHeight(), !turn, false);
                break;

            // idle animation render
            case IDLE:

                Texture idle_texture = (Texture)this.idleAnimation.getKeyFrame(this.idle_state, true);

                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f),
                        0, 0,
                        idle_texture.getWidth(), idle_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)idle_texture.getWidth(), (int) idle_texture.getHeight(), !turn, false);
                break;

            // attack animation render
            case ATTACK:

                Texture attack_texture = (Texture)this.attackAnimation.getKeyFrame(this.attack_state, true);

                batch.draw(attack_texture,
                        super.getPosition().x - (attack_texture.getWidth() / 2.0f), 10+ super.getPosition().y - (attack_texture.getHeight() / 2.0f),
                        0, 0,
                        attack_texture.getWidth(), attack_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)attack_texture.getWidth(), (int) attack_texture.getHeight(), !turn, false);
                break;

            //dying animation render
            case DYING:

                Texture dying_texture = (Texture)this.dyingAnimation.getKeyFrame(this.dying_state, false);

                batch.draw(dying_texture,
                        super.getPosition().x - (dying_texture.getWidth() / 2.0f), super.getPosition().y - (dying_texture.getHeight() / 2.0f),
                        0, 0,
                        dying_texture.getWidth(), dying_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)dying_texture.getWidth(), (int) dying_texture.getHeight(), !turn, false);
                break;

        }


    }

    /**
     * Game loop updating function
     * update for the bat enemy position, stage and action.
     * @param delta Gdx.graphics.getDeltaTime
     */
    public void update(float delta)
    {

        // When the enemy is dying or dead
        if(this.getState() == EnemyState.DYING || this.getState() == EnemyState.DEAD)
        {
            // music play and stop
            if(this.gameAssetsDB.danger_zone_music.isPlaying())
            {
                this.gameAssetsDB.danger_zone_music.stop();
            }

            this.gameAssetsDB.enemy_dead.play();

            this.dying_state += delta; // stage time for dying animation

            // set the enemy to dead when the dying animation is finish
            if(this.dyingAnimation.isAnimationFinished(this.dying_state))
            {
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else // When the enemy is not dying or dead
        {
            this.moving_state += delta; // state time for moving animation
            this.idle_state += delta; // state time for idle animation

            // onHold mean the bat enemy is currently fly holding
            if (onHold)
            {
                this.onHoldTimer += delta; // time use to the control the bat enemy fly holding duration
            }
            else
            {
                this.holdTimer += delta; // timer use to control when should the enemy start fly holding
            }

            // Movement x and y
            float distance_x;
            float distance_y;

            if (turn) // turning and moving left
            {
                distance_x = -(this.moving_speed) * delta;
            }
            else // turning and moving right
            {
                distance_x = this.moving_speed * delta;
            }

            if (rise) // flying up
            {
                distance_y = (this.moving_speed / 2) * delta;
            }
            else // flying down
            {
                distance_y = -(this.moving_speed / 2) * delta;
            }

            // Checking for few situation before setup the Chase state
            // Checking the player are not in the hurt adn dying stage
            // Make sure that the player are in the certain range of the x and y of the enemy

            if ((super.getTargetPlayer().getState() != Player.PlayerState.HURT
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURTING
                    && super.getTargetPlayer().getState() != Player.PlayerState.HURT_END
                    && super.getTargetPlayer().getState() != Player.PlayerState.DEAD
                    && super.getTargetPlayer().getState() != Player.PlayerState.DYING)
                    &&(super.getTargetPlayer().getPosition().x < super.getStartPosition().x + (super.getPatrolRange() * 128))
                    && (super.getTargetPlayer().getPosition().x >= super.getStartPosition().x - (super.getPatrolRange() * 128))
                    && super.getTargetPlayer().getPosition().y >= super.getStartPosition().y - ((super.getPatrolRange()) * 128) - 100f)
            {
                this.gameAssetsDB.danger_zone_music.play();
                super.setState(EnemyState.CHASE);

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
                // Double speed than usual in y

                distance_y *= 2;

                // flying up the player is higher than the enemy
                // flying down if the player are lower than the enemy
                if (super.getPosition().y >= super.getTargetPlayer().getPosition().y + this.getTargetPlayer().getSprite().getHeight() - 60f) {
                    rise = false;
                } else if (super.getPosition().y < super.getTargetPlayer().getPosition().y + getTargetPlayer().getSprite().getHeight() / 2 + 60) {
                    rise = true;
                } else {
                    distance_y = 0;
                }

                // Double speed than usual in x
                distance_x *= 2;

                // when player is on the left the enemy will turn left
                // when player is on the right the enemy will turn right
                if ((super.getPosition().x) - (super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth() + 40f) >= 0) {
                    turn = true;

                } else if ((super.getPosition().x + batWidth / 2 + 40f) - (super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth()) < 0) {
                    turn = false;

                } else {
                    distance_x = 0;
                }

                // when the enemy are getting the ready attack position
                if (distance_x == 0 && distance_y == 0)
                {
                    this.attack_state += delta; // state time use to control the attack animation
                    super.setState(EnemyState.ATTACK); // set the enemy stage to attack
                    this.gameAssetsDB.bat_hit.play();

                    // Set the player to hurt after the enemy attack animation end
                    if (this.attack_state >= attackAnimation.getAnimationDuration())
                    {
                        this.gameAssetsDB.bat_hit.stop();
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        this.attack_state = 0.0f;
                    }
                }

            }
            else  // when the enemy are not in the chase stage
            {
                // when  the enemy are fly hold on the sky it should stop moving right or left
                if (onHold)
                {
                    distance_x = 0;
                }


                // getting the map coordinate of the enemy at the stating position
                int mapInitialX = (int) (Math.round(super.getStartPosition().x / environment.getTileWidth()));

                // getting the distance between the starting position and the future position of the enemy in map cordinate
                int currentPatrol = mapInitialX - (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                // make sure that the enemy are in the patrolling range
                if (Math.abs(currentPatrol) >= super.getPatrolRange())
                {
                    // if the enemy reach the patrolling range they will turn the head back to left or right
                    if (currentPatrol < 0) {
                        turn = true;
                    } else {
                        turn = false;
                    }
                }
                else if (super.getPosition().x + distance_x <= 0 + (batWidth / 2))
                {
                    // make sure that the bat don't fly off the screen
                    turn = false;
                }

                // change position of y
                // make sure the bat don't fly too high and go over the screen
                if (super.getPosition().y + distance_y >= (environment.getHeight() - 1) * 128) {
                    rise = false;
                }
            }

            //Checking collision on y

            //map reference for top and bottom
            int mapFutureY;
            int mapCurrentX;

            //tracking
            float tempX = 0;

            while (tempX < this.batWidth * scale) {

                // Work out the x map reference.
                mapCurrentX = (int) (Math.floor((super.getPosition().x + tempX) / environment.getTileWidth()));

                //bat top
                // Work out the map y for the top of this thing
                mapFutureY = (int) (Math.floor(((super.getPosition().y + 28) + distance_y + (this.batHeight * this.scale)) / environment.getTileHeight()));

                // Are we hitting something at the top
                if (distance_y > 0 && this.environment.getCell(mapCurrentX, mapFutureY - 1) != null) {
                    // We have a hit. Can't go up after all.
                    distance_y = 0;
                    rise = false;
                }

                // Work out the y map reference for the bottom
                mapFutureY = (int) (Math.floor(((super.getPosition().y + 35f) + distance_y) / environment.getTileHeight()));

                //bat below
                // Are we hitting a block from above?
                if (this.environment.getCell(mapCurrentX, mapFutureY - 1) != null)
                {
                    // We have hit. Can't go down after all
                    distance_y = 0;
                    rise = true;
                }

                tempX += 128;

            }

            // Checking for collision on X

            // What map reference for the left and right
            int mapCurrentY;
            int mapFutureX;

            float tempY = 0;

            // The height may go over 128 pixel. Need to check multiple 128
            // stop when the collision checking go over the height
            while (tempY < this.batHeight * this.scale)
            {

                // map reference of the left
                mapCurrentY = (int) (Math.floor((super.getPosition().y + tempY) / environment.getTileHeight()));
                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                // Are we hitting a block to the left?
                if (this.environment.getCell(mapFutureX - 1, mapCurrentY) != null) {
                    // We have a hit. Can't go left, and need to move up to the right of the block if not already there.
                    distance_x = 0;
                    turn = false;
                }

                // map reference of the right
                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x + (this.batWidth * this.scale)) / environment.getTileWidth()));

                // Are we hitting a block to the right?
                if (this.environment.getCell(mapFutureX - 2, mapCurrentY) != null) {
                    // We have a hit. Can't go right, and need to move to the left of the block if not already there.
                    distance_x = 0;
                    turn = true;
                }
                tempY += 128;
            }

            // Every 15 seconds the bat enemy will stating to fly hold
            if (this.holdTimer >= 15)
            {
                onHold = true;
                super.setState(EnemyState.IDLE);
                this.holdTimer = 0;
            }

            // The enemy will only fly hold for 1 seconds
            if (this.onHoldTimer >= 1) {
                onHoldTimer = 0;
                onHold = false;
                super.setState(EnemyState.MOVE);
            }

            // set the position of the enemy
            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }

        // Update the collider position to match the bat enemy's position
        this.collider.setPosition(new Vector2(super.getPosition().x - (batWidth / 2.0f), super.getPosition().y - (batHeight / 2.0f)));
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
