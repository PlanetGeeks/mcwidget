package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget focus lose event. Called when the component has lost the focus.
 *  
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetFocusLoseEvent extends WidgetFocusEvent
{
	public WidgetFocusLoseEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetFocusLoseListener
	{
		@Subscribe void onFocusLost(WidgetFocusLoseEvent event);
	}
}