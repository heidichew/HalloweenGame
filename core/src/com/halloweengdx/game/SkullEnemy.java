package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class SkullEnemy extends Enemy
{
    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    private Animation idleAnimation = null;
    private Animation moveAnimation = null;
    private Animation dyingAnimation = null;
    private Animation attackAnimation = null;
    private Animation jumpingAnimation = null;
    private Animation hurtingAnimation = null;

    private float idle_state;
    private float moving_state;
    private float jumping_state;
    private float dying_state;
    private float attack_state;
    private float hurting_state;

    private float turnHeadTimer;
    private float stayTimer;

    private float moving_speed;

    //environment
    private TiledMapTileLayer environment;

    private int skullWidth;
    private int skullHeight;

    // create collider
    private boolean turn_head;

    private float scale;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     * @param environment
     * @param patrol_range
     */
    public SkullEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int patrol_range, boolean boss)
    {
        super(player, start_xy, start_xy, 50, patrol_range);

        this.environment = environment;

        this.idle_state = 0;
        this.moving_state = 0;
        this.jumping_state = 0;
        this.dying_state = 0;
        this.attack_state = 0;
        this.hurting_state = 0;

        this.turnHeadTimer = 0.0f;

        this.moving_speed = 128f;

        this.turn_head = false;

        if(boss)
        {
            this.idleAnimation = new Animation(0.2f, texture_assets.skull_enemy_idle_texture);
            this.moveAnimation = new Animation(0.05f, texture_assets.skull_enemy_walking_texture);
            this.jumpingAnimation = new Animation(0.2f, texture_assets.skull_enemy_jumping_texture);
            this.dyingAnimation = new Animation(0.05f, texture_assets.skull_enemy_dead_texture);
            this.attackAnimation = new Animation(0.05f, texture_assets.skull_enemy_attacking_texture);


            this.skullWidth = texture_assets.skull_enemy_walking_texture[0].getWidth();
            this.skullHeight = texture_assets.skull_enemy_walking_texture[0].getHeight();

            this.scale = 1.2f;

        }
        else
        {

        }

        super.setState(EnemyState.MOVE);
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        switch (super.getState())
        {
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

            case JUMP:
                Texture jump_texture = (Texture) this.jumpingAnimation.getKeyFrame(this.jumping_state, true);
                batch.draw(jump_texture,
                        super.getPosition().x - (jump_texture.getWidth() / 2.0f), super.getPosition().y - (jump_texture.getHeight() / 2.0f),
                        jump_texture.getWidth() / 2.0f, jump_texture.getHeight() / 2.0f,
                        jump_texture.getWidth(),  jump_texture.getHeight(),
                        0.8f,0.8f,
                        0, 0, 0, (int)jump_texture.getWidth(), (int) jump_texture.getHeight(), turn_head, false);

                break;

            case IDLE:
                Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f),
                        0, 0,
                        idle_texture.getWidth(),  idle_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)idle_texture.getWidth(), (int) idle_texture.getHeight(), turn_head, false);

                break;

            case ATTACK:
                Texture attacking_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
                batch.draw(attacking_texture,
                        super.getPosition().x - (attacking_texture.getWidth() / 2.0f), super.getPosition().y - (attacking_texture.getHeight() / 2.0f),
                        0, 0,
                        attacking_texture.getWidth(),  attacking_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)attacking_texture.getWidth(), (int) attacking_texture.getHeight(), turn_head, false);

                break;

            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(this.dying_state, true);
                batch.draw(dying_texture,
                        super.getPosition().x - (dying_texture.getWidth() / 2.0f), super.getPosition().y - (dying_texture.getHeight() / 2.0f),
                        0, 0,
                        dying_texture.getWidth(),  dying_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);



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
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else
        {
            this.moving_state += delta;
            this.jumping_state += delta;
            this.idle_state += delta;

            this.turnHeadTimer += delta;
            this.stayTimer += delta;

            float distance_x = 0;
            float distance_y = 0;

            boolean getTurn = false;

            if (super.getPatrolRange() > 0) {
                if (super.getState() == EnemyState.IDLE) {
                    if (this.stayTimer >= 3) {
                        stayTimer = 0.0f;
                        super.setState(EnemyState.MOVE);
                    }
                }

                if (super.getState() == EnemyState.MOVE || super.getState() == EnemyState.CHASE)
                {

                    if (turn_head) {
                        distance_x = -(this.moving_speed) * delta;
                    } else {
                        distance_x = this.moving_speed * delta;
                    }

                }

            } else {
                super.setState(EnemyState.IDLE);
            }

            System.out.println(super.getTargetPlayer().getPosition().y);
            System.out.println(super.getStartPosition().y - skullHeight/2);


            if ((super.getTargetPlayer().getState() != Player.PlayerState.HURT && super.getTargetPlayer().getState() != Player.PlayerState.DEAD && super.getTargetPlayer().getState() != Player.PlayerState.DYING )
                    && (super.getTargetPlayer().getPosition().x <= super.getStartPosition().x + (super.getPatrolRange() * 128))
                    && (super.getTargetPlayer().getPosition().x > super.getStartPosition().x - (super.getPatrolRange() * 128))
                    && super.getTargetPlayer().getPosition().y >= super.getStartPosition().y - this.skullHeight/2
                    && super.getTargetPlayer().getPosition().y + super.getTargetPlayer().getSprite().getHeight() < super.getStartPosition().y + skullHeight) {
                super.setState(EnemyState.CHASE);
                this.texture_assets.danger_zone_music.play();
                this.texture_assets.l1_music.pause();

            } else {
                if (super.getState() == EnemyState.CHASE || super.getState() == EnemyState.ATTACK) {
                    super.setState(EnemyState.MOVE);
                    this.texture_assets.danger_zone_music.stop();
                    this.texture_assets.l1_music.play();
                }
            }

            if (super.getState() == EnemyState.CHASE) {

                distance_x *= 2;

                if (super.getPosition().x - super.getTargetPlayer().getPosition().x - ((super.getTargetPlayer().getSprite().getWidth() + 25f)) >= 0) {
                    turn_head = true;

                } else if (super.getPosition().x - super.getTargetPlayer().getPosition().x + this.getTargetPlayer().getSprite().getWidth()/2 < 0) {
                    turn_head = false;

                } else {
                    distance_x = 0;
                    this.attack_state += delta;
                    super.setState(EnemyState.ATTACK);

                    this.texture_assets.skull_hit.play();
                    if(this.attack_state >= this.attackAnimation.getAnimationDuration())
                    {
                        this.texture_assets.skull_hit.stop();
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        this.attack_state = 0.0f;
                    }

                }

            }

            int mapCurrentY = (int) (Math.round(super.getPosition().y / environment.getTileHeight()));
            int mapFutureX = (int) (Math.round((super.getPosition().x + distance_x) / environment.getTileWidth()));

            if (distance_x != 0) {
                int mapInitialX = (int) (Math.round(super.getStartPosition().x / environment.getTileHeight()));

                if (super.getState() == EnemyState.MOVE && (Math.abs(mapInitialX - mapFutureX + 1) >= super.getPatrolRange() ||
                        super.getPosition().x + distance_x <= 0 + skullWidth / 2))// using graphic pixel
                {
                    distance_x = 0;
                    if (new Random().nextInt(10) + 1 > 3) {
                        super.setState(EnemyState.IDLE);
                    } else {
                        turn_head = !turn_head; // no side bar so this may work
                        getTurn = true;
                    }
                } else {

                    int xStep = (int) (Math.round(this.skullWidth / 128)) - 3; //cause by the background

                    TiledMapTileLayer.Cell leftCell = this.environment.getCell(mapFutureX, mapCurrentY); //left
                    TiledMapTileLayer.Cell rightCell = this.environment.getCell(mapFutureX + xStep, mapCurrentY); //right

                    if (leftCell != null || rightCell != null) {
                        distance_x = 0;
                        turn_head = !turn_head;
                        getTurn = true;
                    }
                }

            }


            if (!getTurn && super.getPatrolRange() == 0 && turnHeadTimer >= 3) {
                turn_head = !turn_head;
                turnHeadTimer = 0.0f;

            }

            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
