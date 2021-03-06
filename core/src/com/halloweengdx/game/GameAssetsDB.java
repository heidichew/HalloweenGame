package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import org.w3c.dom.Text;

/**
 * Game Assets Database
 * Run with Singleton pattern
 */
public class GameAssetsDB
{
    /**
     * Singleton pattern whole game only have on DB
     */
    private static GameAssetsDB assetsDB = null;

    /**
     * Button
     */
    protected Texture menu_play;
    protected Texture menu_exit;

    protected Texture buttonSquareTexture;
    protected Texture buttonSquareDownTexture;
    protected Texture buttonLongTexture;
    protected Texture buttonLongDownTexture;
    protected Texture lifeTexture;
    protected Texture pauseTexture;

    /**
     * Screen
     */
    protected TiledMap tiledMap_L1;
    protected TiledMap tiledMap_L2;
    protected Texture L1_background;
    protected Texture L2_background;
    protected Texture menu_background;

    /**
     * Player Texture
     */
    protected Texture[] playerIdleTexture;
    protected Texture[] playerRunTexture;
    protected Texture[] playerJumpStartTexture;
    protected Texture[] playerJumpLoopTexture;
    protected Texture[] playerDieTexture;
    protected Texture[] playerHurtTexture;
    protected Texture[] playerAttackTexture;
    protected Texture[] playerFallTexture;

    /**
     * Player weapon texture
     */
    protected Texture weaponTexture;

    /**
     * Npc
     */
    protected Texture[] pumpkin_Idle_Texture;
    protected Texture[] pumpkin_Idle_Blink_Texture;

    protected Texture[] vampire_Idle_Texture;
    protected Texture[] vampire_Idle_Blink_Texture;

    /**
     * Enemies
     */
    protected Texture[] bat_enemy_idle_texture;
    protected Texture[] bat_enemy_flying_texture;
    protected Texture[] bat_enemy_attacking_texture;

    protected Texture[] skull_enemy_idle_texture;
    protected Texture[] skull_enemy_walking_texture;
    protected Texture[] skull_enemy_dead_texture;
    protected Texture[] skull_enemy_attacking_texture;

    protected Texture[] skull_boss_enemy_idle_texture;
    protected Texture[] skull_boss_enemy_walking_texture;
    protected Texture[] skull_boss_enemy_dead_texture;
    protected Texture[] skull_boss_enemy_attacking_texture;

    protected Texture[] licking_enemy_walking;
    protected Texture[] licking_enemy_jumping;

    protected Texture[] enemy_dead_texture;

    protected Texture[] necromancer_idle_texture;
    protected Texture[] necromancer_attack_ground_texture;
    protected Texture[] necromancer_attack_ground_texture_2;
    protected Texture[] necromancer_hurt_texture;
    protected Texture[] necromancer_dead_texture;

    protected Texture[] necromancer_child_move_texture;
    protected Texture[] necromancer_child_attack_texture;
    protected Texture[] necromancer_child_dying_texture;

    /**
     * Music
     */
    protected Music menu_music;
    protected Music l1_music;
    protected Music l2_music;
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

    protected Music give_heart;

