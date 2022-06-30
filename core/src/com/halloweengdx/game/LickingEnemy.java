package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LickingEnemy extends Enemy {

    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

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
    private float hurting;


    private float moving_speed;

    //environment
    TiledMapTileLayer environment;

    private int lickingWidth;
    private int lickingHeight;

    // create collider
    private boolean turn_head = false;

    private float scale = 0.5f;

    private Rectangle collider;

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player      The player that the enemy instance can kill in the game world
     * @param start_xy    The stating position to place the enemy instance
     * @param environment
     * @param patrol_range
     */
    public LickingEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment,int score, int patrol_range) {
        super(player, start_xy, start_xy, score, patrol_range);

        this.environment = environment;

        this.idle_state = 0;
        this.moving_state = 0;
        this.jumping_state = 0;
        this.dying_state = 0;
        this.attack_state = 0;

        this.moving_speed = 350f;

        //this.idleAnimation = new Animation(0.2f, texture_assets.skull_enemy_idle_texture);
        this.moveAnimation = new Animation(0.03f, gameAssetsDB.licking_enemy_walking);
        this.jumpingAnimation = new Animation(0.2f, gameAssetsDB.licking_enemy_jumping);
        this.dyingAnimation = new Animation(0.05f, gameAssetsDB.enemy_dead_texture);
//        this.dieAnimation = new Animation(0.05f, texture_assets.skull_enemy_dead_texture);
//        this.attackAnimation = new Animation(0.1f, texture_assets.skull_enemy_attacking_texture);


        this.lickingWidth = (int)Math.round(gameAssetsDB.licking_enemy_walking[0].getWidth());
        this.lickingHeight = (int) Math.round(gameAssetsDB.licking_enemy_walking[0].getHeight());

        super.setState(EnemyState.MOVE);

        // Create collider
        collider = new Rectangle(super.getPosition().x, super.getPosition().y, this.lickingWidth * this.scale, this.lickingHeight * this.scale);



/*
        for (int yc = 0; yc < this.environment.getHeight(); yc++) {
            for (int xc = 0; xc < this.environment.getWidth(); xc++) {
                if (this.environment.getCell(xc, yc) == null)
                    System.out.print(".");
                else System.out.print("1");
            }
            System.out.println(" " + yc);
        }
*/

    }


    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.moving_state, true);
//        Texture idle_texture = (Texture) this.idleAnimation.getKeyFrame(this.idle_state, true);
//        Texture attacking_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
//        Texture dead_texture = (Texture) this.dieAnimation.getKeyFrame(this.die_state, true);

        switch (super.getState()) {
            case MOVE:
                batch.draw(move_texture,
                        super.getPosition().x, super.getPosition().y,
                        0,0,
                        move_texture.getWidth() , move_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            case JUMP:
                Texture jump_texture = (Texture) this.jumpingAnimation.getKeyFrame(this.jumping_state, true);
                batch.draw(jump_texture,
                        super.getPosition().x, super.getPosition().y,
                        0, 0,
                        jump_texture.getWidth(), jump_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) jump_texture.getWidth(), (int) jump_texture.getHeight(), turn_head, false);

                break;


            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(dying_state, true);
                batch.draw(dying_texture,
                        super.getPosition().x, super.getPosition().y,
                        0, 0,
                        dying_texture.getWidth(), dying_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);

                break;


        }

    }

    @Override
    public void update(float delta) {
        this.moving_state += delta;
        this.jumping_state += delta;
        this.idle_state += delta;
        this.attack_state += delta;


        float distance_x;
        float distance_y;

        if(super.getState() == EnemyState.DYING || super.getState() == EnemyState.DEAD)
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

            if ( (super.getTargetPlayer().getPosition().y >= super.getPosition().y -128f)
                    &&(super.getTargetPlayer().getPosition().y < super.getPosition().y + (lickingHeight * scale))) {
                if (super.getPosition().x + (lickingWidth * scale) - 160f > super.getTargetPlayer().getPosition().x &&
                        super.getPosition().x + (lickingWidth * scale) <= super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth()) {
                    //System.out.println(this.getTargetPlayer().getState());
                    //System.out.println("Player Y" + super.getTargetPlayer().getPosition().y);
                    //System.out.println("enemy y" + (super.getPosition().y + lickingHeight / 2));
                    // need to change the other way round
                    if ((super.getTargetPlayer().getState() == Player.PlayerState.FALLING)
                            && (super.getTargetPlayer().getPosition().y >= super.getPosition().y + (lickingHeight * scale) - 100f))
                    {
                        super.setState(EnemyState.DYING);
                        this.gameAssetsDB.enemy_dead.play();

                    }
                    else
                    {
                        super.setScore(0);
                        this.gameAssetsDB.licking_hit.play();
                        super.setState(EnemyState.DYING);
                        super.getTargetPlayer().setState(Player.PlayerState.HURT);
                    }
                }
            }

        }


        if(super.getState() != EnemyState.DYING && super.getState()!= EnemyState.DEAD)
        {

            if (turn_head) {
                distance_x = this.moving_speed * delta;
            } else {
                distance_x = -(this.moving_speed) * delta;
            }

            distance_y = 0;

    /*
            for (int yc = 0; yc < this.environment.getHeight(); yc++) {
                for (int xc = 0; xc < this.environment.getWidth(); xc++) {
                    if ((int)(this.getPosition().x / 128) == xc && (int)(this.getPosition().y / 128) == yc)
                        System.out.print("*");
                    else if (this.environment.getCell(xc, yc) == null)
                        System.out.print(".");
                    else
                        System.out.print("1");
                }
                System.out.println(" " + yc);
            }
     */


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

                if(super.getPosition().y < this.environment.getTileHeight()*2)
                {
                    super.setScore(0);
                    super.setState(EnemyState.DYING);
                }
                else
                {
                    super.setState(EnemyState.MOVE);
                }

            }

            Gdx.app.log("", environment.getWidth() + "");

            // Checking for collision on X

            // What map reference are we going to be at after this frame?
            int mapCurrentY;
            int mapFutureX;

            float tempY = 0;

            while (tempY < this.lickingHeight * this.scale) {

                mapCurrentY = (int) (Math.floor((super.getPosition().y + tempY) / environment.getTileHeight()));

                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x) / environment.getTileWidth()));

                // Are we hitting a block to the left?
                if (this.environment.getCell(mapFutureX, mapCurrentY) != null) {
                    // We have a hit. Can't go left, and need to move up to the right of the block if not already there.
                    distance_x = 0;
                    super.setPosition(new Vector2((mapFutureX + 1) * 128, super.getPosition().y));
                    turn_head = true;
                }

                mapFutureX = (int) (Math.floor((super.getPosition().x + distance_x + (this.lickingWidth * this.scale)) / environment.getTileWidth()));

                // Are we hitting a block to the right?
                if (this.environment.getCell(mapFutureX, mapCurrentY) != null) {
                    // We have a hit. Can't go right, and need to move to the left of the block if not already there.
                    distance_x = 0;
                    super.setPosition(new Vector2(mapFutureX * 128 - (this.lickingWidth * this.scale) - 5, super.getPosition().y));
                    turn_head = false;
                }
                tempY += 128;
            }

            if (super.getPosition().x + distance_x >= (this.environment.getWidth() * 128) - (lickingWidth / 2))
            {
                turn_head = false; // no side bar so this may work

            }
            else if(super.getPosition().x + distance_x <= (0 + (lickingWidth / 2) - 120f)) // using graphic pixel)
            {
                turn_head = true;
            }

            super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
        }

        collider.setPosition(super.getPosition());
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