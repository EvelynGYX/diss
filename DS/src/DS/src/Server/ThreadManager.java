//Yizhou Wang
//669026
//DS project1

package Server;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadManager extends Thread{
    private TextArea processes;
    private ServerSocket listeningSocket;
    private TextArea currentuser;
    private TextField currentuserNo;
    private static int clientNum = 0;

    public ThreadManager(ServerSocket serverSocket, TextArea processes, TextArea currentuser, TextField currentuserNo){
        this.processes = processes;
        this.listeningSocket = serverSocket;
        this.currentuser=currentuser;
        this.currentuserNo=currentuserNo;
    }

    @Override
    public void run() {
        try{
            while (true) {
                //Accept an incoming client connection request
                Socket clientSocket = listeningSocket.accept();
                processes.appendText(Thread.currentThread().getName()
                        + " - Client connection accepted"+"\n");
                clientNum++;

                //Create a client connection to listen for and process all the messages
                //sent by the client
                ClientConnection clientConnection = new ClientConnection(clientSocket, clientNum,processes,currentuser,currentuserNo);
                clientConnection.setName("Thread" + clientNum);
                clientConnection.start();
                
                //Update the server state to reflect the new connected client
                ServerState.getInstance().clientConnected(clientConnection);
                currentuserNo.setText(String.valueOf(ServerState.getInstance().getConnectedClients().size())+" client"+"\n");
                //currentuser.setText(String.valueOf(ServerState.getInstance().getConnectedClients().size())+" client"+"\n");
                //currentuser.setText(String.valueOf(ServerState.getInstance().getUserList().size())+" client"+"\n");
                
                /*
                System.out.println(ServerState.getInstance().getUserList() + " list");
                String str = null;
                for(String user : ServerState.getInstance().getUserList())
                		str = str + user + "\n";
                //currentuser.setText(str + "\n");
                 */
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }finally {
            if ( listeningSocket != null ) {
                try {
                    listeningSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    
}
