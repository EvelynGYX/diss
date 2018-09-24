//Yizhou Wang
//669026
//DS project1

package Server;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ClientConnection extends Thread {
	private String username;
	private static String userListStr = "";
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int clientNum;
    private TextArea process;
    private TextArea currentuser;
    private TextField currentuserNo;


    public ClientConnection(Socket clientSocket, int clientNum, TextArea process, TextArea currentusers, TextField currentuserNo) {
        try {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            this.clientNum = clientNum;
            this.process = process;
            this.currentuser = currentusers;
            this.currentuserNo=currentuserNo;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseJsonValue(String json, String key) {
		String message = json.replaceAll("\n", "");
		JSONObject jsonObject = JSONObject.fromObject(message); 
		Map object = (Map) jsonObject;
		return (String)object.get(key);
    }
    
	public String parseJsonKey(String jsonStr) {
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Map object = (Map) jsonObject;
		if(object.containsKey("username"))
			return "username";
		else if(object.containsKey("exit"))
			return "exit";
		return null;
	}
	
	public void printUsers() {
		if(ServerState.getInstance().getUserList().size() > 0) {
			userListStr = "";
			for(String user : ServerState.getInstance().getUserList()) 
				userListStr += (user + "\n");
		}
		currentuser.setText(userListStr);
	}
	
    @Override
    public void run() {
        try {
//            Dictionary word =new Dictionary();
            process.appendText(Thread.currentThread().getName()
                    + " - Reading messages from client's " + clientNum + " connection"+"\n");

            String clientMsg;
            while ((clientMsg = reader.readLine()) != null) {
            		String key = parseJsonKey(clientMsg);
            		clientMsg = parseJsonValue(clientMsg, key);
            		if(key.equals("username")) {
            			this.username = clientMsg;           			
            			ServerState.getInstance().addUser(this.username);
            			printUsers();
            		}
            		else if (!(clientMsg.equals("CONNECTED or NOT")||clientMsg.equals("EXIT")))
                {
                    process.appendText(Thread.currentThread().getName()
                            + " - Message from client " + clientNum + " received: " + clientMsg+"\n");
//                    operation(clientMsg,word);
                }
                else if(clientMsg.equals("EXIT"))
                {
                    process.appendText(Thread.currentThread().getName()
                            + " - Message from client " + clientNum + " received: " + clientMsg+"\n");
                    ServerState.getInstance().removeUser(this.username);
                    printUsers();
                    break;
                }
            }

            clientSocket.close();
            ServerState.getInstance().clientDisconnected(this);
            currentuserNo.setText(String.valueOf(ServerState.getInstance().getConnectedClients().size())+" client"+"\n");
            process.appendText(Thread.currentThread().getName()
                    + " - Client " + clientNum + " disconnected"+"\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public synchronized void operation (String clientMsg,Dictionary word)
//    {
//        int i = clientMsg.indexOf("\t");
//        String operation = clientMsg.substring(0, i);
////                System.out.println(operation);
//        String rest = clientMsg.substring(i+1);
////                System.out.println(rest);
//        if (operation.equalsIgnoreCase("QUERY"))
//        {
//            String dictionary = word.lookup(rest).replaceAll("\n","#");
//            write(dictionary, process);
//        }
//        else if(operation.equalsIgnoreCase("ADD"))
//        {
//            String key =rest.substring(0,rest.indexOf(" "));
//            String value =rest.substring(rest.indexOf(" ")+1);
//            if (value.equals(""))
//            {
//                write("Please enter the value!",process);
//            }
//            write(word.add(key,value),process);
//        }
//        else if(operation.equalsIgnoreCase("REMOVE"))
//        {
//            write(word.remove(rest),process);
//        }
//    }

    //Needs to be synchronized because multiple threads can me invoking this method at the same
    //time
    public synchronized void write(String msg, TextArea process) {
        try {
            writer.write(msg + "\n");
            writer.flush();
            process.appendText(Thread.currentThread().getName() + " - Message sent to client " + clientNum+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

