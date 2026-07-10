package com.siqek.viewports_playground;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport uiViewport;

    private BitmapFont font;

    private static final float WORLD_WIDTH = 100.f;
    private static final float WORLD_HEIGHT = 100.f;

    private SpriteBatch batch;

    private Texture imageTexture;
    private Texture coinTexture;

    private Sprite background;
    private Sprite image;

    private Array<Sprite> marks;
    private Array<Sprite> coins;

    private Rectangle playerRect;
    private Rectangle coinRect;

    private Vector2 touchPos;

    private int collectedCoins = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, camera);
        uiViewport = new ExtendViewport(100, 100);

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.4f);

        batch = new SpriteBatch();

        imageTexture = new Texture("libgdx.png");
        coinTexture = new Texture("coin.png");

        background = new Sprite(new Texture("background.png"));
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        background.setCenter(0.f, 0.f);

        image = new Sprite(imageTexture);
        image.setSize(4.f, 4.f);
        image.setCenter(0.f, 0.f);

        marks = new Array<>();
        coins = new Array<>();

        playerRect = new Rectangle();
        coinRect = new Rectangle();

        touchPos = new Vector2();

        final int COINS_AT_START = 20;
        for (int i = 0; i < COINS_AT_START; ++i) {
            createCoin();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void render() {
//        Gdx.app.log("INFO:FPS", "" + Gdx.graphics.getFramesPerSecond());
//        Gdx.app.log("INFO:MARKS_COUNT", "" + marks.size);

        input();
        logic();

        ScreenUtils.clear(Color.CLEAR);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        background.draw(batch);

        for (Sprite coin : coins) {
            coin.draw(batch);
        }

        for (Sprite mark : marks) {
            mark.draw(batch);
        }

        image.draw(batch);

        batch.end();

        uiViewport.apply();
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        font.draw(batch, "collected coins: " + collectedCoins, 0.f, uiViewport.getWorldHeight() * 0.95f, uiViewport.getWorldWidth(), Align.center, false);

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

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            createMark();
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

        final float newCamPosX = MathUtils.clamp(
            image.getX() + image.getWidth() / 2.f,
            (-WORLD_WIDTH + camera.viewportWidth * camera.zoom) / 2.f,
            (WORLD_WIDTH - camera.viewportWidth * camera.zoom) / 2.f);
        final float newCamPosY = MathUtils.clamp(
            image.getY() + image.getHeight() / 2.f,
            (-WORLD_HEIGHT + camera.viewportHeight * camera.zoom) / 2.f,
            (WORLD_HEIGHT - camera.viewportHeight * camera.zoom) / 2.f);
        camera.position.lerp(new Vector3(newCamPosX, newCamPosY, 0.f), 0.15f);

        camera.update();

        playerRect.set(image.getX(), image.getY(), image.getWidth(), image.getHeight());
        for (Iterator<Sprite> it = coins.iterator(); it.hasNext();) {
            final Sprite coin = it.next();

            coinRect.set(coin.getX(), coin.getY(), coin.getWidth(), coin.getHeight());
            if (playerRect.overlaps(coinRect)) {
                collectedCoins++;
                it.remove();
                createCoin();
            }
        }
    }

    private void createMark() {
        final Sprite mark = new Sprite(imageTexture);
        mark.setSize(1.f, 1.f);
        mark.setCenterX(touchPos.x);
        mark.setY(touchPos.y);

        marks.add(mark);
    }

    private void createCoin() {
        final float coinWidth = 2.f;
        final float coinHeight = 2.f;

        final Sprite coin = new Sprite(coinTexture);
        coin.setSize(coinWidth, coinHeight);
        coin.setX(MathUtils.random(-WORLD_WIDTH / 2.f, WORLD_WIDTH / 2.f - coinWidth));
        coin.setY(MathUtils.random(-WORLD_HEIGHT / 2.f, WORLD_HEIGHT / 2.f - coinHeight));

        coins.add(coin);
    }

    @Override
    public void dispose() {
        batch.dispose();

        imageTexture.dispose();
        coinTexture.dispose();

        background.getTexture().dispose();
    }
}
