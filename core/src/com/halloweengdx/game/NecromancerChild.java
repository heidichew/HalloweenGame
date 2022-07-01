package com.halloweengdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class NecromancerChild extends Enemy
{
    private GameAssetsDB texture_assets = GameAssetsDB.getInstance();

    private Animation moveAnimation;
    private Animation attackAnimation;
    private Animation dyingAnimation;

    private float move_state;
    private float attack_state;
    private float dying_state;


    //w and h
    private int necromancerChild_Width;
    private int necromancerChild_Height;

    //scale
    private float scale;

    //movement speed
    float movementSpeed = 350f;

    //necromancerBoss
    NecromancerBoss parent;

    private Vector2 attack_pos;

    private boolean turn_head;


    /**
     * The constructor to create an enemy that place the enemy at a specific starting position
     *
     * @param player       The player that the enemy instance can kill in the game world
     * @param start_xy     The stating position to place the enemy instance
     * @param score
     */
    public NecromancerChild(NecromancerBoss necromancerBoss, Player player, Vector2 start_xy, int score)
    {
        super(player, start_xy, start_xy, score, 0);

        this.moveAnimation = new Animation(0.07f, this.texture_assets.necromancer_child_move_texture);
        this.attackAnimation = new Animation(0.1f, this.texture_assets.necromancer_child_attack_texture);
        this.dyingAnimation = new Animation(0.2f, this.texture_assets.necromancer_child_dying_texture);

        this.move_state = 0.0f;
        this.attack_state = 0.0f;
        this.dying_state = 0.0f;

        this.scale = 0.36f;

        this.necromancerChild_Width = Math.round(this.texture_assets.necromancer_child_move_texture[0].getWidth() * this.scale);
        this.necromancerChild_Height = Math.round(this.texture_assets.necromancer_child_move_texture[0].getHeight() * this.scale);

        super.setState(EnemyState.MOVE);

        this.parent = necromancerBoss;
        this.attack_pos = null;
    }

    @Override
    public void reset() {

    }

    @Override
    public void draw(SpriteBatch batch) {

        switch (super.getState())
        {
            case CHASE:
            case MOVE:
                Texture move_texture = (Texture) this.moveAnimation.getKeyFrame(this.move_state, true);
                batch.draw(move_texture,
                        super.getPosition().x- this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        move_texture.getWidth() , move_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) move_texture.getWidth(), (int) move_texture.getHeight(), turn_head, false);

                break;

            case ATTACK:
                Texture attack_texture = (Texture) this.attackAnimation.getKeyFrame(this.attack_state, true);
                batch.draw(attack_texture,
                        super.getPosition().x- this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        attack_texture.getWidth() , attack_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) attack_texture.getWidth(), (int) attack_texture.getHeight(), turn_head, false);
                break;

            case DYING:
                Texture dying_texture = (Texture) this.dyingAnimation.getKeyFrame(this.dying_state, true);

                batch.draw(dying_texture,
                        super.getPosition().x- this.necromancerChild_Width/2, super.getPosition().y - this.necromancerChild_Height/2,
                        0,0,
                        dying_texture.getWidth() , dying_texture.getHeight(),
                        this.scale, this.scale,
                        0, 0, 0, (int) dying_texture.getWidth(), (int) dying_texture.getHeight(), turn_head, false);
                break;

        }

    }

    @Override
    public void update(float delta)
    {

        if(super.getState() == EnemyState.DYING || super.getState() == EnemyState.DEAD)
        {
            this.dying_state += delta;
            if(this.dying_state >= this.dyingAnimation.getAnimationDuration())
            {
                this.dying_state = 0.0f;
                this.setState(EnemyState.DEAD);
            }

        }
        else
        {

            switch (super.getState())
            {
                case MOVE:

                    this.move_state += delta;

                    if(super.getTargetPlayer().getPosition().x + super.getTargetPlayer().getSprite().getWidth() - 200f < super.getStartPosition().x)
                    {
                        turn_head = true;
                    }
                    else
                    {
                        turn_head = false;
                    }

                    if (super.getTargetPlayer().getPosition().dst(super.getStartPosition()) < 600f)
                    {
                       super.setState(EnemyState.CHASE);

                    }
                    break;

                case CHASE:

                    float distance_x = 0.0f;
                    float distance_y = 0.0f;

                    this.move_state += delta;

                    System.out.println("Height"+this.necromancerChild_Height);
                    System.out.println("Width"+this.necromancerChild_Width);


                    // for x
                    System.out.println("Player in the area");

                    if(super.getPosition().x + (this.necromancerChild_Width/2 - 280f) < super.getTargetPlayer().getPosition().x)
                    {
                        distance_x += (this.movementSpeed * delta);
                    }
                    else if(super.getPosition().x + (this.necromancerChild_Width/2 - 280f) > super.getTargetPlayer().getPosition().x)
                    {
                        this.attack_pos = new Vector2(super.getTargetPlayer().getPosition());
                        super.setState(EnemyState.ATTACK);
                    }

                    //for y
                    if(super.getPosition().y + this.necromancerChild_Height/2 - 150f < super.getTargetPlayer().getPosition().y + super.getTargetPlayer().getSprite().getHeight())
                    {
                        distance_y += (this.movementSpeed * delta);
                        System.out.println("EY: " + (this.getPosition().y + distance_y));
                    }

                    super.setPosition(new Vector2(super.getPosition().x + distance_x, super.getPosition().y + distance_y));

                    if(super.getPosition().dst(this.parent.getPosition()) > 1000f)
                    {
                        super.setState(EnemyState.DYING);
                    }

                    break;



                case ATTACK:
                    this.attack_state += delta;

                    System.out.println("PX" + super.getTargetPlayer().getPosition().x + "-----------");
                    System.out.println("EX" + super.getPosition().x + "-----------");

                    if(this.attackAnimation.isAnimationFinished(this.attack_state))
                    {
                        this.attack_state =0.0f;
                        if(this.attack_pos != null && super.getTargetPlayer().getPosition().dst(attack_pos) < 100f)
                        {
                            super.getTargetPlayer().isHurt = true;
                            super.getTargetPlayer().setState(Player.PlayerState.HURT);
                        }
                        super.setState(EnemyState.DYING);
                    }

                    break;
            }


        }
    }


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public Rectangle getCollider() {
        return null;
    }
}
