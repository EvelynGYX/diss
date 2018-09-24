import java.util.ArrayList;
import java.util.List;

public class GameSystem {

    static List<ClientConnection> players;
    ArrayList<String> result;
    private static int pass = 0;
    private static int numPlayers;
    private static StandardBoard board;
    static String whoPlay;
    private static int round = 0;
    private static int numVote;
    static String voteResult;
    static boolean voteComplete = false;
    static boolean passComplete = false;

    public GameSystem() {
    }

    /*
     * public static synchronized GameSystem getInstance() { if (instance == null) {
     * instance = new GameSystem(); } return instance; }
     */

    public static void initialize() {
	players = ServerState.getInstance().getConnectedPlayers();
	for (ClientConnection player : players) {
	    player.setInGame(true);// endGame µÄÊ±ºòsetÎªfalse
	}
	numPlayers = players.size();
	whoPlay = players.get(round % numPlayers).getUsername();

	board = new StandardBoard();
    }

    public static void putCharacter(String msg) {
	// boolean invalid = false;
	// while(!invalid) { check input in client side!!!!!
	String[] input = msg.split(",");

	char c = input[0].charAt(0);
	int x = Integer.parseInt(input[1]);
	int y = Integer.parseInt(input[2]);

	// player put a character, return its character c, index x,y
	board.placeCharacter(c, x, y);
	pass = 0;
    }

    public static void pass() {
	round++;
	whoPlay = players.get(round % numPlayers).getUsername();
	
    }
    
    public static void passWithoutPut() {
	pass();
	pass++;
	if(pass == numPlayers) {
	    passComplete = true;
	}
    }

    public static void vote() {
	voteComplete = false;
	numVote = 0;
	voteResult = "true";	
	
    }

    public static void voteResult(String msg) {
	numVote++;
	if (msg.equals("disapprove")) {
	    voteResult = "false";
	} else {
	}
	if (numVote == numPlayers) {
	    voteComplete = true;
	}
    }



    

}
