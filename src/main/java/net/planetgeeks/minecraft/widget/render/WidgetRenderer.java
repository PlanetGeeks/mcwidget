package net.planetgeeks.minecraft.widget.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.planetgeeks.minecraft.widget.Widget;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

public class WidgetRenderer
{
	@Getter
	private final Widget component;
	@Getter
	@Setter
	private FontRenderer fontRenderer;
	private RenderItem itemRenderer;
	private State state = new State();
	private List<State> pushes = new ArrayList<>();

	public WidgetRenderer(@NonNull Widget component, FontRenderer fontRenderer, RenderItem itemRenderer)
	{
		this.component = component;
		this.fontRenderer = fontRenderer;
		this.itemRenderer = itemRenderer;
	}

	public WidgetRenderer(@NonNull Widget component)
	{
		this(component, WidgetUtil.getFontRenderer(), WidgetUtil.getItemRenderer());
	}

	/**
	 * Draw a rectangle area using the set color {@link #getColor()}.
	 * 
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param width - the width of the rectangle.
	 * @param height - the height of the rectangle.
	 */
	public void drawRect(int x, int y, int width, int height)
	{
		drawRect(adjustPosition(x, y), adjustPosition(x, y).translate(width, height));
	}

	private void drawRect(Point p1, Point p2)
	{
		Widget.drawRect(p1.getX(), p1.getY(), p2.getX(), p2.getY(), new Color(state.color).getRGB());
	}

	/**
	 * Draw an horizontal line using the set color {@link #getColor()}.
	 * 
	 * @param x - the start X coordinate.
	 * @param y - the start Y coordinate.
	 * @param length - a positive value to render the line on the right, a
	 *            negative value to render the line on the left. The zero value
	 *            will not render the line.
	 */
	public void drawHorizontalLine(int x, int y, int length)
	{
		if (length != 0)
			drawHorizontalLine(adjustPosition(x, y), adjustPosition(x + (length > 0 ? length - 1 : length + 1), y));
	}

	private void drawHorizontalLine(Point startPoint, Point endPoint)
	{
		component.drawHorizontalLine(startPoint.getX(), endPoint.getX(), startPoint.getY(), new Color(state.color).getRGB());
	}

	/**
	 * Draw a vertical line using the set color {@link #getColor()}.
	 * 
	 * @param x - the start X coordinate.
	 * @param y - the start Y coordinate.
	 * @param length - a positive value to render the line on the top, a
	 *            negative value to render the line on the bottom. The zero
	 *            value will not render the line.
	 */
	public void drawVerticalLine(int x, int y, int length)
	{
		if (length != 0)
			drawVerticalLine(adjustPosition(x, y), adjustPosition(x, y + (length > 0 ? length - 1 : length + 1)));
	}

	private void drawVerticalLine(Point startPoint, Point endPoint)
	{
		component.drawVerticalLine(startPoint.getY(), endPoint.getY(), startPoint.getX(), new Color(state.color).getRGB());
	}

	/**
	 * Draw a String at the given position.
	 * 
	 * @param text - the String to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 */
	public void drawString(@NonNull String text, int x, int y)
	{
		drawString(text, adjustPosition(x, y + 1 /* FIX String y */));
	}

	private void drawString(String text, Point position)
	{
		component.drawString(fontRenderer, text, position.getX(), position.getY(), new Color(state.color).getRGB());
	}

	/**
	 * Draw a texture with offset at {0,0}.
	 * 
	 * @param texture - the texture to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param width - the width to draw. It also represents the width picked
	 *            from the texture.
	 * @param height - the height to draw. It also represents the height picked
	 *            from the texture.
	 */
	public void drawTexture(@NonNull Texture texture, int x, int y, int width, int height)
	{
		drawTexture(texture, x, y, width, height, 0, 0);
	}

	/**
	 * Draw a texture with offset at {textureX, textureY}.
	 * 
	 * @param texture - the texture to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param width - the width to draw. It also represents the width picked
	 *            from the texture.
	 * @param height - the height to draw. It also represents the height picked
	 *            from the texture.
	 * @param textureX - the texture offset's X coordinate.
	 * @param textureY - the texture offset's Y coordinate.
	 */
	public void drawTexture(@NonNull Texture texture, int x, int y, int width, int height, int textureX, int textureY)
	{
		drawTexture(new TextureRegion(texture, textureX, textureY, width, height), x, y);
	}

