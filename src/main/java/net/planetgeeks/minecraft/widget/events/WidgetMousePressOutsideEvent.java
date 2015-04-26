package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseButtonEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The widget mouse move event.
 * 
 * Called when the mouse button is pressed outside the component's HitBox.
 * <p>
 * The pressed component can be retrieved from the given event.
 * <p>
 * If the mouse is clicked on an child component, this method will not be
 * called. Use {@link WidgetMousePressEvent} instead and check
 * the clicked component.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMousePressOutsideEvent extends WidgetMouseButtonEvent
{
	public WidgetMousePressOutsideEvent(Widget component, int mouseX, int mouseY, int mouseButton)
	{
		super(component, mouseX, mouseY, mouseButton);
	}
	
	public static interface WidgetMousePressOutsideListener
	{
		@Subscribe void onMousePressedOutside(WidgetMousePressOutsideEvent event);
	}
}
