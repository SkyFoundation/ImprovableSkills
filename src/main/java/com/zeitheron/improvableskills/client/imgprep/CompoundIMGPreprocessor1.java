package com.zeitheron.improvableskills.client.imgprep;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zeitheron.hammercore.client.utils.IImagePreprocessor;

public class CompoundIMGPreprocessor1 implements IImagePreprocessor
{
	public final int width, height;
	
	public CompoundIMGPreprocessor1(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	@Override
	public BufferedImage process(BufferedImage image)
	{
		BufferedImage dst = new BufferedImage(width, height, image.getType());
		Graphics2D g = dst.createGraphics();
		g.setColor(new Color(0x262628));
		g.fillRect(0, 0, width, height);
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return dst;
	}
}