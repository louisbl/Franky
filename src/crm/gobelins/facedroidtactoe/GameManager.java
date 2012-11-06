package crm.gobelins.facedroidtactoe;

import java.util.ArrayList;

import android.util.Log;

public class GameManager {

	public Player current_player = null;
	public GameMode mode = null;
	public GameState state = null;
	public Player winner = null;
	public ArrayList<Integer> winner_cells = new ArrayList<Integer>(
			GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH);
	public int num_cell_full = 0;

	private ArrayList<ArrayList<Player>> _history = new ArrayList<ArrayList<Player>>();

	public GameManager(GameMode m) {
		mode = m;
	}

	public void resetGame(ArrayList<Player> board) {
		_history.clear();
		winner = null;
		num_cell_full = 0;

		winner_cells.clear();

		for (int i = 0; i < GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH; i++) {
			board.add(Player.EMPTY);
		}

		if (Math.random() > .5) {
			current_player = Player.PLAYER_O;
			state = GameState.PLAYER_O_TURN;
		} else {
			current_player = Player.PLAYER_X;
			state = GameState.PLAYER_X_TURN;
		}

		nextPlayer();
	}

	public boolean play(ArrayList<Player> board, int xx, int yy) {
		return play(board, current_player, _convertToId(xx, yy));
	}

	public boolean play(ArrayList<Player> board, int id) {
		return play(board, current_player, id);
	}

	public boolean play(ArrayList<Player> board, Player player, int id) {
		if (current_player != player)
			return false;

		if (!_isEmpty(board, id))
			return false;

		_setTileValue(board, id);
		num_cell_full++;

		_history.add((ArrayList<Player>) board.clone());

		state = state == GameState.PLAYER_O_TURN ? GameState.PLAYER_X_TURN : GameState.PLAYER_O_TURN;

		_setEndGame(board);

		current_player = current_player == Player.PLAYER_O ? Player.PLAYER_X
				: Player.PLAYER_O;

		
		return true;
	}

	public boolean getIsEnd() {
		return (state == GameState.DRAW || state == GameState.WIN);
	}

	public void nextPlayer() {
		if (mode == GameMode.DUO) {
			_waitForOther();
		} else {
			if (current_player == Player.PLAYER_O)
				_callAI();
		}
	}

	private void _waitForOther() {
	}

	private boolean _isEmpty(ArrayList<Player> board, int id) {
		boolean empty = true;
		try {
			empty = board.get(id) == Player.EMPTY;
		} catch (Exception e) {
			Log.wtf("GOBELINS", e);
		}
		return empty;
	}

	private void _setEndGame(ArrayList<Player> board) {
		boolean win_row = true;
		boolean win_col = true;
		boolean win_diag_top = true;
		boolean win_diag_bottom = true;

		for (int i = 0; i < GameConsts.GAME_WIDTH; i++) {
			for (int j = 0; j < GameConsts.GAME_WIDTH; j++) {
				win_row &= _compareCell(board, _convertToId(i, j),
						_convertToId(i, 0));
				win_col &= _compareCell(board, _convertToId(j, i),
						_convertToId(0, i));
			}

			if (win_row) {
				state = GameState.WIN;
				winner = board.get(_convertToId(i, 0));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					winner_cells.add(k, _convertToId(i, k));
				}
				return;
			}

			if (win_col) {
				state = GameState.WIN;
				winner = board.get(_convertToId(0, i));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					winner_cells.add(k, _convertToId(k, i));
				}
				return;
			}

			win_diag_top &= _compareCell(board, _convertToId(i, i),
					_convertToId(0, 0));
			win_diag_bottom &= _compareCell(board,
					_convertToId(GameConsts.GAME_WIDTH - i - 1, i),
					_convertToId(GameConsts.GAME_WIDTH - 1, 0));
		}

		if (win_diag_top) {
			winner = board.get(_convertToId(0, 0));
			state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				winner_cells.add(k, _convertToId(k, k));
			}
			return;
		}

		if (win_diag_bottom) {
			winner = board.get(_convertToId(0, GameConsts.GAME_WIDTH - 1));
			state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				winner_cells.add(
						k,
						_convertToId(GameConsts.GAME_WIDTH - 1 - k,
								GameConsts.GAME_WIDTH - 1 - k));
			}
			return;
		}

		if (num_cell_full == GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH) {
			state = GameState.DRAW;
			return;
		}

	}

	private boolean _compareCell(ArrayList<Player> board, int cell1, int cell2) {
		if (board.get(cell1) == Player.EMPTY)
			return false;
		if (board.get(cell2) == Player.EMPTY)
			return false;

		return board.get(cell1) == board.get(cell2);
	}

	private void _callAI() {

	}

	private int _convertToId(int row, int col) {
		return row * GameConsts.GAME_WIDTH + col;
	}

	private void _setTileValue(ArrayList<Player> board, int id) {
		Log.d("GOBELINS", " id ::: " + id);
		board.set(id, current_player);
	}

	public ArrayList<Integer> getAvailableCell(ArrayList<Player> board) {
		return null;
	}

}
