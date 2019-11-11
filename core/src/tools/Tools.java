package tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import main.Tomb;

public class Tools 
{
	public static void fixBleeding(TextureRegion textureRegion) 
	{
		float fix = 0.01f;
	    float x = textureRegion.getRegionX();
	    float y = textureRegion.getRegionY();
	    float width = textureRegion.getRegionWidth();
	    float height = textureRegion.getRegionHeight();
	    float invTexWidth = 1f / textureRegion.getTexture().getWidth();
	    float invTexHeight = 1f / textureRegion.getTexture().getHeight();
	    textureRegion.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight); // Trims	                              
	}
	
	public static void fixBleeding(TiledMapTileSets tileSets) 
	{
		for(TiledMapTileSet tileSet : tileSets)
		{
			for(TiledMapTile tile : tileSet)
			{
				fixBleeding(tile.getTextureRegion());
			}
		}
	}
	
	public static boolean isInside(float boxX, float boxY, float boxWidth, float boxHeight, float pointX, float pointY)
	{
		return boxX < pointX && boxY < pointY && boxX + boxWidth > pointX && boxY + boxHeight > pointY;
	}
	
	public static float getHighestPoint(Fixture fixture)
	{
		Shape shape = fixture.getShape();
		float bodyPosY = fixture.getBody().getPosition().y;
		
		float highestPoint;
		if(shape instanceof ChainShape)
		{
			Vector2 vertex = new Vector2();
			((ChainShape) shape).getVertex(0, vertex);
			float worldVertexY = vertex.y + bodyPosY;	
			
			highestPoint = worldVertexY;		
			for(int i = 1; i < ((ChainShape) shape).getVertexCount(); i++) 
			{
				((ChainShape) shape).getVertex(i, vertex); 
				worldVertexY = vertex.y + bodyPosY;	
			    if(worldVertexY > highestPoint)
			    {
			    	highestPoint = worldVertexY;
			    }
			}
		}
		else if(shape instanceof PolygonShape)
		{
			Vector2 vertex = new Vector2();
			((PolygonShape) shape).getVertex(0, vertex);
			float worldVertexY = vertex.y + bodyPosY;	
			
			highestPoint = worldVertexY;		
			for(int i = 1; i < ((PolygonShape) shape).getVertexCount(); i++) 
			{
				((PolygonShape) shape).getVertex(i, vertex); 
				worldVertexY = vertex.y + bodyPosY;	
			    if(worldVertexY > highestPoint)
			    {
			    	highestPoint = worldVertexY;
			    }
			}
		}
		else
		{
			throw new RuntimeException("No such shape. Shape: " + shape.toString());
		}
		return highestPoint;
	}
	
	public static Body mapObjectToBody(MapObject object, World world, float friction, BodyType bodyType, boolean isSensor)
	{
		BodyDef bodyDef = new BodyDef(); 
		Shape shape;
		FixtureDef fixtureDef = new FixtureDef();
		Body body;
		
		if(object instanceof PolygonMapObject)
		{
			Polygon polygon = ((PolygonMapObject) object).getPolygon();
			bodyDef.type = bodyType;
			bodyDef.position.set(polygon.getX() / Tomb.PPM, polygon.getY() / Tomb.PPM);
			body = world.createBody(bodyDef);	
			float[] vertices = polygon.getVertices();
			for(int i = 0; i < vertices.length; i++)
			{
				vertices[i] /= (Tomb.PPM);
			}
			shape = new ChainShape();
			((ChainShape) shape).createChain(vertices);
		}
		else if(object instanceof EllipseMapObject)
		{
			Ellipse ellipse = ((EllipseMapObject) object).getEllipse(); 
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(ellipse.x / Tomb.PPM, ellipse.y / Tomb.PPM);
			body = world.createBody(bodyDef);
			shape = new CircleShape();
			shape.setRadius(ellipse.width / 2 / Tomb.PPM);
		}
		else if(object instanceof RectangleMapObject)
		{
			Rectangle rectangle = ((RectangleMapObject) object).getRectangle(); 
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set((rectangle.x + rectangle.getWidth() / 2) / Tomb.PPM, (rectangle.y + rectangle.getHeight() / 2) / Tomb.PPM);
			body = world.createBody(bodyDef);
			shape = new PolygonShape();
			((PolygonShape) shape).setAsBox(rectangle.getWidth() / 2 / Tomb.PPM, rectangle.getHeight() / 2 / Tomb.PPM);
		}
		else
		{
			throw new RuntimeException("No such MapObject. MapObject: " + object.toString());
		}
		fixtureDef.shape = shape;
		fixtureDef.isSensor = isSensor;
		fixtureDef.friction = friction;
		
		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}
}
