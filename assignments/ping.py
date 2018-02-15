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

def ip_wrapper(source_ip, dest_ip, payload, proto, size = 0, chksum = 0):
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

	sock.sendto(packet, (dest_ip , 0 ))

def pinger(dest_ip, count = 1):

	icmp_header_sample = pack('!BBBBi', 8, 0, 0, 0, 0)

	icmp_header = pack('!BBHi', 8, 0, (checksum(icmp_header_sample)), 0)

	for i in range(0, count):
		ip_wrapper("0.0.0.0", dest_ip, icmp_header, socket.IPPROTO_ICMP )
		start = time.time()
		sock = socket.socket(socket.AF_INET,socket.SOCK_RAW,socket.IPPROTO_ICMP)
		sock.setsockopt(socket.SOL_IP, socket.IP_HDRINCL, 1)

		inputs = [sock]
		outputs = []
		readable, _, _ = select.select(inputs, outputs, inputs, 1)

		if not readable:
			print('The ping operation timed out')
		else:
			data, (addr, _) = sock.recvfrom(1508)
			if(addr == dest_ip):
				type(data)
				ver_ihl, _, recsize, _ = unpack("!BBhp", data[:5])
				ihl = ver_ihl & 0b00001111
				if(data[ihl * 4] == '\x00'):
					end = time.time()
					print("Reply from " + dest_ip + " in " + str(int((end - start) * 1000)) + "ms")
				# print("data = " + ":" . join(hex(ord(x))[2:] for x in data))
			# else:
			# 	print("Somebody else replied :O " + str(addr))

if __name__ == "__main__":
	# print re.match("\number:\number:\number:\number", sys.argv[1])
	destip = sys.argv[1]
	pinger(destip, 4)
# pinger('10.64.13.31', 5)

# newiphead = ip_wrapper('127.0.0.1','127.0.0.7', pack('BBBBBB',65,66,67,68,69,70))