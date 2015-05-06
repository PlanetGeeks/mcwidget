package net.planetgeeks.minecraft.widget;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.gui.GuiScreen;
import net.planetgeeks.minecraft.widget.components.WidgetPanel;
import net.planetgeeks.minecraft.widget.util.Screen;
import net.planetgeeks.minecraft.widget.util.WidgetParent;

public abstract class WidgetScreen extends GuiScreen implements Screen,
		WidgetParent
{
	@Getter
	private WidgetScreenPanel<WidgetScreen> contentPanel = new WidgetScreenPanel<>(this);
	private boolean initialized = false;

	@Override
	public final void initGui()
	{
		contentPanel.onScreenResize();

		if (!initialized)
		{
			onScreenInit();
			initialized = true;
		}

		onScreenResize();
	}

	/**
	 * Add the given component to the content panel of this screen.
	 * 
	 * @param component - the component.
	 * 
	 * @return true if the component has been added.
	 */
	@Override
	public boolean add(@NonNull Widget component)
	{
		return contentPanel != null ? contentPanel.add(component) : false;
	}

	/**
	 * Remove the given component from the content panel of this screen.
	 * 
	 * @param component - the component.
	 * 
	 * @return true if the component has been removed.
	 */
	@Override
	public boolean remove(@NonNull Widget component)
	{
		return contentPanel != null ? contentPanel.remove(component) : false;
	}

	/**
	 * Close this screen. This method is a wrapper method of the long form
	 * <code>closeGuiAndOpen(null)</code>
	 */
	public void closeGui()
	{
		this.mc.displayGuiScreen((GuiScreen) null);
	}

	/**
	 * Close this screen and open the in-game screen.
	 */
	public void closeGuiAndRestoreIngame()
	{
		closeGui();

		if (this.mc.currentScreen == null)
			this.mc.setIngameFocus();
	}

	/**
	 * Close this screen and open another screen.
	 * 
	 * @param screen - the screen to open.
	 */
	public void closeGuiAndOpen(GuiScreen screen)
	{
		closeGui();

		this.mc.displayGuiScreen(screen);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.contentPanel.onUpdate();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.contentPanel.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseReleased(mouseX, mouseY, mouseButton);
		this.contentPanel.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		this.contentPanel.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		this.contentPanel.handleMouseInput();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.contentPanel.draw(mouseX, mouseY, partialTicks, contentPanel.getRenderer());
	}

	public static class WidgetScreenPanel<T extends GuiScreen> extends WidgetPanel
	{
		private T screen;

		public WidgetScreenPanel(T screen)
		{
			super(0, 0);
			this.screen = screen;
		}

		protected void onScreenResize()
		{
			this.setWidth(screen.width);
			this.setHeight(screen.height);
		}

		@Override
		public void onUpdate()
		{
			super.onUpdate();
		}
		
		@Override
		public void handleMouseInput()
		{
			super.handleMouseInput();
		}
	}
}
