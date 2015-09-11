package net.planetgeeks.minecraft.widget.layout;

import lombok.Getter;

public class Gap
{
	@Getter
	private int minimumSize = 0;
	@Getter
	private int maximumSize = Integer.MAX_VALUE;
	@Getter
    private int size = 0;
	
	public Gap()
	{
		this(0);
	}
	
	public Gap(int size)
	{
		setSize(size);
	}
	
	public Gap(int minimumSize, int size, int maximumSize)
	{
		this(size);
		setMinimumSize(minimumSize);
		setMaximumSize(maximumSize);
	}
	
	/**
	 * Set gap size.
	 * 
	 * @param size - the size to set.
	 * @return the effective size set.
	 */
	public int setSize(int size)
	{
		if(size < 0)
			throw new IllegalArgumentException("Gap size cannot be less than 0!");
		
		this.size = size > maximumSize ? maximumSize : (size < minimumSize ? minimumSize : size);
		return this.size;
	}
	
	public void setMinimumSize(int minimumSize)
	{
		if(minimumSize < 0)
			throw new IllegalArgumentException("Gap minimum size cannot be less than 0!");
		
		if(minimumSize > maximumSize)
			throw new IllegalArgumentException("Gap minimum size cannot be greater than maximum size!");
		
		this.minimumSize = minimumSize;
		
		setSize(getSize());
	}
	
	public void setMaximumSize(int maximumSize)
	{
		if(maximumSize < 0)
			throw new IllegalArgumentException("Gap maximum size cannot be less than 0!");
		
		if(maximumSize < minimumSize)
			throw new IllegalArgumentException("Gap maximum size cannot be less than minimum size!");
		
		this.maximumSize = maximumSize;
		
		setSize(getSize());
	}
	
	public static Gap fixedGap(int fixedSize)
	{
		return new Gap(fixedSize, fixedSize, fixedSize);
	}
}
