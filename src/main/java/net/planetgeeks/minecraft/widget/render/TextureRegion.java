package net.planetgeeks.minecraft.widget.render;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ResourceLocation;
import net.planetgeeks.minecraft.widget.layout.Dimension;

/**
 */
public class TextureRegion extends Texture
{
	@Getter
	private final int textureX;
	@Getter
	private final int textureY;
	@Getter
	private final int regionWidth;
	@Getter
	private final int regionHeight;

	/**
	 * Create a new instance of TextureRegion coping the resource from another
	 * Texture object.
	 * 
	 * @param texture - the texture.
	 * @param textureX - the texture offset X coordinate.
	 * @param textureY - the texture offset Y coordinate.
	 * @param regionWidth - the texture region width.
	 * @param regionHeight - the texture region height.
	 */
	public TextureRegion(Texture texture, int textureX, int textureY, int regionWidth, int regionHeight)
	{
		this(texture.getResource(), textureX, textureY, regionWidth, regionHeight);
	}

	/**
	 * Create a new instance of TextureRegion.
	 * 
	 * @param resource - the resource.
	 * @param textureX - the texture offset X coordinate.
	 * @param textureY - the texture offset Y coordinate.
	 * @param regionWidth - the texture region width.
	 * @param regionHeight - the texture region height.
	 */
	public TextureRegion(@NonNull ResourceLocation resource, int textureX, int textureY, int regionWidth, int regionHeight)
	{
		super(resource);

		if (textureX < 0 || textureY < 0)
			throw new IllegalArgumentException(String.format("TextureRegion %s coordinate cannot be less than 0.", textureX < 0 ? (textureY < 0 ? "X and Y" : "X") : "Y"));

		if (regionWidth < 0 || regionHeight < 0)
			throw new IllegalArgumentException(String.format("TextureRegion %s cannot be less than 0.", regionWidth < 0 ? (regionHeight < 0 ? "width and height" : "width") : "height"));

		this.textureX = textureX;
		this.textureY = textureY;
		this.regionWidth = regionWidth;
		this.regionHeight = regionHeight;
	}

	/**
	 * Get a region of this TextureRegion.
	 * 
	 * @param textureX - the texture offset X coordinate.
	 * @param textureY - the texture offset Y coordinate.
	 * @param regionWidth - the texture region width.
	 * @param regionHeight - the texture region height.
	 * @return the splitted region.
	 */
	public TextureRegion split(int textureX, int textureY, int regionWidth, int regionHeight)
	{
		return new TextureRegion(this, this.textureX + textureX, this.textureY + textureY, regionWidth, regionHeight);
	}
	
	public Dimension getSize()
	{
		return new Dimension(getRegionWidth(), getRegionHeight());
	}
}
