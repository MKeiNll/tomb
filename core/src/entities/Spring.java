package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import main.Tomb;
import tools.Tools;

public class Spring extends Sprite
{
	public static final float UP_TIME = 0.2f;
	public static final String ATLAS = "true_assets/atlases/spring.atlas";
	public static final String SPRING_UP = "springboardUp";
	public static final String SPRING_DOWN = "springboardDown";
	
	private Fixture fixture;
	private Filter filter;
	
	private float highestPoint;
	
	private TextureAtlas atlas;
	private TextureRegion springDown;
	private TextureRegion springUp;
	private Timer timer;
	
	private float power;
	
	public Spring(Body body, float power) 
	{
		fixture = body.getFixtureList().get(0);
		fixture.setUserData(this);
		filter = new Filter();
		this.power = power;

		highestPoint = Tools.getHighestPoint(fixture);
		timer = new Timer();
		
		atlas = new TextureAtlas(ATLAS);
		springDown = atlas.findRegion(SPRING_DOWN);
		springUp = atlas.findRegion(SPRING_UP);
		
		setRegion(springDown);
		setBounds(body.getPosition().x - springDown.getRegionWidth() / 2 / Tomb.PPM, body.getPosition().y - springDown.getRegionHeight() / 4 / Tomb.PPM, 
				springDown.getRegionWidth() / Tomb.PPM, springDown.getRegionHeight() / Tomb.PPM);
	}
	
	public void throwUp(Body body)
	{
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
		body.applyLinearImpulse(new Vector2(0, power), body.getWorldCenter(), true);
		
		setRegion(springUp);
		timer.scheduleTask(new Task()
		{
			@Override
			public void run() 
			{
				setRegion(springDown);
			}
		}, UP_TIME);
	}
	
	public void updateAndRender(Player player, SpriteBatch spriteBatch)
	{
		if(player.getLowestPoint() > highestPoint)
		{
			filter.categoryBits = Tomb.PLATFORM_BELOW;
		}
		else
		{
			filter.categoryBits = Tomb.PLATFORM_ABOVE;
		}
		fixture.setFilterData(filter);
		
		spriteBatch.setProjectionMatrix(player.getCamera().combined);
		spriteBatch.begin();
		draw(spriteBatch);
		spriteBatch.end();
	}
	
	public Body getbody()
	{
		return fixture.getBody();		
	}
}
