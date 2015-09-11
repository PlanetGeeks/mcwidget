package net.planetgeeks.minecraft.widget.render;

import java.util.ArrayList;
import java.util.Iterator;
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
import net.planetgeeks.minecraft.widget.layout.Dimension;
import net.planetgeeks.minecraft.widget.render.shape.Rectangle;
import net.planetgeeks.minecraft.widget.util.Color;
import net.planetgeeks.minecraft.widget.util.Point;
import net.planetgeeks.minecraft.widget.util.WidgetUtil;

import org.lwjgl.opengl.GL11;

public class WidgetRenderer
{
	@Getter
	private final Widget component;
	@Getter
	@Setter
	private FontRenderer fontRenderer;
	private RenderItem itemRenderer;
	private Color color = Color.WHITE;
	private Point translation = new Point(0, 0);
	private List<Restorable> pushes = new ArrayList<>();

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
	 * Rotating with a positive angle theta rotates points on the positive x axis toward the positive y axis.
	 * 
	 * @param theta the angle of rotation in radians
	 */
	public void rotate(double theta)
	{
		GL11.glRotated(theta, 0D, 0D, 1D);
	}
	
	/**
	 *  Rotating with a positive angle theta rotates points on the positive x axis toward the positive y axis.
	 *  
	 * @param theta the angle of rotation in radians
	 * @param x the x coordinate of the origin of the rotation
	 * @param y the y coordinate of the origin of the rotation
	 */
	public void rotate(double theta, int x, int y)
	{
		Point position = adjustPosition(x, y);
		GL11.glTranslatef(position.getX(), position.getY(), 0);
		GL11.glRotated(theta, 0D, 0D, 1D);
		GL11.glTranslatef(-position.getX(), -position.getY(), 0);
	}
	
	/**
	 *  Rotating with a positive angle theta rotates points on the positive x axis toward the positive y axis.
	 *  
	 * @param theta the angle of rotation in radians
	 * @param rotationOrigin the origin of the rotation
	 */
	public void rotate(double theta, @NonNull Point rotationOrigin)
	{
		rotate(theta, rotationOrigin.getX(), rotationOrigin.getY());
	}

	/**
	 * Draw a filled rectangle area using the set color {@link #getColor()}.
	 * 
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param width - the width of the rectangle.
	 * @param height - the height of the rectangle.
	 */
	public void drawFilledRect(int x, int y, int width, int height)
	{
		if (width < 0)
			throw new IllegalArgumentException("Rectangle width cannot be less than 0!");
		if (height < 0)
			throw new IllegalArgumentException("Rectangle height cannot be less than 0!");

		if (width == 0 || height == 0)
			return;

		Point p1 = adjustPosition(x, y);
		Point p2 = p1.clone().translate(width, height);

		drawFilledRect(new Rectangle(p1, p2));
	}

	private void drawFilledRect(@NonNull Rectangle rectangle)
	{
		Rectangle clipped = getClippedRect(rectangle);

		// System.out.println(clipped);

		if (clipped != null)
			Widget.drawRect(clipped.getX(), clipped.getY(), clipped.getX() + clipped.getWidth(), clipped.getY() + clipped.getHeight(), color.getHex());
	}

	private Rectangle getClippedRect(@NonNull Rectangle rectangle)
	{
		Rectangle clip = getComponent().getVisibleArea();

		return clip == null ? null : clip.intersect(rectangle);
	}

