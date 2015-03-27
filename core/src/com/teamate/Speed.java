package com.teamate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Speed extends ApplicationAdapter implements InputProcessor, ContactListener {
	
	public static final boolean DEBUG_PHYSICS = false;
	
	public static final short PHYSICS_CATEGORY_LEFT_WALL = 1;
	public static final short PHYSICS_CATEGORY_GROUND = 2;
	public static final short PHYSICS_CATEGORY_USAIN = 4;
	public static final short PHYSICS_CATEGORY_OBSTACLE = 8;
	
	public static final float WORLD_WIDTH = 8; // m
	public static final float WORLD_HEIGHT = 4.5f; // m
	public static final float WORLD_GRAVITY = -25;
	
	public static final float USAIN_X = WORLD_WIDTH / 3;
	
	public static float gameSpeed = 1f;
	
	boolean usainCollidingWithObstacle = false;
	
	GameWorld gameWorld;

	Usain usain;
	
	Body ground;
	Body leftWall;
	
	List<PhysicsSprite> obstacles = new ArrayList<PhysicsSprite>();
	
	int totScore;
	
	@Override
	public void create() {
		
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
		usain.setFilterCollisionMask((short) (PHYSICS_CATEGORY_GROUND | PHYSICS_CATEGORY_OBSTACLE | PHYSICS_CATEGORY_LEFT_WALL));
		
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

	@Override
	public void render() {
		
		if (new Random().nextFloat() < 0.005f) {
			addBlock();
		}
		
		gameSpeed += 0.001f;
		
		for (PhysicsSprite obstacle : obstacles) {
			obstacle.setLinearVelocity(-2.9f * gameSpeed, obstacle.getBody().getLinearVelocity().y);
		}
		
		if (!usainCollidingWithObstacle) {
			if (usain.getX() < USAIN_X - 0.05f) {
				usain.setLinearVelocity(1, usain.getBody().getLinearVelocity().y);
			} else if (usain.getX() > USAIN_X + 0.05f) {
				usain.setLinearVelocity(0, usain.getBody().getLinearVelocity().y);
			}
		}
		
		gameWorld.update();
		gameWorld.render();

		totScore++;
	}
	
	@Override
	public void resize(int width, int height) {
		gameWorld.resize(width, height);
	}
	
	public void addBlock() {
		// Add a block
		PhysicsSprite block = new PhysicsSprite("box.png", gameWorld.getWorld());
		block.setSize(WORLD_WIDTH / 8, WORLD_WIDTH / 8);
		block.setPosition(WORLD_WIDTH + block.getWidth(), WORLD_HEIGHT / 12 + block.getHeight());
		block.setFilterCategory(PHYSICS_CATEGORY_OBSTACLE);
		block.setFilterCollisionMask((short) (PHYSICS_CATEGORY_GROUND | PHYSICS_CATEGORY_USAIN));
		obstacles.add(block);
		gameWorld.addSprite(block);
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

	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		
		if (usain.isJumping() && (a == usain.getBody() || b == usain.getBody())) {
			usain.notifyLanded();
		} else if (a == usain.getBody() && b == leftWall || a == leftWall && b == usain.getBody()) {
			Gdx.app.log("Contact", "Usain collided with the left wall");
			Gdx.app.exit();
		}
		
		if (a == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (b == obstacle.getBody()) {
					usainCollidingWithObstacle = true;
				}
			}
		} else if (b == usain.getBody()) {
			for (PhysicsSprite obstacle : obstacles) {
				if (a == obstacle.getBody()) {
					usainCollidingWithObstacle = true;
				}
			}
		}
	} 

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
