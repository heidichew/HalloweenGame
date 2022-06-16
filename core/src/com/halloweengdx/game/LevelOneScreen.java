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
    private TiledMapTileLayer layer;

    //Enemy
    private List<Enemy> enemies;


    public LevelOneScreen(HalloweenGdxGame game){

        super(game);
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(gameAssetsDB.tiledMap_L1);

        MapLayer L1Layer = gameAssetsDB.tiledMap_L1.getLayers().get("L1");
        this.layer = (TiledMapTileLayer) L1Layer;

        // work out tile height
        this.enemies = new ArrayList<Enemy>();
        this.enemies.add(new BatEnemy(null,
                new Vector2(this.layer.getTileWidth() + 100f, this.layer.getTileHeight() * this.layer.getHeight() - 320f),
                this.layer));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //Clear the screen before drawing.
        Gdx.gl.glClearColor(1, 0, 0, 1);
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

    }

    @Override
    public void update(float delta) {
//        layer.getTileWidth(); to access tile width in pixels.
//        layer.getTileHeight(); to access tile height in pixels.
//        layer.getWidth(); to access number of tiles, horizontally.
//        layer.getHeight(); to access number of tiles, vertically.
//        layer.getTileWidth() * layer.getWidth(); to work out the map width in pixels.
//        layer.getTileHeight() * layer.getHeight(); to work out the map height in pixels.

        for(Enemy e: this.enemies)
        {
            e.update(Gdx.graphics.getDeltaTime());
        }

        // Move camera with bat
        if (this.enemies.get(0).getPosition().x > (Gdx.graphics.getWidth() / 2) - 600) {
            super.camera.position.x = this.enemies.get(0).getPosition().x + 600;

            if(super.camera.position.x >= Gdx.graphics.getWidth() - 190f)
            {
                super.camera.position.x = Gdx.graphics.getWidth() - 190;
            }
        }
        if (this.enemies.get(0).getPosition().y > (Gdx.graphics.getHeight() / 2)) {
            super.camera.position.y = this.enemies.get(0).getPosition().y;
        }
        super.camera.update();

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
