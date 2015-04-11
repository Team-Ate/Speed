package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.teamate.GameWorld;
import com.teamate.PhysicsSprite;
import com.teamate.Usain;

/**
 * Controls the entirety of gameplay.
 * 
 * @author TeamAte
 *
 */
public class GameScreen implements Screen, InputProcessor, ContactListener {

	public static final boolean DEBUG_PHYSICS = false;

	public static final short PHYSICS_CATEGORY_LEFT_WALL = 1;
	public static final short PHYSICS_CATEGORY_GROUND = 2;
	public static final short PHYSICS_CATEGORY_USAIN = 4;
	public static final short PHYSICS_CATEGORY_OBSTACLE = 8;
	public static final short PHYSICS_CATEGORY_ITEM = 16;

	public static final float WORLD_WIDTH = 8; // m
	public static final float WORLD_HEIGHT = 4.5f; // m
	public static final float WORLD_GRAVITY = -25;

	public static final float USAIN_X = WORLD_WIDTH / 3;

	public static float gameSpeed = 1f;
	
	private SpeedGame game;

	boolean usainCollidingWithObstacle = false;
	boolean usainCollidingWithItem = false;
	PhysicsSprite itemUsainCollidedWith = null;

	GameWorld gameWorld;
	Usain usain;
	Body ground;
	Body leftWall;
	public static float totScore;


	List<PhysicsSprite> obstacles = new ArrayList<PhysicsSprite>();
	List<PhysicsSprite> items = new ArrayList<PhysicsSprite>();
	
	/**
	 * Constructor.
	 * 
	 * @param speed
	 * 			The game to be played.
	 */
	public GameScreen(final SpeedGame speed) {
		
		this.game = speed;

		// Resets it after you quit the app
		gameSpeed = 1f;

		// General score setup. 
		totScore = 0;
		SpeedGame.font = new BitmapFont();
		SpeedGame.font.setScale(.01f); // May need to become more modular to adjust to display size.
		SpeedGame.font.setColor(Color.BLACK);
		SpeedGame.font.setUseIntegerPositions(false); // Fixes font scaling issues with small screen.

		gameWorld = new GameWorld();

		// Music starts to play here. Import different sounds for background track.
		// DONT use this for in game sounds. Music is strictly for music and very
		// resource intensive.
		Music music = Gdx.audio.newMusic(Gdx.files.internal("main.wav"));
		music.setVolume(0.5f);
		music.play();
		music.setLooping(true);

		usain = new Usain("sprite_robot1.png", gameWorld.getWorld());
		usain.setSize(WORLD_WIDTH / 8, WORLD_WIDTH / 8);
		usain.setPosition(USAIN_X + usain.getWidth() / 2, WORLD_HEIGHT / 12 + usain.getHeight() / 2);
		usain.setFilterCategory(PHYSICS_CATEGORY_USAIN);
		usain.setFilterCollisionMask((short) (PHYSICS_CATEGORY_GROUND | PHYSICS_CATEGORY_OBSTACLE | PHYSICS_CATEGORY_LEFT_WALL | PHYSICS_CATEGORY_ITEM));

		gameWorld.addSprite(usain);

		// Add the left "wall"
		BodyDef leftDef = new BodyDef();
		leftDef.type = BodyDef.BodyType.StaticBody;

		FixtureDef leftFixDef = new FixtureDef();
		EdgeShape leftShape = new EdgeShape();
		leftShape.set(WORLD_WIDTH / 30, WORLD_HEIGHT, WORLD_WIDTH / 30, 0);
		leftFixDef.shape = leftShape;
		leftFixDef.filter.categoryBits = PHYSICS_CATEGORY_LEFT_WALL;

		leftWall = gameWorld.addBody(leftDef, leftFixDef);

		Gdx.input.setInputProcessor(this);
		gameWorld.getWorld().setContactListener(this);

		// Add the ground line
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyDef.BodyType.StaticBody;

		FixtureDef groundFixDef = new FixtureDef();
		EdgeShape groundShape = new EdgeShape();
		groundShape.set(0, WORLD_HEIGHT / 12, WORLD_WIDTH * 2, WORLD_HEIGHT / 12);
		groundFixDef.shape = groundShape;
		groundFixDef.filter.categoryBits = PHYSICS_CATEGORY_GROUND;
		groundFixDef.filter.maskBits = PHYSICS_CATEGORY_USAIN | PHYSICS_CATEGORY_OBSTACLE;

		ground = gameWorld.addBody(groundDef, groundFixDef);

	}
	
	/**
	 * Method stub.
	 */
	@Override
	public void show() {
		
	}
	
