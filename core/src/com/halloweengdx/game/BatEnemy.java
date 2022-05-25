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

public class BatEnemy extends Enemy{

    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    // Animations for bat enemy
    private Animation moveAnimation = null;
    private Animation dieAnimation = null;
    private Animation attackAnimation = null;




    // create collider

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     */
    public BatEnemy(Player player, Vector2 start_xy) {
        super(player, start_xy, start_xy, 50);

        //TODO Retrieve Target Tile
        //TiledMapTileLayer.Cell targetCell = tileLayer.getCell(characterX + moveX, characterY + moveY);

        this.moveAnimation = new Animation(0.05f,this.texture_assets.bat_enemy_idle_texture);
        //loading texture from db



    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture temp_texture = this.texture_assets.bat_enemy_idle_texture[0];
        batch.draw(temp_texture,
                super.getPosition().x - (temp_texture.getWidth() / 2.0f), super.getPosition().y - (temp_texture.getHeight() / 2.0f) ,
                temp_texture.getWidth() / 2.0f, temp_texture.getHeight()/2.0f,
                temp_texture.getWidth() - 40f, temp_texture.getHeight() - 100f,
                1,1,
                0, 0, 0, (int)temp_texture.getWidth(), (int) temp_texture.getHeight(), false, false);

    }

    public void update(float delta) {

    }

    @Override
    public void dispose() {

    }

}
