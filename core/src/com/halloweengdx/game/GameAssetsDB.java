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




    // Player Texture
    protected Texture[] playerIdleTexture;
    protected Texture[] playerRunTexture;
    protected Texture[] playerJumpStartTexture;
    protected Texture[] playerJumpLoopTexture;
    protected Texture[] playerDieTexture;
    protected Texture[] playerHurtTexture;
    protected Texture[] playerAttackTexture;
    protected Texture[] playerFallTexture;

    private GameAssetsDB()
    {
        //this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map1.tmx");
        this.bat_enemy_idle_texture = new Texture[18];

        for(int i=0; i<18; i++)
        {
            this.bat_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Idle/skeleton-Idle_"+ i +".png"));
        }

        //texture initialise
        loadPlayerTexture();
    }

    private void loadPlayerTexture(){
        playerDieTexture = new Texture[15];
        playerRunTexture = new Texture[12];
        playerAttackTexture = new Texture[12];
        playerHurtTexture = new Texture[12];
        playerFallTexture = new Texture[6];
        playerJumpLoopTexture = new Texture[6];
        playerJumpStartTexture = new Texture[6];

        for(int i = 0; i < 15; i++)
        {
            playerDieTexture[i] = new Texture(Gdx.files.internal("player/dying/dying_"+ i +".png"));
        }

        for(int i = 0; i < 12; i++)
        {
            playerRunTexture[i] = new Texture(Gdx.files.internal("player/running/running_"+ i +".png"));
            playerAttackTexture[i] = new Texture(Gdx.files.internal("player/throwing/throwing_"+ i +".png"));
            playerHurtTexture[i] = new Texture(Gdx.files.internal("player/hurt/hurt_"+ i +".png"));
        }

        for(int i = 0; i < 6; i++)
        {
            playerFallTexture[i] = new Texture(Gdx.files.internal("player/falling/falling_"+ i +".png"));
            playerJumpStartTexture[i] = new Texture(Gdx.files.internal("player/jump_start/jump_"+ i +".png"));
            playerJumpLoopTexture[i] = new Texture(Gdx.files.internal("player/jump/jumping_"+ i +".png"));
        }
    }

    public static GameAssetsDB getInstance()
    {
        if(assetsDB == null)
        {
            assetsDB = new GameAssetsDB();
        }

        return  assetsDB;
    }

    // Dispose all texture
    public void dispose()
    {
        for(int i = 0; i < 15; i++)
        {
            playerDieTexture[i].dispose();
        }

        for(int i = 0; i < 12; i++)
        {
            playerRunTexture[i].dispose();
            playerAttackTexture[i].dispose();
            playerHurtTexture[i].dispose();
        }

        for(int i = 0; i < 6; i++)
        {
            playerFallTexture[i].dispose();
            playerJumpStartTexture[i].dispose();
            playerJumpLoopTexture[i].dispose();
        }
    }




}
