package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Represents the main menu of the game (prior to actual gameplay).
 * 
 * @author TeamAte
 *
 */
public class MainMenuScreen implements Screen {
	
	final SpeedGame game;
	
	OrthographicCamera camera;
	Music introLoop;
	Sound optionSelectionSound;
	
	/**
	 * Constructor.
	 * 
	 * @param speed
	 * 			The game for which this is the menu screen.
	 */
	public MainMenuScreen(final SpeedGame speed){
		game = speed;
		
		introLoop = Gdx.audio.newMusic(Gdx.files.internal("intro.wav"));
		optionSelectionSound = Gdx.audio.newSound(Gdx.files.internal("optionselection.wav"));
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
	}
	
	/**
	 * Begins the entry sequence.
	 */
	@Override
	public void show() {
		introLoop.setLooping(true);
		introLoop.play();

	}
	
	/**
	 * Displays the menu onscreen.
	 * 
	 * @param delta
	 * 			Unused parameter
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		SpeedGame.batch.setProjectionMatrix(camera.combined);
		
		SpeedGame.batch.begin();
		SpeedGame.font.draw(SpeedGame.batch, "SPEED", 100, 150);
		SpeedGame.font.draw(SpeedGame.batch, "Tap Anywhere to Begin", 100, 100);
		SpeedGame.batch.end();
		
		if (Gdx.input.isTouched()) {
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
