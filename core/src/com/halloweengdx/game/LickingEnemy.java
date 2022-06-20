package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class LickingEnemy extends Enemy {

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
    private float hurting;

    private float turnHeadTimer;

    private float moving_speed;

    //environment
    TiledMapTileLayer environment;

    private int lickingWidth;
    private int lickingHeight;

    // create collider
    private boolean turn_head = false;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player      The player that the enemy instance can kill in the game world
     * @param start_xy    The stating position to place the enemy instance
     * @param environment
     * @param patrol_range
     */
    public LickingEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int patrol_range) {
        super(player, start_xy, start_xy, 50, patrol_range);

        this.environment = environment;

        this.idle_state = 0;
        this.moving_state = 0;
        this.jumping_state = 0;
        this.die_state = 0;
        this.attack_state = 0;

        this.turnHeadTimer = 0.0f;

        this.moving_speed = 128f;

        //this.idleAnimation = new Animation(0.2f, texture_assets.skull_enemy_idle_texture);
        this.moveAnimation = new Animation(0.05f, texture_assets.licking_enemy_walking);
//        this.jumpingAnimation = new Animation(0.2f, texture_assets.skull_enemy_jumping_texture);
//        this.dieAnimation = new Animation(0.05f, texture_assets.skull_enemy_dead_texture);
//        this.attackAnimation = new Animation(0.1f, texture_assets.skull_enemy_attacking_texture);


        this.lickingWidth = texture_assets.licking_enemy_walking[0].getWidth();
        this.lickingHeight = texture_assets.licking_enemy_walking[0].getHeight();


        super.setState(EnemyState.MOVE);

    }


    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.moving_state, true);
        Texture jump_texture = (Texture) this.jumpingAnimation.getKeyFrame(this.jumping_state, true);
        Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
        Texture attacking_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
        Texture dead_texture = (Texture) this.dieAnimation.getKeyFrame(this.die_state, true);

        switch (super.getState()) {
            case MOVE:
                batch.draw(move_texture,
                        super.getPosition().x - (move_texture.getWidth() / 2.0f), super.getPosition().y - (move_texture.getHeight() / 2.0f),
                        move_texture.getWidth() / 2.0f, move_texture.getHeight() / 2.0f,
                        move_texture.getWidth(), move_texture.getHeight(),
                        1, 1,
                        0, 0, 0, (int) move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            case JUMP:
                batch.draw(jump_texture,
                        super.getPosition().x - (jump_texture.getWidth() / 2.0f), super.getPosition().y - (jump_texture.getHeight() / 2.0f) + 60f,
                        jump_texture.getWidth() / 2.0f, jump_texture.getHeight() / 2.0f,
                        jump_texture.getWidth(), jump_texture.getHeight(),
                        1, 1,
                        0, 0, 0, (int) jump_texture.getWidth(), (int) jump_texture.getHeight(), turn_head, false);

                break;

            case IDLE:
                batch.draw(idle_texture,
                        super.getPosition().x - (idle_texture.getWidth() / 2.0f), super.getPosition().y - (idle_texture.getHeight() / 2.0f) + 60f,
                        idle_texture.getWidth() / 2.0f, idle_texture.getHeight() / 2.0f,
                        idle_texture.getWidth(), idle_texture.getHeight(),
                        1, 1,
                        0, 0, 0, (int) idle_texture.getWidth(), (int) idle_texture.getHeight(), turn_head, false);

                break;

        }

    }

    @Override
    public void update(float delta) {
        this.moving_state += delta;
        this.jumping_state += delta;
        this.idle_state += delta;
        this.attack_state += delta;
        this.die_state += delta;

        this.turnHeadTimer += delta;

        boolean collision = false;

        float distance_x;
        float distance_y;

        if(turn_head)
        {
            distance_x = -(this.moving_speed) * delta;
        }
        else
        {
            distance_x = this.moving_speed * delta;
        }

        distance_y = (float) (-(this.moving_speed) * 2.5 * delta);

        int mapCurrentX = (int)(Math.round(super.getPosition().x / environment.getTileWidth())); // convert enemy location to tile row and coloumn
        int mapCurrentY = (int)(Math.round(super.getPosition().y / environment.getTileHeight()));

        int mapFutureX = (int)(Math.round((super.getPosition().x + distance_x)  / environment.getTileWidth()));
        int mapFutureY = (int)(Math.round((super.getPosition().y + distance_y)  / environment.getTileHeight()));

        if (distance_y != 0)
        {
            int yStep = (int) (Math.round((this.lickingHeight) / 128));

            Gdx.app.log("", yStep + "");

            // Adding the loop
            TiledMapTileLayer.Cell topCell = this.environment.getCell(mapCurrentX, mapFutureY); //top
            TiledMapTileLayer.Cell bottomCell = this.environment.getCell(mapCurrentX, (mapFutureY - yStep)); //bottom

            if (topCell != null || bottomCell != null)
            {
                super.setState(EnemyState.MOVE);
                distance_y = 0;
            }
            else
            {
                if(mapFutureY- yStep <= 4 )
                {
                    distance_y = 0;
                    super.setState(EnemyState.MOVE);
                    distance_x = 0;
                    super.setState(EnemyState.IDLE);
                }
                else
                {
                    super.setState(EnemyState.JUMP);
                }

                // need to change stage
            }
        }

        Gdx.app.log("",environment.getWidth()+"");

        if (distance_x != 0) {

            int xStep = (int) (Math.round(this.lickingWidth / 128)); //cause by the background

            TiledMapTileLayer.Cell leftCell = this.environment.getCell(mapFutureX, mapCurrentY); //left
            TiledMapTileLayer.Cell rightCell = this.environment.getCell(mapFutureX + xStep, mapCurrentY); //right

            if (leftCell != null || rightCell != null) {
                distance_x = 0;
                turn_head = !turn_head;
            }

        }

        if (super.getPosition().x + distance_x >= (this.environment.getWidth() *128) - (lickingWidth /2) ||
                super.getPosition().x + distance_x <= 0 + lickingWidth /2) // using graphic pixel
        {
            turn_head = !turn_head; // no side bar so this may work

        }
//        else
//        {
//            if(collision == false && this.turnHeadTimer >= 3.0 && super.getState() == EnemyState.MOVE)
//            {
//                this.turnHeadTimer = 0.0f;
//                if(new Random().nextInt(10)+1 >= 5)
//                {
//                    turn_head = !turn_head;
//                }
//            }
//
//        }
        super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
    }


    @Override
    public void dispose() {
        super.dispose();

    }
}
