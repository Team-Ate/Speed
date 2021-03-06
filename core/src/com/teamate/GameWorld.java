package com.teamate;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameScreen;
import com.mygdx.game.SpeedGame;

/**
 * 
 * @author TeamAte
 * 
 * Represents the world in which the game takes place.
 *
 */
public class GameWorld {
	
	private Viewport viewport;
	private Box2DDebugRenderer debugRenderer;
	
	private World world;
	
	private List<PhysicsSprite> sprites = new ArrayList<PhysicsSprite>();
	
	private Sprite background;
	private float scrollTimer = 0;
	private String scoreString;
	
	/**
	 * Constructor.
	 */
	public GameWorld() {
		viewport = new FillViewport(GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT);
		viewport.apply(true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		world = new World(new Vector2(0, GameScreen.WORLD_GRAVITY), true);
		
		Texture wallTex = new Texture("wall.png");
		wallTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		background = new Sprite(wallTex);
		background.setSize(GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT);
		background.setPosition(0, 0);
	}
	
	/**
	 * Adds a PhysicsSprite to track and update.
	 * 
	 * @param sprite
	 * 			The PhysicsSprite to add to the world.
	 */
	public void addSprite(PhysicsSprite sprite) {
		sprites.add(sprite);
	}
	
	/**
	 * Removes a PhysicsSprite that currently exists in the world.
	 * 
	 * @param sprite
	 * 			The PhysicsSprite to remove from the world.
	 */
	public void removeSprite(PhysicsSprite sprite) {
		sprites.remove(sprite);
	}
	
	/**
	 * Creates a Body in the world.
	 * 
	 * @param bodydef
	 * 			The definition of the new Body
	 * @param fixturedef
	 * 			The definition of the Body's Fixture
	 * @return The newly created Body
	 */
	public Body addBody(BodyDef bodydef, FixtureDef fixturedef) {
		Body body = world.createBody(bodydef);
		body.createFixture(fixturedef);
		return body;
	}
	
	/**
	 * Updates the world and all of its PhysicsSprites for another frame.
	 */
	public void update() {
		viewport.getCamera().update();
		world.step(1/60f, 6, 2);
		
		for (PhysicsSprite sprite : sprites) {
			sprite.update();
		}
		
		scrollTimer += 0.006f * GameScreen.gameSpeed;	// More time = faster scroll
		if (scrollTimer > 1f) {
			scrollTimer = 0f;
		}
		
		background.setU(scrollTimer);
		background.setU2(scrollTimer + 1);
	}
	
	/**
	 * Displays the world on screen.
	 */
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		SpeedGame.batch.setProjectionMatrix(viewport.getCamera().combined);
		
		SpeedGame.batch.begin();
		background.draw(SpeedGame.batch);
		
		for (PhysicsSprite sprite : sprites) {
			sprite.draw(SpeedGame.batch);
		}
		
		scoreString = "Score: " + String.format("%.0f", GameScreen.totScore);
		SpeedGame.font.draw(SpeedGame.batch, scoreString, 0.75f, 4.4f);
		SpeedGame.batch.end();
		
		if (GameScreen.DEBUG_PHYSICS) {
			debugRenderer.render(world, SpeedGame.batch.getProjectionMatrix());
		}
	}
	
	/**
	 * Resizes the world.
	 * 
	 * @param width
	 * 			The new height of the world.
	 * @param height
	 * 			The new width of the world.
	 */
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	/**
	 * Returns the world's World object.
	 * 
	 * @return The world's World object
	 */
	public World getWorld() {
		return world;
	}

}
