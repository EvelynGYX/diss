import java.util.List;

public class Game {
	private List<Player> players;
	private int num_of_players;
	//private StandardBoard board;
	private Board board;
	
	//private boolean endGame = false;
	private int pass = 0; //number of player pass in one round
	public Game(List<Player> players) {
		this.players = players;
		num_of_players = this.players.size();
		//board = new StandardBoard();
		try {
			board = (Board) Class.forName("board.StandardBoard").newInstance();		
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void startGame() {
		int round = 0;
		while(pass != players.size()) {
			System.out.println("round " + (round + 1));//broadcastAll
			playGame(players.get(round % num_of_players));
			round++;
		}
		System.out.println("End game");
	}
	
	private void playGame(Player player) {
		System.out.println(player.getName() + "'s turn");//broadcastAll
		if (player.wantPass()) {
			pass++;
		} else {
			putCharacter(player);
			voteOrPass(player);
			pass = 0;
		}	
	}
	
	private void putCharacter(Player player) {
		//boolean invalid = false;
		//while(!invalid) {   check input in client side!!!!!
			String[] input = player.getInput();
						
			char c = input[0].charAt(0);
			int x = Integer.parseInt(input[1]);
			int y = Integer.parseInt(input[2]);
			
			//player put a character, return its character c, index x,y		
			board.placeCharacter(c, x, y);
			
			board.printBoard(); //broadcastAll(board.getBoard());
		//}
	}
	
	private void voteOrPass(Player player) {		
		String command = player.getCommand();
		if (command.equals("vote")) {
			//if vote send command + index + word together?
			
			//check the index at client side, 
			//if valid, send index and word here
			int[] input = player.getIndex();
			//have to decide how to broadcast
			//1 broadcast board(with highlignt word) to user first then vote one by one
			//2 broadcast board(with highlignt word) and vote one by one
			
			//broadcast highlight word and broadcast vote
			if (broadcastAll()) {
				System.out.println("vote = yes");
				
				int point = board.calculatePoint(input[0], input[1], input[2], input[3]);
				System.out.println("point = " + point);
			} else {
				System.out.println("vote = no");
			}
		} 
	}
		
	//send message to one player
	private void broadcastPlayer(Player player, String message) {		
	}
	
	//send message to all players
	private boolean broadcastAll() {
		for(Player player: players) {
			if(!player.vote()) return false;
		}
		return true;
	}
}
