package net.coobird.thumbnailator.filters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.coobird.thumbnailator.builders.BufferedImageBuilder;

/**
 * A class containing rotation filters.
 *  
 * @author coobird
 *
 */
public class Rotation
{
	/**
	 * An {@link ImageFilter} which applies a rotation to an image.
	 * 
	 * @author coobird
	 *
	 */
	public abstract static class Rotator implements ImageFilter
	{
	}
	
	/**
	 * <p>
	 * Performs a rotation of a specified image.
	 * </p>
	 * <p>
	 * This method will only rotate images at a multiple of 90 degrees.
	 * </p>
	 * 
	 * @param img			Image to rotate.
	 * @param angle			The angle to rotate the image by.
	 * @return				The rotated image.
	 */
	private static BufferedImage rotate(BufferedImage img, int angle)
	{
		if (angle % 90 != 0)
		{
			throw new IllegalArgumentException("The specified angle is not in" +
					" a multiple of 90.");
		}
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		BufferedImage newImage;
		
		if (angle % 180 == 90)
		{
			newImage = new BufferedImageBuilder(height, width).build();
		}
		else 
		{
			newImage = new BufferedImageBuilder(width, height).build();
		}
		
		Graphics2D g = newImage.createGraphics();
		g.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		return newImage;
	}
	
	/**
	 * <p>
	 * Creates a new instance of {@code Rotator} which rotates an image at
	 * the specified angle.
	 * </p>
	 * 
	 * @param angle			The angle at which the instance of {@code Rotator}
	 * 						is to rotate a image it acts upon.
	 * @return				An instance of {@code Rotator} which will rotate
	 * 						a given image.
	 */
	public static Rotator newRotator(final double angle)
	{
		Rotator r = new Rotator() {
			
			private double[] calculatePosition(double x, double y, double angle)
			{
				angle = Math.toRadians(angle);
				
				double nx = (Math.cos(angle) * x) - (Math.sin(angle) * y);
				double ny = (Math.sin(angle) * x) + (Math.cos(angle) * y);

				return new double[] {nx, ny};
			}
			
			public BufferedImage apply(BufferedImage img)
			{
				int width = img.getWidth();
				int height = img.getHeight();
				
				BufferedImage newImage;
				
				double[][] newPositions = new double[4][];
				newPositions[0] = calculatePosition(0, 0, angle);
				newPositions[1] = calculatePosition(width, 0, angle);
				newPositions[2] = calculatePosition(0, height, angle);
				newPositions[3] = calculatePosition(width, height, angle);
				
				double minX = Math.min(
						Math.min(newPositions[0][0], newPositions[1][0]), 
						Math.min(newPositions[2][0], newPositions[3][0])
				);
				double maxX = Math.max(
						Math.max(newPositions[0][0], newPositions[1][0]),
						Math.max(newPositions[2][0], newPositions[3][0])
				);
				double minY = Math.min(
						Math.min(newPositions[0][1], newPositions[1][1]),
						Math.min(newPositions[2][1], newPositions[3][1])
				);
				double maxY = Math.max(
						Math.max(newPositions[0][1], newPositions[1][1]), 
						Math.max(newPositions[2][1], newPositions[3][1])
				);
				
				int newWidth = (int)(maxX - minX);
				int newHeight = (int)(maxY - minY);
				newImage = new BufferedImageBuilder(newWidth, newHeight).build();
				
				Graphics2D g = newImage.createGraphics();
				
				// TODO consider RenderingHints to use.
				
				double w = newWidth / 2.0;
				double h = newHeight / 2.0;
				g.rotate(Math.toRadians(angle), w, h);
				int centerX = (int)((newWidth - width) / 2.0);
				int centerY = (int)((newHeight - height) / 2.0);
				
				g.drawImage(img, centerX, centerY, null);
				g.dispose();
				
				return newImage;
			}
		};
		
		return r;
	}

	/**
	 * A {@code Rotator} which will rotate a specified image to the left 90
	 * degrees.
	 */
	public static final Rotator LEFT_90_DEGREES = new Rotator() {
		public BufferedImage apply(BufferedImage img)
		{
			return rotate(img, -90);
		}
	};
	
	/**
	 * A {@code Rotator} which will rotate a specified image to the right 90
	 * degrees.
	 */
	public static final Rotator RIGHT_90_DEGREES = new Rotator() {
		public BufferedImage apply(BufferedImage img)
		{
			return rotate(img, 90);
		}
	};
	
	/**
	 * A {@code Rotator} which will rotate a specified image to the 180 degrees.
	 */
	public static final Rotator ROTATE_180_DEGREES = new Rotator() {
		public BufferedImage apply(BufferedImage img)
		{
			return rotate(img, 180);
		}
	};
}