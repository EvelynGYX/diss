//Yizhou Wang
//669026
//DS project1


import javafx.scene.control.TextArea;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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

    public ClientConnection(Socket clientSocket, int clientNum, TextArea process, TextArea currentusers) {
        try {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            this.clientNum = clientNum;
            this.process = process;
            this.currentuser = currentusers;
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
	
	public String encapsulateJson(String key, String value) {
		Map<String, String> json = new HashMap<>();
		json.put(key, value);
		JSONObject object = JSONObject.fromObject(json);
		return object.toString();
	}
	
	public String encapsulateJsonArray(ArrayList<String> arrayList) {
		return JSONArray.fromObject(arrayList).toString();
	}
	
	public void broadcast(String key, String value) {
		for(ClientConnection client: ServerState.getInstance().getConnectedClients()) {
			String broadcast = encapsulateJson(key,value) + "\n";
			try {				
				client.getWriter().write(broadcast);
				client.getWriter().flush();
				System.out.println(client.getWriter() + " " + broadcast);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
            			String boardcast = encapsulateJsonArray(ServerState.getInstance().getUserList());
            			System.out.println(boardcast);
            			broadcast("update_userList", boardcast);
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
            currentuser.setText(String.valueOf(ServerState.getInstance().getConnectedClients().size())+" client"+"\n");
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

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

}

