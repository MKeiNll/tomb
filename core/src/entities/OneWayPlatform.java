package entities;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

import main.Tomb;
import main.Tomb.UserData;
import tools.Tools;

public class OneWayPlatform 
{
	private Fixture fixture;
	private Filter filter;
	
	private float highestPoint;
	
	public OneWayPlatform(Fixture fixture)
	{
		this.fixture = fixture;
		fixture.setUserData(UserData.GROUND);
		filter = new Filter();
		
		highestPoint = Tools.getHighestPoint(fixture);
	}
	
	public void update(float playerLowestPoint)
	{
		if(playerLowestPoint > highestPoint)
		{
			filter.categoryBits = Tomb.PLATFORM_BELOW;
		}
		else
		{
			filter.categoryBits = Tomb.PLATFORM_ABOVE;
		}
		fixture.setFilterData(filter);
	}
}
