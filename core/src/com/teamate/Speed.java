package com.teamate;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Speed extends ApplicationAdapter implements InputProcessor, ContactListener {
	
	public static final int PixelsPerMeter = 64;
	
	SpriteBatch batch;
	
	World world;

	Usain usain;
	PhysicsSprite ground;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		world = new World(new Vector2(0, -200), true);
		
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		
		usain = new Usain("badlogic.jpg", world);
		usain.setPosition(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 2);
		
		ground = new PhysicsSprite("badlogic.jpg", world);
		ground.setSize(w, ground.getHeight() / 2);
		ground.setPosition(-w/2, 0);
		ground.setBodyType(BodyType.StaticBody);
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(0, 0, w, 0);
		ground.setShape(edgeShape);
		
		Gdx.input.setInputProcessor(this);
		world.setContactListener(this);
	}

	@Override
	public void render() {
		world.step(1f/60f, 6, 2);
		
		if (usain.isJumping()) {
			Gdx.app.log("Usain", "jumping!");
		}
		
		usain.update();
		ground.update();
				
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		usain.draw(batch);
		ground.draw(batch);
		batch.end();
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
		
		if (usain.isJumping() && (a == usain.getBody() || b== usain.getBody())) {
			usain.notifyLanded();
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
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
