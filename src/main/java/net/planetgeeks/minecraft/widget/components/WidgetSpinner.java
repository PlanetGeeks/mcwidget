package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Direction.UP;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent.WidgetMousePressListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent.WidgetMouseReleaseListener;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Direction;
import net.planetgeeks.minecraft.widget.layout.WidgetGroupLayout;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;

public class WidgetSpinner extends WidgetButton
{
	private WidgetArrowButton incrementButton;
	private WidgetArrowButton decrementButton;
	private WidgetTextField inputField;
	@Getter
	@Setter
	private int maximumValue = Integer.MAX_VALUE;
	@Getter
	@Setter
	private int minimumValue = 0;
	private long latestClick = -1L;
	@Getter
	@Setter
	private float incrementSpeed = 3.0F;

	public WidgetSpinner()
	{
		setMinimumSize(new Dimension(13, 14));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

		inputField = new WidgetTextField();
		incrementButton = new WidgetArrowButton(Direction.UP);
		decrementButton = new WidgetArrowButton(Direction.DOWN);
		incrementButton.getEventBus().register(new ArrowButtonHandler(incrementButton));
		decrementButton.getEventBus().register(new ArrowButtonHandler(decrementButton));
		
		WidgetGroupLayout layout = new WidgetGroupLayout();
		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(inputField).addGroup(layout.createParallelGroup().addFixedComponent(incrementButton, 13).addFixedComponent(decrementButton, 13)));
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addComponent(inputField).addGroup(layout.createSequentialGroup().addComponent(incrementButton).addComponent(decrementButton))));
        setLayout(layout);
        layout.dispose();
		
		inputField.setTextPattern(Pattern.compile("[0-9]*", Pattern.DOTALL));
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
	}

	private long getIncrementDelay()
	{
		long delay = -1L;

		if (latestClick != -1L)
		{
			if (System.currentTimeMillis() - latestClick < 1000)
				delay = (long) (300 / incrementSpeed);
			else if (System.currentTimeMillis() - latestClick < 1500)
				delay = (long) (180 / incrementSpeed);
			else if (System.currentTimeMillis() - latestClick < 2000)
				delay = (long) (80 / incrementSpeed);
			else
				delay = (long) (20 / incrementSpeed);
		}
		else
			latestClick = System.currentTimeMillis();

		return delay;
	}

	public int getValue()
	{
		try
		{
			return Integer.valueOf(inputField.getText());
		}
		catch (NumberFormatException e)
		{
			setValue(minimumValue);
			return getValue();
		}
	}

	public void setValue(int value)
	{
		inputField.setText(String.valueOf(value));
	}

	protected class ArrowButtonHandler implements WidgetMousePressListener, WidgetMouseReleaseListener
	{
		private long latestUpdate = 0L;

		private final WidgetArrowButton button;

		public ArrowButtonHandler(@NonNull WidgetArrowButton button)
		{
			this.button = button;
		}

		@Override
		public void onMousePressed(WidgetMousePressEvent event)
		{
			if (!event.isLeftButton())
				return;

			float delay = getIncrementDelay();
			int increment = 1;

			if (delay > 0 && System.currentTimeMillis() - latestUpdate <= delay)
				return;
			else if (delay > 0)
			{
				increment = (int) ((System.currentTimeMillis() - latestUpdate) / delay);
			}

			int current = getValue();

			if (button.getDirection() == UP)
			{
				if (current + increment <= maximumValue)
					setValue(current + increment);
				else
					setValue(maximumValue);
			}
			else
			{
				if (current - increment >= minimumValue)
					setValue(current - increment);
				else
					setValue(minimumValue);
			}

			latestUpdate = System.currentTimeMillis();
		}

		@Override
		public void onMouseReleased(WidgetMouseReleaseEvent event)
		{
			if (event.isLeftButton())
				latestClick = -1L;
		}
	}
}
