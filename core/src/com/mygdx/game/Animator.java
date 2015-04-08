package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 
 * 
 * HOW TO SPRITE
 *
 */
public class Animator{
	
	private static final int        FRAME_COLS = 6;
    private static final int        FRAME_ROWS = 5;
	
    Animation walkAnimation;
	Texture walkSheet;
	TextureRegion[] walkFrames;
	TextureRegion currFrame;
	SpriteBatch batch;
	
	float stateTime;
	
	

	public void create() {
		walkSheet = new Texture(Gdx.files.internal("sprite-animation4.png"));
		TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);
		walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		
		int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        
        walkAnimation = new Animation(0.025f, walkFrames);      
        batch = new SpriteBatch();                
        stateTime = 0f;
		
	}

	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	public void render() {
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);                        
        stateTime += Gdx.graphics.getDeltaTime();         
        currFrame = walkAnimation.getKeyFrame(stateTime, true); 
        batch.begin();
        batch.draw(currFrame, 50, 50);            
        batch.end();
		
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void resume() {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
