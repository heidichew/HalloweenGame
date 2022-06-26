package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class Button {

    float x;    // The x coordinate to place the button on screen
    float y;    // The y coordinate to place the button on screen
    float w;    // The width of the button
    float h;    // The height of the button

    boolean isDown; // True if the user has pressed the button else false

    private Texture textureUp = null;
    private Texture textureDown = null;

    private BitmapFont font = null;
    private String buttonText = "";

    private boolean defaultTexture = true;  // True if use default texture, false use custom texture

    /**
     * The constructor to create button without text and use default textures
     * @param x The x coordinate to render the button
     * @param y The y coordinate to render the button
     * @param w The width of the button
     * @param h The height of the button
     */
    public Button(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isDown = false;
        this.buttonText = "";

        defaultTexture = true;
        create();
    }

    /**
     * The constructor to create button with text and use default textures
     * @param x The x coordinate to render the button
     * @param y The y coordinate to render the button
     * @param w The width of the button
     * @param h The height of the button
     * @param buttonText    The text to draw on the button
     */
    public Button(float x, float y, float w, float h, String buttonText) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isDown = false;
        this.buttonText = buttonText;

        defaultTexture = true;
        create();
    }

    /**
     * The constructor to create button without text and use custom textures
     * @param x The x coordinate to render the button
     * @param y The y coordinate to render the button
     * @param w The width of the button
     * @param h The height of the button
     * @param textureUp     The texture to display when the button is not pressed
     * @param textureDown   The texture to display when the button is pressed
     */
    public Button(float x, float y, float w, float h, Texture textureUp, Texture textureDown) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isDown = false;

        defaultTexture = false;
        this.textureUp = textureUp;
        this.textureDown = textureDown;

        create();
    }

    /**
     * The constructor to create button with text and use custom textures
     * @param x The x coordinate to render the button
     * @param y The y coordinate to render the button
     * @param w The width of the button
     * @param h The height of the button
     * @param buttonText    The text to place on the button
     * @param textureUp     The texture to display when the button is not pressed
     * @param textureDown   The texture to display when the button is pressed
     */
    public Button(float x, float y, float w, float h, String buttonText, Texture textureUp, Texture textureDown) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        isDown = false;

        this.buttonText = buttonText;

        defaultTexture = false;
        this.textureUp = textureUp;
        this.textureDown = textureDown;

        create();
    }

    private void create(){

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(6, 6);

        if (defaultTexture){
            textureUp = new Texture("button/button_long_blue.png");
            textureDown = new Texture("button/button_long_beige.png");
        }
    }

    /**
     * Check if the button is pressed
     * @param checkTouch    Boolean value indicating if the user pressed the screen
     * @param touchX        The x coordinate of where the user touch on the screen
     * @param touchY        The y coordinate of where the user touch on the screen
     */
    public void update(boolean checkTouch, int touchX, int touchY) {
        isDown = false;

        if (checkTouch) {
            int h2 = Gdx.graphics.getHeight();
            //Touch coordinates have origin in top-left instead of bottom left
            if (touchX >= x && touchX <= x + w && h2 - touchY >= y && h2 - touchY <= y + h) {
                isDown = true;
            }else{
                isDown = false;
            }
        }
    }

    /**
     * Draw the specific texture based on the button pressed state
     * @param batch The sprite batch for rendering graphics on screen
     */
    public void draw(SpriteBatch batch) {

        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);
        } else {
            batch.draw(textureDown, x, y, w, h);
        }

        float text_y = y + 150f;
        // If there is a text specified, create a text and render that text at the centre of game world
        // However, this method always draw text at the center of the screen
        if (!buttonText.equals("")){
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(font,buttonText,Color.BLACK,Gdx.graphics.getWidth(), Align.center,true);
            font.draw(batch, glyphLayout, 0, text_y);
        }
    }

    public void dispose()
    {
        font.dispose();
        textureUp.dispose();
        textureDown.dispose();
    }
}
