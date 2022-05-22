package com.halloweengdx.game;

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

    private GameAssetsDB()
    {
        this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map1.tmx");
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
