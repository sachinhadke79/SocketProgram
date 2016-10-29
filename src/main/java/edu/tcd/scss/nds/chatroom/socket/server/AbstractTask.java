package edu.tcd.scss.nds.chatroom.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class AbstractTask implements Task{
	
	public abstract void releaseThread();
	
	public void closeConnecion(Socket clientSocket){
		try{ 
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			String leavingRoomId = null;
			String leavingUserId = null;
			String leavingClientName = null;

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if ((inputLine == null) || inputLine.startsWith("LEAVE_CHATROOM")) {
					leavingRoomId = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
				} else if ((inputLine == null) || inputLine.startsWith("JOIN_ID")) {
					leavingUserId = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
				} else if ((inputLine == null) || inputLine.startsWith("CLIENT_NAME")) {
					leavingClientName = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());					
				} 

				if(leavingRoomId != null && leavingUserId != null && leavingClientName != null){
					// nullify all fields
					leavingRoomId = null;
					leavingUserId = null;
					leavingClientName = null;
					
					// release threads
					releaseThread();
					
					// close client socket
					out.println("server> Client connection closed");
					clientSocket.close();
					
					System.out.println("Maximum pool size "+ThreadPoolManager.getInstance().getMaximumPoolSize());
					System.out.println("Number of threads in pool "+ThreadPoolManager.getInstance().getCurrentPoolSize());
					System.out.println("Number of available threads "+ThreadPoolManager.getInstance().getAvailableThreads());
					System.out.println("Number of busy threads "+ThreadPoolManager.getInstance().getBusyThreads());
                    return;

				}
			}
		} catch (IOException ex){
			System.out.println("Error trying to receive/send data from/to client.");
		}

	}

}
