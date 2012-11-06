package crm.gobelins.facedroidtactoe;

import android.util.Log;

public class GameAI {
	public int computeNexteMove(GameManager gm, GameBoard board) {
		return _randomMove(board);
		// return _negaMaxAB(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 4);
	}

	private int _randomMove(GameBoard board) {
		int index = (int) Math.floor(Math.random() * board.available.size());
		int id = board.available.get(index);
		Log.d("GOBELINS", " AI id ::: " + id);
		return id;
	}

	private int _negaMaxAB(GameManager gm, GameBoard board, int alpha,
			int beta, int horizon) {

		if (horizon == 0) {
			return _eval(board);
		}

		GameBoard board_tmp;
		int best;
		int value;

		best = Integer.MIN_VALUE;

		board_tmp = new GameBoard(board);

		for (int cell : board_tmp.available) {
			gm.play(board_tmp, cell);
			value = -_negaMaxAB(gm, board_tmp, -beta, -alpha, horizon - 1);
			if (value > best) {
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

	private int _eval(GameBoard board) {
		// TODO Auto-generated method stub
		/*
		 * For each row, if there are both X and O, then the score for the row
		 * is 0. If the whole row is empty, then the score is 1. If there is
		 * only one X, then the score is 10. If there are two Xs, then the score
		 * is 100. If there are 3 Xs, then the score is 1000, and the winner is
		 * Player X. For Player O, the score is negative. Player X tries to
		 * maximize the score. Player O tries to minimize the score. If the
		 * current turn is for Player X, then the score of Player X has more
		 * advantage. I gave the advantage rate as 3.
		 */
		return 0;
	}
}