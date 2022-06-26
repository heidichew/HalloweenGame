package com.halloweengdx.game;

public class Reward
{
    public enum RewardType {LIVE, SCORE}

    private int value;

    private  RewardType rewardType;

    public Reward (RewardType rewardType, int value)
    {
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
