package levels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import entities.Gem;
import entities.Gem.GemColor;
import entities.LevelExit;
import entities.NotificationCollider;
import entities.OneWayPlatform;
import entities.Player;
import entities.Spring;
import main.Tomb;
import main.Tomb.UserData;
import tools.Tools;
import ui.HUD;

public abstract class Level implements Disposable 
{
	public static final float GROUND_FRICTION = 1;
	public static final float GRAVITY = -20;
	public static final float PHYSICS_STEP_TIME = 1.0f / 300.0f;
	public static final int PHYSICS_VELOCITY_ITERATIONS = 6;
	public static final int PHYSICS_POSITION_ITERATIONS = 2;
	
	//Object layers
	public static final String SPAWN_LAYER = "start"; //A layer which contains player's spawn point
	public static final String EXIT_LAYER = "exit"; //A layer which contains level exit
	public static final String NOTIFICATION_LAYER = "notifications"; //A layer which contains notification colliders
	public static final String PLATFORM_LAYER = "platforms"; //A layer which contains one-way platforms
	public static final String GROUND_LAYER = "ground"; //A layer which contains solid ground colliders
	public static final String GEM_LAYER = "gems"; //A layer which contains 4 different color gem objects
	public static final String SPRING_LAYER = "springs"; //A layer which contains 4 different color gem objects
	public static final String SPIKE_LAYER = "spikes"; //A layer which contains 4 different color gem objects
	
	private WorldContactListener contactListener;
	private World world;
    private float physicsTimeLeft;
	
	private Vector2 spawnCoordinates;
	private int[] backgroundLayers;
	private int[] foregroundLayers;
	
	private TmxMapLoader tmxMapLoader;
	private TiledMap map;
	
	private OrthogonalTiledMapRenderer mapRenderer;
	private Box2DDebugRenderer box2DRenderer;
	
	private Vector2 mapSize;
	
	private List<OneWayPlatform> oneWayPlatforms;
	private List<NotificationCollider> notifications;
	private List<Gem> gems;
	private List<Spring> springs;
	private LevelExit levelExit;
	
	public Level(String filePath, int[] backgroundLayers, int[] foregroundLayers, Map<String, String> notifications)
	{		
		this.backgroundLayers = backgroundLayers;
		this.foregroundLayers = foregroundLayers;
		
		tmxMapLoader = new TmxMapLoader();
		map = tmxMapLoader.load(filePath);
		MapProperties mapProperties = map.getProperties();

		int mapWidth = mapProperties.get("width", Integer.class);
		int mapHeight = mapProperties.get("height", Integer.class);
		int tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
		int tilePixelHeight = mapProperties.get("tileheight", Integer.class);
		mapSize = new Vector2((mapWidth * tilePixelWidth) / Tomb.PPM, (mapHeight * tilePixelHeight) / Tomb.PPM);
		
		Tools.fixBleeding(map.getTileSets());
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Tomb.PPM); 
		box2DRenderer = new Box2DDebugRenderer();
		
		world = new World(new Vector2(0, GRAVITY), true);
		contactListener = new WorldContactListener();
		world.setContactListener(contactListener);
		
