package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseButtonEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Widget mouse press event.
 * 
 * Called when the mouse is pressed onto a component's HitBox or onto an HitBox
 * that belongs to at least one of its children.
 * <p>
 * The component that has been directly pressed can be retrieved by using {@link #getComponent()}.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMousePressEvent extends WidgetMouseButtonEvent
{
	public WidgetMousePressEvent(Widget component, int mouseX, int mouseY, int mouseButton)
	{
		super(component, mouseX, mouseY, mouseButton);
	}
	
	public static interface WidgetMousePressListener
	{
		@Subscribe void onMousePressed(WidgetMousePressEvent event);
	}
}
