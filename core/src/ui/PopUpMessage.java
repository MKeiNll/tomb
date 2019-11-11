package ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import main.Tomb;

public class PopUpMessage
{	
	public static final float LIFETIME_PER_CHARACTER = 0.1f;
	public static final float EXTRA_LIFETIME = 2.5f;
	
	public static final float SCALE = 6;	
	public static final float POS_Y = 100;
	public static final float PAD_X = 100;
	public static final float PAD_Y = 120;
	
	public static final String PANEL_PATH = "ui/PNG/buttonLong_blue.png";
	
	public static boolean popUpMessageIsShown = false;
	
	private Image panel;
	private AnimatedText text;
	
	private float posX;
	private float posY;
	
	private Timer timer;
	private float lifeTime;
	
	public PopUpMessage(BitmapFont font, String message)
	{
		panel = new Image(new Texture(PANEL_PATH));
		panel.setScale(SCALE);
		
		posX = Tomb.VIEWPORT_WIDTH / 2 - panel.getWidth() / 2 * SCALE;
		posY = POS_Y;
		panel.setPosition(posX, posY);
		
		text = new AnimatedText(font, message, 
				posX + PAD_X / 2, posY + PAD_Y / 2 + (panel.getHeight() * SCALE) / 2, 
				panel.getWidth() * SCALE - PAD_X);
		
		timer = new Timer();
		lifeTime = message.length() * LIFETIME_PER_CHARACTER + EXTRA_LIFETIME;
	}
	
	public void pop()
	{
		popUpMessageIsShown = true;
		HUD.addActor(panel);
		panel.setY(posY + 200f);
		panel.setColor(panel.getColor().r, panel.getColor().g, panel.getColor().b, 0);
		panel.addAction(Actions.parallel(
				Actions.alpha(1, AnimatedText.ANIMATION_TIME), 
				Actions.moveTo(posX, posY, AnimatedText.ANIMATION_TIME, Interpolation.bounceOut)));
		
		text.drop();
		
		timer.scheduleTask(new Task()
				{
					@Override
					public void run() 
					{
						panel.addAction(Actions.sequence(
								Actions.alpha(0, AnimatedText.ANIMATION_TIME),
								Actions.removeActor()));
						text.remove();
						popUpMessageIsShown = false;
					}
				}, lifeTime);
	}
	
	public void removeInstantly()
	{
		panel.remove();
		text.remove();
		popUpMessageIsShown = false;
	}
}
