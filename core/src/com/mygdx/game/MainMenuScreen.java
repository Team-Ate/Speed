package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {
	
	final SpeedGame game;
	
	OrthographicCamera camera;
	Music introLoop;
	Sound optionSelectionSound;
	
	public MainMenuScreen(final SpeedGame speed){
		game = speed;
		
		introLoop = Gdx.audio.newMusic(Gdx.files.internal("intro.wav"));
		optionSelectionSound = Gdx.audio.newSound(Gdx.files.internal("optionselection.wav"));
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
	}

	@Override
	public void show() {
		introLoop.setLooping(true);
		introLoop.play();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "SPEED", 100, 150);
		game.font.draw(game.batch, "Tap Anywhere to Begin", 100, 100);
		game.batch.end();
		
		if (Gdx.input.isTouched()){
			optionSelectionSound.play();
			introLoop.stop();
			game.setScreen(new GameScreen(game));
			dispose();
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
