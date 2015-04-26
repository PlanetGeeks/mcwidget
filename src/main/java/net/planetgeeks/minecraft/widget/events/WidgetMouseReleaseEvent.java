package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseButtonEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget mouse release event.
 * 
 * Called when a mouse button, previously pressed on the component, is released.
 * <p>
 * <b>Important!</b> It doesn't refer to the generic mouse button release event!
 * This is called if the component was previously clicked ({@link WidgetMousePressEvent}).
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMouseReleaseEvent extends WidgetMouseButtonEvent
{
	public WidgetMouseReleaseEvent(Widget component, int mouseX, int mouseY, int mouseButton)
	{
		super(component, mouseX, mouseY, mouseButton);
	}
	
	public static interface WidgetMouseReleaseListener
	{
		@Subscribe void onMouseReleased(WidgetMouseReleaseEvent event);
	}
}
