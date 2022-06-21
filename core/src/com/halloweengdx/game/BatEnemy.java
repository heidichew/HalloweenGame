package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import org.w3c.dom.Text;

import java.util.Random;

public class BatEnemy extends Enemy{

    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    // Animations for bat enemy
    private Animation moveAnimation = null;
    private Animation idleAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;

    private float moving_state;
    private float idle_state;

    private float moving_speed;

    private float holdTimer;
    private float onHoldTimer;
    private boolean onHold = false;

    //environment
    TiledMapTileLayer environment;


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
    public BatEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment, int patrol_range) {
        super(player, start_xy, start_xy, 50, patrol_range);

        this.environment = environment;

        //state
        this.moving_state = 0.0f;
        this.idle_state = 0.0f;

        //moving speed
        this.moving_speed = 128f;

        //animation
        this.moveAnimation = new Animation(0.05f,this.texture_assets.bat_enemy_flying_texture);
        this.idleAnimation = new Animation(0.1f, this.texture_assets.bat_enemy_idle_texture);

        //loading texture from db

        this.batWidth = this.texture_assets.bat_enemy_idle_texture[0].getWidth();
        this.batHeight = this.texture_assets.bat_enemy_idle_texture[0].getHeight();

        super.setState(EnemyState.MOVE);

    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        switch (super.getState()){
            case MOVE:
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
        }


    }

    public void update(float delta) {

        this.moving_state += delta;
        this.idle_state += delta;


        if(onHold)
        {
            this.onHoldTimer += delta;
        }
        else
        {
            this.holdTimer += delta;
        }

        float distance_x;
        float distance_y;


/*        for (int yc = 0; yc < this.environment.getHeight(); yc++) {
            for (int xc = 0; xc < this.environment.getWidth(); xc++) {
                if ((int)(this.getPosition().x / 128) == xc && (int)(this.getPosition().y / 128) == yc)
                    System.out.print("*");
                else if (this.environment.getCell(xc, yc) == null)
                    System.out.print(".");
                else
                    System.out.print("1");
            }
            System.out.println(" " + yc);
        }*/

        if(onHold)
        {
            distance_x = 0;
        }
        else if(turn)
        {
            distance_x = -(this.moving_speed) * delta;
        }
        else
        {
            distance_x = this.moving_speed * delta;
        }

        if(rise)
        {
            distance_y = (this.moving_speed / 2) * delta;
        }
        else
        {
            distance_y = -(this.moving_speed / 2) * delta;
        }

        // Change Position of x don't go over screen
        if (super.getPosition().x + distance_x >= (this.environment.getTileWidth() * super.getPatrolRange()))
        {
            turn = true;

        }
        else if(super.getPosition().x + distance_x <= 0 +  ((batWidth /2) * 0.7))
        {
            turn = false;
        }

        //change position of y
        if(super.getPosition().y + distance_y >= (environment.getHeight() - 7) * 128)
        {
            rise = false;
        }


        //map reference for top and bottom
        int mapFutureY;
        int mapCurrentX;


        //tracking
        float tempX = 0;

        while(tempX < this.batWidth * scale)
        {
            // Work out the x map reference.
            mapCurrentX = (int) (Math.floor((super.getPosition().x + tempX) / environment.getTileWidth()));

            //bat top
            // Work out the map y for the top of this thing
            mapFutureY = (int)(Math.floor(((super.getPosition().y + 28) + distance_y + (this.batHeight * this.scale)) / environment.getTileHeight()));

            // Are we hitting a block from below? Not that this can't be tested until jumping is added
            // so it might need a change.
            if (distance_y > 0 && this.environment.getCell(mapCurrentX, mapFutureY -1) != null) {
                // We have a hit. Can't go up after all.
                System.out.println(distance_y);
                distance_y = 0;
                rise = false;
            }

            // Work out the y map reference for the bottom
            mapFutureY = (int) (Math.floor(((super.getPosition().y + 35f) + distance_y)  / environment.getTileHeight()));

            //bat below
            // Are we hitting a block from above?
            if (this.environment.getCell(mapCurrentX, mapFutureY -1) != null) {
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

            mapCurrentY = (int)(Math.floor((super.getPosition().y + tempY) / environment.getTileHeight()));

            mapFutureX = (int)(Math.floor((super.getPosition().x + distance_x)  / environment.getTileWidth()));

            // Are we hitting a block to the left?
            if (this.environment.getCell(mapFutureX - 1, mapCurrentY) != null) {
                // We have a hit. Can't go left, and need to move up to the right of the block if not already there.
                distance_x = 0;
                turn= !turn;
            }

            mapFutureX = (int)(Math.floor((super.getPosition().x + distance_x + (this.batWidth * this.scale)) / environment.getTileWidth()));

            // Are we hitting a block to the right?
            if (this.environment.getCell(mapFutureX - 2, mapCurrentY) != null) {
                // We have a hit. Can't go right, and need to move to the left of the block if not already there.
                distance_x = 0;
                turn = !turn;
            }
            tempY += 128;
        }

        if(this.holdTimer >= 15)
        {
            onHold = true;
            super.setState(EnemyState.IDLE);
            this.holdTimer = 0;
        }

        if(this.onHoldTimer >= 1)
        {
            onHoldTimer = 0;
            onHold = false;
            super.setState(EnemyState.MOVE);
        }
        super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
    }

    @Override
    public void dispose() {

    }

}
