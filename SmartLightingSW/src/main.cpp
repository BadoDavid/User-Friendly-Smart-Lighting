#include "main.h"

  WiFiUDP UDPTestServer;

  unsigned int UDPPort = 2807;

  unsigned long cntr = 0;

  unsigned long previousMillis = 0;
  const long interval = 5000;

  const int packetSize = 100;
  byte packetBuffer[packetSize];

  Ticker tickerMulticast;

  int ssidAddr = 0;
  int passwordAddr = 64;

  char mySSID[] = ""; //"1113-COYG";
  char myPassword[] = ""; //"arseneWenger4";

  char led_state[11] = "RGB#ABCDEF";

  unsigned long currentTime = 0U;

  unsigned long prevTime = 0U;

  /* 10 sec */
  unsigned long timeDiff = 10000U;

  bool checkTime = true;

  //flag for saving data
  bool shouldSaveConfig = false;

  //callback notifying us of the need to save config
  void saveConfigCallback () {
    Serial.println("Should save config");
    shouldSaveConfig = true;
  }

  WiFiEventHandler disconnectedEventHandler;

void setup() {

	checkTime = true;

	WiFi.softAPdisconnect();
	/* Clear WiFi config */
	//WiFi.disconnect(false);

    //Init Serial
	Serial.begin(115200);
	Serial.println("");

	// MAC address info
	byte mac[6];
	WiFi.macAddress(mac);
	Serial.print("WiFi adapter MAC address: ");
	Serial.print(mac[5], HEX); Serial.print(":"); Serial.print(mac[4], HEX); Serial.print(":");
	Serial.print(mac[3], HEX); Serial.print(":"); Serial.print(mac[2], HEX); Serial.print(":");
	Serial.print(mac[1], HEX); Serial.print(":"); Serial.print(mac[0], HEX); Serial.println();

	//clean FS, for testing
	//SPIFFS.format();

	//read configuration from FS json
	Serial.println("mounting FS...");

	if (SPIFFS.begin()) {
	Serial.println("mounted file system");
	if (SPIFFS.exists("/config.json")) {
	  //file exists, reading and loading
	  Serial.println("reading config file");
	  File configFile = SPIFFS.open("/config.json", "r");
	  if (configFile) {
		Serial.println("opened config file");
		size_t size = configFile.size();
		// Allocate a buffer to store contents of the file.
		std::unique_ptr<char[]> buf(new char[size]);

		configFile.readBytes(buf.get(), size);
		DynamicJsonBuffer jsonBuffer;
		JsonObject& json = jsonBuffer.parseObject(buf.get());
		json.printTo(Serial);
		if (json.success()) {
		  Serial.println("\nparsed json");

		  strcpy(led_state, json["led_state"]);

		  currentTime = json["current_time"];

		  prevTime = json["prev_time"];

		  Serial.println(led_state);
		  Serial.println(currentTime);
		  Serial.println(prevTime);

		} else {
		  Serial.println("failed to load json config");
		}
		configFile.close();
	  }
	}
	} else {
	Serial.println("failed to mount FS");
	}
	//end read

	/*****************************************************************************************/
	//checkTimes()
	if((prevTime == 0U) && (currentTime == 0U))
	{
		Serial.println("RESET SETTINGS!!!");

		String defLedState = "RGB#ABCDEF";
		/* Reset settings */
		WiFi.disconnect(false);
		strcpy(led_state, defLedState.c_str());
	}

	Serial.println("Resaving config");
	DynamicJsonBuffer jsonBuffer;
	JsonObject& json = jsonBuffer.createObject();
	json["led_state"] = led_state;
	json["current_time"] = 0U;
	json["prev_time"] = currentTime;

	File configFile = SPIFFS.open("/config.json", "w");
	if (!configFile) {
	  Serial.println("failed to open config file for writing");
	}

	json.printTo(Serial);
	json.printTo(configFile);
	configFile.close();

	/************************************************************************************/

	String sLedState = led_state;

#if(RGB_LED)
	setupRGBLeds(sLedState);
#endif

#if(POWER_LED)
	setupPowerLed(sLedState);
#endif

	/* Connect to the last configured WiFi AP */
	connectWifi(mySSID, myPassword);

	disconnectedEventHandler = WiFi.onStationModeDisconnected([](const WiFiEventStationModeDisconnected& event)
	{
		if (WiFi.status() == WL_DISCONNECTED)
		{
			Serial.println("WiFi disconnected");

			/* Connect to the last configured WiFi AP */
			if(tickerMulticast.active())
			{
				tickerMulticast.detach();
			}

			connectWifi(mySSID, myPassword);

			tickerMulticast.attach(3, UDPmulticast);
		}
	});

    //setupUDPmulticast();

    UDPTestServer.begin(UDPPort);

    tickerMulticast.attach(3, UDPmulticast);
}

