package crm.gobelins.franky;

import java.util.Random;

public class Particle {
	public int distFromOrigin = 0;
	public int color;
	public float x;
	public float y;

	private double _direction;
	private double _direction_cosine;
	private double _direction_sine;

	private float _init_X;
	private float _init_Y;

	public Particle(float xx, float yy) {
		init(xx, yy);
		_direction = 2 * Math.PI * new Random().nextInt(NO_OF_DIRECTION)
				/ NO_OF_DIRECTION;
		_direction_cosine = Math.cos(_direction);
		_direction_sine = Math.sin(_direction);
		color = new Random().nextInt(3);
	}

	public void init(float xx, float yy) {
		distFromOrigin = 0;
		_init_X = x = xx;
		_init_Y = y = yy;
	}

	public synchronized void move() {
		distFromOrigin += 2;
		x = (float) (_init_X + distFromOrigin * _direction_cosine);
		y = (float) (_init_Y + distFromOrigin * _direction_sine);
	}

	private final static int NO_OF_DIRECTION = 400;

}