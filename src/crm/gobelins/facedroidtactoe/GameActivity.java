package crm.gobelins.facedroidtactoe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends Activity {

	private GameView _game_view;
	private GameManager _game_manager;
	private ArrayList<Player> _board = new ArrayList<Player>(
			GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH);
	
	private HandleCellInterface _cellHandler = new HandleCellInterface() {
		public void onHandle(int xx, int yy) {
			Log.d("GOBELINS", " xx ::: "+xx+ " yy ::: "+yy);
			if( _game_manager.play(_board, xx, yy) ){
				_updateView();
				_game_manager.nextPlayer();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		_game_view = (GameView) findViewById(R.id.game_view);
		_game_manager = new GameManager(GameMode.DUO);
		_game_manager.resetGame(_board);
		
		_game_view.setCellHandler(_cellHandler);
		Bitmap player_pic_o = BitmapFactory.decodeResource(getResources(), R.drawable.player_o);
		Bitmap player_pic_x = BitmapFactory.decodeResource(getResources(), R.drawable.player_x);
		_game_view.setPlayerPicture(player_pic_x, player_pic_o);
	}

	protected void _updateView() {
		_game_view.setState(_game_manager.state);
		_game_view.setWinCells(_game_manager.winner_cells);
		_game_view.setGrid(_board);
	}

}