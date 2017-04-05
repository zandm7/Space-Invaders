/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A game object displayed using an image.
 * 
 * Note that the image is read from the file when the object is constructed, and
 * that all objects created by this constructor share the same image data (i.e.
 * img is static). This important for efficiency: your program will go very
 * slowing if you try to create a new BufferedImage every time the draw method
 * is invoked.
 */
public class Poison extends GameObj {
	public static final String img_file = "poison.png";
	public static final int SIZE = 40;
	public static final int INIT_X = 0;
	public static final int INIT_Y = 0;
	public static final int INIT_VEL_X = 1;
	public static final int INIT_VEL_Y = 0;

	private static BufferedImage img;
	public int initY = 0;
	public int offset = 0;

	public Poison(int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, INIT_Y, SIZE, SIZE, courtWidth,
				courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}
	
	public Direction hitSideWall() {
		if (pos_x + v_x < 0)
			return Direction.LEFT;
		else if (pos_x + v_x > max_x)
			return Direction.RIGHT;
		else return null;
	}
	
	public void bounceDown(Direction d) {
		if (d == null) return;
		switch (d) {
		case UP:    break;  
		case DOWN:  break;
		case LEFT:  v_x = 1; v_y = 0; offset = 10; break;
		case RIGHT: v_x = -1; v_y = 0; offset = 10; break;
		}
		initY = pos_y;
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(img, pos_x, pos_y, width, height, null);
	}

}