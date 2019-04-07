#!/usr/bin/python

import socket
import struct
import json
import os

# http://stackoverflow.com/questions/603852/multicast-in-python
MCAST_GRP = '224.1.1.1'
MCAST_PORT = 7235

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

#sock.bind((MCAST_GRP, MCAST_PORT))
sock.bind(('', MCAST_PORT))
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

ipAddrs = []

cntr = 0

items = open("default.items", "w")
sitemap = open("default.sitemap", "w")

sitemap.write("sitemap default label=\"My first sitemap\" \n")
sitemap.flush()
os.fsync(sitemap.fileno())

while True:
    data = sock.recvfrom(128)
 
#print(type(data));
    print(data[0])

    json_obj = json.loads(data[0])

    #print(json_obj['ip_address'])

    nextIpAddr = json_obj ['ip_address']
    nextPortNum = json_obj ['port_num']

    if(nextIpAddr not in ipAddrs):
        ipAddrs.append(nextIpAddr)

        #TODO: use valid name!!!
        name = "RGB_Blub"

        # fill items file
        #String Switch_A1 "SwitchA1" (bbsb) { udp=">[192.168.2.101:2807:]" }
        items.write("String s"+name+" \"Bado_Bulb\" { udp=\">[");
        items.write(nextIpAddr+":"+str(nextPortNum))
        items.write(":]\" } \n")
        #Color LR_LEDLight_Color "LR_LEDLight_Color"
        items.write("Color c"+name+" \"LR_LEDLight_Color\"")
        items.flush()
        os.fsync(items.fileno())

        # fill sitemap file
        sitemap.write("{ \n\tColorpicker item=c"+name)
        sitemap.write(" label=\"LED RGB Color\" icon=\"light\" \n}")
        sitemap.flush()
        os.fsync(sitemap.fileno())

        # fill rules file, but use function to not make spagetti code
        
        print(ipAddrs)
    else:
        cntr += 1

    if(cntr == 20):
        break;
	
    #print(json_obj ['ip_address'])
    #print(json_obj ['port_num'])
    #print(json_obj ['led_state'])

items.close()
sitemap.close()
