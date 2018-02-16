import java.io.*;
import java.net.*;

class EchoCanyon implements Runnable {

    private Socket clientsock;
    // private Bool connected = true;

    public EchoCanyon(Socket clientsock) {
        this.clientsock = clientsock;
    }

    public void run() {
    	int i;
        System.out.println("Connected : " + clientsock.getRemoteSocketAddress().toString());
        try(
        	DataOutputStream outToServer = new DataOutputStream(clientsock.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientsock.getInputStream()));
        	)
        {
            while((i=inFromServer.read())!=-1)
            {
                outToServer.write(i);
            }
        }
        catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        System.out.println("Connection closed : " + clientsock.getRemoteSocketAddress().toString());
    }
}

class EchoServer 
{
	public static void main(String[] args) throws Exception
	{
		
		if(args.length < 1)
		{
			System.out.println("Please give a port number as an argument");
			return;
		}
		else if(!args[0].matches("^[0-9]+$"))
		{
			System.out.println("Please Enter a number between 1025 and 65535 for port");
			return;
		}

		int portNumber = Integer.parseInt(args[0]);

		ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("Echo server started on port: " + portNumber);

		while(true)
		{
			Socket client = serverSocket.accept();

			EchoCanyon EchoCanyon = new EchoCanyon(client);
			Thread EchoT = new Thread(EchoCanyon);
			EchoT.start();
		}
	}
}
