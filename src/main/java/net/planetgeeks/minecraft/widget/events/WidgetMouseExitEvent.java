package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget mouse exit event.
 * 
 * Called when the mouse exit from component's HitBox.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMouseExitEvent extends WidgetMouseEvent
{
	public WidgetMouseExitEvent(Widget component, int mouseX, int mouseY)
	{
		super(component, mouseX, mouseY);
	}
	
	public static interface WidgetMouseExitListener
	{
		@Subscribe void onMouseExited(WidgetMouseExitEvent event);
	}
}
