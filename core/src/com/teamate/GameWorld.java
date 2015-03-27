package com.teamate;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
	Box2DDebugRenderer debugRenderer;
	
	private World world;
	private SpriteBatch batch;
	
	private List<PhysicsSprite> sprites = new ArrayList<PhysicsSprite>();
	
	public GameWorld() {
		viewport = new FillViewport(Speed.WORLD_WIDTH, Speed.WORLD_HEIGHT);
		viewport.apply(true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		batch = new SpriteBatch();
		world = new World(new Vector2(0, Speed.WORLD_GRAVITY), true);
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
	}
	
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		batch.begin();
		for (PhysicsSprite sprite : sprites) {
			sprite.draw(batch);
		}
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

}
