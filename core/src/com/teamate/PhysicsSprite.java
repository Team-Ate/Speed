package com.teamate;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSprite extends Sprite {
	
	private World world;
	
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	
	private Body body;
	private Fixture fixture;
	
	// ----------------------------------------
	// Constructors
	// ----------------------------------------
	
	public PhysicsSprite(String image, World world) {
		this(new Texture(image), world);
	}
	
	public PhysicsSprite(Texture tex, World world) {
		super(tex);
		
		this.world = world;
		
		// Set up the physics body
		this.bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);
		
		// Create a fixture shape
        PolygonShape shape = new PolygonShape();
        // Strangely uses half the width/height of the box
        shape.setAsBox((getWidth() / 2) / Speed.PixelsPerMeter, (getHeight() / 2) / Speed.PixelsPerMeter);

        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;

        fixture = body.createFixture(fixtureDef);
        
        setOrigin(0.5f, 0.5f);
	}
	
	// ----------------------------------------
	// Overrides from Sprite
	// ----------------------------------------
	
	@Override
	public void setPosition(float x, float y) {
		setPosition(x, y, true);
	}
	
	// ----------------------------------------
	// Updating things
	// ----------------------------------------
	
	private void setPosition(float x, float y, boolean adjustBody) {
		super.setPosition(x, y);
		if (adjustBody) {
			body.destroyFixture(fixture);
			world.destroyBody(body);
			bodyDef.position.set((x + getWidth() / 2) / Speed.PixelsPerMeter, (y + getHeight() / 2) / Speed.PixelsPerMeter);
			body = world.createBody(bodyDef);
			fixture = body.createFixture(fixtureDef);
		}
	}
	
	public void update() {
		setPosition(body.getPosition().x * Speed.PixelsPerMeter, body.getPosition().y * Speed.PixelsPerMeter, false);
		setRotation((float)Math.toDegrees(body.getAngle()));
	}
	
	@Override
	public void draw(Batch batch) {
		batch.draw(this, getX(), getY(), getOriginX(), getOriginY(), 
				getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
	// ----------------------------------------
	// Get stuff
	// ----------------------------------------
	
	public Body getBody() {
		return body;
	}
	
	// ----------------------------------------
	// Wrapper methods for BodyDef
	// ----------------------------------------
	
	public void setBodyType(BodyType type) {
		body.destroyFixture(fixture);
		world.destroyBody(body);
		bodyDef.type = type;
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}
	
	public void applyImpulse(Vector2 impulse) {
		body.applyLinearImpulse(impulse, bodyDef.position, true);
	}
	
	// ----------------------------------------
	// Wrapper methods for FixtureDef
	// ----------------------------------------
	
	public void setShape(Shape shape) {
		body.destroyFixture(fixture);
		fixtureDef.shape = shape;
		fixture = body.createFixture(fixtureDef);
	}
	
	public void setDensity(float density) {
		body.destroyFixture(fixture);
		fixtureDef.density = density;
		fixture = body.createFixture(fixtureDef);
	}
	
	public void setRestitution(float restitution) {
		body.destroyFixture(fixture);
		fixtureDef.restitution = restitution;
		fixture = body.createFixture(fixtureDef);
	}

}
