package net.planetgeeks.minecraft.widget.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode
@Data
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
	
	public int compareX(@NonNull Point point)
	{
		return Integer.valueOf(x).compareTo(point.x);
	}
	
	public int compareY(@NonNull Point point)
	{
		return Integer.valueOf(y).compareTo(point.y);
	}
	
	@Override
	public Point clone()
	{
		return new Point(x, y);
	}
}
