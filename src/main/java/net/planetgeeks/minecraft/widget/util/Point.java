package net.planetgeeks.minecraft.widget.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode
public class Point implements Cloneable
{
	@Getter @Setter
	private int x;
	@Getter @Setter
	private int y;

	public Point()
	{
		this(0, 0);
	}
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Point translate(int xTranslation, int yTranslation)
	{
		this.x += xTranslation;
		this.y += yTranslation;

		return this;
	}

	public Point translate(@NonNull Point translation)
	{
		return translate(translation.x, translation.y);
	}

	public Point moveTo(@NonNull Point point)
	{
		return moveTo(point.x, point.y);
	}
	
	public Point moveTo(int x, int y)
	{
	    this.x = x;
	    this.y = y;
	    
	    return this;
	}
	
	@Override
	public Point clone()
	{
		return new Point(x, y);
	}
}
