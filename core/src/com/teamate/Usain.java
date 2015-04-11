package com.teamate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameScreen;

/**
 * 
 * @author TeamAte
 * 
 * Represents the main character of the game, UsainVolt.
 * 
 */
public class Usain extends PhysicsSprite {
	
	private boolean jumping = false;
	private int animFrame = 0;
	private int updateCount = 0;
	private static final int ANIM_RATE = (int) (8 * GameScreen.gameSpeed);
	
	private Sound jumpSound;
	private Sound landSound;
	
	/**
	 * Constructor.
	 * 
	 * @param image
	 * 			The pathname of the image to represent Usain on screen
	 * @param world
	 * 			The world in which Usain should live
	 */
	public Usain(String image, World world) {
		super(image, world);
		init();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param tex
	 * 			The texture representing Usain on screen
	 * @param world
	 * 			The world in which Usain should live
	 */
	public Usain(Texture tex, World world) {
		super(tex, world);
		init();
	}
	
	/**
	 * Initializes Usain.
	 */
	private void init() {
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("land.wav"));
	}
	
	/**
	 * Determines if Usain is currently jumping.
	 * 
	 * @return True if Usain is jumping, false otherwise.
	 */
	public boolean isJumping() {
		return jumping;
	}
	
	/**
	 * Causes Usain to jump if he is not already jumping.
	 */
	public void jump() {
		if (!jumping) {
			jumping = true;
			applyImpulse(new Vector2(0, 10));
			setTexture(new Texture("sprite_robot1.png"));
			animFrame = 0;
			
			jumpSound.play();
		}
	}
	
	/**
	 * Performs required actions for when Usain lands from a jump, including
	 * playing a sound.
	 */
	public void notifyLanded() {
		if (jumping) {
			jumping = false;
			
			landSound.play();
		}
	}
	
	/**
	 * Updates Usain for another frame.
	 */
	@Override
	public void update() {
		
		updateCount++;
		
		// Animate
		if (!jumping && updateCount % ANIM_RATE == 0) {
			animFrame++;
			animFrame %= 4;
			setTexture(new Texture("sprite_robot" + (animFrame + 1) + ".png"));
		}
		
		super.update();
	}

}
