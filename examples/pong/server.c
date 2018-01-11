#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

/* Port to run this server on */
const int port=8000;
const int clientQueueSize=100;

void checkAndDie(char *info, int cond);
void handle(int conn);
    

int main()
{

    /* Creating the listening socket for queuing connections */
    int listenSock = socket(AF_INET,      /* for IPv4 */
			    SOCK_STREAM,  /* connection oriented protocol */
			    0);           /* almost always 0 */
    checkAndDie("socket", listenSock);
    
    /* Setting up the address to bind the socket on */
    struct sockaddr_in address;
    memset(&address, 0, sizeof(address)); /* clear up address */
    address.sin_family = AF_INET;                /* ipv4 address family      */
    address.sin_addr.s_addr = htonl(INADDR_ANY); /* ip address to listen to  */
    address.sin_port = htons(port);              /* which port to listen on  */

    checkAndDie( "bind",
		 bind(listenSock,(struct sockaddr *) &address, sizeof(address)));

    /* Listening and handling connections */
    listen(listenSock, clientQueueSize);
    int client_conn;
    struct sockaddr_in client_address;
    while(1)
    {
	client_conn = accept(listenSock, NULL, 0);
	handle(client_conn);
    }

    return 0;
}

void handle(int conn)
{
    char message[] = "pong";
    int rbytes;
    
    printf("client connected");
    fflush(stdout);
    
    /* The main loop of the server. Just sends a pong */
    write(conn, message,strlen(message));

    close(conn);
}

    

void checkAndDie(char *info, int cond)
{
    if (cond >= 0) return;
    perror(info); exit(1);
}
