package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.FloatArray;

public class AnimatedText
{
	public static final float LINE_SPACING = 50;
	
	Vector2[] positions;	
	private Label[] chars;
	private Color color;
	
	private float posX;
	private float posY;
	
	public static final float ANIMATION_TIME = 0.5f;
	public static final float TOTAL_DELAY = 2f;
	
	private float delay;
	
	Label label;
	
	public AnimatedText(BitmapFont font, String text, float posX, float posY, float width) 
	{																	
		LabelStyle labelStyle = new LabelStyle(); 
		labelStyle.font = font;
		color = font.getColor();
		chars = new Label[text.length()];
		delay = TOTAL_DELAY / text.length();
	
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, text, font.getColor(), width, 0, true);		
		
		this.posX = posX;
		this.posY = posY;
		positions = new Vector2[text.length()];

		int positionsOffset = 0;
		for(int runIndex = 0; runIndex < layout.runs.size; runIndex++)
		{
			FloatArray advances = layout.runs.get(runIndex).xAdvances;
			
			for(int i = 0; i < layout.runs.get(runIndex).glyphs.size; i++) 
			{
			    if(i == 0) 
			    {
			        positions[0 + positionsOffset] = new Vector2(advances.get(0), runIndex * -LINE_SPACING);
			    } 
			    else 
			    {
			        positions[i + positionsOffset] = new Vector2(positions[i + positionsOffset - 1].x + advances.get(i), runIndex * -LINE_SPACING);  
			    }
			    
				//Convert string text to array of labels containing one char
				chars[i + positionsOffset] = new Label(String.valueOf(text.charAt(i + positionsOffset)), labelStyle);
				chars[i + positionsOffset].setPosition(posX + positions[i + positionsOffset].x, posY + positions[i + positionsOffset].y);
				chars[i + positionsOffset].setOrigin(advances.get(i) / 2, chars[i + positionsOffset].getHeight() / 4);
			}			
			positionsOffset += layout.runs.get(runIndex).glyphs.size;
		}
	}
	
	public void drop() 
	{
		resetText();
		for(int i = 0; i < chars.length; i++) 
		{
			HUD.addActor(chars[i]);
			chars[i].setY(posY + positions[i].y + 200f);
			chars[i].setColor(new Color(color.r, color.g, color.b, 0));
			chars[i].addAction(Actions.delay(delay * i, 
					Actions.parallel(
					Actions.alpha(1, ANIMATION_TIME), 
					Actions.moveTo(posX + positions[i].x, posY + positions[i].y, ANIMATION_TIME, Interpolation.bounceOut))));
		}
	}

	public void appear()
	{
		resetText();
		for(int i = 0; i < chars.length; i++)
		{
			HUD.addActor(chars[i]);
			chars[i].setScale(0f);
			chars[i].setColor(1, 1, 1, 0);
			chars[i].addAction(Actions.delay(delay * i, Actions.parallel(
					Actions.alpha(1, ANIMATION_TIME), 
					Actions.scaleTo(1, 1, ANIMATION_TIME, Interpolation.swingOut))));
		}
	}
	
	private void resetText() 
	{
		for(int i = 0; i < chars.length; i++) 
		{
			chars[i].setPosition(posX + positions[i].x, posY + positions[i].y);
			chars[i].setColor(color);
			chars[i].setScale(1f);
			chars[i].clearActions();
		}
	}
	
	public void remove()
	{
		for(int i = 0; i < chars.length; i++) 
		{
			chars[i].remove();
		}
	}
}
