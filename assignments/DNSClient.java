import java.io.*;
import java.net.*;

public class DNSClient {

    public static void main(String[] args) throws IOException {

        InetAddress ipAddress = InetAddress.getByName(args[1]);

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

        // Type -> 1 for A or 255 for *
        request[pointer++] = 0;
        request[pointer++] = -1;

        // Class 0x01 = IN
        request[pointer++] = 0;
        request[pointer++] = 1;

        System.out.println("Sending: " + pointer + " bytes");
        for (int i =0; i< pointer; i++) {
            System.out.print("0x" + String.format("%x", request[i]) + " " );
        }

        // *** Send DNS Request Frame ***
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(request, request.length, ipAddress, 53);
        socket.send(dnsReqPacket);

        // Await response from DNS server
        byte[] response = new byte[1024];
        DatagramPacket packet = new DatagramPacket(response, response.length);
        socket.receive(packet);

        System.out.println("\n\nReceived: " + packet.getLength() + " bytes");

        int rescode = (int)(response[3] & 0b00001111);

}