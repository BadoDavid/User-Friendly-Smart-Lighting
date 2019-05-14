#!/usr/bin/python

import socket
import struct
import json
import os
import subprocess

#item need to be a string
def make_rule_RGB_item(item):
    rule = "rule  \"c"+item+" changed rule\" \n"
    rule +="when \n\t"
    rule +="Item c"+item+" received command \n"
    rule +="then \n\t"
    rule +="if (receivedCommand instanceof HSBType) \n\t{\n\t\t"
    rule +="val red = (receivedCommand as HSBType).red * 2.55 \n\t\t"
    rule +="val green = (receivedCommand as HSBType).green * 2.55 \n\t\t"
    rule +="val blue = (receivedCommand as HSBType).blue * 2.55 \n\t\t"
    rule +="\n\t\t"
    rule +='s'+item+'.sendCommand'
    rule +='(" {\\"type\\":\\"Config\\",\\"device_name\\":\\"'+name+'\\",\\"red\\":"+red.intValue.toString+",\\"green\\":"+green.intValue.toString+",\\"blue\\":"+blue.intValue.toString+"} ")'
    rule +="\n\t}\n"
    rule +='end\n\n'
    return rule

def make_rule_PWR_item(item):
    rule = "rule  \"c"+item+" changed rule\" \n"
    rule +="when \n\t"
    rule +="Item c"+item+" received command \n"
    rule +="then \n\t"
    rule +="val red = (receivedCommand as Number) * 2.55 \n\t"
    rule +="val green = red \n\t"
    rule +="val blue = red \n\t"
    rule +="\n\t"
    rule +='s'+item+'.sendCommand'
    rule +='(" {\\"type\\":\\"Config\\",\\"device_name\\":\\"'+name+'\\",\\"red\\":"+red.intValue.toString+",\\"green\\":"+green.intValue.toString+",\\"blue\\":"+blue.intValue.toString+"} ")'
    rule +="\n"
    rule +='end\n\n'
    return rule

# http://stackoverflow.com/questions/603852/multicast-in-python
MCAST_GRP = "224.1.1.1"
MCAST_PORT = 17235

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)

sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
#sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)

#sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_TTL, 20)
#sock.setsockopt(socket.SOL_IP, socket.IP_MULTICAST_LOOP, 1)

#sock.bind((MCAST_GRP, MCAST_PORT))
sock.bind(('', MCAST_PORT))
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

intf = socket.gethostbyname(socket.gethostname())
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

#sock.settimeout(10)

ipAddrs = []
smItems = []

items = open("../conf/items/default.items", "w")
#sitemap = open(".\conf\sitemaps\default.sitemap", "w")
rules = open("../conf//rules/default.rules", "w")

#sitemap.write("sitemap default label=\"My first sitemap\" \n")
#sitemap.flush()
#os.fsync(sitemap.fileno())

print("Hello! Multicast msg receiving...")

cntr = 0

while True:
    data = sock.recvfrom(2000)
    #print("Received something...")
    print(data[0])

    json_obj = json.loads(data[0])

    #print(json_obj['ip_address'])

    nextDevName = json_obj ['name']
    nextIpAddr = json_obj ['ip_address']
    nextPortNum = json_obj ['port_num']
    nextLedState = json_obj ['led_state']

    if(nextIpAddr not in ipAddrs):
        ipAddrs.append(nextIpAddr)

        cntr += 1

        name = nextDevName

        led_state = nextLedState

        # fill items file
        #String Switch_A1 "SwitchA1" (bbsb) { udp=">[192.168.2.101:2807:]" }
        items.write("String s"+name+" \"sItem"+name+"\" { udp=\">[");
        items.write(nextIpAddr+":"+str(nextPortNum))
        items.write(":]\" } \n")
        #Color LR_LEDLight_Color "LR_LEDLight_Color"
        if("RGB" in led_state):
            items.write("Color c"+name+" \"cItem"+name+"\"\n")
        elif ("PWR" in led_state):
            items.write("Number c"+name+" \"cItem"+name+"\"\n")
        items.flush()
        os.fsync(items.fileno())

        # fill sitemap file
        if("RGB" in led_state):
            smString = "\n\t\tColorpicker item=c"+name+" label=\""+name+"\" icon=\"light\""
        elif ("PWR" in led_state):
            smString = "\n\t\tSlider item=c"+name+" label=\""+name+"\" icon=\"light\""
        #smString = "\n\t\tColorpicker item=c"+name+" label=\""+name+"\" icon=\"light\""
        smItems.append(smString)
        
        sitemap = open("../conf/sitemaps/default.sitemap", "w")
        sitemap.write("sitemap default label=\"My first sitemap\" \n")
        sitemap.write("{ \n\tSwitch item=MulticastHandling label=\"Scan available lights!\" icon=\"none\" mappings=[0=\"SCAN\"]")
        sitemap.write("\n\tFrame label=\"SmartLights\" { ")
        for smItem in smItems:
            sitemap.write(smItem)
        sitemap.write("\n\t}\n}")
        sitemap.close()
        #sitemap.flush()
        #os.fsync(sitemap.fileno())

        # fill rules file
        if("RGB" in led_state):
            rules.write(make_rule_RGB_item(name))
        elif ("PWR" in led_state):
            rules.write(make_rule_PWR_item(name))
        rules.flush()
        os.fsync(rules.fileno())
        
        print(ipAddrs)
    else:
        cntr += 1

    if(cntr == 10):
        break;
	
    #print(json_obj ['ip_address'])
    #print(json_obj ['port_num'])
    #print(json_obj ['led_state'])

items.close()
#sitemap.close()
rules.close()
