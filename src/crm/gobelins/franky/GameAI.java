package crm.gobelins.franky;

import android.util.Log;

public class GameAI {
	private int _index;

	public int computeNexteMove(GameManager gm, GameBoard board) {
		Log.d("GOBELINS", " AI ::: ");

		return _coeff(gm, board);
		
		// _negaMaxAB(gm, board, Integer.MIN_VALUE, Integer.MAX_VALUE, 4);
		// return _index;
	}

	private int _coeff(GameManager gm, GameBoard board) {
		GameBoard board_tmp;
		int value;
		int best;
		int index = 0;
		
		best = Integer.MIN_VALUE;

		for (int cell : board.available) {
			board_tmp = new GameBoard(board);
			gm.play(board_tmp, cell);
			value = _eval(gm, board_tmp);
			if (value > best) {
				index = cell;
				best = value;
			}
		}
		
		return index;
	}

	private int _negaMaxAB(GameManager gm, GameBoard board, int alpha,
			int beta, int horizon) {

		if (board.state == GameState.DRAW || board.state == GameState.WIN
				|| horizon == 0) {
			return _eval(gm, board);
		}

		GameBoard board_tmp;
		int best;
		int value;

		best = Integer.MIN_VALUE;

		for (int cell : board.available) {
			board_tmp = new GameBoard(board);
			gm.play(board_tmp, cell);
			value = -_negaMaxAB(gm, board_tmp, -beta, -alpha, horizon - 1);
			if (value > best) {
				_index = cell;
				best = value;
				if (best > alpha) {
					alpha = best;
					if (alpha > beta) {
						return best;
					}
				}
			}
		}

		return best;
	}

	private int _eval(GameManager gm, GameBoard board) {

		if (board.state == GameState.WIN)
			if (board.winner == Player.PLAYER_X)
				return -1000;
			else
				return 1000;
		else if (board.state == GameState.DRAW)
			return 0;

		int score = 0;
		int num_x_col;
		int num_x_row;
		int num_o_row;
		int num_o_col;
		int num_x_diag_top;
		int num_o_diag_top;
		int num_x_diag_bot;
		int num_o_diag_bot;

		num_x_diag_bot = 0;
		num_x_diag_top = 0;
		num_o_diag_top = 0;
		num_o_diag_bot = 0;

		for (int i = 0; i < GameConsts.GAME_WIDTH; i++) {
			num_o_col = 0;
			num_x_col = 0;
			num_o_row = 0;
			num_x_row = 0;

			for (int j = 0; j < GameConsts.GAME_WIDTH; j++) {
				if (board.grid.get(gm.convertToId(i, j)) == Player.PLAYER_X)
					num_x_row++;
				if (board.grid.get(gm.convertToId(i, j)) == Player.PLAYER_O)
					num_o_row++;

				if (board.grid.get(gm.convertToId(j, i)) == Player.PLAYER_X)
					num_x_col++;
				if (board.grid.get(gm.convertToId(j, i)) == Player.PLAYER_O)
					num_o_col++;
			}

			score += _getTheScore(num_x_row, num_o_row);
			score += _getTheScore(num_x_col, num_o_col);

			if (board.grid.get(gm.convertToId(i, i)) == Player.PLAYER_X)
				num_x_diag_top++;
			if (board.grid.get(gm.convertToId(i, i)) == Player.PLAYER_O)
				num_o_diag_top++;

			if (board.grid
					.get(gm.convertToId(GameConsts.GAME_WIDTH - i - 1, i)) == Player.PLAYER_X)
				num_x_diag_bot++;
			if (board.grid
					.get(gm.convertToId(GameConsts.GAME_WIDTH - i - 1, i)) == Player.PLAYER_O)
				num_o_diag_bot++;
		}

		score += _getTheScore(num_x_diag_bot, num_o_diag_bot);
		score += _getTheScore(num_x_diag_top, num_o_diag_top);

		if (board.current_player == Player.PLAYER_O)
			score = -score;

		return score;
	}

	private int _getTheScore(int num_x, int num_o) {
		if (num_x == 0) {
			if (num_o != 0)
				return (int) Math.pow(10, num_o);
		} else if (num_o == 0)
			return -(int) Math.pow(10, num_x);
		return 0;
	}
}