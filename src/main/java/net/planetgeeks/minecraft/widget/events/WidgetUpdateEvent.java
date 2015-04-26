package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The widget update event. Called before rendering.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetUpdateEvent extends WidgetEvent
{
	public WidgetUpdateEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetUpdateListener
	{
		@Subscribe void onComponentUpdated(WidgetUpdateEvent event);
	}
}
