import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Chat implements Runnable {
	
	private static ArrayList<Socket> lst = new ArrayList<Socket>();
	private static ArrayList<PrintStream> messageLst = new ArrayList<PrintStream>();
	private Scanner scanner;
	private ServerSocket serverSocket;
	
	public Chat(ServerSocket serverSocket, Socket socket){
		this.serverSocket = serverSocket;
		try {
			this.scanner = new Scanner(socket.getInputStream());
			messageLst.add(new PrintStream(socket.getOutputStream()));
			lst.add(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(scanner.hasNext()){
			Server.message(scanner.nextLine());
		}
	}
	
	public void message(String message){
		if(message.equals("exit()"))
			this.finalize();
		for (PrintStream printStream : messageLst ){
			printStream.println(message);
		}
	}
	
	public void finalizeScanner(){
		scanner.close();
	}
	
	public void finalizeClients(){
		for(Socket socket : lst){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void finalize(){
		this.finalizeScanner();
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finalizeClients();
	}

}
