package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget hide event. Called when the component is set invisible.
 *  
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetHideEvent extends WidgetEvent
{
	public WidgetHideEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetHideListener
	{
		@Subscribe void onComponentHidden(WidgetHideEvent event);
	}
}
