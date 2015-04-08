package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen {
	final SpeedGame game;

	Texture background;
	Sprite wrappingBackground;
	Music mainLoop;
	Sound jumpSound;
	Sound landSound;
	Sound collisionSound;
	OrthographicCamera camera;
	Rectangle usainVolt;
	private float scrollTimer;
	
	Animator usain;

	public GameScreen(final SpeedGame speed){
		this.game = speed;
		usain = new Animator();

		//Load Images
		background = new Texture(Gdx.files.internal("wall.png"));

		//Load Music/Sounds
		mainLoop = Gdx.audio.newMusic(Gdx.files.internal("main.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("land.wav"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision.wav"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		usain.create();

		//wrap background
		background.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		wrappingBackground = new Sprite(background);
		wrappingBackground.setSize(1600, 480);
		wrappingBackground.setPosition(0, 0);

		//Create main sprite's body
		usainVolt = new Rectangle();
		usainVolt.x = 800/2 - 64/2;
		usainVolt.y = 20;
		usainVolt.width = 64;
		usainVolt.height = 64;


	}

	@Override
	public void show() {


	}

	@Override
	public void render(float delta) {
		//Reset screen
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



		camera.update();

		scrollTimer += 0.0047f;
		if (scrollTimer > 1f) {
			scrollTimer = 0f;
		}
		wrappingBackground.setU(scrollTimer);
		wrappingBackground.setU2(scrollTimer + 1);

		game.batch.setProjectionMatrix(camera.combined);

		//Drawing
		game.batch.begin();
		wrappingBackground.draw(game.batch);
		game.font.draw(game.batch, "Bones Touches Little Boys", 0, 480); 
		game.batch.end();

		usain.render();
		
		//Look for user input
		processInput();

	}

	private void processInput(){
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isTouched()){
			game.setScreen(new GameOverScreen(game));
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
