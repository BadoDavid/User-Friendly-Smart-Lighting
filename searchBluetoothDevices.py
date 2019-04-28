import evdev
import socket
import struct
import json
import os

def add_bluetooth_button(mac):
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
    mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)

    intf = socket.gethostbyname(socket.gethostname())
    sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

    ipAddrs = []

    data = sock.recvfrom(1024)
    #print("Received something...")
    print(data[0])

    json_obj = json.loads(data[0])

    #print(json_obj['ip_address'])

    nextIpAddr = json_obj ['ip_address']
    nextPortNum = json_obj ['port_num']

    items = open("./conf/items/default.items", "a+")
    items.write("String sBlBtn \"sItemBluetoothButton\"")
    items.close()

    sitemap = open("./conf/sitemaps/default.sitemap", "r")
    contents = sitemap.readlines()
    sitemap.close()

    sValue = "\tFrame label=\"BluetoothButtons\" { \n\t\tSelection item=sBlBtn label=\"BL Controller\" "
    sValue += "mappings=[0=\"\", 1=\""+nextIpAddr+"\"]\n\t}\n"

    contents.insert(2, sValue)

    sitemap = open("./conf/sitemaps/default.sitemap", "w")
    contents = "".join(contents)
    sitemap.write(contents)
    sitemap.close()

    rules = open("./conf//rules/default.rules", "a+")

    rules.write("\nrule \"Button state changed\"")
    rules.write("\nwhen\n")
    rules.write("\t Item sBlBtn received command \n")
    rules.write("then \n\t")
    rules.write("if (receivedCommand != \"\") \n\t{\n\t\t")
    rules.write("val results = executeCommandLine(\"/usr/bin/python@@/home/xannosz/Dokumentumok/openhab-2.4.0/handleHIDmsg.py\", 5000)")
    rules.write("\n\t\tlogInfo(\"execTest\", results)\n\t}\nend\n\n")
    rules.close()
        

devices = [evdev.InputDevice(path) for path in evdev.list_devices()]
for device in devices:
    print(device.path, device.name, device.phys)

    if("c4:d9:87:b2:95:84" == device.phys):
        #Start generating openHAB files
        print("Found!\n")
        add_bluetooth_button(device.phys)
        break

