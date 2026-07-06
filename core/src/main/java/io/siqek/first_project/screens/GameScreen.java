package io.siqek.first_project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import io.siqek.first_project.MyGame;

public class GameScreen implements Screen {
    final MyGame game;

    private final Texture backgroundTexture;
    private final Texture bucketTexture;
    private final Texture dropTexture;

    private final Sprite bucketSprite;

    private final Sound dropSound;
    private final Music music;

    private final Vector2 touchPos;

    private final Array<Sprite> dropSprites;

    private float dropTimer = 0.f;

    private final Rectangle bucketRectangle;
    private final Rectangle dropRectangle;

    private int dropsGathered = 0;

    public GameScreen(MyGame game) {
        this.game = game;

        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(.2f);

        touchPos = new Vector2();

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
    }


    @Override
    public void show() {
        music.play();
    }

    @Override
    public void render(float deltaTime) {
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
            game.viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        final float worldWidth = game.viewport.getWorldWidth();

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
                dropsGathered++;
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

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        final float worldWidth = game.viewport.getWorldWidth();
        final float worldHeight = game.viewport.getWorldHeight();
        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        bucketSprite.draw(game.batch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(game.batch);
        }

        game.font.draw(game.batch, "Drops collected: " + dropsGathered, 0.f, worldHeight);

        game.batch.end();
    }

    private void createDroplet() {
        final float dropWidth = 1.f;
        final float dropHeight = 1.f;
        final float worldWidth = game.viewport.getWorldWidth();
        final float worldHeight = game.viewport.getWorldHeight();

        final Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0.f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        bucketTexture.dispose();
        dropTexture.dispose();

        dropSound.dispose();
        music.dispose();
    }
}
