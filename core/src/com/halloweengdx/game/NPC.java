package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class NPC implements Actor
{
    public enum NPC_TYPE
    {
        Vampire,
        Pumpkin
    }

    public enum NPC_STATE
    {
        Appear,
        GIVE_REWARD,
        Mission_Complete,
        Hide
    }

    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    private float idle_state;
    private float give_reward_state;

    private float appear_timer;

    private Animation idleAnimation;

    private Animation giveRewardAnimation;

    private final int LIFE_REWARD = 1;

    private final int SCORE_REWARD = 200;

    private Vector2 current_position;
    private Vector2 start_position;
    private Vector2 start_heart;
    private Vector2 target_heart;

    private float deltaX;
    private float deltaY;

    private float HEART_SPEED = 20f;


    private Player targetPlayer;

    private Reward reward;

    private NPC_TYPE npyType;

    private NPC_STATE npcState;

    private float npc_width;
    private float npc_height;

    private boolean left_turn;

    private float scale;

    private Music give_heart_sound = GameAssetsDB.getInstance().give_heart;

    /**
     * The constructor to create NPC
     * @param target_player player object to get its position for collision detection
     * @param x The starting x position of NPC
     * @param y The starting y position of NPC
     * @param npcType type of NPC specified
     */
    public NPC(Player target_player, float x, float y, NPC_TYPE npcType)
    {
        this.targetPlayer = target_player;

        this.current_position = new Vector2(x,y);
        this.start_position = new Vector2(x,y);

        //use vector2 variables to calculate the direction the heat forwards
        this.start_heart = new Vector2(this.current_position.x + 100f,this.current_position.y + 300f);
        this.target_heart = new Vector2(2000, 1900);

        this.deltaX = this.start_heart.x - this.target_heart.x;
        this.deltaY = this.start_heart.y - this.target_heart.y;

        double dist = Math.sqrt( deltaX*deltaX + deltaY*deltaY );

        this.deltaX = (float) (deltaX / dist * HEART_SPEED);
        this.deltaY = (float) (deltaY / dist * HEART_SPEED);

        this.npyType = npcType;

        this.scale = 0.4f;

        this.reward = new Reward(Reward.RewardType.LIFE, LIFE_REWARD);

        this.npcState = NPC_STATE.Hide;

        //pumpkin animation
        if(npcType == NPC_TYPE.Pumpkin)
        {
            this.idleAnimation = new Animation(0.3f, this.gameAssetsDB.pumpkin_Idle_Texture);
            this.giveRewardAnimation = new Animation(0.4f, this.gameAssetsDB.pumpkin_Idle_Blink_Texture);

            this.npc_width = this.gameAssetsDB.pumpkin_Idle_Texture[0].getWidth() * this.scale;
            this.npc_height = this.gameAssetsDB.pumpkin_Idle_Texture[0].getHeight() * this.scale;
        }

        //vampire animation
        else
        {
            this.idleAnimation = new Animation(0.3f, this.gameAssetsDB.vampire_Idle_Texture);
            this.giveRewardAnimation = new Animation(0.4f, this.gameAssetsDB.vampire_Idle_Blink_Texture);

            this.npc_width = this.gameAssetsDB.vampire_Idle_Texture[0].getWidth() * this.scale;
            this.npc_height = this.gameAssetsDB.vampire_Idle_Texture[0].getHeight() * this.scale;
        }

        this.idle_state = 0.0f;

        this.give_reward_state = 0.0f;

        this.appear_timer = 0.0f;

        this.left_turn = false;
    }


    @Override
    public void draw(SpriteBatch batch)
    {
        switch (this.npcState)
        {
            case Appear:
                Texture idleTexture = (Texture)this.idleAnimation.getKeyFrame(this.idle_state, true);
                batch.draw(idleTexture,
                        this.current_position.x, this.current_position.y,
                        0, 0,
                        idleTexture.getWidth(), idleTexture.getHeight(),
                        0.4f,0.4f,
                        0,0,0, (int)idleTexture.getWidth(), (int)idleTexture.getHeight(), this.left_turn, false);

                break;

            case GIVE_REWARD:
                Texture giveRewardTexture = (Texture) this.giveRewardAnimation.getKeyFrame(give_reward_state);

                batch.draw(giveRewardTexture,
                        this.current_position.x, this.current_position.y,
                        0, 0,
                        giveRewardTexture.getWidth(), giveRewardTexture.getHeight(),
                        0.4f,0.4f,
                        0,0,0, (int)giveRewardTexture.getWidth(), (int)giveRewardTexture.getHeight(), this.left_turn, false);
                batch.draw(reward.life_texture, start_heart.x, start_heart.y);


                break;

        }

    }

    @Override
    public void update(float delta)
    {
        switch (this.npcState)
        {
            //NPC appears after 8 seconds
            case Hide:
                this.appear_timer += delta;

                if(this.appear_timer >= 8) // 8 seconds
                {
                    this.npcState = NPC_STATE.Appear;
                    this.appear_timer = 0f;
                }

                break;


            case Appear:
                this.idle_state += delta;

                //If player comes close to the npc position
                if(this.targetPlayer.getPosition().dst(this.current_position) <= npc_width){
                    //if there's a reward to give
                    if(this.reward!=null)
                        {
                            //npc flips its texture and change the state
                            this.left_turn = true;
                            this.npcState = NPC_STATE.GIVE_REWARD;
                            //reward sound plays and player get 1 heart
                            this.give_heart_sound.play();
                            this.targetPlayer.receiveReward(this.reward);
                        }
                }

                break;

            case GIVE_REWARD:

                this.give_reward_state += delta;

                //the heart moves its position close to the ui heart already exisiting
                if(this.start_heart.x >= this.target_heart.x) {
                    this.start_heart.x -= this.deltaX ;
                    this.start_heart.y += this.deltaY ;

                }

                //after the heart arrives, npc changes its state to appear and flip into original texture
                else{
                    if(this.giveRewardAnimation.isAnimationFinished(give_reward_state) )
                    {
                        this.give_reward_state = 0.0f;
                        this.reward = null;
                        this.npcState = NPC_STATE.Appear;
                        this.left_turn = false;
                    }
                }

                break;
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void reset() {

    }

    @Override
    public Vector2 getStartPosition() {

        return this.start_position;
    }

    @Override
    public Vector2 getPosition() {

        return this.current_position;
    }

    @Override
    public void setPosition(Vector2 position)
    {
        if(position != null)
        {
            this.current_position.x = position.x;
            this.current_position.y = position.y;
        }

    }

    public NPC_STATE getNpcState()
    {
        return this.npcState;
    }
}
