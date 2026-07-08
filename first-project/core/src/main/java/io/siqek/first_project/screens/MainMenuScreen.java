package io.siqek.first_project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import io.siqek.first_project.MyGame;

public class MainMenuScreen implements Screen {
    final MyGame game;

    public MainMenuScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {}

    @Override
    public void render(float deltaTime) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.draw(game.batch, "Hello!", 1.5f, 2.5f);
        game.font.draw(game.batch, "Tap or Click anywhere to begin!", 1.5f, 2.f);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
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
    public void dispose() {}
}
