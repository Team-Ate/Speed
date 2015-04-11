package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a game being played.
 * 
 * @author TeamAte
 *
 */
public class SpeedGame extends Game {
	public static SpriteBatch batch;
	public static BitmapFont font;
	
	/**
	 * Initalizes a new game.
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		this.setScreen(new MainMenuScreen(this));
	}
	
	/**
	 * Displays the game on screen.
	 */
	@Override
	public void render () {
		super.render();
	}
	
	/**
	 * Ends the game.
	 */
	public void dispose(){
		batch.dispose();
		font.dispose();
	}
}
