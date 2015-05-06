package net.planetgeeks.minecraft.widget.util;

public interface Intersectable<T extends Intersectable<T>>
{
	/**
	 * Intersects two intersectable object of the same type.
	 * 
	 * @param intersectable object to intersect.
	 * @return the result of the intersection. May be null if the two objects don't intersect.
	 */
	T intersect(T intersectable);
}
