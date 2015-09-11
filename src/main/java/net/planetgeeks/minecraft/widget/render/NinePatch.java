package net.planetgeeks.minecraft.widget.render;

import lombok.Getter;
import lombok.NonNull;
import net.planetgeeks.minecraft.widget.events.WidgetResizeEvent;
import net.planetgeeks.minecraft.widget.interactive.WidgetInteractive;
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.util.Drawable;

import com.google.common.eventbus.Subscribe;

public class NinePatch implements Drawable
{
	private static final byte TOP_LEFT_CORNER = 0, MIDDLE_TOP = 1,
			TOP_RIGHT_CORNER = 2, MIDDLE_LEFT = 3, CENTER = 4,
			MIDDLE_RIGHT = 5, BOTTOM_LEFT_CORNER = 6, MIDDLE_BOTTOM = 7,
			BOTTOM_RIGHT_CORNER = 8;

	@Getter
	private final TextureRegion texture;
	private TextureRegion[] patchedTextures = new TextureRegion[9];
	@Getter
	private int left;
	@Getter
	private int top;
	@Getter
	private int right;
	@Getter
	private int bottom;
	private int middleWidth;
	private int middleHeight;

	public NinePatch(TextureRegion texture, int left, int top, int right, int bottom)
	{
		this(texture, left, top, right, bottom, 0, 0);
	}

	public NinePatch(TextureRegion texture, int left, int top, int right, int bottom, int middleWidth, int middleHeight)
	{
		this.texture = texture;
		setLeft(left, false);
		setRight(right, false);
		setTop(top, false);
		setBottom(bottom, false);
		setMiddleWidth(middleWidth, false);
		setMiddleHeight(middleHeight, false);
		updateTextures();
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks, WidgetRenderer renderer)
	{
		renderer.drawTexture(patchedTextures[TOP_LEFT_CORNER], 0, 0);
		renderer.drawTexture(patchedTextures[TOP_RIGHT_CORNER], left + middleWidth, 0);
		renderer.drawTexture(patchedTextures[MIDDLE_TOP], left, 0, middleWidth, top);
		renderer.drawTexture(patchedTextures[MIDDLE_LEFT], 0, top, left, middleHeight);
		renderer.drawTexture(patchedTextures[CENTER], left, top, middleWidth, middleHeight);
		renderer.drawTexture(patchedTextures[MIDDLE_RIGHT], left + middleWidth, top, right, middleHeight);
		renderer.drawTexture(patchedTextures[BOTTOM_LEFT_CORNER], 0, top + middleHeight);
		renderer.drawTexture(patchedTextures[BOTTOM_RIGHT_CORNER], left + middleWidth, top + middleHeight);
		renderer.drawTexture(patchedTextures[MIDDLE_BOTTOM], left, top + middleHeight, middleWidth, bottom);
	}

	private void updateTextures()
	{
		patchedTextures[TOP_LEFT_CORNER] = texture.split(0, 0, left, top);
		patchedTextures[TOP_RIGHT_CORNER] = texture.split(texture.getWidth() - right, 0, right, top);
		patchedTextures[MIDDLE_TOP] = texture.split(left, 0, texture.getWidth() - left - right, top);
		patchedTextures[MIDDLE_LEFT] = texture.split(0, top, left, texture.getHeight() - top - bottom);
		patchedTextures[CENTER] = texture.split(left, top, texture.getWidth() - left - right, texture.getHeight() - top - bottom);
		patchedTextures[MIDDLE_RIGHT] = texture.split(texture.getWidth() - right, top, right, texture.getHeight() - top - bottom);
		patchedTextures[BOTTOM_LEFT_CORNER] = texture.split(0, texture.getHeight() - bottom, left, bottom);
		patchedTextures[BOTTOM_RIGHT_CORNER] = texture.split(texture.getWidth() - right, texture.getHeight() - bottom, right, bottom);
		patchedTextures[MIDDLE_BOTTOM] = texture.split(left, texture.getHeight() - bottom, texture.getWidth() - left - right, bottom);
	}

	private void checkWidth()
	{
		if (right + left > texture.getWidth())
			throw new IllegalArgumentException("Invalid nine patch! The result of left plus right must be less or equals to texture region width.");
	}

	private void checkHeight()
	{
		if (top + bottom > texture.getHeight())
			throw new IllegalArgumentException("Invalid nine patch! The result of top plus bottom must be less or equals to texture region height.");
	}

	public NinePatch setLeft(int left)
	{
		return setLeft(left, true);
	}

	private NinePatch setLeft(int left, boolean updateTextures)
	{
		this.left = left;
		checkWidth();
		if (updateTextures)
			updateTextures();
		return this;
	}

	public NinePatch setTop(int top)
	{
		return setTop(top, true);
	}

	private NinePatch setTop(int top, boolean updateTextures)
	{
		this.top = top;
		checkHeight();
		if (updateTextures)
			updateTextures();
		return this;
	}

