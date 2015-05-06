package net.planetgeeks.minecraft.widget.events;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

public class WidgetMouseWheelEvent extends WidgetMouseEvent
{
	@Getter
	private int dWheel;
	
	public WidgetMouseWheelEvent(Widget component, int mouseX, int mouseY, int dWheel)
	{
		super(component, mouseX, mouseY);
		this.dWheel = dWheel;
	}

	public static interface WidgetMouseWheelListener
	{
		@Subscribe void onMouseScrolled(WidgetMouseWheelEvent event);
	}
}
