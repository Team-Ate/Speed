package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class GameOverScreen implements Screen{
	
	final SpeedGame game;
	
	OrthographicCamera camera;
	Texture background;
	Sprite wrappingBackground;
	
	float scrollTimer;
	
	
	public GameOverScreen(final SpeedGame speed) {
		game = speed;
		
		background = new Texture(Gdx.files.internal("wall.png"));
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		//wrap background
		background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		wrappingBackground = new Sprite(background);
		wrappingBackground.setSize(854, 480);
		wrappingBackground.setPosition(0, 0);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		SpeedGame.batch.setProjectionMatrix(camera.combined);
		
		scrollTimer += 0.0047f;
		if (scrollTimer > 1f) {
			scrollTimer = 0f;
		}
		wrappingBackground.setU(scrollTimer);
		wrappingBackground.setU2(scrollTimer + 1);
		
		SpeedGame.batch.begin();
		wrappingBackground.draw(SpeedGame.batch);
		SpeedGame.font.draw(SpeedGame.batch, "You Suck", 350, 180);
		SpeedGame.batch.end();
		
		processInput();
		
	}
	
	public void processInput(){
		if (Gdx.input.isTouched()){
			game.setScreen(new MainMenuScreen(game));
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
