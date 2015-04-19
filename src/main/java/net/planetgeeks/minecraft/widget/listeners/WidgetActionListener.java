package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;

/**
 * The listener interface for receiving widget action events.
 * <p>
 * <b>Important!</b> Not all components support this listener.
 * <p>
 * Supported components:
 * <ul>
 * <li> WidgetButton
 * </ul>
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetActionListener extends WidgetListener
{
	/**
	 * Called when an action is performed on a component.
	 * 
	 * @param event - the action event.
	 */
    void onAction(WidgetActionEvent event);
}
