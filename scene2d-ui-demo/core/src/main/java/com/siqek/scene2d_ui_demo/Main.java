package com.siqek.scene2d_ui_demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Stage stage;

    private Skin buttonSkin;

    private BitmapFont font;

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        buttonSkin = new Skin(Gdx.files.internal("my-button-skin.json"));

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(2.5f);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(true);
        stage.addActor(mainTable);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK;
        Label titleLabel = new Label("Title", labelStyle);

        mainTable.add(titleLabel).center().colspan(3);

        mainTable.row().expandX();

        Button myButton1 = new Button(buttonSkin);
        myButton1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("changed but1");
            }
        });
        mainTable.add(myButton1).center().width(140).height(70).pad(4);

        Button myButton2 = new Button(buttonSkin);
        myButton2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("changed but2");
            }
        });
        mainTable.add(myButton2).center().width(140).height(70).pad(4);

        Button myButton3 = new Button(buttonSkin);
        myButton3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("changed but3");
            }
        });
        mainTable.add(myButton3).center().width(140).height(70).pad(4);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);

        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        buttonSkin.dispose();
        font.dispose();
    }
}
