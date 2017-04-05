/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	// the state of the game logic
	private Square square; // the Black Square, keyboard control
	private HashSet<Bullet> bullets = new HashSet<Bullet>();
	private HashSet<Poison> aliens = new HashSet<Poison>();

	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)

	// Game constants
	public static final int COURT_WIDTH = 500;
	public static final int COURT_HEIGHT = 500;
	public static final int SQUARE_VELOCITY = 6;
	// Update interval for timer, in milliseconds
	public static final int INTERVAL = 35;
	public int score = 0;
	public int lives = 3;
	public int rows = 0;

	public GameCourt(JLabel status) {
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.BLACK);
		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		// Enable keyboard focus on the court area.
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// This key listener allows the square to move as long
		// as an arrow key is pressed, by changing the square's
		// velocity accordingly. (The tick method below actually
		// moves the square.)
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					square.v_x = -SQUARE_VELOCITY;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					square.v_x = SQUARE_VELOCITY;
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					Bullet bullet = square.shoot(COURT_WIDTH, COURT_HEIGHT, square.pos_x, square.pos_y);
					bullets.add(bullet);
			} }

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_SPACE) {
				square.v_x = 0;
				square.v_y = 0;
				}
			}
		});

		this.status = status;
	}

	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset(int rows) {

		square = new Square(COURT_WIDTH, COURT_HEIGHT);
		square.pos_y = 500;
		aliens = new HashSet<Poison>();
		if (rows >= 0) makeNewAliens(rows + 3);
		else makeNewAliens(rows + 4);
		bullets = new HashSet<Bullet>();
		if (rows < 0) playing = false;
		else playing = true;
		if (rows >= 0) status.setText("Score: " + Integer.toString(score) + ", Lives: " + 
		Integer.toString(lives) + ", Level: " + Integer.toString(rows + 1));
		else status.setText("Score: " + Integer.toString(score) + ", Lives: " + 
				Integer.toString(lives) + ", Level: 1");
		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
		if (rows >= 6) {
			playing = false;
			status.setText("GAME CLEAR! YOUR SCORE: " + Integer.toString(score));
		}
	}
	
	public void updateAllAliens(Poison g) {
		for (Poison p : aliens) {
			p.v_x = g.v_x;
			p.v_y = g.v_y;
			p.offset = g.offset;
			p.pos_y += 30;
		}
	}
	
	public String didWeHitWall() {
		for (Poison p : aliens) {
			if (p.v_y != 0 && p.offset >= 10) {
				return "movingDown";
			}
			else if ((p.hitWall() == Direction.LEFT || p.hitWall() == Direction.RIGHT)
					&& p.v_y == 0) {
				return "WALL";
			}
		}
		return "NO";
	}
	
	public Poison whichOne() {
		for (Poison p : aliens) {
			if (p.v_y != 0 && p.offset >= 10) {
				return p;
			}
			else if (p.hitWall() == Direction.LEFT || p.hitWall() == Direction.RIGHT) {
				return p;
			}
		}
		return null;
	}
	
	public void bulletHit(Bullet b) {
		HashSet<Poison> copyAliens = new HashSet<Poison>(aliens);
		for (Poison p : copyAliens) {
			if (p.intersects(b)) {
				aliens.remove(p);
				bullets.remove(b);
				score++;
				status.setText("Score: " + Integer.toString(score) + ", Lives: " +
				Integer.toString(lives) + ", Level: " + Integer.toString(rows + 1));
			}
		}
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (playing) {
			square.move();
			HashSet<Bullet> bulletCopy = new HashSet<Bullet>(bullets);
			for (Bullet b : bulletCopy) {
				b.move();
				bulletHit(b);
			}
			for (Poison p : aliens) {
				p.move();
				if (square.intersects(p)) {
					playing = false;
					lives--;
					score /= 2;
					if (lives == 0) status.setText("GAME OVER! YOUR SCORE: " + Integer.toString(score));
					else status.setText("TRY AGAIN? Lives remaining: " + 
					Integer.toString(lives));
				}
			}
			Poison theOne = whichOne();
			if (didWeHitWall().equals("WALL") && theOne != null) {
				theOne.bounceDown(theOne.hitSideWall());
				updateAllAliens(theOne);
			}
			else if (didWeHitWall().equals("movingDown") && theOne != null) {
					if (theOne.pos_x <= 250) {
						theOne.v_x = 1;
						theOne.v_y = 0;
						theOne.offset = 0;
					}
					else if (theOne.pos_x > 250) {
						theOne.v_x = -1;
						theOne.v_y = 0;
						theOne.offset = 0;
					}
				updateAllAliens(theOne);
			}
			if (aliens.isEmpty()) {
				playing = false;
				status.setText("LEVEL CLEAR! YOUR SCORE: " + Integer.toString(score));
				rows++;
			}
			// update the display
			repaint();
		}
	}
	
	
	public void makeNewAliens(int rows) {
		for (int i = 0; i < rows*50; i += 60) {
			for (int c = 50; c < 450; c += 60) {
				Poison alien = new Poison(COURT_WIDTH, COURT_HEIGHT);
				alien.pos_x = c;
				alien.pos_y = i;
				aliens.add(alien);
			}
		}
	}
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		square.draw(g);
		Iterator<Bullet> bulletIt = bullets.iterator();
		Iterator<Poison> aliensIt = aliens.iterator();
		while (aliensIt.hasNext()) aliensIt.next().draw(g);
		while (bulletIt.hasNext()) {
			bulletIt.next().draw(g);
		}
		
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}
