package net.planetgeeks.minecraft.widget.layout;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent.WidgetResizeListener;

public abstract class WidgetLayout implements WidgetResizeListener
{
	@Getter
	private Widget linkedComponent;
	@Getter
	private boolean valid = false;

	protected void validate()
	{
		setValid(true);
	}

	public void invalidate()
	{
		setValid(false);
	}

	protected void setValid(boolean valid)
	{
		this.valid = valid;
	}

	public WidgetLayout link(@NonNull Widget component)
	{
		if (linkedComponent != null)
			throw new IllegalArgumentException("The same instance of a layout cannot be set on multiple components!");

		linkedComponent = component;
		linkedComponent.getEventBus().register(this);

		return this;
	}

	public WidgetLayout unlink()
	{
		if (linkedComponent != null)
		{
			linkedComponent.getEventBus().unregister(this);
			linkedComponent = null;
		}

		return this;
	}

	@Override
	public void onComponentResized(WidgetResizeEvent event)
	{
		if (event.isChanged())
			dispose();	
	}

	public abstract void dispose();
}
