# Assignment 0: Getting started.

The goal of this assignment is to familiarise you with some command
line tools that is required.


## Netcat

1. Familiarise yourself with the netcat command `nc` on most unixes.

2. Use `nc` to get the homepage of IIT Palakkad, http://www.iitpkd.ac.in
   a [simple get of the HTTP protocol][simple-get].

3. Use `nc` to post a message on the IRC channel `##iitpkd-networking`
   at the freenode server. Use your rollno as the `nick` and only post
   a hello message. I suggest you write a small shell
   script here and also connect to the freenode irc server separately
   so that you can test it out.


### Important note

For 3 do not spam or connect from too many places. The script should
just connect send the Hello message and get out.


[simple-get]: <https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Example_session>
[irc-protocol]: <https://tools.ietf.org/html/rfc1459>
