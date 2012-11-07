package crm.gobelins.facedroidtactoe;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GameActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;

	private GameView _game_view;
	private GameManager _game_manager;
	private GameBoard _board;
	private Bitmap _user_bmp;

	private HandleCellInterface _cellHandler = new HandleCellInterface() {
		public void onHandle(int col, int row) {
			Log.d("GOBELINS", " col ::: " + col + " row ::: " + row);
			if (_board.current_player != Player.PLAYER_X)
				return;
			_game_manager.play(_board, col, row);
			onReady();
		}

		public void onReady() {
			_updateView();
			if (_board.state != GameState.DRAW && _board.state != GameState.WIN)
				_game_manager.nextPlayer(_board);
		}
	};

	private void _updateView() {
		if (_game_manager.last_played != -1) {
			_game_view.addOnCell(_game_manager.last_played);
			_game_manager.last_played = -1;
		}

		switch (_board.state) {
		case DRAW:
			_game_view.setBgLoose();
			break;
		case WIN:
			if (_board.winner == Player.PLAYER_X)
				_game_view.setBgWin();
			else
				_game_view.setBgLoose();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// _user_bmp = (Bitmap) getIntent().getExtras().get("mybitmap");
		// String game_mode = getIntent().getExtras().getString("mode");

		String game_mode = "single";

		if (game_mode.equalsIgnoreCase("single"))
			_startGame(GameMode.SOLO);
		else {
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (mBluetoothAdapter != null) {
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				} else {
					_hayBt();
				}
			} else {
				_noBt();
			}
		}
	}

	private void _hayBt() {
		Toast.makeText(GameActivity.this, "Bluetooth !", Toast.LENGTH_LONG)
				.show();
		_startGame(GameMode.DUO);
	}

	private void _noBt() {
		Toast.makeText(GameActivity.this, "Pas de Bluetooth, mode un joueur !",
				Toast.LENGTH_LONG).show();
		_startGame(GameMode.SOLO);
	}

	private void _startGame(GameMode mode) {
		_game_manager = new GameManager();
		_game_manager.cellHandler = _cellHandler;

		_board = new GameBoard(mode);

		_game_view = (GameView) findViewById(R.id.game_view);
		Bitmap player_pic_o = BitmapFactory.decodeResource(getResources(),
				R.drawable.player_o);

		Bitmap player_pic_x = BitmapFactory.decodeResource(getResources(),
				R.drawable.player_x);

		// _game_view.setPlayerPicture(_user_bmp, player_pic_o);
		_game_view.setPlayerPicture(player_pic_x, player_pic_o);

		_game_view.setCellHandler(_cellHandler);
		_game_view.setBoard(_board);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK)
				_hayBt();
			else
				_noBt();
			break;

		default:
			break;
		}
	};
}

interface HandleCellInterface {
	void onHandle(int col, int row);

	void onReady();
}