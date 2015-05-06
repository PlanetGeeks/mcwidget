package net.planetgeeks.minecraft.widget.render.shape;

import lombok.Data;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.util.Intersectable;
import net.planetgeeks.minecraft.widget.util.Point;

@Data
public class Rectangle implements Intersectable<Rectangle>, Shape
{
	private Point position = new Point();
	private Dimension size = new Dimension();

	public Rectangle(@NonNull Point p1, @NonNull Point p2)
	{
	    int x = p1.compareX(p2) < 0 ? p1.getX() : p2.getX();
	    int y = p1.compareY(p2) < 0 ? p1.getY() : p2.getY();
	    int width = Math.abs(p1.getX() - p2.getX());
	    int height = Math.abs(p1.getY() - p2.getY());
	    
	    this.position.moveTo(new Point(x, y));
	    this.size.set(width, height);
	}
	
	public Rectangle(@NonNull Point position, @NonNull Dimension size)
	{
		this.position.moveTo(position);
		this.size.set(size.getWidth(), size.getHeight());
	}

	public Rectangle(int x, int y, int width, int height)
	{
		this.position.moveTo(x, y);
		this.size.set(width, height);
	}

	@Override
	public Rectangle intersect(Rectangle area)
	{
		if (area == null)
			return null;

		Segment horizontal = getHorizontalSegment().intersect(area.getHorizontalSegment());
		
		if(horizontal == null)
			return null;
		
		Segment vertical = getVerticalSegment().intersect(area.getVerticalSegment());
		
		if(vertical == null)
			return null;

		return new Rectangle(new Point(horizontal.start, vertical.start), new Dimension(horizontal.end - horizontal.start, vertical.end - vertical.start));
	} 
	
	public boolean isInside(Point point)
	{
		return point.getX() >= getX() && point.getX() < getX() + getWidth() && point.getY() >= getY() && point.getY() < getY() + getHeight();
	}

	public Point getPosition()
	{
		return position;
	}

	/**
	 * @return the area size.
	 */
	public Dimension getSize()
	{
		return size;
	}

	public int getX()
	{
		return position.getX();
	}

	public int getY()
	{
		return position.getY();
	}

	public int getWidth()
	{
		return size.getWidth();
	}

	public int getHeight()
	{
		return size.getHeight();
	}

	private Segment getHorizontalSegment()
	{
		return new Segment(getX(), getX() + getWidth());
	}

	private Segment getVerticalSegment()
	{
		return new Segment(getY(), getY() + getHeight());
	}

	class Segment implements Intersectable<Segment>
	{
		private int start;
		private int end;

		public Segment(int a, int b)
		{
			this.start = a < b ? a : b;
			this.end = a < b ? b : a;
		}

		@Override
		public Segment intersect(Segment segment)
		{
			if (segment == null)
				return null;
			if (segment.getLength() <= 0 || getLength() <= 0 || segment.getStart() < getStart() && segment.getEnd() <= getStart() || getStart() < segment.getStart() && getEnd() <= segment.getStart())
				return null;

			return new Segment(segment.getStart() > getStart() ? segment.getStart() : getStart(), segment.getEnd() < getEnd() ? segment.getEnd() : getEnd());
		}

		public int getStart()
		{
			return start;
		}

		public int getEnd()
		{
			return end;
		}
		
		public int getLength()
		{
			return end - start;
		}
	}
}
