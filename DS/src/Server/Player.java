

import java.util.Scanner;

//used for testing
public class Player {
	Scanner scan = new Scanner(System.in);
	private String name;
	public Player(String name) {
		this.name = name;
	}
	
	public boolean wantPass() {
		System.out.println("Do you want to pass?");
		String vote = scan.nextLine();
		if (vote.equals("yes")) {
			return true;
		} else {
			return false;
		}
	}
	
	public String[] getInput() {
		System.out.println("Enter board input c, x, y");
		String[] a = new String[3];
		a[0] = scan.nextLine();
		a[1] = scan.nextLine();
		a[2] = scan.nextLine();
		return a;
	}
	
	public int[] getIndex() {
		System.out.println("Enter index of word x1,y1  x2,y2");
		int[] a = new int[4];
		String[] b = scan.nextLine().split(" ");
		a[0] = Integer.parseInt(b[0]);
		a[1] = Integer.parseInt(b[1]);
		a[2] = Integer.parseInt(b[2]);
		a[3] = Integer.parseInt(b[3]);
		return a;
	}
	
	public String getCommand() {
		System.out.println("vote or pass?");
		String command = scan.nextLine();
		return command;
	}
	public boolean vote() {
		System.out.println("vote yes or no?");
		String vote = scan.nextLine();
		if (vote.equals("yes")) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getName() {
		return name;
	}
	
}
