package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class GameAssetsDB
{
    //Singleton pattern whole game only have on DB

    private static GameAssetsDB assetsDB = null;
    // Texture variable declare
    // PS: Make it public so don't need to have a lot of getter and setter
    // The game will run much more quicker if we load all the texture when startup.

    protected TiledMap tiledMap_L1;
    protected Texture[] bat_enemy_idle_texture;

    private GameAssetsDB()
    {
        //this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map1.tmx");
        this.bat_enemy_idle_texture = new Texture[18];

        for(int i=0; i<18; i++)
        {
            this.bat_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Idle/skeleton-Idle_"+ i +".png"));
        }

        //texture initialise
    }

    public static GameAssetsDB getInstance()
    {
        if(assetsDB == null)
        {
            assetsDB = new GameAssetsDB();
        }

        return  assetsDB;
    }




}
