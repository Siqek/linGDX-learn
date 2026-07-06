package io.siqek.first_project;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.siqek.first_project.screens.MainMenuScreen;

public class MyGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(8, 5);

        font.setUseIntegerPositions(false);
        font.getData().setScale(2.5f * viewport.getWorldHeight() / Gdx.graphics.getHeight());

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        Gdx.app.log("INFO:DELTA_TIME", "" + Gdx.graphics.getDeltaTime());
        Gdx.app.log("INFO:FPS", "" + Gdx.graphics.getFramesPerSecond());

        super.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
