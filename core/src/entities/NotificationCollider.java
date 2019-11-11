package entities;

import com.badlogic.gdx.physics.box2d.Fixture;

import ui.HUD;
import ui.PopUpMessage;

public class NotificationCollider
{		
	private PopUpMessage popUpMessage;
	private boolean playerEntered;
	private boolean popped;
	
	public NotificationCollider(Fixture fixture, String message)
	{
		fixture.setUserData(this);
		popUpMessage = new PopUpMessage(HUD.MAIN_FONT, message);
		playerEntered = false;
		popped = false;
	}
	
	public void update(float playerX, float playerY)
	{
		if(playerEntered)
		{
			if(!PopUpMessage.popUpMessageIsShown && !popped)
			{
				popUpMessage.pop();
				popped = true;
			}
		}
	}
	
	public void playerEntered()
	{
		playerEntered = true;
	}
	
	public void remove()
	{
		popUpMessage.removeInstantly();
	}
}
