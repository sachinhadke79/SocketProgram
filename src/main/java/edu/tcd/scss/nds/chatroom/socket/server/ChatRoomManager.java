package edu.tcd.scss.nds.chatroom.socket.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.tcd.scss.nds.chatroom.socket.models.ChatRoom;
import edu.tcd.scss.nds.chatroom.socket.models.ChatUser;

public class ChatRoomManager {
	public static ChatRoomManager instance;
	
	private Set<ChatRoom> chatRooms; 
	private Set<ChatUser> chatUsers; 
	
	private ChatRoomManager(){
		chatRooms = new HashSet<ChatRoom>();
		chatUsers = new HashSet<ChatUser>();
	}
	
	public static ChatRoomManager getInstance(){
		if(instance == null){
			instance = new ChatRoomManager();
		}
		return instance;		
	}
	
	public ChatRoom getChatRoom(String chatRoomName, Socket clientSocket){
		// if room exist then find it and return
		ChatRoom chatRoom = getChatRoomBaseOnName(chatRoomName);
		if(chatRoom != null){
			chatRoom.addClientSocket(clientSocket);
			System.out.println("Chat room already exist. name "+chatRoom.getName());
			Set<Socket> tempSocket = chatRoom.getClientSockets();
			for (Iterator<Socket> iterator = tempSocket.iterator(); iterator.hasNext();) {
				Socket socket = (Socket) iterator.next();
				System.out.println("socket in "+chatRoomName+" room is "+socket);
			}
			return chatRoom;
		}
		System.out.println("Creating new chat room. name "+chatRoomName);
		//else create a new room, 
		chatRoom = new ChatRoom();
		chatRoom.setName(chatRoomName);
		chatRoom.addClientSocket(clientSocket);
		chatRooms.add(chatRoom);
		
		Set<Socket> tempSocket = chatRoom.getClientSockets();
		for (Iterator<Socket> iterator = tempSocket.iterator(); iterator.hasNext();) {
			Socket socket = (Socket) iterator.next();
			System.out.println("socket in "+chatRoomName+" room is "+socket);
		}
		return chatRoom;
	}
	
	private ChatRoom getChatRoomBaseOnName(String chatRoomName){
		for (Iterator<ChatRoom> iterator = chatRooms.iterator(); iterator.hasNext();) {
			ChatRoom room = (ChatRoom) iterator.next();
			if(room.getName().equalsIgnoreCase(chatRoomName)){
				return room;
			}
		}
		return null;
	}
	
	public ChatUser getChatUser(String userName, String clientIp, String port, int socketId, ChatRoom room, Socket clientSocket){
		ChatUser user = getChatUserBaseOnName(userName);
		if(user != null){
			System.out.println("User already exist. name "+user.getName());
			return user;
		}
		
		System.out.println("Creating new user. name "+userName);
		
		//else create a new room, 
		ChatUser newUser = new ChatUser();
		newUser.setName(userName);
		newUser.setHost(clientIp);
		newUser.setPort(port);
		newUser.setClientSocket(clientSocket);	
		chatUsers.add(newUser);

		return newUser;
	}
	
	private ChatUser getChatUserBaseOnName(String userName){
		// if user exist then find it and return
		for (Iterator<ChatUser> iterator = chatUsers.iterator(); iterator.hasNext();) {
			ChatUser user = (ChatUser) iterator.next();
			if(user.getName().equalsIgnoreCase(userName)){
				return user;
			}
		}
		return null;
	}
	
	public void notifyClient(){
		
	}
	
	
	public void handleChatInRoom(String inputLine, Socket clientSocket){
		System.out.println("Inside chat room");
		// get users based on socket
		ChatUser typingUser = null;
		for (Iterator<ChatUser> iterator = chatUsers.iterator(); iterator.hasNext();) {
			ChatUser user = (ChatUser) iterator.next();
			if(user.getClientSocket().equals(clientSocket)){
				typingUser = user;
			}
		}
		
		System.out.println("typing user "+typingUser);
		// get chat room of login user and socket; possibly only socket
		if(typingUser != null){
			ChatRoom typingRoom = null;
			for (Iterator<ChatRoom> iterator = chatRooms.iterator(); iterator.hasNext();) {
				ChatRoom chatRoom = (ChatRoom) iterator.next();
				Set<ChatUser> chatUsers = chatRoom.getUsers();
				for (Iterator<ChatUser> iterator2 = chatUsers.iterator(); iterator2.hasNext();) {
					ChatUser chatUser = (ChatUser) iterator2.next();
					if(chatUser.getName().equals(typingUser.getName())){
						Set<Socket> clientSockets = chatRoom.getClientSockets();
						for (Iterator<Socket> iterator3 = clientSockets.iterator(); iterator3.hasNext();) {
							Socket socket = (Socket) iterator3.next();
							if(socket.equals(clientSocket)){
								typingRoom = chatRoom;
								break;
							}
						}
					}
				}
			}
			
			System.out.println("typing room "+typingRoom);
			if(typingRoom != null){
				Set<Socket> clientSockets = typingRoom.getClientSockets();
				for (Iterator<Socket> iterator = clientSockets.iterator(); iterator.hasNext();) {
					Socket toSendSocket = (Socket) iterator.next();
					PrintWriter out = null;
					try{ 
						out = new PrintWriter(toSendSocket.getOutputStream(), true);
						out.println(inputLine);
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		
	}
}
