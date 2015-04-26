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
}
