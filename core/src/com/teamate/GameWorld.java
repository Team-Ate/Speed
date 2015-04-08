package com.teamate;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameWorld {
	
	private Viewport viewport;
	private Box2DDebugRenderer debugRenderer;
	
	private World world;
	private SpriteBatch batch;
	
	private List<PhysicsSprite> sprites = new ArrayList<PhysicsSprite>();
	
	private Sprite background;
	private float scrollTimer = 0;
	private String scoreString;
	
	public GameWorld() {
		viewport = new FillViewport(Speed.WORLD_WIDTH, Speed.WORLD_HEIGHT);
		viewport.apply(true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		batch = new SpriteBatch();
		world = new World(new Vector2(0, Speed.WORLD_GRAVITY), true);
		
		Texture wallTex = new Texture("wall.png");
		wallTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		background = new Sprite(wallTex);
		background.setSize(Speed.WORLD_WIDTH + 2, Speed.WORLD_HEIGHT);
		background.setPosition(0, 0);
	}
	
	public void addSprite(PhysicsSprite sprite) {
		sprites.add(sprite);
	}
	
	public void removeSprite(PhysicsSprite sprite) {
		sprites.remove(sprite);
	}
	
	public Body addBody(BodyDef bodydef, FixtureDef fixturedef) {
		Body body = world.createBody(bodydef);
		body.createFixture(fixturedef);
		return body;
	}
	
	public void update() {
		viewport.getCamera().update();
		world.step(1/60f, 6, 2);
		
		for (PhysicsSprite sprite : sprites) {
			sprite.update();
		}
		
		scrollTimer += 0.0047f * Speed.gameSpeed;	// More time = faster scroll
		if (scrollTimer > 1f) {
			scrollTimer = 0f;
		}
		
		background.setU(scrollTimer);
		background.setU2(scrollTimer + 1);
	}
	
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		batch.begin();
		background.draw(batch);
		
		for (PhysicsSprite sprite : sprites) {
			sprite.draw(batch);
		}
		
		scoreString = "Score: " + String.format("%.0f", Speed.totScore);
		Speed.font.draw(batch, scoreString, .2f, 4.4f);
		batch.end();
		
		if (Speed.DEBUG_PHYSICS) {
			debugRenderer.render(world, batch.getProjectionMatrix());
		}
	}
	
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	public World getWorld() {
		return world;
	}
	
	public Batch getBatch() {
		return batch;
	}

}
