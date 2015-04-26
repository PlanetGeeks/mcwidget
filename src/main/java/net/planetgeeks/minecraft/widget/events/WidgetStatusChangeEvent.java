package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

public class WidgetStatusChangeEvent extends WidgetEvent
{
	public WidgetStatusChangeEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetStatusChangeListener
	{
		@Subscribe void onStatusChanged(WidgetStatusChangeEvent event);
	}
}
