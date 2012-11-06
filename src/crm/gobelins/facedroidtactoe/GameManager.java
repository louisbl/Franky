package crm.gobelins.facedroidtactoe;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class GameManager {

	private ArrayList<ArrayList<Player>> _history = new ArrayList<ArrayList<Player>>();
	private GameAI _ai = new GameAI();

	public void resetGame(GameBoard board) {
		_history.clear();
		board = new GameBoard(board.mode);
		nextPlayer(board);
	}

	public boolean play(GameBoard board, int xx, int yy) {
		return play(board, board.current_player, _convertToId(xx, yy));
	}

	public boolean play(GameBoard board, int id) {
		return play(board, board.current_player, id);
	}

	public boolean play(GameBoard board, Player player, int id) {
		if (board.current_player != player)
			return false;

		if (!board.available.contains(id))
			return false;

		_setTileValue(board.grid, board.current_player, id);
		board.available.remove((Object) id);

		if (board.mode != GameMode.AI)
			_history.add(new ArrayList<Player>(board.grid));

		board.state = board.state == GameState.PLAYER_O_TURN ? GameState.PLAYER_X_TURN
				: GameState.PLAYER_O_TURN;

		_setEndGame(board);

		board.current_player = board.current_player == Player.PLAYER_O ? Player.PLAYER_X
				: Player.PLAYER_O;

		return true;
	}

	public void nextPlayer(GameBoard board) {
		if (board.mode == GameMode.DUO) {
			_waitForOther();
		} else if (board.mode == GameMode.SOLO) {
			if (board.current_player == Player.PLAYER_O)
				_callAI(board);
		}
	}

	private void _waitForOther() {
	}

	private void _setEndGame(GameBoard board) {
		boolean win_row = true;
		boolean win_col = true;
		boolean win_diag_top = true;
		boolean win_diag_bottom = true;

		for (int i = 0; i < GameConsts.GAME_WIDTH; i++) {
			for (int j = 0; j < GameConsts.GAME_WIDTH; j++) {
				win_row &= _compareCell(board.grid, _convertToId(i, j),
						_convertToId(i, 0));
				win_col &= _compareCell(board.grid, _convertToId(j, i),
						_convertToId(0, i));
			}

			if (win_row) {
				board.state = GameState.WIN;
				board.winner = board.grid.get(_convertToId(i, 0));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					board.winner_cells.add(k, _convertToId(i, k));
				}
				return;
			}

			if (win_col) {
				board.state = GameState.WIN;
				board.winner = board.grid.get(_convertToId(0, i));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					board.winner_cells.add(k, _convertToId(k, i));
				}
				return;
			}

			win_diag_top &= _compareCell(board.grid, _convertToId(i, i),
					_convertToId(0, 0));
			win_diag_bottom &= _compareCell(board.grid,
					_convertToId(GameConsts.GAME_WIDTH - i - 1, i),
					_convertToId(GameConsts.GAME_WIDTH - 1, 0));
		}

		if (win_diag_top) {
			board.winner = board.grid.get(_convertToId(0, 0));
			board.state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				board.winner_cells.add(k, _convertToId(k, k));
			}
			return;
		}

		if (win_diag_bottom) {
			board.winner = board.grid.get(_convertToId(0,
					GameConsts.GAME_WIDTH - 1));
			board.state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				board.winner_cells.add(
						k,
						_convertToId(GameConsts.GAME_WIDTH - 1 - k,
								GameConsts.GAME_WIDTH - 1 - k));
			}
			return;
		}

		if (board.available.size() == 0) {
			board.state = GameState.DRAW;
			return;
		}

	}

	private boolean _compareCell(List<Player> grid, int cell1, int cell2) {
		if (grid.get(cell1) == Player.EMPTY)
			return false;
		if (grid.get(cell2) == Player.EMPTY)
			return false;

		return grid.get(cell1) == grid.get(cell2);
	}

	private void _callAI(GameBoard board) {
		int id = _ai.computeNexteMove(this, board);
		play(board, id);
	}

	private int _convertToId(int row, int col) {
		return row * GameConsts.GAME_WIDTH + col;
	}

	private void _setTileValue(List<Player> grid, Player player, int id) {
		Log.d("GOBELINS", " id ::: " + id);
		grid.set(id, player);

	}

}
