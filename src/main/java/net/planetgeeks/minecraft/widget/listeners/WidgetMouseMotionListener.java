package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseDragEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseMoveEvent;

/**
 * The listener interface for receiving widget mouse motion events.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetMouseMotionListener extends WidgetListener
{
	/**
	 * Called when the mouse is dragged (Mouse moved while pressing one of its buttons).
	 * 
	 * @param event - the mouse event.
	 */
	void onMouseDragged(WidgetMouseDragEvent event);
	
	/**
	 * Called when the mouse is moved inside the component's HitBox.
	 * 
	 * @param event - the mouse event.
	 */
	void onMouseMoved(WidgetMouseMoveEvent event);
}
