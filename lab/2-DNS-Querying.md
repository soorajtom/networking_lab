# Echo server

## Graded (Deadline to be decided)

One of the most important naming systems is the [DNS] used to
_resolve_ human readable hostnames to IP address.

DNS queries are made by sending queries to a domain name server and is
one protocol that uses the UDP protocol.

1. Write a simple client that can make dns queries to any dns sever.
   and resolve the hostname to its ip address.

2. Write a simple dns server does the following: For every query to
   resolve the hostname `www.james.bond` returns the ip address
   `007.007.007.007`. Your name server should work with any program
   like `nslookup` not just your client in step 1.


Here is a simplified description of the dns process. The client asks
questions to the server by sending a dns query to the server as a UDP
packet. The server responds with a UDP packet that has the answer.

The DNS packet is at most 512 bytes in size, which you can use as a
limit on your packet size. See the url for a brief idea of the dns message format.

http://www.tcpipguide.com/free/t_DNSMessageHeaderandQuestionSectionFormat.htm

Note: When you solve 2, you are building a toy _authoritative dns_
server. This is the simplest kind of DNS server. The more complicated
recursive dns server is left as an exercise for those interested.
