package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.util.Point;

import com.google.common.eventbus.Subscribe;

/**
 * The widget move event. Called when the component is moved.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetMoveEvent extends WidgetEvent
{
	@Getter
    private Point latestPosition;
    
	public WidgetMoveEvent(Widget component, Point latestPosition)
	{
		super(component);
		this.latestPosition = latestPosition;
	}
	
	public int getLatestX()
	{
		return latestPosition.getX();
	}
	
	public int getLatestY()
	{
		return latestPosition.getY();
	}
	
	public static interface WidgetMoveListener
	{
		@Subscribe void onComponentMoved(WidgetMoveEvent event);
	}
}
