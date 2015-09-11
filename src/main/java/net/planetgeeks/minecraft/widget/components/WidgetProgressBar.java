package net.planetgeeks.minecraft.widget.components;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.render.WidgetRenderer;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.MathUtil;

public class WidgetProgressBar extends WidgetInteractive
{
	@Getter
	private int minimum = 0;
	@Getter
	private int maximum = 100;
	@Getter
	private int value;
	@Getter
	private boolean indeterminate;
    private long indeterminateTime = -1L;
    private int indeterminateSpeed = 50;

	public WidgetProgressBar()
	{
		this.setMinimumSize(new Dimension(2, 2));
	}

	@Override
	protected void drawComponent(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.push();
		renderer.setColor(Color.WHITE);
		renderer.drawHorizontalLine(0, 0, getWidth() - 1);
		renderer.drawHorizontalLine(0, getHeight() - 1, getWidth() - 1);
		renderer.drawVerticalLine(0, 0, getHeight() - 1);
		renderer.drawVerticalLine(getWidth() - 1, 0, getHeight());
		renderer.setColor(Color.GREEN);
		if (isIndeterminate())
		{
			if(indeterminateTime < 0)
				indeterminateTime = System.currentTimeMillis();
			
			int pos = (int) ((System.currentTimeMillis() - indeterminateTime) * indeterminateSpeed / 1000L) + 1;
			
			if(pos >= getWidth() - 1)
			{
				indeterminateTime = System.currentTimeMillis();
				pos = 1;
			}
			
			renderer.drawFilledRect(pos, 1, 1, getHeight() - 2);
		}
		else
		{
			renderer.drawFilledRect(1, 1, (int) ((getWidth() - 2) * getProgress()), getHeight() - 2);
		}
		renderer.pop();
	}
	
	public void setIndeterminate(boolean indeterminate)
	{
		this.indeterminate = indeterminate;
		
		if(!this.indeterminate)
			this.indeterminateTime = -1L;
	}

	public void setValue(int value)
	{
		this.value = MathUtil.clamp(minimum, value, maximum);
	}

	public void setMinimum(int minimum)
	{
		this.minimum = minimum;

		if (this.maximum < minimum)
			setMaximum(minimum);
		else
			setValue(value);
	}

	public void setMaximum(int maximum)
	{
		this.maximum = maximum;

		if (this.minimum > maximum)
			setMinimum(maximum);
		else
			setValue(value);
	}

	/**
	 * @return a negative float if the progress bar is in the indeterminate
	 *         state, otherwise a float between 0.0F and 1.0F.
	 */
	public float getProgress()
	{
		return indeterminate ? -1.0F : (float) value / (maximum - minimum);
	}
}
