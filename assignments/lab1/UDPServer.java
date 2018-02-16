import java.io.*;
import java.net.*;

class UDPServer
{
	public static void main(String args[]) throws Exception 
	{
		// BufferedReader inFromUser =
		//  new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket(null);
		InetAddress IPAddress = InetAddress.getByName(args[0]);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		System.out.println(args[0] + " : " + args[1]);

		InetSocketAddress address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		System.out.println("Binding");
		clientSocket.bind(address);


		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM "+ receivePacket.getAddress() + " : " + modifiedSentence);
		}
	}
	
}