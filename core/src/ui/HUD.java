package ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import entities.Gem;
import main.Tomb;

public class HUD
{
	public static final String MAIN_FONT_PATH = "true_assets/fonts/kenpixel_mini_square.ttf";
	public static final BitmapFont MAIN_FONT;
	
	public static final String HEALTH_ATLAS = "true_assets/atlases/hud/hud_health.atlas";
	public static final String FULL_HEART_REGION = "hud_heartFull";
	public static final String HALF_HEART_REGION = "hud_heartHalf";
	public static final String EMPTY_HEART_REGION = "hud_heartEmpty";
	public static final float HEART_SCALE = 2.5f;
	public static final float HEART_WIDTH = 53 * HEART_SCALE;
	public static final float HEALTH_OFFSET_X = 50;
	public static final float HEALTH_POS_Y = 1300;
	public static final float HEALTH_PADDING = 20;
	
	public static final String GEMS_ATLAS = "true_assets/atlases/hud/hud_gems.atlas";
	public static final String ORANGE_GEM_REGION = "hud_gem_red";
	public static final String GREEN_GEM_REGION = "hud_gem_green";
	public static final String BLUE_GEM_REGION = "hud_gem_blue";
	public static final String YELLOW_GEM_REGION = "hud_gem_yellow";
	public static final float GEM_SCALE = 2;
	public static final float GEM_WIDTH = 46 * GEM_SCALE;
	public static final float GEMS_OFFSET_X = 50;
	public static final float GEMS_POS_Y = 1200;
	public static final float GEM_PADDING = 20;
	
	private static Viewport viewport;
	private static Stage stage;
	
	private static TextureAtlas healthAtlas;
	private static List<Image> currentlyShownHearts;
	private static AtlasRegion fullHeart;
	private static AtlasRegion halfHeart;
	private static AtlasRegion emptyHeart;
	private static float[] heartXPositions;
	
	private static TextureAtlas gemAtlas;
	private static Image gemOrange;
	private static Image gemGreen;
	private static Image gemBlue;
	private static Image gemYellow;
	private static float[] gemXPositions;
	private static int gemsCollected;
	
	static
	{
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(MAIN_FONT_PATH));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 50;
		parameter.color = Color.WHITE;
		MAIN_FONT = generator.generateFont(parameter); 
		
		viewport = new FitViewport(Tomb.VIEWPORT_WIDTH, Tomb.VIEWPORT_HEIGHT, new OrthographicCamera());
		stage = new Stage(viewport, new SpriteBatch());	
		
		//Initialize gems
		gemAtlas = new TextureAtlas(GEMS_ATLAS);
		gemOrange = new Image(gemAtlas.findRegion(ORANGE_GEM_REGION));
		gemOrange.setScale(GEM_SCALE);
		gemGreen = new Image(gemAtlas.findRegion(GREEN_GEM_REGION));
		gemGreen.setScale(GEM_SCALE);
		gemBlue = new Image(gemAtlas.findRegion(BLUE_GEM_REGION));
		gemBlue.setScale(GEM_SCALE);
		gemYellow = new Image(gemAtlas.findRegion(YELLOW_GEM_REGION));
		gemYellow.setScale(GEM_SCALE);
		gemsCollected = 0;
		gemXPositions = new float[] 
				{
					GEMS_OFFSET_X, 
					GEMS_OFFSET_X + GEM_PADDING + GEM_WIDTH, 
					GEMS_OFFSET_X + (GEM_PADDING + GEM_WIDTH) * 2,
					GEMS_OFFSET_X + (GEM_PADDING + GEM_WIDTH) * 3
				};
		
		//Initialize health
		healthAtlas = new TextureAtlas(HEALTH_ATLAS);
		currentlyShownHearts = new ArrayList<Image>();
		fullHeart = healthAtlas.findRegion(FULL_HEART_REGION);
		halfHeart = healthAtlas.findRegion(HALF_HEART_REGION);
		emptyHeart = healthAtlas.findRegion(EMPTY_HEART_REGION);
		heartXPositions = new float[] 
				{
					HEALTH_OFFSET_X, 
					HEALTH_OFFSET_X + HEALTH_PADDING + HEART_WIDTH, 
					HEALTH_OFFSET_X + (HEALTH_PADDING + HEART_WIDTH) * 2,
				};
	}
	
	public static void addActor(Actor actor)
	{
		stage.addActor(actor);
	}
	
	public static void addGem(Gem.GemColor color)
	{
		switch(color)
		{
			case ORANGE:
				gemOrange.setPosition(gemXPositions[gemsCollected], GEMS_POS_Y);
				stage.addActor(gemOrange);
				break;
			case GREEN:
				gemGreen.setPosition(gemXPositions[gemsCollected], GEMS_POS_Y);
				stage.addActor(gemGreen);
				break;
			case BLUE:
				gemBlue.setPosition(gemXPositions[gemsCollected], GEMS_POS_Y);
				stage.addActor(gemBlue);
				break;
			case YELLOW:
				gemYellow.setPosition(gemXPositions[gemsCollected], GEMS_POS_Y);
				stage.addActor(gemYellow);
				break;
		}
		gemsCollected++;
	}
	
	public static void setHealth(int health)
	{
		for(Image image : currentlyShownHearts)
		{
			image.remove();
		}
		currentlyShownHearts.clear();
		
		switch(health)
		{
			case 0:
				currentlyShownHearts.add(new Image(emptyHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				break;
			case 1:
				currentlyShownHearts.add(new Image(halfHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				break;
			case 2:
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				break;
			case 3:
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(halfHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				break;
			case 4:
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(emptyHeart));
				break;
			case 5:
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(halfHeart));
				break;
			case 6:
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(fullHeart));
				currentlyShownHearts.add(new Image(fullHeart));
				break;
		}
		
		for(int i = 0; i < currentlyShownHearts.size(); i++)
		{
			Image image = currentlyShownHearts.get(i);
			image.setPosition(heartXPositions[i], HEALTH_POS_Y);
			image.setScale(HEART_SCALE);
			stage.addActor(image);
		}
	}
	
	public static void render(float delta, SpriteBatch spriteBatch)
	{
		spriteBatch.setProjectionMatrix(stage.getCamera().combined);
		spriteBatch.begin();
		MAIN_FONT.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 50, 50); 
		spriteBatch.end();
		stage.act(delta);
		stage.draw();
	}
	
	public static void clearGems()
	{
		gemsCollected = 0;
		gemOrange.remove();
		gemGreen.remove();
		gemBlue.remove();
		gemYellow.remove();
	}
	
	public static int getGemsCollected()
	{
		return gemsCollected;
	}
	
	public static void dispose()
	{
		stage.dispose();
		gemAtlas.dispose();
		healthAtlas.dispose();
	}
}