	/**
	 * Updates the game for a new frame and displays it onscreen.
	 * 
	 * @param delta
	 * 			Unused parameter.
	 */
	@Override
	public void render(float delta) {
		
		// Occasionally generate a new obstacle
		if (new Random().nextFloat() < 0.005f * gameSpeed) {
			addBlock();
		}
		
		// Occasionally generate a new battery
		if (new Random().nextFloat() < 0.005f * gameSpeed) {
			addItem();
		}
		
		// Handle item pickup
		if (usainCollidingWithItem) {

			// Remove the item Usain collided with
			gameWorld.removeSprite(itemUsainCollidedWith);
			items.remove(itemUsainCollidedWith);
			itemUsainCollidedWith = null;
			usainCollidingWithItem = false;

			// Increase scrolling speed
			gameSpeed += 0.5f;
		}
		
		// Increase score at a rate proportional of the square of the game speed
		totScore += Math.pow(gameSpeed, 2) / 6f;
		
		// Set the obstacles' velocity
		for (PhysicsSprite obstacle : obstacles) {
			obstacle.setLinearVelocity(-2.9f * gameSpeed, obstacle.getBody().getLinearVelocity().y);
		}
		
		// Set the items' velocity
		for (PhysicsSprite item : items) {
			item.setLinearVelocity(-2.9f * gameSpeed, item.getBody().getLinearVelocity().y);
		}
		
		// Handle Usain running into an obstacle
		if (!usainCollidingWithObstacle) {
			if (usain.getX() < USAIN_X - 0.05f) {
				usain.setLinearVelocity(1, usain.getBody().getLinearVelocity().y);
			} else if (usain.getX() > USAIN_X + 0.05f) {
				usain.setLinearVelocity(0, usain.getBody().getLinearVelocity().y);
			}
		}
		
		gameWorld.update();
		gameWorld.render();

	}
	
	/**
	 * Creates a new obstacle just outside the right edge of the screen.
	 */
	public void addBlock() {
		
		// Set up the block's physical characteristics
		PhysicsSprite block = new PhysicsSprite("box.png", gameWorld.getWorld());
		block.setSize(WORLD_WIDTH / 8, WORLD_WIDTH / 8);
		block.setPosition(WORLD_WIDTH * 1.5f, WORLD_HEIGHT / 12 + block.getHeight());
		block.setFilterCategory(PHYSICS_CATEGORY_OBSTACLE);
		block.setFilterCollisionMask((short) (PHYSICS_CATEGORY_GROUND | PHYSICS_CATEGORY_USAIN));
		
		// Add the block to the world it belongs to
		obstacles.add(block);
		gameWorld.addSprite(block);
	}
	
	/**
	 * Creates a new item at a random height just outside the right edge of the screen
	 */
	public void addItem() {
		// Set up the item's physical characteristics
		PhysicsSprite item = new PhysicsSprite("item.png", gameWorld.getWorld());
		item.setSize(WORLD_WIDTH / 46.5f, WORLD_WIDTH / 16);
		item.setPosition(WORLD_WIDTH * 1.5f, WORLD_HEIGHT / 12 + item.getHeight() + (new Random().nextFloat()) * (WORLD_HEIGHT - item.getHeight()));
		item.setFilterCategory(PHYSICS_CATEGORY_ITEM);
		item.setFilterCollisionMask((short) (PHYSICS_CATEGORY_USAIN));
		item.setGravityScale(0);
		
		// Add the item to the world it belongs to
		items.add(item);
		gameWorld.addSprite(item);
	}
	
	/**
	 * Resize the game window
	 * 
	 * @param width
	 * 			The new width of the game (in pixels)
	 * @param height
	 * 			The new height of the game (in pixels)
	 */
	@Override
	public void resize(int width, int height) {
		gameWorld.resize(width, height);

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

	// ----------------------------------------
	// InputProcessor
	// ----------------------------------------

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Handles the only input for the game - the user touching the screen
	 * 
	 * @param screenX
	 * 			Unused parameter
	 * @param screenY
	 * 			Unused parameter
	 * @param pointer
	 * 			Unused parameter
	 * @param button
	 * 			Unused parameter
	 * @return True
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		usain.jump();
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	// ----------------------------------------
	// ContactListener
	// ----------------------------------------
	
	/**
	 * Handle collisions of any kind.
	 * 
	 * @param contact
	 * 			The collision that just occurred
	 */
	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		if (usain.isJumping() && (a == usain.getBody() || b == usain.getBody())) {
			usain.notifyLanded();
		} else if (a == usain.getBody() && b == leftWall || a == leftWall && b == usain.getBody()) {
			Gdx.app.log("Contact", "Usain collided with the left wall");
			
			game.setScreen(new GameOverScreen(game));
		}

		if (a == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (b == obstacle.getBody()) {
					usainCollidingWithObstacle = true;
				}
			}

			for (PhysicsSprite item : items) {
				if (b == item.getBody()) {
					itemUsainCollidedWith = item;
					usainCollidingWithItem = true;
				}
			}
		} else if (b == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (a == obstacle.getBody()) {
					usainCollidingWithObstacle = true;
				}
			}

			for (PhysicsSprite item : items) {
				if (a == item.getBody()) {
					itemUsainCollidedWith = item;
					usainCollidingWithItem = true;
				}
			}
		}
	} 
	
	/**
	 * Handle the end of any collision.
	 * 
	 * @param contact
	 * 			The collision that just ended
	 */
	@Override
	public void endContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		if (a == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (b == obstacle.getBody()) {
					usainCollidingWithObstacle = false;
				}
			}
		} else if (b == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (a == obstacle.getBody()) {
					usainCollidingWithObstacle = false;
				}
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}
}
