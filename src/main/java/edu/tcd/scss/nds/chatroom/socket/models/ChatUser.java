package edu.tcd.scss.nds.chatroom.socket.models;

import java.net.Socket;


public class ChatUser {
	private String name;
	private String id;
	private String host;
	private String port;
	private Socket clientSocket;
	
	public ChatUser(){
		id = String.valueOf(Math.random());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
		

	@Override
	public String toString() {
		return "ChatUser [name=" + name + ", id=" + id + ", host=" + host
				+ ", port=" + port + ", clientSocket=" + clientSocket + "]";
	}
	
	
}
