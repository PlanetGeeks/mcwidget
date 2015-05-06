package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget key type event. Called when a character is typed with the keyboard.
 *  
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetKeyTypeEvent extends WidgetKeyEvent
{
	public WidgetKeyTypeEvent(Widget component, char typedChar, int keyCode)
	{
		super(component, typedChar, keyCode);
	}
	
	public static interface WidgetKeyTypeListener
	{
		@Subscribe void onKeyTyped(WidgetKeyTypeEvent event);
	}

	public static abstract class WidgetKeyTypeHandler implements WidgetKeyTypeListener
	{
		private Object listenedKeyValue = null;
		
		public WidgetKeyTypeHandler(int keycode)
		{
			this.listenedKeyValue = keycode;
		}
		
		public WidgetKeyTypeHandler(char character)
		{
			this.listenedKeyValue = character;
		}
		
		@Subscribe
		public void onEvent(WidgetKeyTypeEvent event)
		{
			if(listenedKeyValue instanceof Integer && event.getKeyCode() == (int) listenedKeyValue || listenedKeyValue instanceof Character && event.getTypedChar() == (char) listenedKeyValue)
				onKeyTyped(event);
		}
		
		/**
		 * Called only if the key specified when this handler has been constructed is typed.
		 */
		public abstract void onKeyTyped(WidgetKeyTypeEvent event);
	}
}
