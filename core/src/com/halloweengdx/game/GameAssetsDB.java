package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import org.w3c.dom.Text;

public class GameAssetsDB
{
    //Singleton pattern whole game only have on DB

    private static GameAssetsDB assetsDB = null;
    // Texture variable declare
    // PS: Make it public so don't need to have a lot of getter and setter
    // The game will run much more quicker if we load all the texture when startup.

    protected TiledMap tiledMap_L1;

    protected Texture[] bat_enemy_idle_texture;
    protected Texture[] bat_enemy_flying_texture;
    protected Texture[] bat_enemy_attacking_texture;

    protected Texture[] skull_enemy_idle_texture;
    protected Texture[] skull_enemy_walking_texture;
    protected Texture[] skull_enemy_jumping_texture;
    protected Texture[] skull_enemy_dead_texture;
    protected Texture[] skull_enemy_hurt_texture;
    protected Texture[] skull_enemy_attacking_texture;

    protected Texture[] licking_enemy_walking;
    protected Texture[] licking_enemy_jumping;



    private GameAssetsDB()
    {
        this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map_level01.tmx");

        this.bat_enemy_idle_texture = new Texture[18];
        this.bat_enemy_flying_texture = new Texture[18];
        this.bat_enemy_attacking_texture = new Texture[18];

        this.skull_enemy_idle_texture = new Texture[12];
        this.skull_enemy_walking_texture = new Texture[18];
        this.skull_enemy_jumping_texture = new Texture[5];
        this.skull_enemy_hurt_texture = new Texture[12];
        this.skull_enemy_dead_texture = new Texture[15];
        this.skull_enemy_attacking_texture = new Texture[12];

        this.licking_enemy_walking = new Texture[18];
        this.licking_enemy_jumping = new Texture[18];

        for(int i=0; i<18; i++)
        {
            this.bat_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Idle/skeleton-Idle_"+ i +".png"));
            this.bat_enemy_flying_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Fly/skeleton-Fly_"+ i +".png"));
            this.bat_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Attack/skeleton-Attack_"+ i +".png"));

            this.skull_enemy_walking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Walking/Walking_0"+ i +".png"));

            this.licking_enemy_walking[i] = new Texture(Gdx.files.internal("enemies/licking-monster/Walk/skeleton-Walk_"+i+".png"));
            this.licking_enemy_jumping[i] = new Texture(Gdx.files.internal("enemies/licking-monster/Jump/skeleton-Jump_"+i+".png"));
        }

        for(int i=0; i<15; i++)
        {
            this.skull_enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Dying/Dying_0"+ i +".png"));
        }

        for(int i=0; i<12; i++)
        {
            this.skull_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Idle Blink/Idle Blink_0"+ i +".png"));
            this.skull_enemy_hurt_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Hurt/Hurt_0"+ i +".png"));
            this.skull_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Attacking/Attacking_0"+ i +".png"));
        }

        for(int i=0; i<5; i++)
        {
          this.skull_enemy_jumping_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Jump Loop/Jump Loop_00"+ i +".png"));

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
