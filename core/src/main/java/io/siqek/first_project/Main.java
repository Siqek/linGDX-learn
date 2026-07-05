package io.siqek.first_project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Texture backgroundTexture;
    private Texture bucketTexture;
    private Texture dropTexture;

    private Sprite bucketSprite;

    private Sound dropSound;
    private Music music;

    private SpriteBatch batch;
    private FitViewport viewport;

    private Vector2 touchPos;

    private Array<Sprite> dropSprites;

    private float dropTimer = 0.f;

    private Rectangle bucketRectangle;
    private Rectangle dropRectangle;

    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        touchPos = new Vector2();

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        music.setLooping(true);
        music.setVolume(.2f);
        music.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        final float delta = Gdx.graphics.getDeltaTime();
        Gdx.app.log("INFO:DELTA_TIME", "" + delta);
        Gdx.app.log("INFO:FPS", "" + Gdx.graphics.getFramesPerSecond());

        input();
        logic();
        draw();
    }

    private void input() {
        final float speed = 2.f;
        final float deltaTime = Gdx.graphics.getDeltaTime();

        float speedMultiplier = 1.f;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            speedMultiplier *= 4.f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucketSprite.translateX(speed * speedMultiplier * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucketSprite.translateX(-speed * speedMultiplier * deltaTime);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        final float worldWidth = viewport.getWorldWidth();
        final float worldHeight = viewport.getWorldHeight();

        final float bucketWidth = bucketSprite.getWidth();
        final float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight * 0.7f);

        final float deltaTime = Gdx.graphics.getDeltaTime();

        for (int i = dropSprites.size - 1; i >= 0; --i) {
            final Sprite dropSprite = dropSprites.get(i);
            final float dropWidth = dropSprite.getWidth();
            final float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2.f * deltaTime);

            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
                continue;
            }

            if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }

        dropTimer += deltaTime;
        if (dropTimer > 1.f) {
            dropTimer = 0.f;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        final float worldWidth = viewport.getWorldWidth();
        final float worldHeight = viewport.getWorldHeight();
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        bucketSprite.draw(batch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(batch);
        }

        batch.end();
    }

    private void createDroplet() {
        final float dropWidth = 1.f;
        final float dropHeight = 1.f;
        final float worldWidth = viewport.getWorldWidth();
        final float worldHeight = viewport.getWorldHeight();

        final Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0.f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