	/**
	 * Draw a texture region.
	 * <p>
	 * Note that {@link #drawTexture(texture, x, y, width, height)} and
	 * {@link #drawTexture(texture, x, y, width, height, textureX, textureY)}
	 * are wrapper methods of this one.
	 * 
	 * @param textureRegion - the texture region to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 */
	public void drawTexture(TextureRegion textureRegion, int x, int y)
	{
		bindTextureResource(textureRegion.getResource());

		drawTexturedModalRect(x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), textureRegion.getTextureX(), textureRegion.getTextureY());
	}

	/**
	 * Fill the given size with a texture region.
	 * <p>
	 * If the TextureRegion is less than the area to draw, it will be repeated
	 * to cover the area entirely.
	 * 
	 * @param textureRegion - the texture region to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param widthToFill - the width to fill.
	 * @param heightToFill - the height to fill.
	 */
	public void drawTexture(TextureRegion textureRegion, int x, int y, int widthToFill, int heightToFill)
	{
		Point position = adjustPosition(x, y);

		int filledWidth = 0;
		int filledHeight = 0;

		while (filledHeight < heightToFill)
		{
			int nextFillHeight = filledHeight + textureRegion.getRegionHeight() > heightToFill ? heightToFill - filledHeight : textureRegion.getRegionHeight();

			while (filledWidth < widthToFill)
			{
				int nextFillWidth = filledWidth + textureRegion.getRegionWidth() > widthToFill ? widthToFill - filledWidth : textureRegion.getRegionWidth();

				drawTexturedModalRect(position, textureRegion.getTextureX(), textureRegion.getTextureY(), nextFillWidth, nextFillHeight);
				position.translate(nextFillWidth, 0);
				filledWidth += nextFillWidth;

			}

			position.translate(-filledWidth, nextFillHeight);
			filledWidth = 0;
			filledHeight += nextFillHeight;
		}
	}

	private void drawTexturedModalRect(int x, int y, int width, int height, int textureX, int textureY)
	{
		drawTexturedModalRect(adjustPosition(x, y), textureX, textureY, width, height);
	}

	private void drawTexturedModalRect(Point position, int textureX, int textureY, int width, int height)
	{
		component.drawTexturedModalRect(position.getX(), position.getY(), textureX, textureY, width, height);
	}

	/**
	 * Draw an ItemStack at the given position.
	 * 
	 * @param stack - the ItemStack to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 */
	public void drawItemStack(@NonNull ItemStack stack, int x, int y)
	{
		drawItemStackWithOverlay(stack, x, y, null);
	}

	/**
	 * Draw an ItemStack with a custom overlay (where usually is rendered
	 * ItemStack's size) at the given position.
	 * 
	 * @param stack - the ItemStack to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param overlayText
	 */
	public void drawItemStackWithOverlay(ItemStack stack, int x, int y, String overlayText)
	{
		drawItemStack(stack, adjustPosition(x, y), overlayText);
	}

	private void drawItemStack(ItemStack stack, Point position, String overlayText)
	{
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		component.setZLevel(200.0F);
		itemRenderer.zLevel = 200.0F;
		itemRenderer.renderItemAndEffectIntoGUI(stack, position.getX(), position.getY());
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, stack, position.getX(), position.getY(), overlayText);
		component.setZLevel(0.0F);
		itemRenderer.zLevel = 0.0F;
		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Bind a texture resource to Minecraft's TextureManager.
	 * 
	 * @param resource - the resource to bind.
	 */
	public void bindTextureResource(ResourceLocation resource)
	{
		WidgetUtil.getTextureManager().bindTexture(resource);
	}

	/**
	 * Get the width of a rendered String.
	 * 
	 * @param text - the String object.
	 * 
	 * @return the width of the red String.
	 */
	public int getStringWidth(@NonNull String text)
	{
		return fontRenderer.getStringWidth(text);
	}

	/**
	 * Get the height of a rendered String. Always return the FontRenderer
	 * FONT_HEIGHT constant.
	 * 
	 * 
	 * @return the rendered String height.
	 */
	public int getStringHeight()
	{
		return fontRenderer.FONT_HEIGHT;
	}

	/**
	 * Translate rendering.
	 * <p>
	 * It's suggested to call this method between {@link #push()} and
	 * {@link #pop()}.
	 * 
	 * @param x - the x translation to apply.
	 * @param y - the y translation to apply.
	 */
	public void translate(int x, int y)
	{
		this.state.translate(x, y);
	}

	/**
	 * Preserve current renderer state. It can be restored using {@link #pop()}
	 * <p>
	 * It's suggested to pop every pushed state at the end of the renderer. See
	 * {@link #popAll()}
	 */
	public void push()
	{
		synchronized (pushes)
		{
			this.pushes.add(state.clone());
		}
	}

	/**
	 * Set the draw color.
	 * 
	 * @param color - an Integer value that represents the color (e.g 0xffffff -
	 *            white).
	 */
	public void setColor(int color)
	{
		state.color = color;
	}

	/**
	 * Get the draw color.
	 * 
	 * 
	 * @return the set color.
	 */
	public int getColor()
	{
		return state.color;
	}

	/**
	 * Restore the latest pushed state.
	 * 
	 * 
	 * @return true if a state has been restored, false if there's no state to
	 *         restore.
	 */
	public boolean pop()
	{
		synchronized (pushes)
		{
			if (this.pushes.size() > 0)
			{
				this.state = this.pushes.remove(this.pushes.size() - 1);
				return true;
			}
		}

		return false;
	}

	/**
	 * Restore all pushed states.
	 */
	public void popAll()
	{
		while (pop())
			;
	}

	private Point adjustPosition(int x, int y)
	{
		return new Point(x, y).translate(component.getXOnScreen(), component.getYOnScreen()).translate(state.translation);
	}

	class State implements Cloneable
	{
		private int color;
		private Point translation;

		public State(Point translation, int color)
		{
			this.color = color;
			this.translation = translation;
		}

		public State()
		{
			this(new Point(0, 0), 0xfffff);
		}

		@Override
		public State clone()
		{
			return new State(translation.clone(), color);
		}

		public void translate(int x, int y)
		{
			this.translation.translate(x, y);
		}

		public void translate(@NonNull Point translation)
		{
			this.translation.translate(translation);
		}
	}
}
