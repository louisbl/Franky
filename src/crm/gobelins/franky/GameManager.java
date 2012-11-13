package crm.gobelins.franky;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.util.Log;

public class GameManager {

	public int last_played = -1;
	public HandleCellInterface cellHandler;

	private ArrayList<ArrayList<Player>> _history = new ArrayList<ArrayList<Player>>();
	private GameAI _ai = new GameAI();
	private Timer _timer = new Timer();

	public void resetGame(GameBoard board) {
		_history.clear();
		board = new GameBoard(board.mode);
		nextPlayer(board);
	}

	public void play(GameBoard board, int col, int row) {
		play(board, convertToId(col, row));
	}

	public void play(GameBoard board, int id) {

		if (board.state == GameState.DRAW || board.state == GameState.WIN)
			return;

		if (!board.available.contains(id))
			return;

		_setTileValue(board.grid, board.current_player, id);
		board.available.remove((Object) id);

		last_played = id;

		if (board.mode != GameMode.AI)
			_history.add(new ArrayList<Player>(board.grid));

		board.state = board.state == GameState.PLAYER_O_TURN ? GameState.PLAYER_X_TURN
				: GameState.PLAYER_O_TURN;

		_setEndGame(board);

		board.current_player = board.current_player == Player.PLAYER_O ? Player.PLAYER_X
				: Player.PLAYER_O;
	}

	public void nextPlayer(GameBoard board) {
		last_played = -1;
		if (board.mode == GameMode.DUO) {
			_waitForOther();
		} else if (board.mode == GameMode.SOLO) {
			if (board.current_player == Player.PLAYER_O) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				_callAI(board);
			}
		}
	}

	private void _waitForOther() {

	}

	private void _setEndGame(GameBoard board) {
		boolean win_row;
		boolean win_col;

		boolean win_diag_top = true;
		boolean win_diag_bottom = true;

		for (int i = 0; i < GameConsts.GAME_WIDTH; i++) {
			win_row = true;
			win_col = true;
			for (int j = 0; j < GameConsts.GAME_WIDTH; j++) {
				win_row &= compareCell(board.grid, convertToId(i, j),
						convertToId(i, 0));
				win_col &= compareCell(board.grid, convertToId(j, i),
						convertToId(0, i));
			}

			if (win_row) {
				board.state = GameState.WIN;
				board.winner = board.grid.get(convertToId(i, 0));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					board.winner_cells.add(k, convertToId(i, k));
				}
				return;
			}

			if (win_col) {
				board.state = GameState.WIN;
				board.winner = board.grid.get(convertToId(0, i));
				for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
					board.winner_cells.add(k, convertToId(k, i));
				}
				return;
			}

			win_diag_top &= compareCell(board.grid, convertToId(i, i),
					convertToId(0, 0));
			win_diag_bottom &= compareCell(board.grid,
					convertToId(GameConsts.GAME_WIDTH - i - 1, i),
					convertToId(GameConsts.GAME_WIDTH - 1, 0));
		}

		if (win_diag_top) {
			board.winner = board.grid.get(convertToId(0, 0));
			board.state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				board.winner_cells.add(k, convertToId(k, k));
			}
			return;
		}

		if (win_diag_bottom) {
			board.winner = board.grid.get(convertToId(0,
					GameConsts.GAME_WIDTH - 1));
			board.state = GameState.WIN;
			for (int k = 0; k < GameConsts.GAME_WIDTH; k++) {
				board.winner_cells.add(
						k,
						convertToId(GameConsts.GAME_WIDTH - 1 - k,
								GameConsts.GAME_WIDTH - 1 - k));
			}
			return;
		}

		if (board.available.size() == 0) {
			board.state = GameState.DRAW;
			return;
		}

	}

	public boolean compareCell(List<Player> grid, int cell1, int cell2) {
		if (grid.get(cell1) == Player.EMPTY)
			return false;
		if (grid.get(cell2) == Player.EMPTY)
			return false;

		return grid.get(cell1) == grid.get(cell2);
	}

	private void _callAI(GameBoard board) {
		int id = _ai.computeNexteMove(this, board);
		if (id != -1) {
			play(board, id);
			cellHandler.onReady();
		} else
			Log.d("GOBELINS", "crash ai");
	}

	public int convertToId(int col, int row) {
		return row * GameConsts.GAME_WIDTH + col;
	}

	private void _setTileValue(List<Player> grid, Player player, int id) {
		grid.set(id, player);

	}

	public int getLastPlayedX() {
		return 0;
	}

	public int getLastPlayedY() {
		return 0;
	}

}
