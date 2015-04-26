package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget mouse enter event.
 * 
 * Called when the mouse enter into component's HitBox.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMouseEnterEvent extends WidgetMouseEvent
{
	public WidgetMouseEnterEvent(Widget component, int mouseX, int mouseY)
	{
		super(component, mouseX, mouseY);
	}
	
	public static interface WidgetMouseEnterListener
	{
		@Subscribe void onMouseEntered(WidgetMouseEnterEvent event);
	}
}