	/**
	 * Draw a rectangle area without filling it. The border will be rendered
	 * using the set color {@link #getColor()}.
	 * 
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param width - the width of the rectangle.
	 * @param height - the height of the rectangle.
	 */
	public void drawRect(int x, int y, int width, int height)
	{
		drawHorizontalLine(x, y, width);
		drawHorizontalLine(x, y + height, width);
		drawVerticalLine(x, y, height);
		drawVerticalLine(x + width, y, height);
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
		{
			Point p1 = adjustPosition(x, y);
			Point p2 = p1.clone().translate(length, 1);

			drawFilledRect(new Rectangle(p1, p2));
		}
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
		{
			Point p1 = adjustPosition(x, y);
			Point p2 = p1.clone().translate(1, length);

			drawFilledRect(new Rectangle(p1, p2));
		}
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
		Rectangle clipped = new Rectangle(position, getStringSize(text)).intersect(getComponent().getVisibleArea());

		if (clipped != null)
		{
			component.drawString(fontRenderer, fontRenderer.trimStringToWidth(text, clipped.getWidth()), position.getX(), position.getY(), color.getHex());
		}
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

		drawTexturedModalRect(new Rectangle(adjustPosition(x, y), textureRegion.getSize()), textureRegion.getTextureX(), textureRegion.getTextureY());
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
		bindTextureResource(textureRegion.getResource());

		Point position = adjustPosition(x, y);

		int filledWidth = 0;
		int filledHeight = 0;

		while (filledHeight < heightToFill)
		{
			int nextFillHeight = filledHeight + textureRegion.getHeight() > heightToFill ? heightToFill - filledHeight : textureRegion.getHeight();

			while (filledWidth < widthToFill)
			{
				int nextFillWidth = filledWidth + textureRegion.getWidth() > widthToFill ? widthToFill - filledWidth : textureRegion.getWidth();

				drawTexturedModalRect(new Rectangle(position, new Dimension(nextFillWidth, nextFillHeight)), textureRegion.getTextureX(), textureRegion.getTextureY());
				position.translate(nextFillWidth, 0);
				filledWidth += nextFillWidth;
			}

			position.translate(-filledWidth, nextFillHeight);
			filledWidth = 0;
			filledHeight += nextFillHeight;
		}
	}

	private void drawTexturedModalRect(@NonNull Rectangle rectangle, int textureX, int textureY)
	{
		Rectangle clipped = getClippedRect(rectangle);

		if (clipped != null)
		{
			int x = clipped.getX();
			int y = clipped.getY();
			int width = clipped.getWidth();
			int height = clipped.getHeight();
			textureX += clipped.getX() - rectangle.getX();
			textureY += clipped.getY() - rectangle.getY();
			component.drawTexturedModalRect(x, y, textureX, textureY, width, height);
		}
	}

	/**
	 * Draws an ItemStack at the given position.
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
	 * Draws an ItemStack with a custom overlay (the overlay rendered is usually
	 * the ItemStack's size) at the given position.
	 * 
	 * @param stack - the ItemStack to draw.
	 * @param x - the X coordinate.
	 * @param y - the Y coordinate.
	 * @param overlayText - the custom overlay text.
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

	private Dimension getStringSize(@NonNull String text)
	{
		return new Dimension(getStringWidth(text), getStringHeight());
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
		this.translation.translate(x, y);
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
			RestorableGroup pushGroup = new RestorableGroup();

			pushGroup.add(new RestorableObject<Color>(color.clone())
			{
				@Override
				public void restore()
				{
					color = getRestorableObj();
				}
			});

			pushGroup.add(new RestorableObject<Point>(translation.clone())
			{
				@Override
				public void restore()
				{
					translation = getRestorableObj();
				}
			});
			
			pushGroup.add(pushGLMatrix());

			this.pushes.add(pushGroup);
		}
	}
	
	private RestorableGLMatrix pushGLMatrix()
	{
		GL11.glPushMatrix();
		return new RestorableGLMatrix();
	}

	/**
	 * Set the draw color.
	 * 
	 * @param color - an Integer value that represents the color (e.g 0xffffff -
	 *            white).
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/**
	 * Get the draw color.
	 * 
	 * 
	 * @return the set color.
	 */
	public Color getColor()
	{
		return color;
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
				this.pushes.remove(this.pushes.size() - 1).restore();
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
		return new Point(x, y).translate(component.getXOnScreen(), component.getYOnScreen()).translate(translation);
	}

	interface Restorable
	{
		void restore();
	}

	abstract class RestorableObject<T> implements Restorable
	{
		@Getter
		private T restorableObj;

		public RestorableObject(T obj)
		{
			this.restorableObj = obj;
		}
	}
	
	class RestorableGLMatrix implements Restorable
	{
		@Override
		public void restore()
		{
			GL11.glPopMatrix();
		}
	}

	class RestorableGroup implements Restorable
	{
		private List<Restorable> elements = new ArrayList<>();

		public void add(Restorable element)
		{
			synchronized (elements)
			{
				elements.add(element);
			}
		}

		@Override
		public void restore()
		{
			synchronized (elements)
			{
				Iterator<Restorable> it = elements.iterator();

				while (it.hasNext())
				{
					it.next().restore();
					it.remove();
				}
			}
		}
	}
}
