package net.planetgeeks.minecraft.widget.components;

import static net.planetgeeks.minecraft.widget.layout.Direction.DOWN;
import static net.planetgeeks.minecraft.widget.layout.Direction.LEFT;
import static net.planetgeeks.minecraft.widget.layout.Direction.RIGHT;
import static net.planetgeeks.minecraft.widget.layout.Direction.UP;

import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMousePressEvent.WidgetMousePressListener;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent;
import net.planetgeeks.minecraft.widget.events.WidgetMouseReleaseEvent.WidgetMouseReleaseListener;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent.WidgetResizeListener;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.layout.Direction;
import net.planetgeeks.minecraft.widget.render.NinePatch;
import net.planetgeeks.minecraft.widget.render.TextureRegion;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetSpinner extends WidgetInteractive
{
	private static final byte ENABLED = 0, HOVERED = 1, PRESSED = 2;
	private static final TextureRegion TEXTURE = new TextureRegion(WidgetUtil.guiTexture("components.png"), 0, 0, 42, 16);

	private ArrowButton incrementButton;
	private ArrowButton decrementButton;
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

	public WidgetSpinner(int width, int height)
	{
		super(width, height);

		getEventBus().register(new WidgetResizeListener()
		{
			@Override
			public void onComponentResized(WidgetResizeEvent event)
			{
				if (inputField != null)
					inputField.setWidth(getWidth() - 13);

				if (incrementButton != null)
					incrementButton.setX(getWidth() - 13);

				if (decrementButton != null)
					decrementButton.setX(getWidth() - 13);
			}
		});
		
		setMinimumSize(new Dimension(13, 14));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

		add(inputField = new WidgetTextField(getWidth() - 13, 14));
		add(incrementButton = new ArrowButton(Direction.UP, getWidth() - 13, 0, 13, 7));
		add(decrementButton = new ArrowButton(Direction.DOWN, getWidth() - 13, 7, 13, 7));
		
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

	public class ArrowButton extends WidgetInteractive
	{
		@Getter
		private Direction direction;
		private NinePatch ninePatches[];
		private TextureRegion arrowTexture;

		public ArrowButton(Direction direction, int xPosition, int yPosition, int width, int height)
		{
			super(xPosition, yPosition, width, height);

			ninePatches = new NinePatch[3];
			ninePatches[ENABLED] = new NinePatch.Dynamic(this, TEXTURE.split(0, 9, 13, 7), 1, 1, 1, 1);
			ninePatches[HOVERED] = new NinePatch.Dynamic(this, TEXTURE.split(13, 9, 13, 7), 1, 1, 1, 1);
			ninePatches[PRESSED] = new NinePatch.Dynamic(this, TEXTURE.split(26, 9, 13, 7), 1, 1, 1, 1);		
			
			getEventBus().register(new ArrowButtonHandler());		
			setMinimumSize(new Dimension(2, 2));
			setDirection(direction);
		}

		public ArrowButton(Direction direction, int width, int height)
		{
			this(direction, 0, 0, width, height);
		}
		
		protected class ArrowButtonHandler implements WidgetMousePressListener, WidgetMouseReleaseListener
		{
			private long latestUpdate = 0L;
			
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

				if (direction == UP)
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

		public void setDirection(Direction direction)
		{
			if (direction == LEFT || direction == RIGHT)
				throw new IllegalArgumentException("Direction can be only UP or DOWN");

			this.direction = direction;
			this.arrowTexture = TEXTURE.split(36, direction == DOWN ? 1 : 5, 5, 3);
		}

		@Override
		protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
		{
			ninePatches[isPressed() ? PRESSED : (isHovered(mouseX, mouseY) ? HOVERED : ENABLED)].draw(mouseX, mouseY, partialTicks, renderer);

			renderer.drawTexture(arrowTexture, this.getWidth() / 2 - arrowTexture.getRegionWidth() / 2, this.getHeight() / 2 - arrowTexture.getRegionHeight() / 2);
		}
	}

}
