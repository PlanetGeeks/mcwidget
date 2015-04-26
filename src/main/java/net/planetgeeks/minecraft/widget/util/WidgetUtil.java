package net.planetgeeks.minecraft.widget.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.planetgeeks.minecraft.widget.render.Texture;

import org.lwjgl.input.Mouse;

public class WidgetUtil
{
	public static final Texture COMPONENTS_TEXTURE = guiTexture("components.png");

	public static Texture guiTexture(String path)
	{
		return new Texture(new ResourceLocation("widget", "textures/gui/" + path));
	}

	public static SoundHandler getSoundHandler()
	{
		return Minecraft.getMinecraft().getSoundHandler();
	}

	public static FontRenderer getFontRenderer()
	{
		return Minecraft.getMinecraft().fontRendererObj;
	}

	public static TextureManager getTextureManager()
	{
		return Minecraft.getMinecraft().getTextureManager();
	}

	public static RenderItem getItemRenderer()
	{
		return Minecraft.getMinecraft().getRenderItem();
	}

	public static void warn(String message)
	{
		System.err.println("[Warn] " + message);
	}

	public static void info(String message)
	{
		System.out.println("[Info] " + message);
	}

	public static void error(String message)
	{
		System.err.println("[Error] " + message);
	}

	public static void warn(String format, Object... args)
	{
		warn(String.format(format, args));
	}

	public static void info(String format, Object... args)
	{
		info(String.format(format, args));
	}

	public static void error(String format, Object... args)
	{
		error(String.format(format, args));
	}

	/**
	 * Get mouse x position on screen.
	 * <p>
	 * Supports minecraft's scaled resolution.
	 * 
	 * 
	 * @return the mouse x coordinate on screen.
	 */
	public static int getMouseX()
	{
		Minecraft mc = Minecraft.getMinecraft();
		return Mouse.getX() * (new ScaledResolution(mc, mc.displayWidth, mc.displayHeight)).getScaledWidth() / mc.displayWidth;
	}

	/**
	 * Get mouse y position on screen.
	 * <p>
	 * Supports minecraft's scaled resolution.
	 * 
	 * 
	 * @return the mouse y coordinate on screen.
	 */
	public static int getMouseY()
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int scaledHeight = resolution.getScaledHeight();
		return scaledHeight - Mouse.getY() * scaledHeight / mc.displayHeight - 1;
	}

	/**
	 * Get mouse position on screen.
	 * <p>
	 * Supports minecraft's scaled resolution.
	 * 
	 * @return a Point object that represents mouse position on screen.
	 */
	public static Point getMousePosition()
	{
		return new Point(getMouseX(), getMouseY());
	}
	
	public static String getStringFromClipboard()
	{
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try
		{
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		}
		catch (UnsupportedFlavorException e)
		{
			System.out.println("Clipboard content flavor is not supported " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("Clipboard content could not be retrieved " + e.getMessage());
		}
		return null;
	}

	public static void setStringToClipboard(String stringContent)
	{
		StringSelection stringSelection = new StringSelection(stringContent);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
}
