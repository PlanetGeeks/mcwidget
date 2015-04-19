package net.planetgeeks.minecraft.widget.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;

public abstract class WidgetMouseEvent extends WidgetEvent
{
	public static final byte LEFT_BUTTON = 0;
	public static final byte RIGHT_BUTTON = 1;
	public static final byte MIDDLE_BUTTON = 2;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Widget component;
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
	}

	public static class WidgetMousePressEvent extends WidgetMouseButtonEvent
	{
		public WidgetMousePressEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY, mouseButton);
		}
	}

	public static class WidgetMouseReleaseEvent extends WidgetMouseButtonEvent
	{
		public WidgetMouseReleaseEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY, mouseButton);
		}
	}

	public static class WidgetMouseClickOutsideEvent extends WidgetMouseButtonEvent
	{
		public WidgetMouseClickOutsideEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY, mouseButton);
		}
	}
	
	public static class WidgetMouseDragEvent extends WidgetMouseButtonEvent
	{
	    public WidgetMouseDragEvent(Widget component, int mouseX, int mouseY, int mouseButton)
	    {
	    	super(component, mouseX, mouseY, mouseButton);
	    }
	}

	public static class WidgetMouseEnterEvent extends WidgetMouseEvent
	{
		public WidgetMouseEnterEvent(Widget component, int mouseX, int mouseY)
		{
			super(component, mouseX, mouseY);
		}
	}

	public static class WidgetMouseExitEvent extends WidgetMouseEvent
	{
		public WidgetMouseExitEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY);
		}
	}

	public static class WidgetMouseMoveEvent extends WidgetMouseEvent
	{
		public WidgetMouseMoveEvent(Widget component, int mouseX, int mouseY, int mouseButton)
		{
			super(component, mouseX, mouseY);
		}
	}
}
