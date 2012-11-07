package crm.gobelins.facedroidtactoe;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	private SurfaceHolder _surface_holder;
	private Handler _handler;
	private Context _context;
	private Bitmap _background_image;
	private Bitmap _player_pic_x;
	private Bitmap _player_pic_o;
	private Bitmap _src_player_pic_x;
	private Bitmap _src_player_pic_o;

	private int _canvas_height = 1;
	private int _canvas_width = 1;
	private Boolean _is_running = false;
	private GameBoard _board;
	private float _top;
	private float _left;
	private float _top_size;
	private float _left_size;
	private float _left_tmp;
	private float _top_tmp;
	private Player cell;

	private List<Particle> _particle_list = new ArrayList<Particle>();
	private List<Particle> _recycle_list = new ArrayList<Particle>();
	private Bitmap _particle_image[] = new Bitmap[3];

	public GameThread(SurfaceHolder surfaceHolder, Context context,
			Handler handler) {
		_surface_holder = surfaceHolder;
		_handler = handler;
		_context = context;
		_particle_image[0] = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.yellow_spark)).getBitmap();
		_particle_image[1] = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.blue_spark)).getBitmap();
		_particle_image[2] = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.red_spark)).getBitmap();

	}

	public void setSurfaceSize(int width, int height) {
		synchronized (_surface_holder) {
			_canvas_width = width;
			_canvas_height = height;

			_background_image = decodeSampledBitmapFromResource(
					_context.getResources(), R.drawable.franky, _canvas_width,
					_canvas_height);

			_player_pic_x = Bitmap.createScaledBitmap(_src_player_pic_x,
					(int) (_canvas_width * GameConsts.WIDTH)
							/ GameConsts.GAME_WIDTH,
					(int) (_canvas_height * GameConsts.HEIGHT)
							/ GameConsts.GAME_WIDTH, true);
			_player_pic_o = Bitmap.createScaledBitmap(_src_player_pic_o,
					(int) (_canvas_width * GameConsts.WIDTH)
							/ GameConsts.GAME_WIDTH,
					(int) (_canvas_height * GameConsts.HEIGHT)
							/ GameConsts.GAME_WIDTH, true);

			_src_player_pic_x.recycle();
			_src_player_pic_x = null;
			_src_player_pic_o.recycle();
			_src_player_pic_o = null;

			_top = GameConsts.TOP * _canvas_height;
			_left = GameConsts.LEFT * _canvas_width;
			_top_size = (GameConsts.HEIGHT * _canvas_height)
					/ GameConsts.GAME_WIDTH;
			_left_size = (GameConsts.WIDTH * _canvas_width)
					/ GameConsts.GAME_WIDTH;
		}
	}

	public void setRunning(Boolean b) {
		_is_running = b;
		if (!b) {
			_background_image.recycle();
			_player_pic_o.recycle();
			_player_pic_x.recycle();

			_background_image = null;
			_player_pic_o = null;
			_player_pic_x = null;
			_src_player_pic_o = null;
			_src_player_pic_x = null;
			System.gc();
		}
	}

	public void setBoard(GameBoard board) {
		synchronized (_surface_holder) {
			_board = board;
		}
	}

	public void setBgLoose() {
		synchronized (_surface_holder) {
			_background_image = decodeSampledBitmapFromResource(
					_context.getResources(), R.drawable.bg_looser,
					_canvas_width, _canvas_height);
		}
	}

	public void setBgWin() {
		synchronized (_surface_holder) {
			_background_image = decodeSampledBitmapFromResource(
					_context.getResources(), R.drawable.bg_win, _canvas_width,
					_canvas_height);
		}
	}

	public void setPlayerPicture(Bitmap player_pic_x, Bitmap player_pic_o) {
		synchronized (_surface_holder) {
			_src_player_pic_o = player_pic_o;
			_src_player_pic_x = player_pic_x;
		}
	}

	@Override
	public void run() {
		while (_is_running) {
			Canvas c = null;
			try {
				c = _surface_holder.lockCanvas();
				synchronized (_surface_holder) {
					_doDraw(c);
					_updateNewCellPos(c);
				}
			} catch (Exception e) {
			} finally {
				if (c != null)
					_surface_holder.unlockCanvasAndPost(c);
			}
		}
	}

	private void _updateNewCellPos(Canvas c) {
		synchronized (_particle_list) {
			for (int i = 0; i < _particle_list.size(); i++) {
				Particle p = _particle_list.get(i);
				p.move();
				c.drawBitmap(_particle_image[p.color], p.x - 10, p.y - 10, null);
				if (p.x < 0 || p.x > _canvas_width || p.y < 0
						|| p.y > _canvas_height) {
					_recycle_list.add(_particle_list.remove(i));
					i--;
				}
			}
		}

	}

	private void _doDraw(Canvas c) {
		c.drawBitmap(_background_image, 0, 0, null);
		for (int i = 0; i < _board.grid.size(); i++) {

			_top_tmp = _top;
			_top_tmp += (i / GameConsts.GAME_WIDTH) * _top_size;

			_left_tmp = _left;
			_left_tmp += (i % GameConsts.GAME_WIDTH) * _left_size;

			cell = _board.grid.get(i);

			switch (cell) {
			case EMPTY:
				break;
			case PLAYER_O:
				c.drawBitmap(_player_pic_o, _left_tmp, _top_tmp, null);
				break;
			case PLAYER_X:
				c.drawBitmap(_player_pic_x, _left_tmp, _top_tmp, null);
				break;
			}
		}

	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeResource(res, resId, options);
		Bitmap bmp_resized;

		bmp_resized = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, true);
		bmp.recycle();
		bmp = null;
		return bmp_resized;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public void addOnCell(int id) {
		Particle p;
		int recycleCount = 0;

		float yy = _top + ((id / GameConsts.GAME_WIDTH) * _top_size);
		yy += _top_size / 2;
		float xx = _left + ((id % GameConsts.GAME_WIDTH) * _left_size);
		xx += _left_size / 2;

		if (_recycle_list.size() >= GameConsts.NUM_PARTICLES)
			recycleCount = GameConsts.NUM_PARTICLES;
		else
			recycleCount = _recycle_list.size();

		for (int i = 0; i < recycleCount; i++) {
			p = _recycle_list.remove(0);
			p.init(xx, yy);
			_particle_list.add(p);
		}

		for (int i = 0; i < GameConsts.NUM_PARTICLES - recycleCount; i++)
			_particle_list.add(new Particle(xx, yy));

	}

}