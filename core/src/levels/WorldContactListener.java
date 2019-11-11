package levels;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entities.Gem;
import entities.NotificationCollider;
import entities.Spring;
import main.Tomb;

public class WorldContactListener implements ContactListener
{	
	private int playerAndGroundCollisionCounter;
	private int playerAndLevelExitCollisionCounter;
	private boolean playerDead;
	
	public WorldContactListener()
	{
		playerAndGroundCollisionCounter = 0;
		playerAndLevelExitCollisionCounter = 0;
		playerDead = false;
	}
	
	@Override
	public void beginContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Object userDataA = contact.getFixtureA().getUserData();
		Object userDataB = contact.getFixtureB().getUserData();
		
		if(compareUserData(userDataA, userDataB, Tomb.UserData.GROUND, Tomb.UserData.PLAYER_FEET))
		{	
			playerAndGroundCollisionCounter++;
		} 
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER, Tomb.UserData.LEVEL_EXIT))
		{
			playerAndLevelExitCollisionCounter++;
		}
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER, Tomb.UserData.SPIKES))
		{
			playerDead = true;
		}
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER_FEET, Tomb.UserData.SPIKES))
		{
			playerDead = true;
		}
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER_FEET, Spring.class))
		{	
			Spring spring;
			Body playerBody;
			if(userDataA instanceof Spring)
			{
				spring = (Spring) userDataA;
				playerBody = fixtureB.getBody(); 
			}
			else
			{
				spring = (Spring) userDataB;
				playerBody = fixtureA.getBody(); 
			}	
			spring.throwUp(playerBody);
		} 
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER, Gem.class))
		{
			if(userDataA instanceof Gem)
			{
				((Gem) userDataA).pickedUp();
			}
			else
			{
				((Gem) userDataB).pickedUp();
			}
		}
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER, NotificationCollider.class))
		{
			if(userDataA instanceof NotificationCollider)
			{
				((NotificationCollider) userDataA).playerEntered();
			}
			else
			{
				((NotificationCollider) userDataB).playerEntered();
			}
		}
	}

	@Override
	public void endContact(Contact contact) 
	{
		Object userDataA = contact.getFixtureA().getUserData();
		Object userDataB = contact.getFixtureB().getUserData();
		
		if(compareUserData(userDataA, userDataB, Tomb.UserData.GROUND, Tomb.UserData.PLAYER_FEET))
		{	
			playerAndGroundCollisionCounter--;
		} 
		else if(compareUserData(userDataA, userDataB, Tomb.UserData.PLAYER, Tomb.UserData.LEVEL_EXIT))
		{
			playerAndLevelExitCollisionCounter--;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
	}
	
	private boolean compareUserData(Object userDataA, Object userDataB, 
			Tomb.UserData targetUserDataA, Tomb.UserData targetUserDataB)
	{	
		return (userDataA == targetUserDataA || userDataB == targetUserDataA)
				&& (userDataA == targetUserDataB  || userDataB == targetUserDataB);
	}
	
	private boolean compareUserData(Object userDataA, Object userDataB, 
			Object targetUserDataA, Class<?> targetUserDataB)
	{	
		return (userDataA == targetUserDataA || userDataB == targetUserDataA)
				&& (targetUserDataB.isInstance(userDataA)  || targetUserDataB.isInstance(userDataB));
	}
	
	public boolean playerDead()
	{
		return playerDead;
	}

	public boolean playerInAir()
	{
		if(playerAndGroundCollisionCounter == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean playerNearDoor()
	{
		if(playerAndLevelExitCollisionCounter == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
