package edu.tcd.scss.nds.chatroom.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ResponseTask implements Task{
	
	public void perform(Socket clientSocket){
		PrintWriter out = null;
		BufferedReader in = null;
		try{ 
			out = new PrintWriter(clientSocket.getOutputStream(), true);                   
			out.println("server> To join existing room or create a new room type in JOIN_CHATROOM:<name of chat room>");
			out.println("server> To leave room type in LEAVE_CHATROOM:<name of chat room>");
			
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			// TODO: need to release client connection or remove this class not needed. 
			// hooking the closing logic
//			checkAndClose(in, out, clientSocket);
		} catch (IOException ex){
			System.out.println("Error trying to receive/send data from/to client.");
		} finally {
//			try{
////				clientSocket.close();// just close at one place; in request task
//				in.close();
//				out.close();
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
		}
	}
	
	private void checkAndClose(BufferedReader in, PrintWriter out, Socket clientSocket) throws IOException{
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
				ThreadPoolManager.getInstance().releaseTask();
				
				// close client socket, the return text is used to terminate the client
				out.println("server> Client connection closed");
				out.close();
				in.close();
//				clientSocket.close(); // just close at one place; in request task
				
                return;
			}
		}
	}
}
