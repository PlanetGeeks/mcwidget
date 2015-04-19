package net.planetgeeks.minecraft.widget.listeners;

import net.planetgeeks.minecraft.widget.events.WidgetKeyEvent.WidgetKeyTypeEvent;

/**
 * The listener interface for receiving widget key events.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public interface WidgetKeyListener extends WidgetListener
{
	void onKeyTyped(WidgetKeyTypeEvent event);
}


