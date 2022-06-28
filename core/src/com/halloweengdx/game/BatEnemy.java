package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class BatEnemy extends Enemy{

    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    // Animations for bat enemy
    private Animation moveAnimation;
    private Animation idleAnimation;
    private Animation dyingAnimation;
    private Animation attackAnimation;

    private float moving_state;
    private float idle_state;
    private float attack_state;
    private float dying_state;

    private float moving_speed;

    private float holdTimer;
    private float onHoldTimer;
    private boolean onHold = false;

    //environment
    private TiledMapTileLayer environment;


    private int batWidth;
    private int batHeight;

    // create collider
    private boolean rise  = false;
    private boolean turn = false;

    private float scale = 0.7f;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     */
    public BatEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int score, int patrol_range) {
        super(player, start_xy, start_xy, score, patrol_range);

        this.environment = environment;

        //state
        this.moving_state = 0.0f;
        this.idle_state = 0.0f;
        this.attack_state = 0.0f;

        //moving speed
        this.moving_speed = 128f;

        //animation
        this.moveAnimation = new Animation(0.05f,this.gameAssetsDB.bat_enemy_flying_texture);
        this.idleAnimation = new Animation(0.1f, this.gameAssetsDB.bat_enemy_idle_texture);
        this.attackAnimation = new Animation(0.03f, this.gameAssetsDB.bat_enemy_attacking_texture);
        this.dyingAnimation = new Animation(0.05f, this.gameAssetsDB.enemy_dead_texture);

        //loading texture from db

        this.batWidth = this.gameAssetsDB.bat_enemy_idle_texture[0].getWidth();
        this.batHeight = this.gameAssetsDB.bat_enemy_idle_texture[0].getHeight();

        super.setState(EnemyState.MOVE);

    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        switch (super.getState()){
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

            case IDLE:

                Texture idle_texture = (Texture)this.idleAnimation.getKeyFrame(this.idle_state, true);

                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f),
                        0, 0,
                        idle_texture.getWidth(), idle_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)idle_texture.getWidth(), (int) idle_texture.getHeight(), !turn, false);
                break;

            case ATTACK:

                Texture attack_texture = (Texture)this.attackAnimation.getKeyFrame(this.attack_state, true);

                batch.draw(attack_texture,
                        super.getPosition().x - (attack_texture.getWidth() / 2.0f), 10+ super.getPosition().y - (attack_texture.getHeight() / 2.0f),
                        0, 0,
                        attack_texture.getWidth(), attack_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)attack_texture.getWidth(), (int) attack_texture.getHeight(), !turn, false);
                break;

            case DYING:
                Texture dying_texture = (Texture)this.dyingAnimation.getKeyFrame(this.dying_state, true);

                batch.draw(dying_texture,
                        super.getPosition().x - (dying_texture.getWidth() / 2.0f), super.getPosition().y - (dying_texture.getHeight() / 2.0f),
                        0, 0,
                        dying_texture.getWidth(), dying_texture.getHeight(),
                        scale,scale,
                        0, 0, 0, (int)dying_texture.getWidth(), (int) dying_texture.getHeight(), !turn, false);
                break;

        }


    }

    public void update(float delta) {

        if(this.getState() == EnemyState.DYING || this.getState() == EnemyState.DEAD)
        {
            this.dying_state += delta;
            if(this.dying_state >= this.dyingAnimation.getAnimationDuration())
            {
                this.gameAssetsDB.enemy_dead.play();
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else
        {
            this.moving_state += delta;
            this.idle_state += delta;

            if (onHold) {
                this.onHoldTimer += delta;
            } else {
                this.holdTimer += delta;
            }


            float distance_x;
            float distance_y;

            if (turn) {
                distance_x = -(this.moving_speed) * delta;
            } else {
                distance_x = this.moving_speed * delta;
            }

            if (rise) {
                distance_y = (this.moving_speed / 2) * delta;
            } else {
                distance_y = -(this.moving_speed / 2) * delta;
            }

            if ((super.getTargetPlayer().getState() != Player.PlayerState.HURT && super.getTargetPlayer().getState() != Player.PlayerState.DEAD && super.getTargetPlayer().getState() != Player.PlayerState.DYING)
                    &&(super.getTargetPlayer().getPosition().x < super.getStartPosition().x + (super.getPatrolRange() * 128))
                    && (super.getTargetPlayer().getPosition().x >= super.getStartPosition().x - (super.getPatrolRange() * 128))
                    && super.getTargetPlayer().getPosition().y >= super.getStartPosition().y - ((super.getPatrolRange()) * 128))
            {
                this.gameAssetsDB.danger_zone_music.play();
                super.setState(EnemyState.CHASE);

            } else {
                if (super.getState() == EnemyState.CHASE || super.getState() == EnemyState.ATTACK) {
                    super.setState(EnemyState.MOVE);
                    this.gameAssetsDB.danger_zone_music.stop();
                }
            }

            if (super.getState() == EnemyState.CHASE) {
                distance_y *= 2;


                if (super.getPosition().y >= super.getTargetPlayer().getPosition().y + this.getTargetPlayer().getSprite().getHeight() - 60f) {
                    rise = false;
                } else if (super.getPosition().y < super.getTargetPlayer().getPosition().y + getTargetPlayer().getSprite().getHeight() / 2 + 60) {
                    rise = true;
                } else {
                    distance_y = 0;
                }

                distance_x *= 2;

                if ((super.getPosition().x) - (super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth() + 40f) >= 0) {
                    turn = true;

                } else if ((super.getPosition().x + batWidth / 2 + 40f) - (super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth()) < 0) {
                    turn = false;

                } else {
                    distance_x = 0;
                }

                if (distance_x == 0 && distance_y == 0)
                {
                    this.attack_state += delta;
                    super.setState(EnemyState.ATTACK);
                    this.gameAssetsDB.bat_hit.play();

                    if (this.attack_state >= attackAnimation.getAnimationDuration())
                    {
                        this.gameAssetsDB.bat_hit.stop();
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        this.attack_state = 0.0f;
                    }
                }

            } else {
                if (onHold) {
                    distance_x = 0;
                }


                int mapInitialX = (int) (Math.round(super.getStartPosition().x / environment.getTileWidth()));
                int currentPatrol = mapInitialX - (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                if (Math.abs(currentPatrol) >= super.getPatrolRange()) {
                    if (currentPatrol < 0) {
                        turn = true;
                    } else {
                        turn = false;
                    }

                } else if (super.getPosition().x + distance_x <= 0 + (batWidth / 2)) {
                    turn = false;
                }

                //change position of y
                if (super.getPosition().y + distance_y >= (environment.getHeight() - 1) * 128) {
                    rise = false;
                }
            }


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

                // Are we hitting a block from below? Not that this can't be tested until jumping is added
                // so it might need a change.
                if (distance_y > 0 && this.environment.getCell(mapCurrentX, mapFutureY - 1) != null) {
                    // We have a hit. Can't go up after all.
                    distance_y = 0;
                    rise = false;
                }

                // Work out the y map reference for the bottom
                mapFutureY = (int) (Math.floor(((super.getPosition().y + 35f) + distance_y) / environment.getTileHeight()));

                //bat below
                // Are we hitting a block from above?
                if (this.environment.getCell(mapCurrentX, mapFutureY - 1) != null) {
                    distance_y = 0;
                    rise = true;
                }

                tempX += 128;

            }

            // Checking for collision on X

            // What map reference are we going to be at after this frame?
            int mapCurrentY;
            int mapFutureX;

            float tempY = 0;

            while (tempY < this.batHeight * this.scale) {

                mapCurrentY = (int) (Math.floor((super.getPosition().y + tempY) / environment.getTileHeight()));

                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                // Are we hitting a block to the left?
                if (this.environment.getCell(mapFutureX - 1, mapCurrentY) != null) {
                    // We have a hit. Can't go left, and need to move up to the right of the block if not already there.
                    distance_x = 0;
                    turn = false;
                }

                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x + (this.batWidth * this.scale)) / environment.getTileWidth()));

                // Are we hitting a block to the right?
                if (this.environment.getCell(mapFutureX - 2, mapCurrentY) != null) {
                    // We have a hit. Can't go right, and need to move to the left of the block if not already there.
                    distance_x = 0;
                    turn = true;
                }
                tempY += 128;
            }

            if (this.holdTimer >= 15) {
                onHold = true;
                super.setState(EnemyState.IDLE);
                this.holdTimer = 0;
            }

            if (this.onHoldTimer >= 1) {
                onHoldTimer = 0;
                onHold = false;
                super.setState(EnemyState.MOVE);
            }

            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

}
