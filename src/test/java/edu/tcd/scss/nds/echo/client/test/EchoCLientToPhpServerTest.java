package edu.tcd.scss.nds.echo.client.test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import edu.tcd.scss.nds.echo.client.EchoClientToPhpServer;

public class EchoCLientToPhpServerTest {
	private EchoClientToPhpServer client;
	
//	@Before
	public void setup(){
		client = new EchoClientToPhpServer();
	}
	
//	@Test
	public void testJavaEcho(){
		client.setURLParam("Java_API");
		try {
			String response = client.callPhpServerUsingGET_JavaVersion();
			assertTrue("Match response", response.equals("JAVA_API\\n"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testApacheEcho(){
		String message = "Apache_API";
		client.setURLParam(message);
		try {
			String response = client.callPhpServerUsingGET_ApacheVersion();
			System.out.println(response.substring(message.length(), response.length()));
			System.out.println("APACHE_API\n");
			assertTrue("Match response", response.substring(0, message.length()).equals("APACHE_API"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
