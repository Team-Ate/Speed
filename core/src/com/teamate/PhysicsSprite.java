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

/**
 * 
 * @author TeamAte
 * 
 * PhysicsSprite is a sprite that has a Body. This emulates a real world object
 * which is both visible and has forces act on it. Obstacles, items, and Usain
 * are all PhysicsSprites.
 *
 */
public class PhysicsSprite extends Sprite {
	
	private World world;
	
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	
	private Body body;
	private Fixture fixture;
	
	// ----------------------------------------
	// Constructors
	// ----------------------------------------
	
	/**
	 * Wrapper constructor for the PhysicsSprite which just calls its other
	 * available constructor.
	 * 
	 * @param image
	 * 			The filepath to the image used to visually represent the Sprite
	 * @param world
	 * 			The world in which the PhysicsSprite exists
	 */
	public PhysicsSprite(String image, World world) {
		this(new Texture(image), world);
	}
	
	/**
	 * Constructor for the PhysicsSprite.
	 * 
	 * @param tex
	 * 			The texture used to visually represent the Sprite
	 * @param world
	 * 			The world in which the PhysicsSprite exists
	 */
	public PhysicsSprite(Texture tex, World world) {
		super(tex);
		
		this.world = world;
		
		// Set up the physics body
		this.bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		body = world.createBody(bodyDef);
		
		// Create a fixture shape
        PolygonShape shape = new PolygonShape();
        // Strangely uses half the width/height of the box
        shape.setAsBox(getWidth() / 2, getHeight() / 2);

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
	
	/**
	 * Wrapper for the setPosition method, which assumes that the new position
	 * should also apply to the Sprite and its Body.
	 * 
	 * @param x
	 * 			The new x-coordinate (in meters)
	 * @param y
	 * 			The new y-coordinate (in meters)
	 */
	@Override
	public void setPosition(float x, float y) {
		setPosition(x, y, true);
	}
	
	/**
	 * Adjusts the size of the Sprite and its corresponding Body
	 * 
	 * @param width
	 * 			The new width (in meters)
	 * @param height
	 * 			The new height (in meters)
	 */
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		PolygonShape shape = new PolygonShape();
        // Strangely uses half the width/height of the box
        shape.setAsBox(getWidth() / 2, getHeight() / 2);
        setShape(shape);
	}
	
	// ----------------------------------------
	// Updating things
	// ----------------------------------------
	
	/**
	 * Changes the position of the Sprite, but provides an option to specify
	 * whether the PhysicsSprite's Body should also be changed.
	 * 
	 * @param x
	 * 			The new x-coordinate (in meters)
	 * @param y
	 * 			The new y-coordinate (in meters)
	 * @param adjustBody
	 * 			If true, also changes the Body's position
	 */
	private void setPosition(float x, float y, boolean adjustBody) {
		super.setPosition(x, y);
		if (adjustBody) {
			body.destroyFixture(fixture);
			world.destroyBody(body);
			bodyDef.position.set(x, y);
			body = world.createBody(bodyDef);
			fixture = body.createFixture(fixtureDef);
		}
	}
	
	/**
	 * Adjusts PhysicsSprite's position based on the Body's size, which may have
	 * changed due to interactions with other Bodies.
	 */
	public void update() {
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2, false);
		setRotation((float)Math.toDegrees(body.getAngle()));
	}
	
	/**
	 * Draws the Sprite on the screen
	 * 
	 * @param batch
	 * 			The Batch that should be used for drawing to the screen
	 */
	@Override
	public void draw(Batch batch) {
		batch.draw(this, getX(), getY(), getOriginX(), getOriginY(), 
				getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
	// ----------------------------------------
	// Get stuff
	// ----------------------------------------
	
	/**
	 * Returns the PhysicsSprite's Body.
	 * 
	 * @return The PhysicsSprite's Body
	 */
	public Body getBody() {
		return body;
	}
	
	// ----------------------------------------
	// Wrapper methods for BodyDef
	// ----------------------------------------
	
	/**
	 * Changes the Body's type.
	 * 
	 * @param type
	 * 			The new Body type
	 */
	public void setBodyType(BodyType type) {
		body.destroyFixture(fixture);
		world.destroyBody(body);
		bodyDef.type = type;
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}
	
	/**
	 * Wrapper method for Body.ApplyLinearImpulse. Assumes the impulse is
	 * applied directly to the center of the Body and that the Body should be
	 * woken up.
	 * 
	 * @param impulse
	 * 			The impulse vector to be applied
	 */
	public void applyImpulse(Vector2 impulse) {
		body.applyLinearImpulse(impulse, bodyDef.position, true);
	}
	
	/**
	 * Wrapper method for Body.setLinearVelocity.
	 * 
	 * @param x
	 * 			The x-component of the velocity (in m/s)
	 * @param y
	 * 			The y-component of the velocity (in m/s)
	 */
	public void setLinearVelocity(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	/**
	 * Wrapper method for Body.setGravityScale.
	 * 
	 * @param scale
	 * 			The gravity scaling factor for this Body.
	 */
	public void setGravityScale(float scale) {
		body.setGravityScale(scale);
	}
	
	// ----------------------------------------
	// Wrapper methods for FixtureDef
	// ----------------------------------------
	
	/**
	 * Gives the Body's Fixture a new shape.
	 * 
	 * @param shape
	 * 			The new shape of the Body's Fixture
	 */
	public void setShape(Shape shape) {
		if (body != null) {
			body.destroyFixture(fixture);
			fixtureDef.shape = shape;
			fixture = body.createFixture(fixtureDef);
		}
	}
	
	/**
	 * Sets the density of the Body's Fixture.
	 * 
	 * @param density
	 * 			The new density of the Body's Fixture
	 */
	public void setDensity(float density) {
		body.destroyFixture(fixture);
		fixtureDef.density = density;
		fixture = body.createFixture(fixtureDef);
	}
	
	/**
	 * Sets the friction coefficient of the Body's Fixture.
	 * 
	 * @param friction
	 * 			The new friction coefficient of the Body's Fixture
	 */
	public void setFriction(float friction) {
		body.destroyFixture(fixture);
		fixtureDef.friction = friction;
		fixture = body.createFixture(fixtureDef);
	}
	
	/**
	 * Sets the restitution coefficient of the Body's Fixture.
	 * 
	 * @param restitution
	 * 			The new restitution coefficient of the Body's Fixture
	 */
	public void setRestitution(float restitution) {
		body.destroyFixture(fixture);
		fixtureDef.restitution = restitution;
		fixture = body.createFixture(fixtureDef);
	}
	
	/**
	 * Applies a new Filter Category to the Body's Fixture.
	 * 
	 * @param cat
	 * 			The new filter category of the Body's Fixture.
	 */
	public void setFilterCategory(short cat) {
		body.destroyFixture(fixture);
		fixtureDef.filter.categoryBits = cat;
		fixture = body.createFixture(fixtureDef);
	}
	
	/**
	 * Applies a new collision filter bitmask which determines which other
	 * Filter Categories this Body can collide with.
	 * 
	 * @param mask
	 * 			The collision filter bitmask
	 */
	public void setFilterCollisionMask(short mask) {
		body.destroyFixture(fixture);
		fixtureDef.filter.maskBits = mask;
		fixture = body.createFixture(fixtureDef);
	}
	
}