	public NinePatch setRight(int right)
	{
		return setRight(right, true);
	}

	private NinePatch setRight(int right, boolean updateTextures)
	{
		this.right = right;
		checkWidth();
		if (updateTextures)
			updateTextures();
		return this;
	}

	public NinePatch setBottom(int bottom)
	{
		return setBottom(bottom, true);
	}

	private NinePatch setBottom(int bottom, boolean updateTextures)
	{
		this.bottom = bottom;
		checkHeight();
		if (updateTextures)
			updateTextures();
		return this;
	}

	/**
	 * Set the width of the middle column of the patch.
	 * 
	 * @param middleWidth - the middle width.
	 * @return this object.
	 */
	public NinePatch setMiddleWidth(int middleWidth)
	{
		return setMiddleWidth(middleWidth, true);
	}

	private NinePatch setMiddleWidth(int middleWidth, boolean updateTextures)
	{
		if (middleWidth < 0)
			throw new IllegalArgumentException("Middle width cannot be less than 0!");

		this.middleWidth = middleWidth;

		if (updateTextures)
			updateTextures();

		return this;
	}

	/**
	 * Set the height of the middle row of the patch.
	 * 
	 * @param middleHeight - the middle height.
	 * @return this object.
	 */
	public NinePatch setMiddleHeight(int middleHeight)
	{
		return setMiddleHeight(middleHeight, true);
	}

	private NinePatch setMiddleHeight(int middleHeight, boolean updateTextures)
	{
		if (middleHeight < 0)
			throw new IllegalArgumentException("Middle height cannot be less than 0!");

		this.middleHeight = middleHeight;

		if (updateTextures)
			updateTextures();

		return this;
	}

	/**
	 * Get the width of the middle column of the patch.
	 * 
	 * @return the width of the middle column.
	 */
	public int getMiddleWidth()
	{
		return middleWidth;
	}

	/**
	 * Get the height of the middle row of the patch.
	 * 
	 * @return the height of the middle row.
	 */
	public int getMiddleHeight()
	{
		return middleHeight;
	}

	/**
	 * Set the size of the patch.
	 * 
	 * @param width - the width to set.
	 * @param height - the height to set.
	 * @return this object.
	 * @see {@link #setTotalWidth(int)} and {@link #setTotalHeight(int)}.
	 */
	public NinePatch setSize(int width, int height)
	{
		return setTotalWidth(width).setTotalHeight(height);
	}
	
	/**
	 * Set the size of the patch.
	 * <p>
	 * Wrapper method of {@link #setSize(int, int)}.
	 * @param size - the size to set.
	 * @return this object.
	 */
	public NinePatch setSize(@NonNull Dimension size)
	{
		return setSize(size.getWidth(), size.getHeight());
	}
	
	/**
	 * Set total width.
	 * <p>
	 * It set the middle width calculated by removing left and right from the total width.
	 * 
	 * @param totalWidth - the total width to set. It must be greater or equals to the result of left plus right.
	 * @return this object.
	 */
	public NinePatch setTotalWidth(int totalWidth)
	{
		if (totalWidth < left + right)
			throw new IllegalArgumentException("Total width cannot be less than left plus right!");

		return setMiddleWidth(totalWidth - left - right);
	}

	/**
	 * Set total height.
	 * <p>
	 * It set the middle height calculated by removing top and bottom from the total width.
	 * 
	 * @param totalHeight - tht total height to set. It must be greater or equals to the result of top plus bottom.
	 * @return this object.
	 */ 
	public NinePatch setTotalHeight(int totalHeight)
	{
		if (totalHeight < top + bottom)
			throw new IllegalArgumentException("Total height cannot be less than top plus bottom!");

		return setMiddleHeight(totalHeight - top - bottom);
	}

	/**
	 * Get total width.
	 * 
	 * @return the result of {@link #getLeft()} + {@link #getMiddleWidth()} +
	 *         {@link #getRight()}.
	 */
	public int getTotalWidth()
	{
		return left + middleWidth + right;
	}

	/**
	 * Get total height.
	 * 
	 * @return the result of {@link #getTop()} + {@link #getMiddleHeight()} +
	 *         {@link #getBottom()}.
	 */
	public int getTotalHeight()
	{
		return top + middleHeight + bottom;
	}
	
	public static class Dynamic extends NinePatch
	{
		@Getter
		private WidgetInteractive component;
		
		public Dynamic(@NonNull WidgetInteractive component, TextureRegion texture, int left, int top, int right, int bottom)
		{
			this(component, texture, left, top, right, bottom, 0, 0);
		}
		
		public Dynamic(@NonNull WidgetInteractive component, TextureRegion texture, int left, int top, int right, int bottom, int middleWidth, int middleHeight)
		{
			super(texture, left, top, right, bottom, middleWidth, middleHeight);
			this.component = component;
			this.component.getEventBus().register(this);
		}

		@Subscribe
		public void onResize(WidgetResizeEvent event)
		{
			setSize(component.getSize());
		}
	}
}