void loop() {

	if((checkTime == true) && (millis() > timeDiff))
	{
		Serial.println("Resaving config");
		DynamicJsonBuffer jsonBuffer;
		JsonObject& json = jsonBuffer.createObject();
		json["led_state"] = led_state;
		json["current_time"] = millis();
		json["prev_time"] = currentTime;

		File configFile = SPIFFS.open("/config.json", "w");
		if (!configFile) {
		  Serial.println("failed to open config file for writing");
		}

		json.printTo(Serial);
		json.printTo(configFile);
		configFile.close();

		checkTime = false;
	}

	/*
	  if(sentMulticasts == 20)
	  {
		  tickerMulticast.detach();
	  }
	*/

	delay(100);

  	/* handleUDPServer */
	//UDPTestServer.setTimeout(3000U);

  	int cb = UDPTestServer.parsePacket();

  	if (cb)
  	{
  		UDPTestServer.read(packetBuffer, packetSize);

  		String led = "";
		String state = "";

		String myData = "";

        for(int i = 0; i < packetSize; i++) {
			myData += (char)packetBuffer[i];
		}

        Serial.println(myData);

		DynamicJsonBuffer jsonBuffer(50);

		JsonObject& root = jsonBuffer.parseObject(packetBuffer);

		const char* type = root["type"];

		if(!strcmp(type, "Network"))
		{
			Serial.print("\n Network Message received!");

			String myString = root["ssid"];

			String sPassword = root["password"];

			char* ssid = (char*) malloc(myString.length());
			char* password = (char*) malloc(sPassword.length());

			strcpy(ssid, myString.c_str());
			Serial.println(ssid);
			strcpy(password, sPassword.c_str());

			Serial.print("N:");
			Serial.print(ssid);
			Serial.print(":");
			Serial.print(password);
			Serial.println(":");

			tickerMulticast.detach();

			connectWifi(ssid, password);

			tickerMulticast.attach(3, UDPmulticast);

			sentMulticasts = 21;
		}
		else
		{
			if(!strcmp(type, "Controll"))
			{
				Serial.println("Controll message!");

				String ledType = "RGB#";
				char buf [2];

				int redValue = root["red"];
				analogWrite(redPin, 1024-4*redValue);
				sprintf(buf, "%02x", redValue);
				ledType += buf;

				int greenValue = root["green"];
				analogWrite(greenPin, 1024-4*greenValue);
				sprintf(buf, "%02x", greenValue);
				ledType +=  buf;

				int blueValue = root["blue"];
				analogWrite(bluePin, 1024-4*blueValue);
				sprintf(buf, "%02x", blueValue);
				ledType +=  buf;

				strcpy(led_state, ledType.c_str());

				Serial.print(led_state);

				Serial.println("");

				Serial.println("saving config");
				DynamicJsonBuffer jsonBuffer;
				JsonObject& json = jsonBuffer.createObject();
				json["led_state"] = led_state;
				json["current_time"] = millis();
				json["prev_time"] = currentTime;

				File configFile = SPIFFS.open("/config.json", "w");
				if (!configFile) {
				  Serial.println("failed to open config file for writing");
				}

				json.printTo(Serial);
				json.printTo(configFile);
				configFile.close();
				//end save
			}

//#if(RGB_LED)


//#if(POWER_LED)
//			if((messageType != 'N') && (num >= 0) && (num <= 256))
//			{
//				if(messageType == 'R')
//				{
//					analogWrite(redPin, 1024-4*num);
//				}
//			}
//#endif
		}
  	}

}
