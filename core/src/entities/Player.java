package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import main.Tomb;
import screens.GameplayScreen;
import ui.HUD;

public class Player extends Sprite implements Disposable
{
	public static final int INITIAL_HEALTH = 6;
	public static final int MAX_HEALTH = 6;
	
	public static final float BODY_FRICTION = 0;
	public static final float BODY_RESTITUTION = 0;
	public static final float FEET_FRICTION = 1;
	public static final float FEET_RESTITUTION = 0;

	public static final float HORIZONTAL_SPEED = 1.2f;
	public static final float IN_AIR_HORIZONTAL_SPEED = 0.6f;
	public static final float JUMP_POWER = 9f;
	public static final float MAX_HORIZONTAL_VELOCITY = 3.2f;
	
	public static final float BODY_WIDTH = 60;
	public static final float BODY_HEIGHT = 92;
	public static final float BODY_OFFSET_Y = -3;
	public static final float FEET_WIDTH = 58;
	public static final float FEET_HEIGHT = 2;
	
	public static final String TEXTURE_ATLAS = "true_assets/atlases/p1.atlas";
	public static final String WALK_REGION = "walk";
	public static final String CLIMB_REGION = "climb";
	public static final String STAND_REGION = "stand";
	public static final String JUMP_REGION = "jump";
	public static final String HURT_REGION = "hurt";
	
	public static final float RUN_ANIMATION_SPEED = 0.05f;
	public static final float CLIMB_ANIMATION_SPEED = 0.2f;
	
	public enum State
	{
		STANDING, JUMPING, RUNNING, PROCESSING_TO_NEXT_LEVEL
	}
	
	private GameplayScreen gameplayScreen;
	
	private State currentState;
	private State previousState;
	private float stateTimer;
	
	private boolean runningLeft;
	private boolean inAir;
	private boolean processingToNextLevel;
	
	private OrthographicCamera camera; 
	private Body body;
	
	private TextureAtlas atlas;
	private TextureRegion stand;
	private TextureRegion jump;
	private Animation walk;
	private Animation climb;
	private float alpha;
	
	private int health;
	
	public Player(GameplayScreen gameplayScreen, World world, Vector2 spawnCoordinates)
	{
		this.gameplayScreen = gameplayScreen;
		
		atlas = new TextureAtlas(TEXTURE_ATLAS);
		stand = atlas.findRegion(STAND_REGION);
		jump = atlas.findRegion(JUMP_REGION);
		walk = new Animation(RUN_ANIMATION_SPEED, atlas.findRegions(WALK_REGION));
		climb = new Animation(CLIMB_ANIMATION_SPEED, atlas.findRegions(CLIMB_REGION));
		alpha = 1;
		
		setBounds(0, 0, 100 / Tomb.PPM, 100 / Tomb.PPM);
		
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		
		runningLeft = false;
		processingToNextLevel = false;
		
		camera = new OrthographicCamera();
		
		spawn(world, spawnCoordinates);
	}
	
