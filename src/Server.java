import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.Timer;

public class Server {
	
	private static ServerSocket serverSocket;
	private static Timer timer;
	private static String jogador[];
	private static Socket socket[];
	private static Scanner scanner[];
	private static PrintStream printStream[];
	private static int numberOfPlayers = 0;
	private static Chat[] chats;
	private static Thread[] t;

	public static void main(String args[]){
		
		//inicializando numero de jogadores
		jogador = new String[4];
		socket = new Socket[4];
		scanner = new Scanner[4];
		printStream = new PrintStream[4];
		chats = new Chat[4];
		t = new Thread[4];
		
		//timer para contar os 3 minutos antes da partida começar
		timer = new Timer(60000*3, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Server.exit();
			}
		});
		
		//começa a contar o timer
		timer.start();
		
		try {
			//abrindo a porta
			serverSocket = new ServerSocket(5501);
			System.out.println("porta 5501 aberta");
		
			//espera os 4 jogadores entrarem
			for(int i = 0; i < 4; i++){
				socket[i] = serverSocket.accept();
				scanner[i] = new Scanner(socket[i].getInputStream());
				printStream[i] = new PrintStream(socket[i].getOutputStream());
				if(scanner[i].hasNext()){
					jogador[i] = scanner[i].next();
				}
				System.out.println("nome do jogador " + (i+1) + ": " + jogador[i]);
				Server.numberOfPlayers++;
			}
			
			//para o timer caso os 4 jogadores entrem a tempo
			timer.stop();
			Server.inicialState();
			
			
			//começa o loop infinito para as jogadas
			
			for(int i = 0; i <4 ; i++){
				chats[i] = new Chat(serverSocket, socket[i]);
				t[i] = new Thread(chats[i]);
				t[i].start();
			}
			while(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//implementar fim de jogo por questoes de timer
	public static void exit(){
		for(int i = 0; i < numberOfPlayers; i++){
			printStream[i].println(999);
		}
	}
	
	public static void message(String message){
		
		if(message.equals("exit()"))
			Server.exit();
		for (int i = 0; i < 4 ; i++){
			String newMessage = (i+1)+ " " + message;
			System.out.println(newMessage);
			printStream[i].println(newMessage);
		}
	}
	
	public static void inicialState(){
		for(int i = 0; i < numberOfPlayers; i++){
			String message = (i+1) + " " + 1;
//			printStream[i].println(i+1);
//			printStream[i].println(1);
			for (int j = 0; j < 32; j++){
				message += " " + 0;
//				printStream[i].println(0);
			}
			printStream[i].println(message);
		}
	}
	
}
