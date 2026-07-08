package com.siqek.viewports_playground;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;

    private static final float WORLD_WIDTH = 100.f;
    private static final float WORLD_HEIGHT = 100.f;

    private SpriteBatch batch;
    private Sprite background;
    private Sprite image;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, camera);

        batch = new SpriteBatch();

        background = new Sprite(new Texture("background.png"));
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        background.setCenter(0.f, 0.f);

        image = new Sprite(new Texture("libgdx.png"));
        image.setSize(4.f, 4.f);
        image.setCenter(0.f, 0.f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        input();
        logic();

        ScreenUtils.clear(Color.CLEAR);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        background.draw(batch);
        image.draw(batch);

        batch.end();
    }

    private void input() {
        final float deltaTime = Gdx.graphics.getDeltaTime();
        final float speed = 20.f;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            image.translate(0.f, speed * deltaTime);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            image.translate(0.f, -speed * deltaTime);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            image.translate(-speed * deltaTime, 0.f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            image.translate(speed * deltaTime, 0.f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom = Math.min(10.f, camera.zoom + 2.f * deltaTime);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom = Math.max(0.2f, camera.zoom - 2.f * deltaTime);
        }
    }

    private void logic() {
        image.setX(MathUtils.clamp(image.getX(), -WORLD_WIDTH / 2.f, WORLD_WIDTH / 2.f - image.getWidth()));
        image.setY(MathUtils.clamp(image.getY(), -WORLD_HEIGHT / 2.f, WORLD_HEIGHT / 2.f - image.getHeight()));

        final float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        final float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        if (effectiveViewportWidth > WORLD_WIDTH
            || effectiveViewportHeight > WORLD_HEIGHT) {
            camera.zoom = Math.min(WORLD_WIDTH / camera.viewportWidth, WORLD_HEIGHT / camera.viewportHeight);
        }

        camera.position.x = MathUtils.clamp(
            image.getX() + image.getWidth() / 2.f,
            (-WORLD_WIDTH + camera.viewportWidth * camera.zoom) / 2.f,
            (WORLD_WIDTH - camera.viewportWidth * camera.zoom) / 2.f);
        camera.position.y = MathUtils.clamp(
            image.getY() + image.getHeight() / 2.f,
            (-WORLD_HEIGHT + camera.viewportHeight * camera.zoom) / 2.f,
            (WORLD_HEIGHT - camera.viewportHeight * camera.zoom) / 2.f);

        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.getTexture().dispose();
        image.getTexture().dispose();
    }
}
