package com.siqek.tile_map_demo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.siqek.tile_map_demo.Main;

public class TileMapScreen extends ScreenAdapter {
    final Main game;

    private final Batch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final OrthogonalTiledMapRenderer mapRenderer;

    public TileMapScreen(Main game) {
        this.game = game;

        batch = game.getBatch();
        camera = game.getCamera();
        viewport = game.getViewport();

        TiledMap map = new TmxMapLoader().load("maps/my-tile-map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, Main.UNIT_SCALE, batch);
    }

    @Override
    public void render(float delta) {
        input(delta);

        viewport.apply();

        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private void input(final float deltaTime) {
        final float speed = 10.f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= speed * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += speed * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += speed * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= speed * deltaTime;
        }
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
    }
}
