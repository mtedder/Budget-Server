/**
 * 
 */
package com.gc.myserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Maurice Sample program to illustrate how a Web Server works Access
 *         url: http://localhost:4321/web/HelloWorld.html?firstname=John&lastname=Doe
 */
public class BudgetServer {

	private static final int PORT = 4321;
	private static String filePath;// = "./web/ServerPage.html";
	private static String requestParameters;
	private static String requestUrl;
	final static String CRLF = "\r\n";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Server is running");

		// Get input/output streams
		// BufferedReader in = null;
		// PrintWriter out = null;
		// BufferedReader reader = null;
		// ServerSocket socket = null;
		// try with resources
		ServerSocket socket = null;
		Socket client = null;
		OutputStream output = null;
		BufferedReader input = null;
		try {
			// Create socket on port 4321
			socket = new ServerSocket(PORT);
			// Wait for someone to connect
			client = socket.accept();
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = client.getOutputStream();
			// PrintWriter out = new PrintWriter(client.getOutputStream(),
			// true);
			System.out.println("Here is the client request:\n");

			// Response header values
			String statusLine = "HTTP/1.0 200 OK" + CRLF;
			String contentTypeLine = "text/html";
			String contentLengthLine = "Content-Length: " + CRLF;

			// Send the status line.
			output.write(statusLine.getBytes());
			// System.out.println(statusLine);

			// Send the server line.
			// output.write(serverLine.getBytes());
			// System.out.println(serverLine);

			// Send the content type line.
			output.write(contentTypeLine.getBytes());
			// System.out.println(contentTypeLine);

			// Send the Content-Length
			output.write(contentLengthLine.getBytes());

			output.write(CRLF.getBytes());

			// out.write(entityBody.getBytes());

			while (input.ready()) {
				// System.out.print((char)in.read());// display http
				// POSTBrequest
				String request = input.readLine();
				System.out.println(request);// display http
											// GET request
				String[] parse = request.split(" ");// determine request method
													// and requested resource
				if (parse.length > 0 && parse[0].equalsIgnoreCase("GET")) {
					requestUrl = parse[1];
				}
			}

			System.out.println("End of the client request\n");
			
			// String filePathSeparator =
			// System.getProperty("file.separator");
			String[] requestValues = requestUrl.split("\\?");// get parameters
			if (requestValues.length > 0) {
				// Set filepath
				System.out.println("Requested resource file:" + requestValues[0]);
				filePath = requestValues[0];
			}
			String[] params = null;			
			if (requestValues.length > 1) {
				// Set request parameters
				requestParameters = requestValues[1];
				// Parse request parameters
				params = requestParameters.split("&");
				System.out.println("params:" + Arrays.toString(params));
			}
			// Return html response file from local drive based on Get
			// request			
			Path path = Paths.get(".".concat(filePath)).normalize();			
			File file = path.toAbsolutePath().toFile();// get file - run for
														// eclipse
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuffer returnStuff = new StringBuffer();

			while (reader.ready()) {// read file contents into string
									// buffer
				String line = reader.readLine();
				// System.out.println("*:" + line);
				if (line.trim().equalsIgnoreCase("<body>")) {
					// insert dynamic content into return html page
					returnStuff.append("<p>Your parameters:<br>");
					for (int i = 0; i < params.length; i++) {
						returnStuff.append(params[i]).append("<br>");
					}
					returnStuff.append("</p>");
				}
				returnStuff.append(line);
			}
			output.write(returnStuff.toString().getBytes());// write response to
															// socket
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("IOException");
			System.exit(-1);
		} finally { // Close all open streams
			
			if (output != null) {//this is bad - rewrite later
				try {
					output.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
			
			if(input != null){//this is bad - rewrite later
				try {
					input.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
			
			if(socket != null){//this is bad - rewrite later
				try {
					socket.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}			
			
			if(client != null){//this is bad - rewrite later
				try {
					client.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
			System.out.println("\nServer stopped");
		}
	}
}
