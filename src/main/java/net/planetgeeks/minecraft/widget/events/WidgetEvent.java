package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

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
	
	public static interface WidgetListener
	{
		@Subscribe void onWidgetEvent(WidgetEvent event);
	}
}