    private GameAssetsDB()
    {
        //player
        this.playerDieTexture = new Texture[15];
        this.playerRunTexture = new Texture[12];
        this.playerAttackTexture = new Texture[12];
        this.playerHurtTexture = new Texture[12];
        this.playerFallTexture = new Texture[6];
        this.playerJumpLoopTexture = new Texture[6];
        this.playerJumpStartTexture = new Texture[6];

        this.weaponTexture = new Texture(Gdx.files.internal("player/sword.png"));

        //Npc
        this.pumpkin_Idle_Texture = new Texture[18];
        this.pumpkin_Idle_Blink_Texture = new Texture[18];

        this.vampire_Idle_Texture = new Texture[18];
        this.vampire_Idle_Blink_Texture = new Texture[18];

        // enemies
        this.bat_enemy_idle_texture = new Texture[18];
        this.bat_enemy_flying_texture = new Texture[18];
        this.bat_enemy_attacking_texture = new Texture[18];

        this.skull_enemy_idle_texture = new Texture[12];
        this.skull_enemy_walking_texture = new Texture[18];
        this.skull_enemy_dead_texture = new Texture[15];
        this.skull_enemy_attacking_texture = new Texture[12];

        this.skull_boss_enemy_idle_texture = new Texture[12];
        this.skull_boss_enemy_walking_texture = new Texture[18];
        this.skull_boss_enemy_dead_texture = new Texture[15];
        this.skull_boss_enemy_attacking_texture = new Texture[12];

        this.licking_enemy_walking = new Texture[18];
        this.licking_enemy_jumping = new Texture[18];

        this.enemy_dead_texture = new Texture[10];

        this.necromancer_idle_texture = new Texture[10];
        this.necromancer_attack_ground_texture = new Texture[10];
        this.necromancer_attack_ground_texture_2 = new Texture[10];
        this.necromancer_hurt_texture = new Texture[10];
        this.necromancer_dead_texture = new Texture[10];

        this.necromancer_child_move_texture = new Texture[10];
        this.necromancer_child_attack_texture = new Texture[10];
        this.necromancer_child_dying_texture = new Texture[10];

        // General UI and game level related textures
        this.buttonSquareTexture = new Texture("button/button_square_blue.png");
        this.buttonSquareDownTexture = new Texture("button/button_square_beige.png");
        this.buttonLongTexture = new Texture("button/button_long_blue.png");
        this.buttonLongDownTexture = new Texture("button/button_long_beige.png");

        this.lifeTexture = new Texture("ui/life.png");
        this.pauseTexture = new Texture(Gdx.files.internal("button/pause_button_round.png"));

        this.tiledMap_L1 = new TmxMapLoader().load("TileMap/tile_map_level01.tmx");
        this.tiledMap_L2 = new TmxMapLoader().load("TileMap/tile_map_level02.tmx");

        this.L1_background = new Texture(Gdx.files.internal("background/L1_background.png"));
        this.L2_background = new Texture(Gdx.files.internal("background/L2_background.png"));

        // Menu Screen
        this.menu_background = new Texture(Gdx.files.internal("background/menu_background_2.png"));
        this.menu_play = new Texture(Gdx.files.internal("button/play_button.png"));
        this.menu_exit =  new Texture(Gdx.files.internal("button/exit_button.png"));


        // Player, Enemy and NPC textures load
        for(int i=0; i<18; i++)
        {
            this.pumpkin_Idle_Texture[i] = new Texture(Gdx.files.internal("npc/Pumpkin Head Guy/Idle/Idle_0"+i+".png"));
            this.pumpkin_Idle_Blink_Texture[i] = new Texture(Gdx.files.internal("npc/Pumpkin Head Guy/Idle Blinking/Idle Blinking_0"+i+".png"));

            this.vampire_Idle_Texture[i] = new Texture(Gdx.files.internal("npc/Vampire/Idle/Idle_0"+i+".png"));
            this.vampire_Idle_Blink_Texture[i] = new Texture(Gdx.files.internal("npc/Vampire/Idle Blinking/Idle Blinking_0"+i+".png"));

            this.bat_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Idle/skeleton-Idle_"+ i +".png"));
            this.bat_enemy_flying_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Fly/skeleton-Fly_"+ i +".png"));
            this.bat_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/bat-monster/Attack/skeleton-Attack_"+ i +".png"));

            this.skull_enemy_walking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Walking/Walking_0"+ i +".png"));

            this.skull_boss_enemy_walking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 03/PNG Sequences/Walking/Walking_0"+ i +".png"));

            this.licking_enemy_walking[i] = new Texture(Gdx.files.internal("enemies/licking-monster/Walk/skeleton-Walk_"+i+".png"));
            this.licking_enemy_jumping[i] = new Texture(Gdx.files.internal("enemies/licking-monster/Jump/skeleton-Jump_"+i+".png"));
        }

        for(int i=0; i<15; i++)
        {
            this.playerDieTexture[i] = new Texture(Gdx.files.internal("player/dying/dying_"+ i +".png"));
            this.skull_enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Dying/Dying_0"+ i +".png"));

            this.skull_boss_enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 03/PNG Sequences/Dying/Dying_0"+ i +".png"));
        }

        for(int i=0; i<12; i++)
        {
            this.playerRunTexture[i] = new Texture(Gdx.files.internal("player/running/running_"+ i +".png"));
            this.playerAttackTexture[i] = new Texture(Gdx.files.internal("player/throwing/throwing_"+ i +".png"));
            this.playerHurtTexture[i] = new Texture(Gdx.files.internal("player/hurt/hurt_"+ i +".png"));

            this.skull_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Idle Blink/Idle Blink_0"+ i +".png"));
            this.skull_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 01/PNG Sequences/Attacking/Attacking_0"+ i +".png"));

            this.skull_boss_enemy_idle_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 03/PNG Sequences/Idle Blink/Idle Blink_0"+ i +".png"));
            this.skull_boss_enemy_attacking_texture[i] = new Texture(Gdx.files.internal("enemies/skull_monster/PNG/Skull 03/PNG Sequences/Attacking/Attacking_0"+ i +".png"));
        }

        for(int i =0; i<10; i++)
        {
            this.enemy_dead_texture[i] = new Texture(Gdx.files.internal("enemies/death_sprite/skeleton-animation_"+i+".png"));

            this.necromancer_idle_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer/Necromancer_03__IDLE_00"+i+".png"));
            this.necromancer_attack_ground_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer/Necromancer_03__ATTACK_02_00"+i+".png"));
            this.necromancer_attack_ground_texture_2[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer/Necromancer_03__ATTACK_01_00"+i+".png"));
            this.necromancer_hurt_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer/Necromancer_03__HURT_00"+i+".png"));
            this.necromancer_dead_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer/Necromancer_03__DIE_00"+i+".png"));

            this.necromancer_child_move_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer_child/Skeleton_03__RUN_00"+i+".png"));
            this.necromancer_child_attack_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer_child/Skeleton_03__ATTACK_00"+i+".png"));
            this.necromancer_child_dying_texture[i] = new Texture(Gdx.files.internal("enemies/boss_necromancer/necromancer_child/Skeleton_03__DIE_00"+i+".png"));
        }

        for(int i = 0; i < 6; i++)
        {
            this.playerFallTexture[i] = new Texture(Gdx.files.internal("player/falling/falling_"+ i +".png"));
            this.playerJumpStartTexture[i] = new Texture(Gdx.files.internal("player/jump_start/jump_"+ i +".png"));
            this.playerJumpLoopTexture[i] = new Texture(Gdx.files.internal("player/jump/jumping_"+ i +".png"));
        }

        // Music and sound effect
        this.menu_music = Gdx.audio.newMusic(Gdx.files.internal("music/Ghost_Stories_by_Steve_Oxen.mp3"));
        menu_music.setLooping(true);

        this.l1_music = Gdx.audio.newMusic(Gdx.files.internal("music/Dancing_Skeletons_by_Steve_Oxen.mp3"));
        this.l1_music.setLooping(true);

        this.l2_music = Gdx.audio.newMusic(Gdx.files.internal("music/Haunted_Carnival_by_Steve_Oxen.mp3"));
        this.l2_music.setVolume(0.8f);
        this.l2_music.setLooping(true);

        this.danger_zone_music = Gdx.audio.newMusic(Gdx.files.internal("music/Danger-Zone_SIPML_J.mp3"));
        this.danger_zone_music.setLooping(true);

        this.game_over = Gdx.audio.newMusic(Gdx.files.internal("music/Spook-AShamaluevMusic.mp3"));
        this.game_over.setLooping(true);

        this.satire = Gdx.audio.newMusic(Gdx.files.internal("music/sf_devil_laugh.mp3"));

        this.player_hurt = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-player_hit.wav"));

        this.player_attack = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-player_attack.mp3"));

        this.player_fall_down = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_player_fall.mp3"));

        this.skull_hit = Gdx.audio.newMusic(Gdx.files.internal("music/sword_hit_zapsplat.mp3"));

        this.bat_hit = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_bat_attack.mp3"));

        this.licking_hit = Gdx.audio.newMusic(Gdx.files.internal("music/zapsplat_body_hit.mp3"));

        this.enemy_dead = Gdx.audio.newMusic(Gdx.files.internal("music/mixkit-enemy_get_kill.wav"));

        this.give_heart = Gdx.audio.newMusic(Gdx.files.internal("music/give_heart.wav"));
    }

    /**
     * Get the GameAssets Database (Singleton)
     * @return GameAssets
     */
    public static GameAssetsDB getInstance()
    {
        if(assetsDB == null)
        {
            assetsDB = new GameAssetsDB();
        }

        return  assetsDB;
    }

    /**
     * Dispose all texture when game is exit
     */
    public void dispose()
    {
        //Button
        this.menu_play.dispose();
        this.menu_exit.dispose();

        this.buttonSquareTexture.dispose();
        this.buttonSquareDownTexture.dispose();
        this.buttonLongTexture.dispose();
        this.buttonLongDownTexture.dispose();
        this.lifeTexture.dispose();
        this.pauseTexture.dispose();

        // Screen
        this.tiledMap_L1.dispose();
        this.L1_background.dispose();
        this.menu_background.dispose();

        // Player weapon texture
        this.weaponTexture.dispose();

        //PLayer, enemies, npc texture dispose
        for(int i=0; i<18; i++)
        {
            this.pumpkin_Idle_Texture[i].dispose();
            this.pumpkin_Idle_Blink_Texture[i].dispose();

            this.vampire_Idle_Texture[i].dispose();
            this.vampire_Idle_Blink_Texture[i].dispose();

            this.bat_enemy_idle_texture[i].dispose();
            this.bat_enemy_flying_texture[i].dispose();
            this.bat_enemy_attacking_texture[i].dispose();

            this.skull_enemy_walking_texture[i].dispose();
            this.skull_boss_enemy_walking_texture[i].dispose();

            this.licking_enemy_walking[i].dispose();
            this.licking_enemy_jumping[i].dispose();
        }

        for(int i=0; i<15; i++)
        {
            this.playerDieTexture[i].dispose();
            this.skull_enemy_dead_texture[i].dispose();
            this.skull_boss_enemy_dead_texture[i].dispose();
        }

        for(int i=0; i<12; i++)
        {
            this.playerRunTexture[i].dispose();
            this.playerAttackTexture[i].dispose();
            this.playerHurtTexture[i].dispose();
            this.skull_enemy_idle_texture[i].dispose();
            this.skull_boss_enemy_idle_texture[i].dispose();
            this.skull_enemy_attacking_texture[i].dispose();
            this.skull_boss_enemy_attacking_texture[i].dispose();
        }

        for(int i =0; i<10; i++)
        {
            this.enemy_dead_texture[i].dispose();

            this.necromancer_idle_texture[i].dispose();
            this.necromancer_attack_ground_texture[i].dispose();
            this.necromancer_attack_ground_texture_2[i].dispose();
            this.necromancer_hurt_texture[i].dispose();
            this.necromancer_dead_texture[i].dispose();

            this.necromancer_child_move_texture[i].dispose();
            this.necromancer_child_attack_texture[i].dispose();
            this.necromancer_child_dying_texture[i].dispose();

        }

        for(int i = 0; i < 6; i++)
        {
            this.playerFallTexture[i].dispose();
            this.playerJumpStartTexture[i].dispose();
            this.playerJumpLoopTexture[i].dispose();
        }


        //Music
        this.menu_music.dispose();
        this.l1_music.dispose();
        this.l2_music.dispose();
        this.danger_zone_music.dispose();
        this.game_over.dispose();
        this.satire.dispose();

        this.player_hurt.dispose();
        this.player_attack.dispose();
        this.player_fall_down.dispose();

        this.skull_hit.dispose();
        this.bat_hit.dispose();
        this.licking_hit.dispose();
        this.enemy_dead.dispose();
    }




}
