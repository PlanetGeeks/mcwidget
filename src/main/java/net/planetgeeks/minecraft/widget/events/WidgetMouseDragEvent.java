package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseButtonEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The widget mouse drag event.  Called when the mouse is dragged (Mouse moved while pressing his left button).
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMouseDragEvent extends WidgetMouseButtonEvent
{
	@Getter
	private int latestMouseX; 
	@Getter
	private int latestMouseY;
	
    public WidgetMouseDragEvent(Widget component, int mouseX, int mouseY, int mouseButton, int latestMouseX, int latestMouseY)
    {
    	super(component, mouseX, mouseY, mouseButton);
    	this.latestMouseX = latestMouseX;
    	this.latestMouseY = latestMouseY;
    }
    
    public static interface WidgetMouseDragListener
    {
    	@Subscribe void onMouseDragged(WidgetMouseDragEvent event);
    }
}