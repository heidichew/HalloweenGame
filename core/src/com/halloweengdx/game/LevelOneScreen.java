package com.halloweengdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class LevelOneScreen extends GameScreen
{
    //assets
    private GameAssetsDB gameAssetsDB = GameAssetsDB.getInstance();

    //tileMap Render
    private TiledMapRenderer tiledMapRenderer;

    //Tile map layer
    TiledMapTileLayer layer;

    //Enemy
    List<Enemy> enemies;

    private boolean temp_bol = true;

    public LevelOneScreen(HalloweenGdxGame game){

        super(game);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(gameAssetsDB.tiledMap_L1);

        MapLayer L1Layer = gameAssetsDB.tiledMap_L1.getLayers().get("L1");
        this.layer = (TiledMapTileLayer) L1Layer;

        // work out tile height
        this.enemies = new ArrayList<Enemy>();
        this.enemies.add(new BatEnemy(null, new Vector2(this.layer.getTileWidth() + 100f, this.layer.getTileHeight() * this.layer.getHeight() - 320f)));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        update(Gdx.graphics.getDeltaTime());

        //render tile map
        this.tiledMapRenderer.setView(super.camera);
        this.tiledMapRenderer.render();

        //render enemy
        super.batch.setProjectionMatrix(super.camera.combined);
        super.batch.begin();
        this.enemies.get(0).draw(super.batch);
        super.batch.end();



        if(temp_bol)
        {
            super.camera.translate(new Vector2(0,10));
            super.camera.update();
            //temp_bol = !temp_bol;
        }

    }

    @Override
    public void update(float delta) {
//        layer.getTileWidth(); to access tile width in pixels.
//        layer.getTileHeight(); to access tile height in pixels.
//        layer.getWidth(); to access number of tiles, horizontally.
//        layer.getHeight(); to access number of tiles, vertically.
//        layer.getTileWidth() * layer.getWidth(); to work out the map width in pixels.
//        layer.getTileHeight() * layer.getHeight(); to work out the map height in pixels.




    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
