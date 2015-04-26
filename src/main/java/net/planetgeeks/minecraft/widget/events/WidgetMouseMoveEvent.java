package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

/**
 * The widget mouse move event. Called when the mouse is moved inside the
 * component's HitBox.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMouseMoveEvent extends WidgetMouseEvent
{
	@Getter
	private int latestMouseX;
	@Getter
	private int latestMouseY;
	
	public WidgetMouseMoveEvent(Widget component, int mouseX, int mouseY, int latestMouseX, int latestMouseY)
	{
		super(component, mouseX, mouseY);
		this.latestMouseX = latestMouseX;
		this.latestMouseY = latestMouseY;
	}
	
	public static interface WidgetMouseMoveListener
	{
		@Subscribe void onMouseMoved(WidgetMouseMoveEvent event);
	}
}