	public void handleInput(float delta, boolean nearOpenedExit)
	{			
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && nearOpenedExit)
		{
			processingToNextLevel = true; 
		}
		if(!processingToNextLevel)
		{
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && !inAir)
			{
				body.applyLinearImpulse(new Vector2(0, JUMP_POWER), body.getWorldCenter(), true);
			}
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && body.getLinearVelocity().x <= MAX_HORIZONTAL_VELOCITY)
			{
				if(inAir)
				{
					body.applyLinearImpulse(new Vector2(IN_AIR_HORIZONTAL_SPEED, 0), body.getWorldCenter(), true);
				}
				else
				{
					body.applyLinearImpulse(new Vector2(HORIZONTAL_SPEED, 0), body.getWorldCenter(), true);
				}
			}
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && body.getLinearVelocity().x >= -MAX_HORIZONTAL_VELOCITY)
			{
				if(inAir)
				{
					body.applyLinearImpulse(new Vector2(-IN_AIR_HORIZONTAL_SPEED, 0), body.getWorldCenter(), true);
				}
				else
				{
					body.applyLinearImpulse(new Vector2(-HORIZONTAL_SPEED, 0), body.getWorldCenter(), true);
				}
			}
		}
	}
	
	public void updateAndRender(float delta, Vector2 mapSize, SpriteBatch spriteBatch, boolean inAir, boolean nearOpenedExit)
	{	
		this.inAir = inAir;
		
		if(health == 0)
		{
			gameplayScreen.restartLevel();
		}
		
		handleInput(delta, nearOpenedExit);	
		
		if(body.getPosition().x < camera.viewportWidth / 2)
		{
			camera.position.x = camera.viewportWidth / 2;
		}
		else if(body.getPosition().x > mapSize.x - (camera.viewportWidth / 2))
		{
			camera.position.x =  mapSize.x - (camera.viewportWidth / 2);
		}
		else
		{
			camera.position.x = body.getPosition().x;
		}
		
		if (body.getPosition().y < camera.viewportHeight / 2)
		{
			camera.position.y = camera.viewportHeight / 2;
		}
		else if(body.getPosition().y > mapSize.y - (camera.viewportHeight / 2))
		{
			camera.position.y = mapSize.y - (camera.viewportHeight / 2);
		}
		else
		{
			camera.position.y = body.getPosition().y;
		}
		camera.update();
		
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
		setRegion(manageStates(delta));
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		draw(spriteBatch);
		spriteBatch.end();
	}
	
	private TextureRegion manageStates(float delta)
	{
		TextureRegion textureRegion;
		if(processingToNextLevel)
		{
			alpha -= 0.6f * delta;
			currentState = State.PROCESSING_TO_NEXT_LEVEL;
			textureRegion = climb.getKeyFrame(stateTimer, true);
			if(alpha > 0)
			{
				setAlpha(alpha);
			}
			else
			{
				processingToNextLevel = false;
				gameplayScreen.switchToNextLevel();
			}
		}
		else if(body.getLinearVelocity().x != 0 && !inAir)
		{
			currentState = State.RUNNING;
			textureRegion = walk.getKeyFrame(stateTimer, true);
		}
		else if(body.getLinearVelocity().y != 0)
		{
			currentState = State.JUMPING;
			textureRegion = jump;
		}
		else
		{
			currentState = State.STANDING;
			textureRegion = stand;
		}

		if((body.getLinearVelocity().x < 0 || runningLeft) && !textureRegion.isFlipX())
		{
			textureRegion.flip(true, false);
			runningLeft = true;
		}
		else if((body.getLinearVelocity().x > 0 || !runningLeft) && textureRegion.isFlipX())
		{
			textureRegion.flip(true, false);
			runningLeft = false;
		} 
		
		if(currentState == previousState)
		{
			stateTimer += delta;
		}
		else
		{
			stateTimer = 0;
		}
		
		previousState = currentState;
		return textureRegion;
	}
	
	public void spawn(World world, Vector2 spawnCoordinates)
	{
		//Define main collider 
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(spawnCoordinates);
		bodyDef.type = BodyType.DynamicBody;
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.filter.maskBits = Tomb.PLATFORM_BELOW | Tomb.DEFAULT_BIT;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(BODY_WIDTH / 2 / Tomb.PPM, BODY_HEIGHT / 2 / Tomb.PPM, new Vector2(0, BODY_OFFSET_Y / Tomb.PPM), 0);
		fixtureDef.shape = shape;
		fixtureDef.friction = BODY_FRICTION;
		fixtureDef.restitution = BODY_RESTITUTION;
		body.createFixture(fixtureDef).setUserData(Tomb.UserData.PLAYER);

		//Define high-frictional feet	
		PolygonShape feetShape = new PolygonShape();
		feetShape.setAsBox(FEET_WIDTH / 2 / Tomb.PPM, FEET_HEIGHT / 2 / Tomb.PPM, new Vector2(0, (-BODY_HEIGHT / 2 + BODY_OFFSET_Y) / Tomb.PPM), 0);	
		fixtureDef.shape = feetShape;
		fixtureDef.friction = FEET_FRICTION;
		fixtureDef.restitution = FEET_RESTITUTION;
		body.createFixture(fixtureDef).setUserData(Tomb.UserData.PLAYER_FEET);
		feetShape.dispose();
		shape.dispose();
		
		alpha = 1;
		setAlpha(alpha);
		health = INITIAL_HEALTH;
		HUD.setHealth(health);
	}
	
	public void killInstantly()
	{
		HUD.setHealth(0);
		health = 0;
	}
	
	public OrthographicCamera getCamera()
	{
		return camera;
	}
	
	public boolean isInAir()
	{
		return inAir;
	}
	
	public float getLowestPoint()
	{
		return body.getPosition().y - (BODY_HEIGHT / 2 - BODY_OFFSET_Y + FEET_HEIGHT / 2) / Tomb.PPM;
	}
	
	public Vector2 getVelocity()
	{
		return body.getLinearVelocity();
	}
	
	public Vector2 getPosition()
	{
		return body.getPosition();
	}
	
	public Body getBody()
	{
		return body;
	}
	
	@Override
	public void dispose() 
	{
		atlas.dispose();
	}
}
