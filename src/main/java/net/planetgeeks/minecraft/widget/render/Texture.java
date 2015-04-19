package net.planetgeeks.minecraft.widget.render;

import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.ResourceLocation;

public class Texture
{
	@Getter
	private final ResourceLocation resource;
	
	/**
	 * Create a new instance of Texture with the given resource.
	 * 
	 * @param resource - the texture resource location.
	 */
    public Texture(@NonNull ResourceLocation resource)
    {
    	this.resource = resource;
    }
}
