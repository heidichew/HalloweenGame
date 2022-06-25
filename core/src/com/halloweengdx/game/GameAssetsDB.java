package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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

    protected Texture L1_background;

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

    protected Texture[] enemy_dead_texture;

    // Player Texture
    protected Texture[] playerIdleTexture;
    protected Texture[] playerRunTexture;
    protected Texture[] playerJumpStartTexture;
    protected Texture[] playerJumpLoopTexture;
    protected Texture[] playerDieTexture;
    protected Texture[] playerHurtTexture;
    protected Texture[] playerAttackTexture;
    protected Texture[] playerFallTexture;

    //Music
    protected Music menu_music;
    protected Music l1_music;
    protected Music danger_zone_music;
    protected Music game_over;
    protected Music satire;

    protected Music player_hurt;
    protected Music player_attack;
    protected Music player_fall_down;

    protected Music skull_hit;
    protected Music bat_hit;
    protected Music licking_hit;
    protected Music enemy_dead;



    private GameAssetsDB()
    {
        this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map_level01.tmx");

        this.L1_background = new Texture(Gdx.files.internal("background/L1_background.png"));

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

        this.enemy_dead_texture = new Texture[10];

        playerDieTexture = new Texture[15];
        playerRunTexture = new Texture[12];
        playerAttackTexture = new Texture[12];
        playerHurtTexture = new Texture[12];
        playerFallTexture = new Texture[6];
        playerJumpLoopTexture = new Texture[6];
        playerJumpStartTexture = new Texture[6];

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
            playerDieTexture[i] = new Texture(Gdx.files.internal("player/dying/dying_"+ i +".png"));
            this.skull_enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Dying/Dying_0"+ i +".png"));
        }

        for(int i=0; i<12; i++)
        {
            playerRunTexture[i] = new Texture(Gdx.files.internal("player/running/running_"+ i +".png"));
            playerAttackTexture[i] = new Texture(Gdx.files.internal("player/throwing/throwing_"+ i +".png"));
            playerHurtTexture[i] = new Texture(Gdx.files.internal("player/hurt/hurt_"+ i +".png"));
            this.skull_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Idle Blink/Idle Blink_0"+ i +".png"));
            this.skull_enemy_hurt_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Hurt/Hurt_0"+ i +".png"));
            this.skull_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Attacking/Attacking_0"+ i +".png"));
        }

        for(int i =0; i<10; i++)
        {
            this.enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/Death Sprite/skeleton-animation_"+i+".png"));
        }

        for(int i = 0; i < 6; i++)
        {
            playerFallTexture[i] = new Texture(Gdx.files.internal("player/falling/falling_"+ i +".png"));
            playerJumpStartTexture[i] = new Texture(Gdx.files.internal("player/jump_start/jump_"+ i +".png"));
            playerJumpLoopTexture[i] = new Texture(Gdx.files.internal("player/jump/jumping_"+ i +".png"));
        }

        for(int i=0; i<5; i++)
        {
          this.skull_enemy_jumping_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Jump Loop/Jump Loop_00"+ i +".png"));

        }

        this.menu_music = Gdx.audio.newMusic(Gdx.files.internal("music/Ghost_Stories_by_Steve_Oxen.mp3"));
        menu_music.setLooping(true);

        this.l1_music = Gdx.audio.newMusic(Gdx.files.internal("music/Dancing_Skeletons_by_Steve_Oxen.mp3"));
        this.l1_music.setLooping(true);

        this.danger_zone_music = Gdx.audio.newMusic(Gdx.files.internal("music/Danger-Zone_SIPML_J.mp3"));
        this.danger_zone_music.setLooping(true);

        this.game_over = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-player-losing-or-failing.wav"));

        this.satire = Gdx.audio.newMusic(Gdx.files.internal("music/sf_devil_10.mp3"));

        this.player_hurt = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-player_hit.wav"));

        this.player_attack = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-player_attack.mp3"));

        this.player_fall_down = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_player_fall.mp3"));

        this.skull_hit = Gdx.audio.newMusic(Gdx.files.internal("music/sword_hit_zapsplat.mp3"));

        this.bat_hit = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_bat_attack.mp3"));

        this.licking_hit = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_body_hit.mp3"));

        this.enemy_dead = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-enemy_get_kill.wav"));

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
