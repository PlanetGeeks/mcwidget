package net.planetgeeks.minecraft.widget.interactive;

import static net.planetgeeks.minecraft.widget.interactive.WidgetFocusable.FocusPolicy.PRESS_COMPONENT;
import static net.planetgeeks.minecraft.widget.interactive.WidgetFocusable.FocusPolicy.PRESS_COMPONENT_OR_CHILD;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.events.WidgetFocusGainEvent;
import net.planetgeeks.minecraft.widget.events.WidgetFocusLoseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressOutsideEvent;
import net.planetgeeks.minecraft.widget.util.Focusable;

import com.google.common.eventbus.Subscribe;

/**
 * Represents an interactive widget ({@link WidgetInteractive}) that can gain and lose focus (e.g. gain focus by clicking on it).
 * <p>
 * Focus policy can be set by using {@link #setFocusPolicy(FocusPolicy)}. 
 * <p>
 * <b>Complete list of supported events.</b>
 * <ul>
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetFocusEvent WidgetFocusEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetFocusGainEvent WidgetFocusGainEvent}
 * <li> {@link net.planetgeeks.minecraft.widget.events.WidgetFocusLoseEvent WidgetFocusLoseEvent}
 * </ul>
 * 
 * @author Vincenzo Fortunato - (Flood)
 */
public abstract class WidgetFocusable extends WidgetInteractive implements Focusable
{
	@Setter
	private boolean canLoseFocus = true;
	@Getter
	private boolean focused = false;
	private FocusPolicy focusPolicy = PRESS_COMPONENT_OR_CHILD;

	public WidgetFocusable(int width, int height)
	{
		super(width, height);
	}

	public WidgetFocusable(int xPosition, int yPosition, int width, int height)
	{
		super(xPosition, yPosition, width, height);

		class FocusableHandler
		{
			private final WidgetFocusable focusable;
			
			public FocusableHandler(WidgetFocusable focusable)
			{
				this.focusable = focusable;
			}
			
			@Subscribe
			public void onEvent(WidgetMousePressOutsideEvent event)
			{
				if (event.isLeftButton() && canLoseFocus())
				{
					setFocused(false);
				}
			}
			
			@Subscribe
			public void onEvent(WidgetMousePressEvent event)
			{
				if (event.isLeftButton())
					setFocused(focusPolicy == PRESS_COMPONENT ? event.getComponent() == focusable : true);
			}
		}
		
		this.getEventBus().register(new FocusableHandler(this));
	}

	@Override
	public void setFocused(boolean focused)
	{
		if (this.focused != focused)
			this.getEventBus().post((this.focused = focused) ? new WidgetFocusGainEvent(this) : new WidgetFocusLoseEvent(this));
	}

	@Override
	public boolean canLoseFocus()
	{
		return canLoseFocus;
	}
	
	/**
	 * Set the focus gain/lose policy.
	 * 
	 * @param focusPolicy - the policy to set.
	 */
	public void setFocusPolicy(@NonNull FocusPolicy focusPolicy)
	{
		this.focusPolicy = focusPolicy;
	}
	
	/**
	 * Get the set focus gain/lose policy.
	 * 
	 * @return the set policy.
	 */
	public FocusPolicy getFocusPolicy()
	{
		return focusPolicy;
	}
	
	public static enum FocusPolicy
	{
		/**
		 * Set component focused when it's clicked or when a child of it is
		 * clicked.
		 */
		PRESS_COMPONENT_OR_CHILD,
		/**
		 * Set component focused when it's clicked. Ignores clicks on children.
		 */
		PRESS_COMPONENT;
	}
}