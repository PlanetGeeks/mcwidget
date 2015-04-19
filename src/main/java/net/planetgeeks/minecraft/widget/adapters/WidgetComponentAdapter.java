package net.planetgeeks.minecraft.widget.adapters;

import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetHideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetMoveEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetShowEvent;
import net.planetgeeks.minecraft.widget.events.WidgetEvent.WidgetUpdateEvent;
import net.planetgeeks.minecraft.widget.listeners.WidgetComponentListener;
 
public class WidgetComponentAdapter implements WidgetComponentListener
{
	@Override
	public void onComponentHidden(WidgetHideEvent event)
	{
	}

	@Override
	public void onComponentMoved(WidgetMoveEvent event)
	{	
	}

	@Override
	public void onComponentResized(WidgetResizeEvent event)
	{
	}

	@Override
	public void onComponentShown(WidgetShowEvent event)
	{	
	}

	@Override
	public void onComponentUpdate(WidgetUpdateEvent event)
	{
	}
}
