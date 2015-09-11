package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.layout.Dimension;

import com.google.common.eventbus.Subscribe;

/**
 * The widget resize event. Called when the component is resized.
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public class WidgetResizeEvent extends WidgetEvent
{
	@Getter
	private Dimension latestSize;
	
	public WidgetResizeEvent(Widget component, Dimension latestSize)
	{
		super(component);
		
		this.latestSize = latestSize;
	}
	
	public int getLatestWidth()
	{
		return latestSize.getWidth();
	}
	
	public int getLatestHeight()
	{
		return latestSize.getHeight();
	}
	
	/**
	 * 
	 * @return true, if latest size is different from actual size.
	 */
	public boolean isChanged()
	{
		return !latestSize.equals(getComponent().getSize());
	}
	
	public static interface WidgetResizeListener
	{
		@Subscribe void onComponentResized(WidgetResizeEvent event);
	}
}

