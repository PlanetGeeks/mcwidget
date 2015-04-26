package net.planetgeeks.minecraft.widget.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;

import com.google.common.eventbus.Subscribe;

public abstract class WidgetMouseEvent extends WidgetEvent
{
	public static final byte LEFT_BUTTON = 0;
	public static final byte RIGHT_BUTTON = 1;
	public static final byte MIDDLE_BUTTON = 2;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private int mouseX;
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private int mouseY;

	public WidgetMouseEvent(Widget component, int mouseX, int mouseY)
	{
		super(component);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	public static interface WidgetMouseListener
	{
		@Subscribe void onMouseEvent(WidgetMouseEvent event);
	}
	
	public static abstract class WidgetMouseButtonEvent extends WidgetMouseEvent
	{
		@Getter
		@Setter(AccessLevel.PROTECTED)
		private int mouseButton;
		
		public WidgetMouseButtonEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY);
			this.mouseButton = mouseButton;
		}
		
		public boolean isLeftButton()
		{
			return isButton(LEFT_BUTTON);
		}

		public boolean isRightButton()
		{
			return isButton(RIGHT_BUTTON);
		}

		public boolean isMiddleButton()
		{
			return isButton(MIDDLE_BUTTON);
		}
		
		public boolean isButton(int mouseButton)
		{
			return getMouseButton() == mouseButton;
		}
		
		public static interface WidgetMouseButtonListener
		{
			@Subscribe void onMouseButtonEvent(WidgetMouseEvent event);
		}
	}
}
