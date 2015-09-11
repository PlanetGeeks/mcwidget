package net.planetgeeks.minecraft.widget.layout;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

@Getter
public class Insets
{
	private int left = 0;
	private int right = 0;
	private int top = 0;
	private int bottom = 0;

	public Insets(){}

	public Insets(int left, int top, int right, int bottom)
	{
	    setLeft(left);
	    setTop(top);
	    setRight(right);
	    setBottom(bottom);
	}

	public Insets setLeft(int gap)
	{
		if (validateGap(gap))
			this.left = gap;
		
		return this;
	}

	public Insets setRight(int gap)
	{
		if(validateGap(gap))
			this.right = gap;
		
		return this;
	}

	public Insets setTop(int gap)
	{
		if(validateGap(gap))
			this.top = gap;
		
		return this;
	}
	
	public Insets setBottom(int gap)
	{
		if(validateGap(gap))
			this.bottom = gap;
		
		return this;
	}

	private boolean validateGap(int gap)
	{
		if (gap < 0)
		{
			WidgetUtil.warn(String.format("Invalid gap! Gap must be greater or equals to 0! (%s < 0)", gap));
			return false;
		}

		return true;
	}
}
