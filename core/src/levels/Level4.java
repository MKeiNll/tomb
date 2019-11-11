package levels;
import java.util.HashMap;
import java.util.Map;

public class Level4 extends Level
{
	private static final Map<String, String> NOTIFICATIONS;
	static
	{
		NOTIFICATIONS = new HashMap<String, String>();
		NOTIFICATIONS.put("f", "Are these spikes made of steel? They look 100% deadly in any case.");
	}
	
	public Level4() 
	{
		super("true_assets/levels/4.tmx", new int[]{0, 1, 2}, new int[]{3, 4}, NOTIFICATIONS);
	}
}
