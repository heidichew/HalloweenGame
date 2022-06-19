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

    private float moving_state;

    private float moving_speed;

    //environment
    TiledMapTileLayer environment;

    private boolean[][] collisionMap = new boolean[260][170];

    private int batWidth;
    private int batHeight;

    // create collider

    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player   The player that the enemy instance can kill in the game world
     * @param start_xy The stating position to place the enemy instance
     */
    public BatEnemy(Player player, Vector2 start_xy, TiledMapTileLayer environment) {
        super(player, start_xy, start_xy, 50);

        this.environment = environment;

        //TODO Retrieve Target Tile
        //TiledMapTileLayer.Cell targetCell = tileLayer.getCell(characterX + moveX, characterY + moveY);

        //state
        this.moving_state = 0.0f;

        //moving speed
        this.moving_speed = 100f;

        //animation
        this.moveAnimation = new Animation(0.05f,this.texture_assets.bat_enemy_idle_texture);

        //loading texture from db

        this.batWidth = this.texture_assets.bat_enemy_idle_texture[0].getWidth() / 2;
        this.batHeight = this.texture_assets.bat_enemy_idle_texture[0].getHeight() / 2;

        for (int y = environment.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < environment.getWidth(); x ++) {
                if (environment.getCell(x,y) != null) {
                    for (int y1 = 0; y1 < 13; y1++) {
                        for (int x1 = 0; x1 < 13; x1++) {
                            if (x + x1 < 260 && y + y1 < 170) {
                                this.collisionMap[x + x1][y + y1] = true;
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture temp_texture = (Texture)this.moveAnimation.getKeyFrame(this.moving_state, true);
        batch.draw(temp_texture,
                super.getPosition().x - (temp_texture.getWidth() / 2.0f), super.getPosition().y - (temp_texture.getHeight() / 2.0f) ,
                temp_texture.getWidth() / 2.0f, temp_texture.getHeight()/2.0f,
                temp_texture.getWidth() - 80f, temp_texture.getHeight() - 100f,
                1,1,
                0, 0, 0, (int)temp_texture.getWidth(), (int) temp_texture.getHeight(), false, false);

    }

    public void update(float delta) {
        this.moving_state += delta;

        //TODO Determine Character Movement Distance
        float distance_x;
        float distance_y;

        distance_x = this.moving_speed * delta;
        distance_y = -(this.moving_speed / 2) * delta;

        // Find bottom-left corner tile
        int left = (int) Math.floor(Math.min(super.getPosition().x, super.getPosition().x + distance_x));
        int bottom = (int) Math.floor(Math.min(super.getPosition().y ,super.getPosition().y + distance_y));

        // Don't move off the screen
        if (super.getPosition().x + distance_x > environment.getWidth() * 10 || super.getPosition().x + distance_x < 0) {
            distance_x = 0;
        }
        if (super.getPosition().y + distance_y > environment.getHeight() * 10 || super.getPosition().y + distance_y < 0) {
            distance_y = 0;
        }

        int mapCurrentX = (int)(Math.round(super.getPosition().x / environment.getTileWidth()));
        int mapCurrentY = (int)(Math.round(super.getPosition().y / environment.getTileHeight()));

        int mapFutureX = (int)(Math.round((super.getPosition().x + distance_x)  / environment.getTileWidth()));
        int mapFutureY = (int)(Math.round((super.getPosition().y + distance_y)  / environment.getTileHeight()));

        if (this.collisionMap[mapFutureX][mapCurrentY]) { distance_x = 0;}

        if (distance_y != 0) {
            int yStep = this.batHeight / 10;
            for (int steps = 0; steps < this.batWidth / 10; steps++) {
                if (this.collisionMap[mapCurrentX + steps][mapFutureY] || this.collisionMap[mapCurrentX + steps][mapFutureY - yStep]) {
                    distance_y = 0;
                }
            }
        }

        if (distance_x != 0) {
            int xStep = this.batWidth / 10;
            for (int steps = 0; steps < this.batHeight / 10; steps++) {
                if (this.collisionMap[mapFutureX][mapCurrentY + steps] || this.collisionMap[mapFutureX + xStep][mapCurrentY + steps]) {
                    distance_x = 0;
                }
            }
        }

        super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));
    }

    @Override
    public void dispose() {

    }

}
