package crm.gobelins.facedroidtactoe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
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

	public GameThread(SurfaceHolder surfaceHolder, Context context,
			Handler handler) {
		_surface_holder = surfaceHolder;
		_handler = handler;
		_context = context;
		Resources res = _context.getResources();

		_background_image = BitmapFactory
				.decodeResource(res, R.drawable.franky);

	}

	public void setSurfaceSize(int width, int height) {
		synchronized (_surface_holder) {
			_canvas_width = width;
			_canvas_height = height;

			_background_image = Bitmap.createScaledBitmap(_background_image,
					width, height, true);
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
	}

	public void setBoard(GameBoard board) {
		synchronized (_surface_holder) {
			_board = board;
		}
	}

	public void setBgDraw() {
		synchronized (_surface_holder) {
			_background_image = BitmapFactory.decodeResource(
					_context.getResources(), R.drawable.bg_looser);
			_background_image = Bitmap.createScaledBitmap(_background_image,
					_canvas_width, _canvas_height, true);
		}
	}

	public void setBgWin() {
		synchronized (_surface_holder) {
			_background_image = BitmapFactory.decodeResource(
					_context.getResources(), R.drawable.bg_win);
			_background_image = Bitmap.createScaledBitmap(_background_image,
					_canvas_width, _canvas_height, true);
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
				}
			} catch (Exception e) {
			} finally {
				if (c != null)
					_surface_holder.unlockCanvasAndPost(c);
			}
		}
	}

	private void _doDraw(Canvas c) {
		c.drawBitmap(_background_image, 0, 0, null);
		for (int i = 0; i < _board.grid.size(); i++) {

			_top_tmp = _top;
			_top_tmp += (i % GameConsts.GAME_WIDTH) * _top_size;

			_left_tmp = _left;
			_left_tmp += (i / GameConsts.GAME_WIDTH) * _left_size;

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

}