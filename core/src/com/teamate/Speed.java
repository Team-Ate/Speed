package com.teamate;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Speed extends ApplicationAdapter implements InputProcessor, ContactListener {
	
	private static final boolean DEBUG_PHYSICS = false;
	
	public static final float WORLD_WIDTH = 8; // m
	public static final float WORLD_HEIGHT = 4.5f; // m
	
	private Viewport viewport;
	private OrthographicCamera camera;
	
	Box2DDebugRenderer debugRenderer;
	
	SpriteBatch batch;
	
	World world;

	Usain usain;
	PhysicsSprite ground;
	Texture texture;
	Texture texture2; // TODO: Get rid of once combo of floor+wall
	Sprite wall;
	Sprite movingFloor; // TODO: Get rid of once combo of floor+wall
	Body leftWall;
	float scrollTimer = 0;
	
	@Override
	public void create() {
		
		camera = new OrthographicCamera();
		viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport.apply();
		
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		
		debugRenderer = new Box2DDebugRenderer();
		
		batch = new SpriteBatch();
		world = new World(new Vector2(0, -25), true);
		
		ground = new PhysicsSprite("floor.png", world);
		ground.setSize(WORLD_WIDTH, WORLD_HEIGHT / 6);
		ground.setPosition(WORLD_WIDTH / 2, ground.getHeight() / 2);
		ground.setBodyType(BodyType.StaticBody);
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(-ground.getWidth() / 2, 0, ground.getWidth() / 2, 0);
		ground.setShape(edgeShape);
		
		
		// TODO: Combine the floor texture and the wall texture to create one congruent
		// scrolling picture. Will be better for performance and be easier to deal with
		// scrolling. @Michael should be able to photoshop floor and wall together quick.
		texture = new Texture("wall.png");
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		wall = new Sprite(texture);
		wall.setSize(WORLD_WIDTH + 2, WORLD_HEIGHT);
		wall.setPosition(0, 0);
		
		// TODO: Once combined, get rid of this block. 
		texture2 = new Texture("floor.png"); 
		texture2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		movingFloor = new Sprite(texture2);
		movingFloor.setSize(WORLD_WIDTH, WORLD_HEIGHT / 6);
		movingFloor.setPosition(0, 0);
	
		usain = new Usain("sprite_robot1.png", world);
		usain.setSize(WORLD_WIDTH / 8, WORLD_WIDTH / 8);
		usain.setPosition(WORLD_WIDTH / 6, WORLD_HEIGHT / 2);
		
		BodyDef leftDef = new BodyDef();
		leftDef.type = BodyDef.BodyType.StaticBody;
		leftWall = world.createBody(leftDef);
		
		FixtureDef leftFixDef = new FixtureDef();
		EdgeShape leftShape = new EdgeShape();
		leftShape.set(WORLD_WIDTH / 30, WORLD_HEIGHT, WORLD_WIDTH / 30, 0);
		leftFixDef.shape = leftShape;
		leftWall.createFixture(leftFixDef);
		
		Gdx.input.setInputProcessor(this);
		world.setContactListener(this);
	}

	@Override
	public void render() {
		camera.update();
		world.step(1f/60f, 6, 2);
		usain.update();
		ground.update();
				
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		Matrix4 debugMatrix = batch.getProjectionMatrix();
				
		batch.begin();
		scrollTimer += 0.0047f;  // More time = faster scroll
		if (scrollTimer > 1.0f) scrollTimer = 0.0f; // Reset the timer so we scroll again
		wall.setU(scrollTimer); // Set up the next scroll rotation for the background.
		movingFloor.setU(scrollTimer); // TODO: Get rid of once combo of floor+wall
		wall.setU2(scrollTimer+1); // Set up the next scroll rotation for the background.
		movingFloor.setU2(scrollTimer); // TODO: Get rid of once combo of floor+wall
		wall.draw(batch); // Finally draw the wall. 
		movingFloor.draw(batch); // Finally draw the ground. 
		usain.draw(batch);
		batch.end();
		
		if (DEBUG_PHYSICS) debugRenderer.render(world, debugMatrix);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
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
