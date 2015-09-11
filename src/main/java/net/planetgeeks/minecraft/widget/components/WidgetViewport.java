package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent.WidgetResizeListener;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;
import net.planetgeeks.minecraft.widget.util.Point;

public class WidgetViewport extends WidgetPanel
{
	public static final byte VIEWPORT_CHANGE_ACTION = 0;
	
	@Getter
	private Widget view;

	public WidgetViewport()
	{
		final Widget viewport = this;
		getEventBus().register(new WidgetResizeListener()
		{
			@Override
			public void onComponentResized(WidgetResizeEvent event)
			{
				getEventBus().post(new WidgetActionEvent(viewport, VIEWPORT_CHANGE_ACTION));
			}
		});
	}

	public void setViewPosition(@NonNull Point position)
	{
		if(view == null)
			return;
		
		view.setPosition(position);
	}

	public void setViewSize(@NonNull Dimension size)
	{
		if(view == null)
			return;
		
		view.setSize(size);
	}
	
	public Point getViewPosition()
	{
		return view == null ? new Point() : view.getPosition();
	}
	
	public Dimension getViewSize()
	{
		return view == null ? getSize().clone() : view.getSize();
	}
	
	public void setView(Widget view)
	{
		final Widget viewport = this;
		this.view = view;
		this.view.getEventBus().register(new WidgetResizeListener()
		{
			@Override
			public void onComponentResized(WidgetResizeEvent event)
			{
				getEventBus().post(new WidgetActionEvent(viewport, VIEWPORT_CHANGE_ACTION));
			}	
		});
		
		setLayout(view != null ? new WidgetViewportLayout() : null);
		layout();
	}
	
	protected class WidgetViewportLayout extends WidgetGroupLayout
	{
		public WidgetViewportLayout()
		{
			setHorizontalGroup(createHorizontalGroup());
			setVerticalGroup(createVerticalGroup());
		}

		protected Group<?> createHorizontalGroup()
		{
			return createSequentialGroup().addComponent(view);
		}

		protected Group<?> createVerticalGroup()
		{
			return createSequentialGroup().addComponent(view);
		}
	}
}
