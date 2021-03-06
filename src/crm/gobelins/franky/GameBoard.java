package crm.gobelins.franky;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
	public Player current_player = null;
	public GameMode mode = null;
	public GameState state = null;
	public Player winner = null;
	public List<Integer> available = new ArrayList<Integer>(
			GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH);
	public List<Integer> winner_cells = new ArrayList<Integer>(
			GameConsts.GAME_WIDTH);
	public List<Player> grid = new ArrayList<Player>(GameConsts.GAME_WIDTH
			* GameConsts.GAME_WIDTH);

	public GameBoard(GameMode m) {
		mode = m;
		winner = null;
		grid.clear();
		winner_cells.clear();

		for (int i = 0; i < GameConsts.GAME_WIDTH * GameConsts.GAME_WIDTH; i++) {
			grid.add(Player.EMPTY);
			available.add(i);
		}

		if( Math.random() > .5 ){
			current_player = Player.PLAYER_O;
			state = GameState.PLAYER_O_TURN;						
		}else{
			current_player = Player.PLAYER_X;
			state = GameState.PLAYER_X_TURN;			
		}

	}

	public GameBoard(GameBoard board) {
		current_player = board.current_player;
		mode = board.mode;
		state = board.state;
		winner = board.winner;
		available = new ArrayList<Integer>(board.available.size());
		for (Integer cell : board.available) {
			available.add(cell);
		}
		winner_cells = new ArrayList<Integer>(board.winner_cells.size());
		for (Integer cell : board.winner_cells) {
			winner_cells.add(cell);
		}
		grid = new ArrayList<Player>(board.grid.size());
		for (Player p : board.grid) {
			grid.add(p);
		}		
	}
}
