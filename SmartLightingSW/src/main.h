#ifndef INCLUDE_MAIN_H_
#define INCLUDE_MAIN_H_

#include "Arduino.h"
//add your includes for the project SmartLight here
#include <FS.h>                   //this needs to be first, or it all crashes and burns...
#include <ESP8266WiFi.h>          //https://github.com/esp8266/Arduino

//needed for library
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager

#include <ESP8266HTTPClient.h>
#include <WiFiUDP.h>
#include <Ticker.h>

#include <ArduinoJson.h>          //https://github.com/bblanchon/ArduinoJson

//end of add your includes here

#define RGB_LED			false
#define POWER_LED		true

extern int redPin;
extern int greenPin;
extern int bluePin;

extern bool apState;

extern char led_state[11];

extern int sentMulticasts;

extern WiFiUDP udp;

//add your function definitions for the project SmartLight here
#if(RGB_LED)
void setupRGBLeds(String myChar);
#endif

#if(POWER_LED)
void setupRGBLeds(String myChar);
void setupPowerLed(String myChar);
#endif

void connectWifi(char ssid[], char password[]);
void UDPmulticast();



#endif /* INCLUDE_MAIN_H_ */
