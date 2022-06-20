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
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;

    private float moving_state;

    private float moving_speed;

    //environment
    TiledMapTileLayer environment;


    private int batWidth;
    private int batHeight;

    // create collider
    private boolean rise  = false;
    private boolean turn = false;


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

        //moving speed
        this.moving_speed = 128f;

        //animation
        this.moveAnimation = new Animation(0.05f,this.texture_assets.bat_enemy_flying_texture);

        //loading texture from db

        this.batWidth = this.texture_assets.bat_enemy_idle_texture[0].getWidth();
        this.batHeight = this.texture_assets.bat_enemy_idle_texture[0].getHeight();

        Gdx.app.log("",environment.getHeight()+"");
        Gdx.app.log("",environment.getWidth()+"");


    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture temp_texture = (Texture)this.moveAnimation.getKeyFrame(this.moving_state, true);

        batch.draw(temp_texture,
                super.getPosition().x - (temp_texture.getWidth() / 2.0f), super.getPosition().y - (temp_texture.getHeight() / 2.0f) + 22f ,
                temp_texture.getWidth() / 2.0f, temp_texture.getHeight()/2.0f,
                temp_texture.getWidth() + 40f, temp_texture.getHeight() - 100f,
                1,1,
                0, 0, 0, (int)temp_texture.getWidth(), (int) temp_texture.getHeight(), !turn, false);

    }

    public void update(float delta) {
        this.moving_state += delta;

        float distance_x;
        float distance_y;


        if(turn)
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

        // Change Position of x
        if (super.getPosition().x + distance_x >= this.environment.getTileWidth() * super.getPatrolRange())
        {
            turn = true;

        }
        else if(super.getPosition().x + distance_x <= 0 + batWidth /2)
        {
            turn = false;
        }

        //change position of y
        if(super.getPosition().y + distance_y >= (environment.getHeight() - 7) * 128)
        {
            rise = false;
        }

        int mapCurrentX = (int)(Math.round(super.getPosition().x / environment.getTileWidth())); // convert enemy location to tile row and coloumn
        int mapCurrentY = (int)(Math.round(super.getPosition().y / environment.getTileHeight()));

        int mapFutureX = (int)(Math.round((super.getPosition().x + distance_x)  / environment.getTileWidth()));
        int mapFutureY = (int)(Math.round((super.getPosition().y + distance_y)  / environment.getTileHeight()));


        if (distance_y != 0)
        {
            int yStep = (int)(Math.round((this.batHeight) / 128));

            // Adding the loop
            TiledMapTileLayer.Cell topCell = this.environment.getCell(mapCurrentX - 1, mapFutureY); //top
            TiledMapTileLayer.Cell bottomCell = this.environment.getCell(mapCurrentX - 1, (mapFutureY - yStep)); //bottom
            if(topCell != null || bottomCell != null)
            {
                distance_y = 0;
                rise = !rise;
            }
        }

        if (distance_x != 0) {
            int xStep = (int)(Math.round(this.batWidth / 128)) -3; //cause by the background

            TiledMapTileLayer.Cell leftCell = this.environment.getCell(mapFutureX, mapCurrentY - 1); //left
            TiledMapTileLayer.Cell rightCell = this.environment.getCell(mapFutureX + xStep, mapCurrentY - 1); //right

            if(leftCell != null || rightCell != null)
            {
                leftCell.setTile(null);
                distance_x = 0;
                turn = !turn;
            }
        }
        super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
    }

    @Override
    public void dispose() {

    }

}
