package com.teamate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameScreen;

public class Usain extends PhysicsSprite {
	
	private boolean jumping = false;
	private int animFrame = 0;
	private int updateCount = 0;
	private static final int ANIM_RATE = (int) (8 * GameScreen.gameSpeed);
	
	private Sound jumpSound;
	private Sound landSound;
	
	public Usain(String image, World world) {
		super(image, world);
		init();
	}
	
	public Usain(Texture tex, World world) {
		super(tex, world);
		init();
	}
	
	private void init() {
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("land.wav"));
	}
	
	public boolean isJumping() {
		return jumping;
	}
	
	public void jump() {
		if (!jumping) {
			jumping = true;
			applyImpulse(new Vector2(0, 10));
			setTexture(new Texture("sprite_robot1.png"));
			animFrame = 0;
			
			jumpSound.play();
		}
	}
	
	public void notifyLanded() {
		if (jumping) {
			jumping = false;
			
			landSound.play();
		}
	}
	
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
