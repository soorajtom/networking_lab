import java.io.*;
import java.net.*;

class TCPListener implements Runnable {

    private BufferedReader inFromServer;

    public TCPListener(BufferedReader inFromServer) {
        this.inFromServer = inFromServer;
    }

    public void run() {
    	int i;
        try{
            while(true)
            {
                if((i=inFromServer.read())!=-1){  
                    System.out.print((char)i);  
                } 
            }
        }
        catch (Exception e) {
                System.out.println("Listening stopped");
            }
		
    }
}


class TCPClient
{
	public static void main(String args[]) throws Exception 
	{
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try(
			Socket newsocket = new Socket(hostName, portNumber);
			DataOutputStream outToServer = new DataOutputStream(newsocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(newsocket.getInputStream()));
			)
		{
			
			TCPListener TCPListener = new TCPListener(inFromServer);
			Thread listenerT = new Thread(TCPListener);
			listenerT.start();

			String request = "";

			while(true){
				String sentence = System.console().readLine();
				outToServer.writeBytes(sentence + "\r\n");
			}
			//newsocket.close();
		}
		catch (Exception e) {
                System.out.println("Sending stopped");
            }
	}
	
}