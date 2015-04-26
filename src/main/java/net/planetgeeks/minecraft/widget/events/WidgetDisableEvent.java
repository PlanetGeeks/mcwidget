package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The widget disable event.
 * Called when the status of a component is changed and it has been disabled.
 * <p>
 * <b>Important!</b> Only WidgetInteractive components support this event.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetDisableEvent extends WidgetStatusChangeEvent
{
	public WidgetDisableEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetDisableListener
	{
		@Subscribe void onDisabled(WidgetDisableEvent event);
	}
}
