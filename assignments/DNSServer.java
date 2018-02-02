import java.io.*;
import java.net.*;

class DNSServer
{
   public static void main(String args[]) throws Exception
      {
         int serverport = 53;

         if(args.length > 0)
         {
            serverport = Integer.parseInt(args[0]);
         }

         DatagramSocket serverSocket = new DatagramSocket(serverport);
         System.out.println("Starting server on port:" + serverport);
            byte[] query = new byte[1024];
            byte[] response = new byte[1024];
            while(true)
               {
                  DatagramPacket receivePacket = new DatagramPacket(query, query.length);
                  serverSocket.receive(receivePacket);

                  System.out.println("Incoming query");
                  // for (int i =0; i< query.length; i++) {
                  //     System.out.print("0x" + String.format("%x", query[i]) + " " );
                  // }
                  response = query;
                  response[2] = (byte)0b10000100; //Response, authoritative

                  int pointer = 12;
                  String domainname = "";

                  while(true)
                  {
                     int size = query[pointer++];
                     for(int i = 0; i < size; i++)
                     {
                        domainname = domainname + (char)query[pointer++];
                     }
                     if(query[pointer] != 0x00)
                        domainname = domainname + ".";
                     else
                        break;
                  }




                  if(domainname.equals("www.james.bond"))
                  {
                     response[3] = 0b00000000;  //Response code
                     response[6] = 0;
                     response[7] = 1;           //Answer count

                     response[32] = (byte)0xc0;
                     response[33] = 0x0c;
                     response[34] = 0;  //Type
                     response[35] = 1;  //Type
                     response[36] = 0;
                     response[37] = 1;  //Class

                     response[38] = 0;
                     response[39] = 0;
                     response[40] = 0;
                     response[41] = 0;  //TTL

                     response[42] = 0;  //Length
                     response[43] = 4;

                     response[44] = 7;
                     response[45] = 7;
                     response[46] = 7;
                     response[47] = 7;
                  }
                  else
                  {
                     response[3] = 0b00000011;  //Response code
                  }

                  InetAddress IPAddress = receivePacket.getAddress();
                  int port = receivePacket.getPort();

                  DatagramPacket sendPacket = new DatagramPacket(response, response.length, IPAddress, port);
                  serverSocket.send(sendPacket);
               }
      }
}