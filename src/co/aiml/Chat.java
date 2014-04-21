package co.aiml;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import bitoflife.chatterbean.AliceBot;

public class Chat {
	public static final String END = "bye";
	
	public static AliceBotMother mother = new AliceBotMother();
	public static AliceBot bot;
	public static String input() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("you say>");
		String input = "";
		try {
			input = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}
	public static String talk(String input) {
		return bot.respond(input);
	}
	public static void main(String[] args) throws Exception {
		mother.setUp();
		bot = mother.newInstance();
		(new SocketServerThread()).start();
	}
}

class SocketServerThread extends java.lang.Thread {

	private boolean OutServer = false;
	private ServerSocket server;
	private final int ServerPort = 8080;// 要監控的port

	public SocketServerThread() {
		try {
			server = new ServerSocket(ServerPort);

		} catch (java.io.IOException e) {
			System.out.println("Socket啟動有問題 !");
			System.out.println("IOException :" + e.toString());
		}
	}

	public void run() {
		Socket socket;
		java.io.BufferedInputStream in;

		System.out.println("伺服器已啟動 !");
		while (!OutServer) {
			socket = null;
			try {
				socket = server.accept();
				(new myHTTPServer(socket)).start();
				System.out.println("取得連線 : InetAddress = "
						+ socket.getInetAddress());
			} catch (java.io.IOException e) {
				System.out.println("Socket連線有問題 !");
				System.out.println("IOException :" + e.toString());
			}

		}
	}

}
