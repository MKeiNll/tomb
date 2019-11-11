package levels;

import java.util.HashMap;
import java.util.Map;

public class Level2 extends Level
{	
	private static final Map<String, String> NOTIFICATIONS;
	static
	{
		NOTIFICATIONS = new HashMap<String, String>();
		NOTIFICATIONS.put("first", "The exit is closed in some levels. In order to unlock it you need to collect all 4 gems!");
	}
	
	public Level2() 
	{
		super("true_assets/levels/2.tmx", new int[]{0, 1, 2}, new int[]{3, 4}, NOTIFICATIONS);
	}
}
