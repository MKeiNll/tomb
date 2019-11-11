package entities;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import main.Tomb;
import main.Tomb.UserData;
import ui.HUD;
import ui.PopUpMessage;

public class LevelExit extends Sprite implements Disposable
{
	public static final String MESSAGE = "Great! The door is opened now.";
	public static final float WIDTH = Tomb.PPT / Tomb.PPM;
	public static final float HEIGHT = Tomb.PPT * 2 / Tomb.PPM;
	
	public static final String ATLAS = "true_assets/atlases/door.atlas";
	public static final String CLOSED_REGION = "door_closed";
	public static final String OPEN_REGION = "door_open";

	private TextureAtlas atlas;
	private AtlasRegion openRegion;
	private boolean locked;
	private PopUpMessage message;
	
	public LevelExit(Body body, boolean locked)
	{
		this.locked = locked;
		message = new PopUpMessage(HUD.MAIN_FONT, MESSAGE);
		body.getFixtureList().get(0).setUserData(UserData.LEVEL_EXIT);
		
		atlas = new TextureAtlas(ATLAS);
		Array<AtlasRegion> closedRegions = atlas.findRegions(CLOSED_REGION);
		Array<AtlasRegion> openRegions = atlas.findRegions(OPEN_REGION);
		openRegion = openRegions.get(ThreadLocalRandom.current().nextInt(0, openRegions.size));
		setBounds(body.getPosition().x - WIDTH / 2, body.getPosition().y - HEIGHT / 2, WIDTH, HEIGHT);
		if(locked)
		{
			setRegion(closedRegions.get(ThreadLocalRandom.current().nextInt(0, closedRegions.size)));
		}
		else
		{
			setRegion(openRegion);
		}
	}
	
	public void render(Player player, SpriteBatch spriteBatch)
	{
		spriteBatch.setProjectionMatrix(player.getCamera().combined);
		spriteBatch.begin();
		draw(spriteBatch);
		spriteBatch.end();
	}
	
	public void unlock()
	{
		if(locked)
		{
			locked = false;
			setRegion(openRegion);
			message.pop();
		}
	}
	
	public boolean isLocked()
	{
		return locked;
	}

	@Override
	public void dispose() 
	{
		message.removeInstantly();
		atlas.dispose();
	}
}
