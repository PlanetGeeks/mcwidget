package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget show event. Called when the component is shown.
 *  
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetShowEvent extends WidgetEvent
{
	public WidgetShowEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetShowListener
	{
		@Subscribe void onComponentShown(WidgetShowEvent event);
	}
}
