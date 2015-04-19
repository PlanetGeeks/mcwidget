package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetHideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetShowEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetUpdateEvent;

/**
 * The listener interface for receiving widget events.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetComponentListener extends WidgetListener
{
	/**
	 * Called when the component is set invisible.
	 * 
	 * @param event - the event.
	 */
	void onComponentHidden(WidgetHideEvent event);
	
	/**
	 * Called when the component is moved.
	 * 
	 * @param event - the event.
	 */
	void onComponentMoved(WidgetMoveEvent event);
	
	/**
	 * Called when the component is resized.
	 * 
	 * @param event - the event.
	 */ 
	void onComponentResized(WidgetResizeEvent event);
	
	/**
	 * Called when the component is shown.
	 * 
	 * @param event - the event.
	 */
	void onComponentShown(WidgetShowEvent event);
	
	/**
	 * Called before each render.
	 * 
	 * @param event - the event.
	 */
	void onComponentUpdate(WidgetUpdateEvent event);
}
