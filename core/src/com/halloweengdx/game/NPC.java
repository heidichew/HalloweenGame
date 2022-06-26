package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class NPC implements Actor
{
    //What kind of texture you like
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

    private final int LIVE_REWARD = 1;

    private final int SCORE_REWARD = 200;

    private Vector2 current_position;
    private Vector2 start_position;

    private Player targetPlayer;

    private Reward reward;

    private NPC_TYPE npyType;

    private NPC_STATE npcState;

    private float npc_width;
    private float npc_height;

    private boolean left_turn;

    private float scale;


    public NPC(Player target_player, float x, float y, NPC_TYPE npcType)
    {
        this.targetPlayer = target_player;

        this.current_position = new Vector2(x,y);
        this.start_position = new Vector2(x,y);

        this.npyType = npcType;

        this.scale = 0.4f;

        this.reward = new Reward(Reward.RewardType.LIVE, LIVE_REWARD);

        this.npcState = NPC_STATE.Appear;

        if(npcType == NPC_TYPE.Pumpkin)
        {
            this.idleAnimation = new Animation(0.3f, this.gameAssetsDB.pumpkin_Idle_Texture);
            this.giveRewardAnimation = new Animation(0.4f, this.gameAssetsDB.pumpkin_Idle_Blink_Texture);

            this.npc_width = this.gameAssetsDB.pumpkin_Idle_Texture[0].getWidth() * this.scale;
            this.npc_height = this.gameAssetsDB.pumpkin_Idle_Texture[0].getHeight() * this.scale;
        }
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
                Texture giveRewardTexture = (Texture) this.giveRewardAnimation.getKeyFrame(give_reward_state, true);
                batch.draw(giveRewardTexture,
                        this.current_position.x, this.current_position.y,
                        0, 0,
                        giveRewardTexture.getWidth(), giveRewardTexture.getHeight(),
                        0.4f,0.4f,
                        0,0,0, (int)giveRewardTexture.getWidth(), (int)giveRewardTexture.getHeight(), this.left_turn, false);
                break;

        }

    }

    @Override
    public void update(float delta)
    {
        switch (this.npcState)
        {
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

                if(this.targetPlayer.position.y < this.current_position.y + this.npc_height/2
                        && this.targetPlayer.position.y >= this.current_position.y - this.npc_height/2)
                {
                    if(this.targetPlayer.position.x + this.targetPlayer.getSprite().getWidth() >= this.current_position.x + npc_width/2 + 20f &&
                    this.targetPlayer.position.x + this.targetPlayer.getSprite().getWidth() < this.current_position.x + npc_width + 120f)
                    {
                        if(this.reward!=null)
                        {
                           this.npcState = NPC_STATE.GIVE_REWARD;
                        }

                    }
                }

                break;

            case GIVE_REWARD:

                this.give_reward_state += delta;

                // Do one more check make sure that the player still in the range
                if(this.targetPlayer.position.y < this.current_position.y + this.npc_height/2
                        && this.targetPlayer.position.y >= this.current_position.y - this.npc_height/2)
                {
                    if(this.targetPlayer.position.x + this.targetPlayer.getSprite().getWidth() >= this.current_position.x + npc_width/2 + 20f &&
                            this.targetPlayer.position.x + this.targetPlayer.getSprite().getWidth() < this.current_position.x + npc_width + 120f)
                    {
                        if(this.giveRewardAnimation.isAnimationFinished(give_reward_state))
                        {
                            this.targetPlayer.receiveReward(this.reward);
                            this.give_reward_state = 0.0f;

                            this.reward = null;
                            this.npcState = NPC_STATE.Mission_Complete;
                        }

                    }
                    else
                    {
                        this.npcState = NPC_STATE.Appear;
                    }
                }

                break;
        }

        //checking for player location turn x and y


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
