#!/usr/bin/python

import socket
import struct
import json
import os

#item need to be a string
def make_rule_RGB_item(item):
    rule = "rule  \"c"+item+" changed rule\" \n"
    rule +="when \n\t"
    rule +="Item c"+item+" received command \n"
    rule +="then \n\t"
    rule +="if (receivedCommand instanceof HSBType) \n{\n\t\t"
    rule +="val red = (receivedCommand as HSBType).red * 2.55 \n\t\t"
    rule +="val green = (receivedCommand as HSBType).green * 2.55 \n\t\t"
    rule +="val blue = (receivedCommand as HSBType).blue * 2.55 \n\t\t"
    rule +="\n\t\t"
    rule +='s'+item+'.sendCommand'
    rule +='(" {\"type\":\"Controll\",\"red\":"+red.intValue.toString+",\"green\":"+green.intValue.toString+",\"blue\":"+blue.intValue.toString+"} ")'
    rule +="\n\t}\n"
    rule +='end\n\n'
    return rule

# http://stackoverflow.com/questions/603852/multicast-in-python
MCAST_GRP = "224.1.1.1"
MCAST_PORT = 7235

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
#sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)

#sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 20)
#sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_LOOP, 1)

#sock.bind((MCAST_GRP, MCAST_PORT))
sock.bind(('', MCAST_PORT))
mreq = struct.pack("4sL", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

intf = socket.gethostbyname(socket.gethostname())
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

#sock.settimeout(10)

ipAddrs = []

cntr = 0

# TODO: move to the appropriate places
items = open(".\conf\items\default.items", "w")
sitemap = open(".\conf\sitemaps\default.sitemap", "w")
rules = open(".\conf\\rules\default.rules", "w")

sitemap.write("sitemap default label=\"My first sitemap\" \n")
sitemap.flush()
os.fsync(sitemap.fileno())

print("Hello! Multicast msg receiving...")

while True:
    data = sock.recvfrom(128)

    print("Received something...")
 
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

        # fill rules file
        rules.write(make_rule_RGB_item(name))
        rules.flush()
        os.fsync(rules.fileno())
        
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
rules.close()
