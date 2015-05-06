package net.planetgeeks.minecraft.widget.util;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Color implements Cloneable
{
	/**
	 * The color white. In the default sRGB space.
	 */
	public final static Color WHITE = new Color(255, 255, 255);

	/**
	 * The color light gray. In the default sRGB space.
	 */
	public final static Color LIGHT_GRAY = new Color(192, 192, 192);

	/**
	 * The color gray. In the default sRGB space.
	 */
	public final static Color GRAY = new Color(128, 128, 128);

	/**
	 * The color dark gray. In the default sRGB space.
	 */
	public final static Color DARK_GRAY = new Color(64, 64, 64);

	/**
	 * The color black. In the default sRGB space.
	 */
	public final static Color BLACK = new Color(0, 0, 0);

	/**
	 * The color red. In the default sRGB space.
	 */
	public final static Color RED = new Color(255, 0, 0);

	/**
	 * The color pink. In the default sRGB space.
	 */
	public final static Color PINK = new Color(255, 175, 175);

	/**
	 * The color orange. In the default sRGB space.
	 */
	public final static Color ORANGE = new Color(255, 200, 0);

	/**
	 * The color yellow. In the default sRGB space.
	 */
	public final static Color YELLOW = new Color(255, 255, 0);

	/**
	 * The color green. In the default sRGB space.
	 */
	public final static Color GREEN = new Color(0, 255, 0);

	/**
	 * The color magenta. In the default sRGB space.
	 */
	public final static Color MAGENTA = new Color(255, 0, 255);

	/**
	 * The color cyan. In the default sRGB space.
	 */
	public final static Color CYAN = new Color(0, 255, 255);

	/**
	 * The color blue. In the default sRGB space.
	 */
	public final static Color BLUE = new Color(0, 0, 255);

	private final int hexColor;

	public Color(float red, float green, float blue, float alpha)
	{
		hexColor = encodeToHex(red, green, blue, alpha);
	}

	public Color(float red, float green, float blue)
	{
		this(red, green, blue, 1.0F);
	}

	public Color(int red, int green, int blue, int alpha)
	{
		hexColor = encodeToHex(red, green, blue, alpha);
	}

	public Color(int red, int green, int blue)
	{
		this(red, green, blue, 255);
	}

	public Color(int hexColor)
	{
		this.hexColor = hexColor;
	}

	public static int encodeToHex(int red, int green, int blue, int alpha)
	{
		return Integer.parseUnsignedInt(String.format("%02x%02x%02x%02x", alpha, red, green, blue), 16);
	}

	public static int encodeToHex(float red, float green, float blue, float alpha)
	{
		red = (red > 1.0F ? 1.0F : (red < 0.0F ? 1.0F : red)) * 255;
		green = (green > 1.0F ? 1.0F : (green < 0.0F ? 1.0F : green)) * 255;
		blue = (blue > 1.0F ? 1.0F : (blue < 0.0F ? 1.0F : blue)) * 255;
		alpha = (alpha > 1.0F ? 1.0F : (alpha < 0.0F ? 1.0F : alpha)) * 255;

		return encodeToHex((int) red, (int) green, (int) blue, (int) alpha);
	}

	public int getHex()
	{
		return hexColor;
	}

	public int getRed()
	{
		return hexColor >> 16 & 255;
	}

	public float getRedAsFloat()
	{
		return getRed() / 255.0F;
	}

	public int getGreen()
	{
		return hexColor >> 8 & 255;
	}

	public float getGreenAsFloat()
	{
		return getGreen() / 255.0F;
	}

	public int getBlue()
	{
		return hexColor & 255;
	}

	public float getBlueAsFloat()
	{
		return getBlue() / 255.0F;
	}

	public int getAlpha()
	{
		return hexColor >> 24 & 255;
	}

	public float getAlphaAsFloat()
	{
		return getAlpha() / 255.0F;
	}
	
	@Override
	public Color clone()
	{
		return new Color(hexColor);
	}
}
