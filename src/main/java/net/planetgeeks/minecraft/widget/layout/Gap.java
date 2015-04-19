package net.planetgeeks.minecraft.widget.layout;

import lombok.Getter;

public class Gap
{
	@Getter
    private int size;
	
	public Gap(int size)
	{
		setSize(size);
	}
	
	public void setSize(int size)
	{
		if(size < 0)
			throw new IllegalArgumentException("Gap cannot be less than 0");
		
		this.size = size;
	}
}
