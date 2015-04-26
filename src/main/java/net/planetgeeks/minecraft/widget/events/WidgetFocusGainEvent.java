package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget focus gain event. Called when the component has gained the focus.
 *  
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetFocusGainEvent extends WidgetFocusEvent
{
	public WidgetFocusGainEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetFocusGainListener
	{
		@Subscribe void onFocusGained(WidgetFocusGainEvent event);
	}
}
