package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.util.Point;

public class WidgetEvent
{
	private Widget component;

	public WidgetEvent(Widget component)
	{
		this.component = component;
	}

	/**
	 * @return the component involved in the event.
	 */
	public Widget getComponent()
	{
		return component;
	}
	
	public static class WidgetShowEvent extends WidgetEvent
	{
		public WidgetShowEvent(Widget component)
		{
			super(component);
		}
		
	}
	
	public static class WidgetHideEvent extends WidgetEvent
	{
		public WidgetHideEvent(Widget component)
		{
			super(component);
		}
	}
	
	public static class WidgetResizeEvent extends WidgetEvent
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
	}
	
	public static class WidgetMoveEvent extends WidgetEvent
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
	}
	
	public static class WidgetUpdateEvent extends WidgetEvent
	{
		public WidgetUpdateEvent(Widget component)
		{
			super(component);
		}
	}
	
	public static class WidgetEnableEvent extends WidgetEvent
	{
		public WidgetEnableEvent(Widget component)
		{
			super(component);
		}
	}
	
	public static class WidgetDisableEvent extends WidgetEvent
	{
		public WidgetDisableEvent(Widget component)
		{
			super(component);
		}
	}
}
