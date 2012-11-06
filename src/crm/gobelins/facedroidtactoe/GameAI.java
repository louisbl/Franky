package crm.gobelins.facedroidtactoe;

import java.util.ArrayList;

public class GameAI {
	private GameManager _gm;
	
	public int computeNexteMove( ArrayList<Player> board ){
		return _NegaMaxAB(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 4);
	}
	
	private int _NegaMaxAB( ArrayList<Player> board, int alpha, int beta, int horizon ){
		
		if( horizon == 0 ){
			return _eval(board);
		}
		
		ArrayList<Player> board_tmp;
		ArrayList<Integer> list_position;
		int best;
		int value;
		
		best = Integer.MIN_VALUE;
		
		board_tmp = (ArrayList<Player>) board.clone();
		list_position = _gm.getAvailableCell(board);
		
		for( int cell : list_position ){
			_gm.play(board_tmp, cell);
			value = - _NegaMaxAB(board_tmp, -beta, -alpha, horizon-1);
			if( value > best ){
				best = value;
				if( best > alpha){
					alpha = best;
					if( alpha > beta ){
						return best;
					}
				}
			}
		}
		
		return best;
	}

	private int _eval(ArrayList<Player> board) {
		// TODO Auto-generated method stub
		/*
For each row, if there are both X and O, then the score for the row is 0.
If the whole row is empty, then the score is 1.
If there is only one X, then the score is 10.
If there are two Xs, then the score is 100.
If there are 3 Xs, then the score is 1000, and the winner is Player X.
For Player O, the score is negative.
Player X tries to maximize the score.
Player O tries to minimize the score.
If the current turn is for Player X, then the score of Player X has more advantage. I gave the advantage rate as 3.
		 */
		return 0;
	}
}