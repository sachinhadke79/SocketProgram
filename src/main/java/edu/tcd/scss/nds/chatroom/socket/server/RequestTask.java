package edu.tcd.scss.nds.chatroom.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.tcd.scss.nds.chatroom.socket.models.ChatRoom;
import edu.tcd.scss.nds.chatroom.socket.models.ChatUser;

public class RequestTask implements Task{

	public void perform(Socket clientSocket){
		BufferedReader in = null;
		PrintWriter out = null;
		try{ 
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println("server> To join existing room or create a new room type in JOIN_CHATROOM:<name of chat room>");
			out.println("server> To leave room type in LEAVE_CHATROOM:<name of chat room>");
			
			String roomName = null;
			String clientAddress = null;
			String port = null;
			String clientName = null;

			String leavingRoomId = null;
			String leavingUserId = null;
			String leavingClientName = null;

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				PooledThread resThread = (PooledThread)Thread.currentThread();
				System.out.println("Client input is "+inputLine+" inside thread "+resThread.getName()+" and thread status is "+resThread.getRunnbingStatus());

				if ((inputLine == null) || inputLine.startsWith("JOIN_CHATROOM")) {
					roomName = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
					out.println("server>Enter CLIENT_IP:<client host>");
				} else if ((inputLine == null) || inputLine.startsWith("CLIENT_IP")) {
					clientAddress = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
					out.println("server>Enter PORT:<client port>");
				} else if ((inputLine == null) || inputLine.startsWith("PORT")) {
					port = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
					out.println("server>Enter CLIENT_NAME:<client name>");
				} else if ((inputLine == null) || inputLine.startsWith("CLIENT_NAME")) {
					clientName = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());					
				} else if ((inputLine == null) || inputLine.startsWith("LEAVE_CHATROOM")) {
					leavingRoomId = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
					out.println("server>Enter JOIN_ID:<user id>");
				} else if ((inputLine == null) || inputLine.startsWith("JOIN_ID")) {
					leavingUserId = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());
					out.println("server>Enter CLIENT_NAME:<client name>");
				} else if ((inputLine == null) || inputLine.startsWith("CLIENT_NAME")) {
					leavingClientName = inputLine.substring(inputLine.indexOf(':')+1, inputLine.length());					
				} else {
					ChatRoomManager.getInstance().handleChatInRoom(inputLine, clientSocket);
				}

				if(roomName != null && clientAddress != null && port != null && clientName != null){
					ChatRoomManager manager = ChatRoomManager.getInstance();
					ChatRoom room = manager.getChatRoom(roomName, clientSocket);
					ChatUser user = manager.getChatUser(clientName, clientAddress, port, clientSocket.getLocalPort(), room, clientSocket);
					room.addUser(user);
					
					System.out.println("User "+user.getName()+" is connected to chat room "+room.getName());
					out.println("server> JOINED_CHATROOM: "+room.getName());
					out.println("server> SERVER_IP: "+user.getHost());
					out.println("server> PORT: "+user.getPort());
					out.println("server> ROOM_REF: "+room.getId());
					out.println("server> JOIN_ID: "+user.getName());
					
					roomName = null;
					clientAddress = null;
					port = null;
					clientName = null;

				}
				

				if(leavingRoomId != null && leavingUserId != null && leavingClientName != null){
					// release the task
					ThreadPoolManager.getInstance().releaseTask();
					
					// close client socket
					in.close();
					out.close();
					clientSocket.close();
					
                    return;
				}
			}
		} catch (IOException ex){
			System.out.println("Error trying to receive/send data from/to client.");
		} finally{
			try{
				in.close();
				out.close();
				clientSocket.close();
			}catch(Exception ex){
				// i don't care;
			}
		}
	}
}
