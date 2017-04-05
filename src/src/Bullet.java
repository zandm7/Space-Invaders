import java.awt.*;
public class Bullet extends GameObj {
	

	public static final int SIZE = 10;
	public static final int INIT_X = 0;
	public static final int INIT_Y = 0;
	public static final int INIT_VEL_X = 0;
	public static final int INIT_VEL_Y = 0;
	public Bullet(int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, INIT_Y, SIZE, SIZE, courtWidth,
				courtHeight);
	}

	@Override
	public void clip() {

	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawLine(pos_x, pos_y, pos_x, pos_y - height);
	}
}
