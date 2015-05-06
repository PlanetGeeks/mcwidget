package net.planetgeeks.minecraft.widget.util;

public class MathUtil
{
	public static <T extends Comparable<T>> T clamp(T minimum, T value, T maximum)
	{
		return value.compareTo(minimum) < 0 ? minimum : (value.compareTo(maximum) > 0 ? maximum : value);
	}
}
