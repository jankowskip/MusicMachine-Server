import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class MusicMachineServer {

	ArrayList<ObjectOutputStream> outStreamToClients;
	
	public static void main(String[] args) {
	 new MusicMachineServer().start();

	}
	public class ClientService implements Runnable {
		ObjectInputStream in;
		Socket clientSocket;
		
		public ClientService(Socket socket){
			try{
				clientSocket = socket;
				in = new ObjectInputStream(clientSocket.getInputStream());
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		@Override
		public void run() {
			Object o2 = null;
			Object o1 = null;
			try {
				while ((o1 = in.readObject()) != null){
					o2 = in.readObject();
					System.out.println("Read two objects");
					messageToAll(o1,o2);
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	public void start() {
		outStreamToClients = new ArrayList<ObjectOutputStream>();
		try {
			ServerSocket serverSocket = new ServerSocket(4999);
			while(true){
				Socket clientSocket = serverSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				outStreamToClients.add(out);
				
				Thread thread = new Thread(new ClientService(clientSocket));
				thread.start();
				
				System.out.println("We got connection");
			}
		} catch( Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void messageToAll(Object obj1, Object obj2){
		Iterator iter = outStreamToClients.iterator();
		while(iter.hasNext()){
			try {
				ObjectOutputStream out = (ObjectOutputStream) iter.next();
				out.writeObject(obj1);
				out.writeObject(obj2);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
