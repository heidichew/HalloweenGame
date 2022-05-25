package com.halloweengdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class BatEnemy extends Enemy{


    // Animations for bat enemy
    private Animation moveAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;

    //Tile map
    TiledMapTileLayer layer;



    // create collider

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     */
    public BatEnemy(Player player, Vector2 start_xy) {
        super(player, start_xy, start_xy, 50);
        GameAssetsDB texture_assets = GameAssetsDB.getInstance();
        this.layer = texture_assets.tiledMap_L1.getLayers();

        this.moveAnimation = new Animation(0.05f, texture_assets.bat_enemy_idle_texture);
        //loading texture from db
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.dra

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

}
