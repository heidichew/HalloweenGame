package com.halloweengdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;

public class Reward
{
    private GameAssetsDB reward_assets = GameAssetsDB.getInstance();

    public enum RewardType {LIFE, SCORE}

    private int value;

    private  RewardType rewardType;

    public Texture life_texture;

    public Reward (RewardType rewardType, int value)
    {
        life_texture = GameAssetsDB.getInstance().lifeTexture;
        this.rewardType = rewardType;
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public RewardType getRewardType()
    {
        return this.rewardType;
    }
}
