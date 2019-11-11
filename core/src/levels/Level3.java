package levels;

import java.util.HashMap;
import java.util.Map;

public class Level3 extends Level
{	
	private static final Map<String, String> NOTIFICATIONS;
	static
	{
		NOTIFICATIONS = new HashMap<String, String>();
		NOTIFICATIONS.put("f", "Giant springboards are fun.");
	}
	
	public Level3() 
	{
		super("true_assets/levels/3.tmx", new int[]{0, 1}, new int[]{2, 3}, NOTIFICATIONS);
	}
}
