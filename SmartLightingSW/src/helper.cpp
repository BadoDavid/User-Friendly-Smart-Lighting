/*
 * helper.cpp
 *
 *  Created on: 2019. jan. 23.
 *      Author: Badbeloved
 */
#include "main.h"

int redPin = D5;
int greenPin = D6;
int bluePin = D7;

bool apState = false;

WiFiUDP udp;

void setupPowerLed(char* myChar)
{
  //set up LEDs
  pinMode(redPin, OUTPUT);
  //digitalWrite(redPin, LOW);
  analogWrite(redPin,512);

  delay(1000);
  //digitalWrite(redPin, HIGH);
  analogWrite(redPin,1024);
}

void setupRGBLeds(String myChar)
{
  //set up LEDs
  pinMode(redPin, OUTPUT);
  //digitalWrite(redPin, LOW);
//  analogWrite(redPin,512);
//  delay(100);
//  analogWrite(redPin,1024);
//  delay(100);

  pinMode(greenPin, OUTPUT);
  //digitalWrite(greenPin, LOW);
//  analogWrite(greenPin,512);
//  delay(100);
//  analogWrite(greenPin,1024);
//  delay(100);

  pinMode(bluePin, OUTPUT);
  //digitalWrite(bluePin, LOW);
//  analogWrite(bluePin,512);
//  delay(100);
//  analogWrite(bluePin,1024);
//  delay(100);

  /* Parse char */
  //strtol(myChar.substring(4,5).c_str(), NULL, 16);

  int redValue = strtol(myChar.substring(4,6).c_str(), NULL, 16); Serial.print(redValue);
  int greenValue = strtol(myChar.substring(6,8).c_str(), NULL, 16); Serial.print(greenValue);
  int blueValue = strtol(myChar.substring(8,10).c_str(), NULL, 16); Serial.print(blueValue);

  analogWrite(redPin,1024-(4*redValue));
  analogWrite(greenPin,1024-(4*greenValue));
  analogWrite(bluePin,1024-(4*blueValue));

}


void configModeCallback (WiFiManager *myWiFiManager) {
  Serial.println("Entered config mode");
  Serial.println(WiFi.softAPIP());
  //if you used auto generated SSID, print it
  Serial.println(myWiFiManager->getConfigPortalSSID());

  apState = true;
}

/**
 * First: the node is AP, one mobile connect to it
 */
void connectWifi(char ssid[], char password[])
{
    int i = 0;

    if(apState)
    {
    	WiFi.softAPdisconnect();
        apState = false;
    }

    // Connect to Wifi
    Serial.print("Connecting to ");

    if(strcmp("", ssid))
    {
    	Serial.print(ssid);
    }
    else
    {
    	Serial.print(WiFi.SSID());
    }

    Serial.print(":");

    Serial.println(password);

    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
      i++;
      delay(500);
      Serial.print(".");
      if(i >= 30)
      {
        // start AP
        char ssidMine[] = "Bado_Blub";
        char passwordMine[] = "";
        Serial.print("Configuring access point...\n");
        /* You can remove the password parameter if you want the AP to be open. */
        WiFi.softAP(ssidMine, passwordMine);

        IPAddress myIP = WiFi.softAPIP();
        Serial.print("AP created, the IP address: ");
        Serial.print(myIP);
        Serial.print("  the SSID: ");
        Serial.print(ssidMine);

        apState = true;

        break;
      }
    }

    if(false == apState)
    {
		Serial.println("");
        if(WiFi.status() == WL_CONNECTED)
        {
        	Serial.println("WiFi connected");
        }

		// Print wifi info
		Serial.print("SSID: "); Serial.println(WiFi.SSID());
		IPAddress ip = WiFi.localIP();

		Serial.print("  WiFi status: "+WiFi.status());

		Serial.print("IP Address: "); Serial.println(ip);
		long rssi = WiFi.RSSI();
		Serial.print("Signal strength (RSSI): "); Serial.print(rssi); Serial.println(" dBm");
    }
}

int sentMulticasts = 0;

void UDPmulticast()
{
	udp.beginMulticast(WiFi.localIP(), {224, 1, 1, 1}, 7235);

	//Serial.print("Sending packet: IP address to "); Serial.print(IPAddress({224, 1, 1, 1})); Serial.print(':'); Serial.println(7235);
	int result = udp.beginPacketMulticast({224, 1, 1, 1}, 7235, WiFi.localIP(), 20);
	if (result == 1) {
	  //udp.write(WiFi.localIP());

		IPAddress ipno;
		String stateLed;

		if(apState)
		{
			ipno = WiFi.softAPIP();
			stateLed = "NA";
		}
		else
		{
			ipno = WiFi.localIP();
			stateLed = led_state;
		}

		DynamicJsonBuffer jsonBuffer;
		JsonObject& json = jsonBuffer.createObject();
		json["ip_address"] = ipno.toString();
		json["port_num"] = 2807;
		json["led_state"] = stateLed;

		String replyMsg;

		json.printTo(replyMsg);

		Serial.println(replyMsg);

		udp.write(replyMsg.c_str());

		udp.endPacket();

	  //const char buffer [6] = {ipno[0], ipno[1], ipno[2], ipno[3], (byte)(2807), (byte)(2807 >> 8)};
	  //udp.write(buffer, 6);

//	  if(apState)
//	  {
//		  udp.write('x');
//	  }
//	  else
//	  {
//		  udp.write(':');
//		  udp.write(led_state, 11);
//	  }


	  //String* sentData = new String(buffer);

	  //udp.endPacket();

	  Serial.println("Source: "+ ipno.toString()+" Gateway: "+WiFi.gatewayIP().toString());

	  //Serial.println(" Data: "+ipno.toString()+":"+"2807");

	  sentMulticasts++;
	}
	else
	{
	  Serial.println("Cannot send packet");
	}
}
