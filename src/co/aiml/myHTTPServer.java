package co.aiml;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLDecoder;
import java.util.StringTokenizer;

public class myHTTPServer extends Thread {

	static final String HTML_START = "<html>"
			+ "<title>HTTP Server in java</title>" + "<body>";

	static final String HTML_END = "</body>" + "</html>";

	Socket connectedClient = null;
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;

	public myHTTPServer(Socket client) {
		connectedClient = client;
	}

	public String getURLArgument(String URL, String argu) {
		URL = URL.substring(2);
		StringTokenizer tokenizer = new StringTokenizer(URL, "&");
		while (tokenizer.hasMoreTokens()) {
			String token[] = tokenizer.nextToken().split("=");
			String key = token[0];
			String value = token[1];
			try {
				value = java.net.URLDecoder.decode(token[1], "UTF-8");
			} catch (Exception e) {

			}
			if (key.equals(argu))
				return value;
		}
		return "NOT FOUND";
	}

	public void run() {

		try {

			System.out.println("The Client " + connectedClient.getInetAddress()
					+ ":" + connectedClient.getPort() + " is connected");

			inFromClient = new BufferedReader(new InputStreamReader(
					connectedClient.getInputStream()));
			outToClient = new DataOutputStream(
					connectedClient.getOutputStream());

			String requestString = inFromClient.readLine();
			String headerLine = requestString;

			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			String httpMethod = tokenizer.nextToken();
			String httpQueryString = tokenizer.nextToken();

			StringBuffer responseBuffer = new StringBuffer();
			// System.out.println("The HTTP request string is ....");
			// while (inFromClient.ready()) {
			// Read the HTTP complete HTTP Query
			// responseBuffer.append(requestString + "<BR>");
			// System.out.println(requestString);
			// requestString = inFromClient.readLine();
			// }

			if (httpMethod.equals("GET")) {
				if (httpQueryString.charAt(0) == '/') {
					// The default home page
					String message = getURLArgument(httpQueryString, "msg");
					String test = Chat.talk(message);
					if(test.trim().length() == 0)
						test = "mmmmm...";
					System.out.println("IN >" + message);
					System.out.println("OUT >" + test);
					responseBuffer.append(test);
					sendResponse(200, responseBuffer.toString(), false);
				} else {
					// This is interpreted as a file name
					String fileName = httpQueryString.replaceFirst("/", "");
					fileName = URLDecoder.decode(fileName);
					if (new File(fileName).isFile()) {
						sendResponse(200, fileName, true);
					} else {
						sendResponse(
								404,
								"<b>The Requested resource not found ...."
										+ "Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>",
								false);
					}
				}
			} else
				sendResponse(
						404,
						"<b>The Requested resource not found ...."
								+ "Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>",
						false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(int statusCode, String responseString,
			boolean isFile) throws Exception {

		String statusLine = null;
		String serverdetails = "Server: Java HTTPServer";
		String contentLengthLine = null;
		String fileName = null;
		String contentTypeLine = "Content-Type: text/html; charset=utf-8" + "\r\n";
		FileInputStream fin = null;

		if (statusCode == 200)
			statusLine = "HTTP/1.1 200 OK" + "\r\n";
		else
			statusLine = "HTTP/1.1 404 Not Found" + "\r\n";

		if (isFile) {
			fileName = responseString;
			fin = new FileInputStream(fileName);
			contentLengthLine = "Content-Length: "
					+ Integer.toString(fin.available()) + "\r\n";
			if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
				contentTypeLine = "Content-Type: \r\n";
		} else {
			responseString = /* myHTTPServer.HTML_START + */responseString
			/* + myHTTPServer.HTML_END */;
			contentLengthLine = "Content-Length: " + responseString.length()
					+ "\r\n";
		}

		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(serverdetails);
		outToClient.writeBytes("\r\nAccess-Control-Allow-Origin: *\r\n");
		outToClient.writeBytes(contentTypeLine);
		outToClient.writeBytes(contentLengthLine);
		outToClient.writeBytes("Connection: close\r\n");
		outToClient.writeBytes("\r\n");

		if (isFile) {
			sendFile(fin, outToClient);
		} else {
			outToClient.writeBytes(responseString);
		}

		outToClient.close();
	}

	public void sendFile(FileInputStream fin, DataOutputStream out)
			throws Exception {
		byte[] buffer = new byte[1024];
		int bytesRead;

		while ((bytesRead = fin.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		fin.close();
	}
}