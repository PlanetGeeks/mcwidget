package net.planetgeeks.minecraft.widget.layout;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode
public class Dimension implements Cloneable
{
	@Getter
	private int width;
	@Getter
	private int height;

	public Dimension(int width, int height)
	{
		setWidth(width);
		setHeight(height);
	}

	public Dimension()
	{
		this(0, 0);
	}

	/**
	 * Set dimension's width.
	 * 
	 * @param width - an integer value greater or equals to 0.
	 * 
	 * 
	 * @return the changed dimension. * @throws IllegalArgumentException if the
	 *         argument is less than 0.
	 */
	public Dimension setWidth(int width)
	{
		this.width = checkComponent(width);

		return this;
	}

	/**
	 * Set dimension's height.
	 * 
	 * @param height - an integer value greater or equals to 0.
	 * 
	 * 
	 * @return the changed dimension. * @throws IllegalArgumentException if the
	 *         argument is less than 0.
	 */
	public Dimension setHeight(int height)
	{
		this.height = checkComponent(height);

		return this;
	}
	
	public Dimension set(int width, int height)
	{
		setWidth(width);
		setHeight(height);
		return this;
	}

	private int checkComponent(int component)
	{
		if (component < 0)
			throw new IllegalArgumentException("A negative dimension cannot be set!");

		return component;
	}

	/**
	 * Compares two Dimension objects.
	 * 
	 * @param dimension - the compared dimension.
	 * 
	 * @return the value 0 if this Dimension's width is equal to the argument's
	 *         width; a value less than 0 if this Dimension's width is
	 *         numerically less than the argument's width; and a value greater
	 *         than 0 if this Dimension's width is numerically greater than the
	 *         argument's width; and finally the value 1 if the argument is
	 *         null.
	 */
	public int compareWidth(Dimension dimension)
	{
		if (dimension == null)
			return 1;

		return getWidth() - dimension.getWidth();
	}

	/**
	 * Compares two Dimension objects.
	 * 
	 * @param dimension - the compared dimension.
	 * 
	 * @return the value 0 if this Dimension's height is equal to the argument's
	 *         height; a value less than 0 if this Dimension's height is
	 *         numerically less than the argument's height; and a value greater
	 *         than 0 if this Dimension's height is numerically greater than the
	 *         argument's height; and finally the value 1 if the argument is
	 *         null.
	 */
	public int compareHeight(Dimension dimension)
	{
		if (dimension == null)
			return 1;

		return getHeight() - dimension.getHeight();
	}
	
	@Override
	public Dimension clone()
	{
		return new Dimension(width, height);
	}
}
