package levels;

import java.util.HashMap;
import java.util.Map;

public class Level1 extends Level
{
	private static final Map<String, String> NOTIFICATIONS;
	static
	{
		NOTIFICATIONS = new HashMap<String, String>();
		NOTIFICATIONS.put("First", "Use arrow keys to move around.");
		NOTIFICATIONS.put("Second", "Reach the exit!");
		NOTIFICATIONS.put("Exit", "Press spacebar to enter.");
	}
	
	public Level1() 
	{
		super("true_assets/levels/1.tmx", new int[]{5, 6, 7}, new int[]{8, 9}, NOTIFICATIONS);
	}
}
