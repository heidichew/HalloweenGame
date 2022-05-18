package com.halloweengdx.game;

public class GameAssetsDB
{
    //Singleton pattern whole game only have on DB

    private static GameAssetsDB assetsDB = null;
    //Texture variable declare
    // PS:make it public so don't need to have a lot of getter and setter
    // The game will run much more quicker if we load all the texture when startup.

    private GameAssetsDB()
    {
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
