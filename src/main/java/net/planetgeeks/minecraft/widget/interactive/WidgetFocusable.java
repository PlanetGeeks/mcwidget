package net.planetgeeks.minecraft.widget.interactive;

import static net.planetgeeks.minecraft.widget.interactive.WidgetFocusable.FocusPolicy.CLICK_COMPONENT;
import static net.planetgeeks.minecraft.widget.interactive.WidgetFocusable.FocusPolicy.CLICK_COMPONENT_OR_CHILD;
import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.adapters.WidgetMouseAdapter;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMouseClickOutsideEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseEvent.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.util.Focusable;

public abstract class WidgetFocusable extends WidgetInteractive implements
		Focusable
{
	@Setter
	private boolean canLoseFocus = true;
	@Getter
	@Setter
	private boolean focused = false;
	private FocusPolicy focusPolicy = CLICK_COMPONENT_OR_CHILD;

	public WidgetFocusable(int width, int height)
	{
		super(width, height);
	}

	public WidgetFocusable(int xPosition, int yPosition, int width, int height)
	{
		super(xPosition, yPosition, width, height);
		final WidgetFocusable focusable = this;
		this.addListener(new WidgetMouseAdapter()
		{
			@Override
			public void onMouseClickedOutside(WidgetMouseClickOutsideEvent event)
			{
				if (event.isLeftButton() && canLoseFocus())
				{
					setFocused(false);
				}
			}

			@Override
			public void onMousePressed(WidgetMousePressEvent event)
			{
				if (event.isLeftButton())
					setFocused(focusPolicy == CLICK_COMPONENT ? event.getComponent() == focusable : true);
			}
		});
	}

	@Override
	public boolean canLoseFocus()
	{
		return canLoseFocus;
	}

	public static enum FocusPolicy
	{
		/**
		 * Set component focused when it's clicked or when a child of it is
		 * clicked.
		 */
		CLICK_COMPONENT_OR_CHILD,
		/**
		 * Set component focused when it's clicked. Ignores clicks on children.
		 */
		CLICK_COMPONENT;
	}
}