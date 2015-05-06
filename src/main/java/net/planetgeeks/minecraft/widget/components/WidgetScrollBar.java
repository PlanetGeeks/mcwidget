package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent;
import net.planetgeeks.minecraft.widget.events.WidgetActionEvent.WidgetActionListener;
import net.planetgeeks.minecraft.widget.events.WidgetKeyTypeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetKeyTypeEvent.WidgetKeyTypeListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseDragEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseDragEvent.WidgetMouseDragListener;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent.WidgetMousePressListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseWheelEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseWheelEvent.WidgetMouseWheelListener;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent.WidgetResizeListener;
import net.planetgeeks.minecraft.widget.interactive.WidgetFocusable;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Direction;
import net.planetgeeks.minecraft.widget.layout.Orientation;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.Point;

import org.lwjgl.input.Keyboard;

@Getter
public class WidgetScrollBar extends WidgetInteractive implements WidgetResizeListener
{
	private Orientation orientation;
	private int value = 0;
	private int readableValue = 43;
	private int totalValue = 100;
	private int incrementUnit = 6;
	private boolean usingArrows = true;

	private WidgetArrowButton incrementButton;
	private WidgetArrowButton decrementButton;
	private WidgetBarContainer barContainer;
	private WidgetBar bar;

	public WidgetScrollBar(int width, int height, Orientation orientation)
	{
		this(0, 0, width, height, orientation);
	}

	public WidgetScrollBar(int xPosition, int yPosition, int width, int height, @NonNull Orientation orientation)
	{
		super(xPosition, yPosition, width, height);
		this.orientation = orientation;
		this.init();
	}

	protected void init()
	{
		class IncrementAction
				implements WidgetActionListener
		{
			private boolean increment;

			public IncrementAction(boolean increment)
			{
				this.increment = increment;
			}

			@Override
			public void onAction(WidgetActionEvent event)
			{
				increment(increment ? 1 : -1);
			}
		}

		add(this.incrementButton = createIncrementButton());
		this.incrementButton.setRepeatActionEvent(true);
		this.incrementButton.getEventBus().register(new IncrementAction(true));

		add(this.decrementButton = createDecrementButton());
		this.decrementButton.setRepeatActionEvent(true);
		this.decrementButton.getEventBus().register(new IncrementAction(false));

		add(this.barContainer = createBarContainer());
		this.barContainer.add(this.bar = createDraggableBar());

		setArrowsEnabled(this.usingArrows);
	}

	protected WidgetArrowButton createIncrementButton()
	{
		if (orientation == Orientation.HORIZONTAL)
			return new WidgetArrowButton(getHeight(), getHeight(), Direction.RIGHT);
		else
			return new WidgetArrowButton(getWidth(), getWidth(), Direction.DOWN);
	}

	protected WidgetArrowButton createDecrementButton()
	{
		if (orientation == Orientation.HORIZONTAL)
			return new WidgetArrowButton(getHeight(), getHeight(), Direction.LEFT);
		else
			return new WidgetArrowButton(getWidth(), getWidth(), Direction.UP);
	}

	protected WidgetBar createDraggableBar()
	{
		return new WidgetBar(0, 0);
	}

	protected WidgetBarContainer createBarContainer()
	{
		return new WidgetBarContainer(0, 0);
	}

	public void setOrientation(@NonNull Orientation orientation)
	{
		this.orientation = orientation;
	}

	public void setValue(int value)
	{
		this.value = value > getMaximumValue() ? getMaximumValue() : (value < 0 ? 0 : value);
		dispose();
	}

	public int getMaximumValue()
	{
		return this.totalValue - this.readableValue;
	}

	public void setReadableValue(int readableValue)
	{
		if (readableValue < 0)
			throw new IllegalArgumentException("Scroll bar readable value cannot be less than 0!");

		this.readableValue = readableValue;
		setValue(this.value);
	}

	public void setTotalValue(int totalValue)
	{
		if (totalValue < 0)
			throw new IllegalArgumentException("Scroll bar total value cannot be less than 0!");

		this.totalValue = totalValue;
		setValue(this.value);
	}

	public void setIncrementUnit(int incrementUnit)
	{
		if (incrementUnit < 1)
			throw new IllegalArgumentException("Scroll bar increment unit cannot be less than 1!");

		this.incrementUnit = incrementUnit;
	}

	public void increment(int units)
	{
		setValue(value + units * incrementUnit);
	}

	public void decrement(int units)
	{
		setValue(value - units * incrementUnit);
	}

	public float getScrolledAmount()
	{
		float scrolledAmount = value / (float) (totalValue - readableValue);
		return scrolledAmount < 0 ? 0.0F : (scrolledAmount > 1.0F ? 1.0F : scrolledAmount);
	}

	public void enableArrows()
	{
		setArrowsEnabled(true);
	}

	public void disableArrows()
	{
		setArrowsEnabled(false);
	}

	public void setArrowsEnabled(boolean arrowsEnabled)
	{
		this.usingArrows = arrowsEnabled;
		this.incrementButton.setVisible(this.usingArrows);
		this.decrementButton.setVisible(this.usingArrows);
		dispose();
	}

	protected void dispose()
	{
		disposeArrows();
		disposeBarContainer();
		disposeBar();
	}

