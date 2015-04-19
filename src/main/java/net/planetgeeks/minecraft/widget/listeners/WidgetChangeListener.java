package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetDisableEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetEnableEvent;

/**
 * The listener interface for receiving widget change events (e.g enable/disable events).
 * <p>
 * <b>Important!</b> Only WidgetInteractive components support this listener.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetChangeListener extends WidgetListener
{
	/**
	 * Called when the status of a component is changed and it has been enabled.
	 * 
	 * @param the change event.
	 */
	void onComponentEnabled(WidgetEnableEvent event);
	
	/**
	 * Called when the status of a component is changed and it has been disabled.
	 * 
	 * @param the change event.
	 */
	void onComponentDisabled(WidgetDisableEvent event);
}
