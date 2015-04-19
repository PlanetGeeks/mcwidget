package net.planetgeeks.minecraft.widget.layout;

import lombok.Getter;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

@Getter
public class SideGap
{
	private int leftGap = 0;
	private int rightGap = 0;
	private int topGap = 0;
	private int bottomGap = 0;

	public SideGap(){}

	public SideGap(int left, int top, int right, int bottom)
	{
	    setLeftGap(left);
	    setTopGap(top);
	    setRightGap(right);
	    setBottomGap(bottom);
	}

	public SideGap setLeftGap(int gap)
	{
		if (validateGap(gap))
			this.leftGap = gap;
		
		return this;
	}

	public SideGap setRightGap(int gap)
	{
		if(validateGap(gap))
			this.rightGap = gap;
		
		return this;
	}

	public SideGap setTopGap(int gap)
	{
		if(validateGap(gap))
			this.topGap = gap;
		
		return this;
	}
	
	public SideGap setBottomGap(int gap)
	{
		if(validateGap(gap))
			this.bottomGap = gap;
		
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
