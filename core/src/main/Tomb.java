package main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import screens.GameplayScreen;

public class Tomb extends Game 
{	
	public static final int VIEWPORT_WIDTH = 2400;
	public static final int VIEWPORT_HEIGHT = 1440;
	public static final float PPM = 100; //Pixels per meter
	public static final float PPT = 70; //Pixels per tile
	
	public static final short DEFAULT_BIT = 1;
	public static final short PLATFORM_BELOW = 2;
	public static final short PLATFORM_ABOVE = 4;
	
	public enum UserData
	{
		GROUND, PLAYER_FEET, PLAYER, LEVEL_EXIT, SPIKES
	}
	
	private SpriteBatch spriteBatch;
	private GameplayScreen gameplayScreen;
	
	@Override
	public void create() 
	{	
		spriteBatch = new SpriteBatch();
		gameplayScreen = new GameplayScreen(spriteBatch);
		setScreen(gameplayScreen);
	}
	
	@Override
	public void dispose()
	{
		gameplayScreen.dispose();
		spriteBatch.dispose();
	}
	
	public SpriteBatch getSpriteBatch()
	{
		return spriteBatch;	
	}
	
	//TODO
	
	// Refactoring (Carefully go through all code, simplify it, make it more clear, add comments to it in hard places)
		//In some places it's ugly af
	
	// UI, such as pause menu, main menu, level select menu; at least some basic logic
	
	// Level 5
	
	// Level 6
	
	// Level 7
}
