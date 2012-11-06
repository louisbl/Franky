package crm.gobelins.facedroidtactoe;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends Activity {

	private GameView _game_view;
	private GameManager _game_manager;
	private GameBoard _board;

	private HandleCellInterface _cellHandler = new HandleCellInterface() {
		public void onHandle(int xx, int yy) {
			//Log.d("GOBELINS", " xx ::: " + xx + " yy ::: " + yy);
			if (_game_manager.play(_board, xx, yy)) {
				_game_manager.nextPlayer(_board);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		_game_view = (GameView) findViewById(R.id.game_view);
		Bitmap player_pic_o = BitmapFactory.decodeResource(getResources(),
				R.drawable.player_o);
		Bitmap player_pic_x = BitmapFactory.decodeResource(getResources(),
				R.drawable.player_x);
		_game_view.setPlayerPicture(player_pic_x, player_pic_o);

		_game_manager = new GameManager();
		_board = new GameBoard(GameMode.SOLO);

		_game_view.setCellHandler(_cellHandler);
		_game_view.setBoard(_board);

		_game_manager.resetGame(_board);
	}
}