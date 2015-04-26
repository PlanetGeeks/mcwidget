package net.planetgeeks.minecraft.widget.interactive;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.util.Point;

@Getter
@Setter
public class WidgetHitbox
{
	private @NonNull Widget component;
	private int width;
	private int height;

	public WidgetHitbox(@NonNull Widget component, int width, int height)
	{
		this.component = component;
		this.width = width;
		this.height = height;
	}

	/**
	 * Wrapper method of {@link #isPointInside(Point)}.
	 * 
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * @return true if the point is inside the Hitbox.
	 */
	public boolean isPointInside(int x, int y)
	{
		return isPointInside(new Point(x, y));
	}

	/**
	 * Check if the given point on the screen is inside this HitBox.
	 * 
	 * @param x - the x coordinate on the screen.
	 * @param y - the y coordinate on the screen.
	 * 
	 * @return true if the point is inside the HitBox.
	 */
	public boolean isPointInside(Point point)
	{
		return point.getX() >= component.getXOnScreen() && point.getX() < component.getXOnScreen() + getWidth() && point.getY() >= component.getYOnScreen() && point.getY() < component.getYOnScreen() + getHeight();
	}

	public static class Dynamic extends WidgetHitbox
	{
		public Dynamic(@NonNull Widget component)
		{
			super(component, -1, -1);
		}

		@Override
		public int getWidth()
		{
			return super.getWidth() >= 0 ? super.getWidth() : getComponent().getWidth();
		}

		@Override
		public int getHeight()
		{
			return super.getHeight() >= 0 ? super.getHeight() : getComponent().getHeight();
		}
	}
}