	protected void disposeArrows()
	{
		if (isUsingArrows())
		{
			int size, decrementX, decrementY, incrementX, incrementY;

			if (orientation == Orientation.HORIZONTAL)
			{
				size = getHeight();
				incrementX = getWidth() - size;
				decrementX = decrementY = incrementY = 0;
			}
			else
			{
				size = getWidth();
				incrementY = getHeight() - size;
				decrementX = decrementY = incrementX = 0;
			}

			decrementButton.setSize(new Dimension(size, size));
			decrementButton.setPosition(new Point(decrementX, decrementY));

			incrementButton.setSize(new Dimension(size, size));
			incrementButton.setPosition(new Point(incrementX, incrementY));
		}
	}

	protected void disposeBarContainer()
	{
		int containerWidth = 0, containerHeight = 0, containerX = 0, containerY = 0;

		if (orientation == Orientation.HORIZONTAL)
		{
			containerWidth = getWidth() - (isUsingArrows() ? getHeight() * 2 : 0);
			containerHeight = getHeight();
			containerX = getHeight();
		}
		else
		{
			containerWidth = getWidth();
			containerHeight = getHeight() - (isUsingArrows() ? getWidth() * 2 : 0);
			containerY = getWidth();
		}

		barContainer.setSize(new Dimension(containerWidth, containerHeight));
		barContainer.setPosition(new Point(containerX, containerY));
	}

	protected void disposeBar()
	{
		int barWidth = 0, barHeight = 0, barX = 0, barY = 0;

		if (orientation == Orientation.HORIZONTAL)
		{
			barWidth = Math.round(bar.getBarSize());
			barHeight = getHeight();
			barX = Math.round(bar.getBarPosition());
		}
		else
		{
			barWidth = getWidth();
			barHeight = Math.round(bar.getBarSize());
			barY = Math.round(bar.getBarPosition());
		}

		bar.setSize(new Dimension(barWidth, barHeight));
		bar.setPosition(new Point(barX, barY));
	}

	@Override
	public void onComponentResized(WidgetResizeEvent event)
	{
		dispose();
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
	}

	protected class WidgetBarContainer extends WidgetInteractive
	{
		public WidgetBarContainer(int width, int height)
		{
			this(0, 0, width, height);
		}

		public WidgetBarContainer(int xPosition, int yPosition, int width, int height)
		{
			super(xPosition, yPosition, width, height);
			final Widget instance = this;
			this.getEventBus().register(new WidgetMousePressListener()
			{
				@Override
				public void onMousePressed(WidgetMousePressEvent event)
				{
					if (event.isLeftButton() && event.getComponent() == instance)
					{
						int position = orientation == Orientation.HORIZONTAL ? event.getMouseX() - getXOnScreen() : event.getMouseY() - getYOnScreen();
						setValue(bar.getValueByPosition(position - bar.getBarSize() / 2));
					}
				}
			});
		}

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			renderer.setColor(Color.BLACK);
			renderer.drawFilledRect(0, 0, getWidth(), getHeight());
		}

		public int getContainerSize()
		{
			return orientation == Orientation.HORIZONTAL ? getWidth() : getHeight();
		}
	}

	protected class WidgetBar extends WidgetFocusable
	{
		public WidgetBar(int width, int height)
		{
			this(0, 0, width, height);
		}

		public WidgetBar(int xPosition, int yPosition, int width, int height)
		{
			super(xPosition, yPosition, width, height);

			this.getEventBus().register(new WidgetMouseDragListener()
			{
				@Override
				public void onMouseDragged(WidgetMouseDragEvent event)
				{
					int position = orientation == Orientation.HORIZONTAL ? event.getMouseX() - barContainer.getXOnScreen() : event.getMouseY() - barContainer.getYOnScreen();
					setValue(getValueByPosition(position - getBarSize() / 2));
				}
			});

			this.getEventBus().register(new WidgetMouseWheelListener()
			{
				@Override
				public void onMouseScrolled(WidgetMouseWheelEvent event)
				{
					if (isFocused())
					{
						decrement(event.getDWheel() > 0 ? 1 : -1);
					}
				}
			});

			this.getEventBus().register(new WidgetKeyTypeListener()
			{
				@Override
				public void onKeyTyped(WidgetKeyTypeEvent event)
				{
					if (isFocused())
						switch (event.getKeyCode())
						{
							case Keyboard.KEY_UP:
								decrement(1);
								break;
							case Keyboard.KEY_DOWN:
								increment(1);
								break;
						}
				}
			});
		}

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			renderer.setColor(isFocused() ? Color.RED : Color.GRAY);
			renderer.drawFilledRect(0, 0, getWidth(), getHeight());
			renderer.setColor(Color.WHITE);
			renderer.drawHorizontalLine(0, 0, getWidth());
			renderer.drawHorizontalLine(0, getHeight() - 1, getWidth());
			renderer.drawVerticalLine(0, 0, getHeight());
			renderer.drawVerticalLine(getWidth() - 1, 0, getHeight());
		}

		public int getValueByPosition(float position)
		{
			return (int) ((position * readableValue) / getBarSize());
		}

		public float getBarSize()
		{
			return (barContainer.getContainerSize() * readableValue) / (float) totalValue;
		}

		public float getBarPosition()
		{
			return (value / (float) readableValue) * getBarSize();
		}
	}
}
