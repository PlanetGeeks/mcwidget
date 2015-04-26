package net.planetgeeks.minecraft.widget.events;

import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

public abstract class WidgetFocusEvent extends WidgetEvent
{
	public WidgetFocusEvent(Widget component)
	{
		super(component);
	}
	
	public static interface WidgetFocusListener
	{
		@Subscribe void onFocusEvent(WidgetFocusEvent event);
	}
}
