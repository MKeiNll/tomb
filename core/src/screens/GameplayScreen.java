package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import entities.Player;
import levels.Level;
import levels.LevelFactory;
import main.Tomb;
import ui.HUD;

public class GameplayScreen implements Screen
{	
	private Level currentLevel;
	private boolean switchToNextLevel;
	private boolean restartLevel;
	
	private SpriteBatch spriteBatch;
	private Player player;	
	private Viewport viewport;
	
	public GameplayScreen(SpriteBatch spriteBatch)
	{		
		this.spriteBatch = spriteBatch;
		currentLevel = LevelFactory.loadLevel(1);

		player = new Player(this, currentLevel.getWorld(), currentLevel.getSpawnCoordinates());		
		viewport = new FitViewport(Tomb.VIEWPORT_WIDTH / Tomb.PPM, Tomb.VIEWPORT_HEIGHT / Tomb.PPM, player.getCamera()); 
		
		switchToNextLevel = false;
		restartLevel = false;
	}
	
	public void switchToNextLevel()
	{
		switchToNextLevel = true;
	}
	
	public void restartLevel()
	{
		restartLevel = true;
	}
	
	@Override
	public void show() 
	{
	}

	@Override
	public void render(float delta) 
	{			
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if(switchToNextLevel)
		{
			currentLevel = LevelFactory.nextLevel();
			player.spawn(currentLevel.getWorld(), currentLevel.getSpawnCoordinates());
			switchToNextLevel = false;
		}
		else if(restartLevel)
		{
			currentLevel = LevelFactory.restartLevel();
			player.spawn(currentLevel.getWorld(), currentLevel.getSpawnCoordinates());
			restartLevel = false;
		}
		
		currentLevel.render(player, delta, spriteBatch);
		HUD.render(delta, spriteBatch);
	}

	@Override
	public void resize(int width, int height) 
	{	
		viewport.update(width, height);
	}

	@Override
	public void pause() 
	{
	}

	@Override
	public void resume() 
	{	
	}

	@Override
	public void hide() 
	{
	}

	@Override
	public void dispose() 
	{	
		HUD.dispose();
		player.dispose();
		LevelFactory.unload();
	}
}
