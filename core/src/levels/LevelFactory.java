package levels;

import java.util.HashMap;
import java.util.Map;

public class LevelFactory 
{
	public static final Map<Integer, Class<? extends Level>> LEVELS;
	static
	{
		LEVELS = new HashMap<Integer, Class<? extends Level>>();
		LEVELS.put(1, Level1.class);
		LEVELS.put(2, Level2.class);
		LEVELS.put(3, Level3.class);
		LEVELS.put(4, Level4.class);
	}
	
	private static Level currentlyLoadedLevel = null;
	private static int currentlyLoadedLevelIndex = 0;
	
	public static Level loadLevel(int level)
	{
		try 
		{
			unload();
			currentlyLoadedLevelIndex = level;
			currentlyLoadedLevel = LEVELS.get(level).newInstance();
			return currentlyLoadedLevel;
		} 
		catch(InstantiationException | IllegalAccessException e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	public static Level nextLevel()
	{
		if(currentlyLoadedLevelIndex < LEVELS.size())
		{
			return loadLevel(++currentlyLoadedLevelIndex);
		}
		else
		{
			throw new RuntimeException("No such level. Index: " + ++currentlyLoadedLevelIndex);
		}
	}
	
	public static Level restartLevel()
	{
		return loadLevel(currentlyLoadedLevelIndex);
	}
	
	public static Level getLevel()
	{
		return currentlyLoadedLevel;
	}
	
	public static void unload()
	{
		if(currentlyLoadedLevel != null)
		{
			currentlyLoadedLevel.dispose();
			currentlyLoadedLevel = null;
		}
	}
}
