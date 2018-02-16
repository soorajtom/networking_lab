/*Author: Sooraj Tom*/
import java.io.*;
import java.net.*;

public class DNSClient {

    public static void main(String[] args) throws IOException {

        InetAddress ipAddress = InetAddress.getByName(args[1]);

        int serverport = 53;

        if(args.length > 2)
         {
            serverport = Integer.parseInt(args[2]);
         }
        byte[] request = new byte[512];
        int pointer = 12;

        // Identifier: A 16-bit identification field
        request[0] = 12;
        request[1] = 34;        

        // Write Query Flags
        request[2] = 0b00000001;  //Recursion desired is set to 1
        request[3] = 0b00000000;

        // Question Count: Specifies the number of questions in the Question section of the message.
        request[4] = 0;
        request[5] = 1;

        // Answer Record Count: Specifies the number of resource records in the Answer section of the message.
        request[6] = 0;
        request[7] = 0;

        // Authority Record Count: Specifies the number of resource records in the Authority section of 
        // the message. (“NS” stands for “name server”)
        request[8] = 0;
        request[9] = 0;

        // Additional Record Count: Specifies the number of resource records in the Additional section of the message.
        request[10] = 0;
        request[11] = 0;

        String[] domainParts = args[0].split("\\.");

        for (int i = 0; i<domainParts.length; i++) {
            byte[] domBytes = domainParts[i].getBytes("UTF-8");
            request[pointer++] = (byte)domBytes.length;
            for(int j = 0; j < domBytes.length ; j++)
                request[pointer++] = domBytes[j];
        }

        // No more parts
        request[pointer++] = 0;

        // Type -> 1 for A or 255 (-1) for *
        request[pointer++] = 0;
        request[pointer++] = -1;

        // Class 0x01 = IN
        request[pointer++] = 0;
        request[pointer++] = 1;

        // System.out.println("Sending: " + pointer + " bytes");
        // for (int i =0; i< pointer; i++) {
        //     System.out.print("0x" + String.format("%x", request[i]) + " " );
        // }

        // *** Send DNS Request Frame ***
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(request, request.length, ipAddress, serverport);
        socket.send(dnsReqPacket);

        // Await response from DNS server
        byte[] response = new byte[1024];
        DatagramPacket packet = new DatagramPacket(response, response.length);
        socket.receive(packet);

        // System.out.println("\n\nReceived: " + packet.getLength() + " bytes");

        int rescode = (int)(response[3] & 0b00001111);

        switch(rescode)
        {
            case 0:
                System.out.println("DNS Record Found.");
                parseresponse(response);
                break;
            case 1:System.out.println("Formal Error");
                break;
            case 2:System.out.println("Server Failure");
                break;
            case 3:System.out.println("Name Error");
                break;
            case 4:System.out.println("Error: Not Implemented");
                break;
            case 5:System.out.println("Error: Refused");
                break;
            case 6:System.out.println("Error: YX Domain");
                break;
            case 7:System.out.println("Error: YX RR Set");
                break;
            case 8:System.out.println("Error: NX RR Set");
                break;
            case 9:System.out.println("Error: Not Auth");
                break;
            case 10:System.out.println("Error: Not Zone");
                break;
        }
    }

    public static void parseresponse(byte[] response)
    {
        int no_res = ((int)response[6]) * 256 + ((int)response[7]);
        // System.out.println(no_res);

        int pointer;

        for (int i = 12; ;i++)
        {
            if(response[i] == 0)
            {
                pointer = i + 5;
                break;
            }
        }

        // for(int i = 0; i < response.length; i++)
        //     System.out.print(" | " + String.format("%8s", Integer.toBinaryString(response[i] & 0xFF)) + " " );

        for(int i=0; i<no_res; i++)
        {
            int type =  ((int) response[pointer + 2]) * 256 + ((int) response[pointer + 3]);
            // System.out.print("Type: ");
            // System.out.println(type);
            int len = ((int) response[pointer + 10]) * 256 + ((int) response[pointer + 11]);
            // System.out.println(len);
            if(type == 1)
            {
                System.out.print("Address: ");
                System.out.print((int)(response[pointer + 12] & 0xFF));
                System.out.print(".");
                System.out.print((int)(response[pointer + 13] & 0xFF));
                System.out.print(".");
                System.out.print((int)(response[pointer + 14] & 0xFF));
                System.out.print(".");
                System.out.println((int)(response[pointer + 15] & 0xFF));
            }
            pointer = pointer + 11 + len + 1;
        }
    }
}