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
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;
    private Animation jumpingAnimation = null;
    private Animation hurtingAnimation = null;

    private float idle_state;
    private float moving_state;
    private float jumping_state;
    private float die_state;
    private float attack_state;
    private float hurting_state;

    private float turnHeadTimer;
    private float stayTimer;

    private float moving_speed;

    //environment
    TiledMapTileLayer environment;

    private int skullWidth;
    private int skullHeight;

    // create collider
    private boolean turn_head = false;

    private float scale = 1f;



    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     * @param environment
     * @param patrol_range
     */
    public SkullEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int patrol_range)
    {
        super(player, start_xy, start_xy, 50, patrol_range);

        this.environment = environment;

        this.idle_state = 0;
        this.moving_state = 0;
        this.jumping_state = 0;
        this.die_state = 0;
        this.attack_state = 0;
        this.hurting_state = 0;

        this.turnHeadTimer = 0.0f;

        this.moving_speed = 128f;

        this.idleAnimation = new Animation(0.2f, texture_assets.skull_enemy_idle_texture);
        this.moveAnimation = new Animation(0.05f, texture_assets.skull_enemy_walking_texture);
        this.jumpingAnimation = new Animation(0.2f, texture_assets.skull_enemy_jumping_texture);
        this.dieAnimation = new Animation(0.05f, texture_assets.skull_enemy_dead_texture);
        this.attackAnimation = new Animation(0.1f, texture_assets.skull_enemy_attacking_texture);


        this.skullWidth = texture_assets.skull_enemy_walking_texture[0].getWidth();
        this.skullHeight = texture_assets.skull_enemy_walking_texture[0].getHeight();

        super.setState(EnemyState.MOVE);
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        Texture move_texture = (Texture)this.moveAnimation.getKeyFrame(this.moving_state, true);
        Texture jump_texture = (Texture) this.jumpingAnimation.getKeyFrame(this.jumping_state, true);
        Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
        Texture attacking_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
        Texture dead_texture = (Texture) this.dieAnimation.getKeyFrame(this.die_state, true);

        switch (super.getState())
        {
            case MOVE:
                batch.draw(move_texture,
                        super.getPosition().x - (move_texture.getWidth() / 2.0f), super.getPosition().y - (move_texture.getHeight() / 2.0f),
                        0, 0,
                        move_texture.getWidth(),  move_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            case JUMP:
                batch.draw(jump_texture,
                        super.getPosition().x - (jump_texture.getWidth() / 2.0f), super.getPosition().y - (jump_texture.getHeight() / 2.0f),
                        jump_texture.getWidth() / 2.0f, jump_texture.getHeight() / 2.0f,
                        jump_texture.getWidth(),  jump_texture.getHeight(),
                        0.8f,0.8f,
                        0, 0, 0, (int)jump_texture.getWidth(), (int) jump_texture.getHeight(), turn_head, false);

                break;

            case IDLE:
                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f),
                        0, 0,
                        idle_texture.getWidth(),  idle_texture.getHeight(),
                        this.scale,this.scale,
                        0, 0, 0, (int)idle_texture.getWidth(), (int) idle_texture.getHeight(), turn_head, false);

                break;

        }

    }

    @Override
    public void update(float delta)
    {
        this.moving_state += delta;
        this.jumping_state += delta;
        this.idle_state += delta;
        this.attack_state += delta;
        this.die_state += delta;

        this.turnHeadTimer +=delta;
        this.stayTimer+=delta;

        float distance_x = 0;
        float distance_y = 0;

        boolean getTurn = false;

        if(super.getPatrolRange() > 0)
        {
            if(super.getState() == EnemyState.IDLE)
            {
                if(this.stayTimer >= 3)
                {
                    stayTimer = 0.0f;
                    super.setState(EnemyState.MOVE);
                }
            }

            if(super.getState()==EnemyState.MOVE)
            {

                if(turn_head)
                {
                    distance_x = -(this.moving_speed) * delta;
                }
                else
                {
                    distance_x = this.moving_speed * delta;
                }

            }

        }
        else
        {
            super.setState(EnemyState.IDLE);
        }

        int mapCurrentY = (int)(Math.round(super.getPosition().y / environment.getTileHeight()));
        int mapFutureX = (int)(Math.round((super.getPosition().x + distance_x)  / environment.getTileWidth()));

        if (distance_x != 0)
        {
            int mapInitialX = (int)(Math.round(super.getStartPosition().x / environment.getTileHeight()));

            if (Math.abs(mapInitialX - mapFutureX) >= super.getPatrolRange() ||
                    super.getPosition().x + distance_x <= 0 + skullWidth /2) // using graphic pixel
            {
                distance_x = 0;
                if(new Random().nextInt(10)+1 > 3)
                {
                    super.setState(EnemyState.IDLE);
                }
                else
                {
                    turn_head = !turn_head; // no side bar so this may work
                    getTurn = true;
                }
            }
            else {

                int xStep = (int) (Math.round(this.skullWidth / 128)) - 3; //cause by the background

                TiledMapTileLayer.Cell leftCell = this.environment.getCell(mapFutureX, mapCurrentY); //left
                TiledMapTileLayer.Cell rightCell = this.environment.getCell(mapFutureX + xStep, mapCurrentY); //right

                if (leftCell != null || rightCell != null)
                {
                    distance_x = 0;
                    turn_head = !turn_head;
                    getTurn = true;
                }
            }

        }


        if(!getTurn && super.getPatrolRange() == 0 && turnHeadTimer >= 3)
        {
            turn_head = !turn_head;
            turnHeadTimer = 0.0f;

        }

        super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
