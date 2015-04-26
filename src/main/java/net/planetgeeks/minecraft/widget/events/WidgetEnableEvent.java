package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The widget enable event.
 * Called when the status of a component is changed and it has been enabled.
 * <p>
 * <b>Important!</b> Only WidgetInteractive components support this event.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetEnableEvent extends WidgetStatusChangeEvent
{
	public WidgetEnableEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetEnableListener
	{
		@Subscribe void onEnabled(WidgetEnableEvent event);
	}
}
