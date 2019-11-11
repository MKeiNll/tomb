package entities;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Disposable;

import main.Tomb;
import ui.HUD;

public class Gem extends Sprite implements Disposable
{
	public static final String GEMS_ATLAS = "true_assets/atlases/gems.atlas";
	public static final String ORANGE_GEM_REGION = "gemRed";
	public static final String GREEN_GEM_REGION = "gemGreen";
	public static final String BLUE_GEM_REGION = "gemBlue";
	public static final String YELLOW_GEM_REGION = "gemYellow";
	
	public static final float GEM_SIZE = 100;
	
	public static final float SWING_SPEED = 0.2f;
	public static final int AMPLITUDE = 8;
	
	public enum GemColor
	{
		ORANGE, GREEN, BLUE, YELLOW
	}
	
	private TextureAtlas atlas = new TextureAtlas(GEMS_ATLAS);
	private GemColor color;
	private Body body;
	private boolean pickedUp;
	
	private Vector2 origin;
	private float movementDelta;
	
	public Gem(Body body, GemColor color)
	{
		pickedUp = false;
		this.body = body;
		origin = new Vector2(body.getPosition());
		body.setTransform(origin.x, (float) (origin.y + ThreadLocalRandom.current().nextInt(-AMPLITUDE, AMPLITUDE + 1) / Tomb.PPM), 0); // To make gems swing independently 
		movementDelta = SWING_SPEED;
		this.color = color;
		Fixture fixture = body.getFixtureList().get(0);
		fixture.setUserData(this);
		switch(color)
		{
			case ORANGE:
				setRegion(atlas.findRegion(ORANGE_GEM_REGION));
				break;
			case GREEN:
				setRegion(atlas.findRegion(GREEN_GEM_REGION));
				break;
			case BLUE:
				setRegion(atlas.findRegion(BLUE_GEM_REGION));
				break;
			case YELLOW:
				setRegion(atlas.findRegion(YELLOW_GEM_REGION));
				break;
		}
		setBounds(fixture.getBody().getPosition().x - GEM_SIZE / 2 / Tomb.PPM, 
				fixture.getBody().getPosition().y - GEM_SIZE / 2 / Tomb.PPM, 
				GEM_SIZE / Tomb.PPM, GEM_SIZE / Tomb.PPM);
	}
	
	public void updateAndRender(Player player, float delta, SpriteBatch spriteBatch)
	{
		if(!pickedUp)
		{
			if(body.getPosition().y > origin.y + AMPLITUDE / Tomb.PPM)
			{
				movementDelta = -SWING_SPEED;
			}
			else if(body.getPosition().y < origin.y - AMPLITUDE / Tomb.PPM)
			{
				movementDelta = SWING_SPEED;
			}	
			body.setTransform(body.getPosition().x, body.getPosition().y + movementDelta * delta, 0);
		
			setPosition(body.getPosition().x - GEM_SIZE / 2 / Tomb.PPM, 
					body.getPosition().y - GEM_SIZE / 2 / Tomb.PPM);
			spriteBatch.setProjectionMatrix(player.getCamera().combined);
			spriteBatch.begin();
			draw(spriteBatch);
			spriteBatch.end();
		}
	}
	
	public void pickedUp()
	{
		if(!pickedUp)
		{
			HUD.addGem(color);
			pickedUp = true;
		}
	}
	
	public GemColor getGemColor()
	{
		return color;
	}

	@Override
	public void dispose() 
	{
		atlas.dispose();
	}
}