		MapLayers layers = map.getLayers();
		if(layers.get(GROUND_LAYER) != null)
		{
			defineGround();
		}
		if(layers.get(PLATFORM_LAYER) != null)
		{
			defineOneWayPlatforms();
		}
		if(layers.get(GEM_LAYER) != null)
		{
			defineGems();
		}	
		if(layers.get(SPRING_LAYER) != null)
		{
			defineSprings();
		}
		if(layers.get(SPIKE_LAYER) != null)
		{
			defineSpikes();
		}
		if(notifications != null)
		{
			defineNotificationColliders(notifications);
		}
		defineSpawnCoordinates();
		defineExit();
	}
	
	private void defineSpikes()
	{
		for(RectangleMapObject object : map.getLayers().get(SPIKE_LAYER).getObjects().getByType(RectangleMapObject.class))
		{		
			Tools.mapObjectToBody(object, world, GROUND_FRICTION, BodyType.StaticBody, true).getFixtureList().first().setUserData(UserData.SPIKES);
		}
	}
	
	private void defineSprings()
	{
		springs = new ArrayList<Spring>();
		for(RectangleMapObject object : map.getLayers().get(SPRING_LAYER).getObjects().getByType(RectangleMapObject.class))
		{		
			springs.add(new Spring(Tools.mapObjectToBody(object, world, GROUND_FRICTION, BodyType.StaticBody, false), Float.parseFloat(object.getName())));
		}
	}

	private void defineGround()
	{
		for(PolygonMapObject object : map.getLayers().get(GROUND_LAYER).getObjects().getByType(PolygonMapObject.class))
		{		
			Tools.mapObjectToBody(object, world, GROUND_FRICTION, BodyType.StaticBody, false).getFixtureList().first().setUserData(UserData.GROUND);
		}
	}
	
	private void defineOneWayPlatforms()
	{
		oneWayPlatforms = new ArrayList<OneWayPlatform>();
		for(PolygonMapObject object : map.getLayers().get(PLATFORM_LAYER).getObjects().getByType(PolygonMapObject.class))
		{		
			oneWayPlatforms.add(new OneWayPlatform(Tools.mapObjectToBody(object, world, GROUND_FRICTION, BodyType.StaticBody, false).getFixtureList().first()));
		}
	}
	
	private void defineNotificationColliders(Map<String, String> notifications)
	{
		this.notifications = new ArrayList<NotificationCollider>();
		for(Map.Entry<String, String> entry : notifications.entrySet())
		{
			RectangleMapObject object = (RectangleMapObject) map.getLayers().get(NOTIFICATION_LAYER).getObjects().get(entry.getKey());
			this.notifications.add(new NotificationCollider(Tools.mapObjectToBody(object, world, 0, BodyType.StaticBody, true).getFixtureList().first(), 
					entry.getValue()));
		}
	}
	
	private void defineSpawnCoordinates()
	{
		Rectangle rectangle = ((RectangleMapObject) map.getLayers().get(SPAWN_LAYER).getObjects().get(0)).getRectangle();
		spawnCoordinates = new Vector2(rectangle.getX() / Tomb.PPM, rectangle.getY() / Tomb.PPM);
	}
	
	private void defineExit()
	{
		RectangleMapObject rectangleMapObject = ((RectangleMapObject) map.getLayers().get(EXIT_LAYER).getObjects().get(0));
		
		levelExit = new LevelExit(Tools.mapObjectToBody(rectangleMapObject, world, 0, BodyType.StaticBody, true),
				gems != null);
	}
	
	private void defineGems()
	{
		gems = new ArrayList<Gem>();
		List<GemColor> colors = new ArrayList<GemColor>(Arrays.asList(GemColor.values())); 
		Collections.shuffle(colors);
		
		int i = 0;
		for(EllipseMapObject object : map.getLayers().get(GEM_LAYER).getObjects().getByType(EllipseMapObject.class))
		{				
			gems.add(new Gem(Tools.mapObjectToBody(object, world, 0, BodyType.KinematicBody, true), colors.get(i)));
			i++;
		}
	}
	
	public void render(Player player, float delta, SpriteBatch spriteBatch)
	{
		OrthographicCamera camera = player.getCamera();
		
		physicsTimeLeft += delta;
        while (physicsTimeLeft >= PHYSICS_STEP_TIME) 
        {
            world.step(PHYSICS_STEP_TIME, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
            physicsTimeLeft -= PHYSICS_STEP_TIME;
        }
        
		mapRenderer.setView(camera);			
		mapRenderer.render(backgroundLayers);
		renderEntities(player, delta, spriteBatch);
		if(contactListener.playerDead())
		{
			player.killInstantly();
		}
		player.updateAndRender(delta, mapSize, spriteBatch, contactListener.playerInAir(), contactListener.playerNearDoor() && !levelExit.isLocked());	
		mapRenderer.render(foregroundLayers);
		updateEntities(player, delta, spriteBatch);
		//box2DRenderer.render(world, camera.combined); 
	}
	
	private void updateEntities(Player player, float delta, SpriteBatch spriteBatch)
	{
		if(oneWayPlatforms != null)
		{
			for(OneWayPlatform platform : oneWayPlatforms)
			{
				platform.update(player.getLowestPoint());
			}
		}		
		if(notifications != null)
		{
			for(NotificationCollider notification : notifications)
			{
				notification.update(player.getPosition().x, player.getPosition().y);
			}
		}
	}
	
	private void renderEntities(Player player, float delta, SpriteBatch spriteBatch)
	{
		levelExit.render(player, spriteBatch);
		if(springs != null)
		{
			for(Spring spring : springs)
			{
				spring.updateAndRender(player, spriteBatch);
			}
		}
		if(gems != null)
		{
			for(Gem gem : gems)
			{
				gem.updateAndRender(player, delta, spriteBatch);
			}
			if(HUD.getGemsCollected() == 4)
			{
				levelExit.unlock();
			}
		}
	}
	
	public Vector2 getSpawnCoordinates()
	{
		return spawnCoordinates;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	@Override
	public void dispose()
	{
		map.dispose();
		mapRenderer.dispose();
		box2DRenderer.dispose();
		world.dispose();
		levelExit.dispose();
		
		if(gems != null)
		{
			HUD.clearGems();
			for(Gem gem : gems)
			{
				gem.dispose();
			}
		}
		if(notifications != null)
		{
			for(NotificationCollider notification : notifications)
			{
				notification.remove();
			}
		}
	}
}
