import evdev
import socket
import struct
import json
import os
import sys


def sendUDPmsg(cntr):    
    UDP_IP_ADDRESS = target
    UDP_PORT_NO = 2807

    clientSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    # a Python object (dict):
    x = {
        "type": "Controll",
        "red": (255-cntr*255),
        "green": (255-cntr*255),
        "blue": (255-cntr*255),
    }

    # convert into JSON:
    Message = json.dumps(x)

    clientSock.sendto(Message, (UDP_IP_ADDRESS, UDP_PORT_NO))

    print("\nMessage sent: ")
    # the result is a JSON string:
    print(Message)

if len(sys.argv) < 2:
    print("Not enough arguments!")
    sys.exit(2)

target = sys.argv[1]

print(target+"\n")

cntr=0

devices = [evdev.InputDevice(path) for path in evdev.list_devices()]
for device in devices:
    print(device.path, device.name, device.phys)

    if("c4:d9:87:b2:95:84" == device.phys):
        #Start generating openHAB files
        print("Found!\n")
        for event in device.read_loop():
            if (event.code == evdev.ecodes.KEY_ENTER) and (event.value == 1):
                cntr+=1
                print("Button pressed!")
                sendUDPmsg(cntr%2)
        #add_bluetooth_button(device.phys)
        break
print("NOT Found!\n")
