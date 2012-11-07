package crm.gobelins.facedroidtactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	public static final int STATE_READY = 0;
	private int _width;
	private int _height;
	private GameThread _thread;
	private HandleCellInterface _cellHandler;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		_thread = new GameThread(holder, context, new MessageHandler());

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("GOBELINS", format + "  " + width + "  " + height);
		_width = width;
		_height = height;
		_thread.setSurfaceSize(width, height);
		
		if (_cellHandler != null)
			_cellHandler.onReady();
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("GOBELINS", "surface created");
		
		_thread.setRunning(true);
		_thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("GOBELINS", " destroyed ::: ");
		boolean retry = true;
		_thread.setRunning(false);
		while (retry) {
			try {
				_thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public GameThread getThread() {
		return _thread;
	}
	
	public void setBgLoose() {
		_thread.setBgLoose();
	}
	
	public void setBgWin() {
		_thread.setBgWin();
	}

	public void setPlayerPicture(Bitmap player_pic_x, Bitmap player_pic_o) {
		_thread.setPlayerPicture(player_pic_x, player_pic_o);
	}

	public void setBoard(GameBoard board) {
		_thread.setBoard(board);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getX() < GameConsts.LEFT * _width
				|| event.getX() > GameConsts.RIGHT * _width)
			return false;

		if (event.getY() < GameConsts.TOP * _height
				|| event.getY() > GameConsts.BOTTOM * _height)
			return false;

		if (event.getActionMasked() == MotionEvent.ACTION_UP) {

			int col = (int) (event.getX() - GameConsts.LEFT * _width);
			col /= (int) (_width * GameConsts.WIDTH) / GameConsts.GAME_WIDTH;

			int row = (int) (event.getY() - GameConsts.TOP * _height);
			row /= (int) (_height * GameConsts.HEIGHT) / GameConsts.GAME_WIDTH;

			if (_cellHandler != null)
				_cellHandler.onHandle(col, row);
		}

		return true;
	}

	public void setCellHandler(HandleCellInterface cellHandler) {
		_cellHandler = cellHandler;
	}
	
	static class MessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.d("GOBELINS", msg.toString());
		}
	}

	public void addOnCell(int last_played) {
		_thread.addOnCell(last_played);
	}

}
