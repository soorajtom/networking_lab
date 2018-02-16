import psutil
import socket, sys
import select
import time
# import regex
from struct import *

def checksum(source_string):
    """
    A port of the functionality of in_cksum() from ping.c
    Ideally this would act on the string as a series of 16-bit ints (host
    packed), but this works.
    Network data is big-endian, hosts are typically little-endian
    """
    countTo = (int(len(source_string) / 2)) * 2
    my_sum = 0
    count = 0

    # Handle bytes in pairs (decoding as short ints)
    loByte = 0
    hiByte = 0
    while count < countTo:
        if (sys.byteorder == "little"):
            loByte = source_string[count]
            hiByte = source_string[count + 1]
        else:
            loByte = source_string[count + 1]
            hiByte = source_string[count]
        try:     # For Python3
            my_sum = my_sum + (hiByte * 256 + loByte)
        except:  # For Python2
            my_sum = my_sum + (ord(hiByte) * 256 + ord(loByte))
        count += 2

    # Handle last byte if applicable (odd-number of bytes)
    # Endianness should be irrelevant in this case
    if countTo < len(source_string):  # Check for odd length
        loByte = source_string[len(source_string) - 1]
        try:      # For Python3
            my_sum += loByte
        except:   # For Python2
            my_sum += ord(loByte)

    my_sum &= 0xffffffff  # Truncate sum to 32 bits (a variance from ping.c,
                          # which uses signed ints, but overflow is unlikely
                          # in ping)

    my_sum = (my_sum >> 16) + (my_sum & 0xffff)  # Add high 16 and low 16 bits
    my_sum += (my_sum >> 16)                     # Add carry from above, if any
    answer = ~my_sum & 0xffff                    # Invert & truncate to 16 bits
    answer = socket.htons(answer)
    return answer

def get_ip_addresses(family):
    for interface, snics in psutil.net_if_addrs().items():
        for snic in snics:
            if snic.family == family:
                # yield (interface, snic.address)
                # yield socket.inet_aton(snic.address)
                yield (snic.address)

def ip_wrapper(source_ip, dest_ip, payload, proto, dport = 0, size = 0, chksum = 0):
	try:
	    sock = socket.socket(socket.AF_INET, socket.SOCK_RAW, socket.IPPROTO_RAW)
	except socket.error , msg:
	    print 'Socket could not be created. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
	    sys.exit()
	ip_ver = 4
	ip_ihl = 5
	ip_ihl_ver = (ip_ver << 4) + ip_ihl

	source_ip_p = socket.inet_aton ( source_ip )
	dest_ip_p = socket.inet_aton ( dest_ip )

	ip_header = pack('!BBHHHBBH4s4s' , ip_ihl_ver, 0, size,   	#DSCP&ECN -- length
		 12345, 0, 												#Identifier -- flags&fragment offset
		 255, proto, chksum,									#ttl -- protocol -- checksum
		  source_ip_p, dest_ip_p)								#source ip -- destination ip

	packet = ip_header + payload

	sock.sendto(packet, (dest_ip , dport ))

def pong(pongport = 9999):
	
	try:
	    sock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
	    sock.bind(("0.0.0.0", pongport))
	except socket.error , msg:
	    print 'Socket could not be created. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
	    sys.exit()
	nativeip = list(get_ip_addresses(socket.AF_INET))
	print "listening to port " + str(pongport) + " on "
	print(nativeip)
	while 1:
		data, (addr, sport) = sock.recvfrom(1024)
		# srcip, destip = unpack("!4s4s", data[12:20])
		# ip1, ip2, ip3, ip4 = unpack("!BBBB", destip)
		# # ipformat = socket.inet_aton ( srcip )
		# properip = (str(ip1) + "." + str(ip2) + "." + str(ip3) + "." + str(ip4))
		# # print properip
		# if(addr in nativeip):
		content = data
		print addr + ":" + str(sport) + " says " + content

		dport = sport
		sport = pongport
		udp_header = pack('!HHHH6s', sport, dport, 14, 0, "pong\n\n")

		ip_wrapper("0.0.0.0", addr, udp_header, socket.IPPROTO_UDP, dport)

if __name__ == "__main__":
	pong(int(sys.argv[1]))