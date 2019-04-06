################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_cache.c \
E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_check.c \
E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_gc.c \
E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_hydrogen.c \
E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_nucleus.c 

C_DEPS += \
.\core\core\spiffs\spiffs_cache.c.d \
.\core\core\spiffs\spiffs_check.c.d \
.\core\core\spiffs\spiffs_gc.c.d \
.\core\core\spiffs\spiffs_hydrogen.c.d \
.\core\core\spiffs\spiffs_nucleus.c.d 

AR_OBJ += \
.\core\core\spiffs\spiffs_cache.c.o \
.\core\core\spiffs\spiffs_check.c.o \
.\core\core\spiffs\spiffs_gc.c.o \
.\core\core\spiffs\spiffs_hydrogen.c.o \
.\core\core\spiffs\spiffs_nucleus.c.o 


# Each subdirectory must supply rules for building sources it contributes
core\core\spiffs\spiffs_cache.c.o: E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_cache.c
	@echo 'Building file: $<'
	@echo 'Starting C compile'
	"E:\eclipse\arduinoPlugin\packages\esp8266\tools\xtensa-lx106-elf-gcc\1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-gcc" -D__ets__ -DICACHE_FLASH -U__STRICT_ANSI__ "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/lwip2/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/libc/xtensa-lx106-elf/include" "-IE:/David/eclipseWS/SmartLightingSW/esp8266/core" -c -Wall -Wextra  -Os -g -Wpointer-arith -Wno-implicit-function-declaration -Wl,-EL -fno-inline-functions -nostdlib -mlongcalls -mtext-section-literals -falign-functions=4 -MMD -std=gnu99 -ffunction-sections -fdata-sections -DF_CPU=80000000L -DLWIP_OPEN_SRC -DTCP_MSS=536   -DARDUINO=10802 -DARDUINO_ESP8266_WEMOS_D1MINI -DARDUINO_ARCH_ESP8266 -DARDUINO_BOARD="\"ESP8266_WEMOS_D1MINI\""   -DESP8266   -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\variants\d1_mini" -I"E:\eclipse\arduinoPlugin\libraries\ArduinoJson\5.13.4\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\DNSServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\EEPROM" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\esp8266\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266HTTPClient\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WebServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WiFi\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Ticker" -I"E:\eclipse\arduinoPlugin\libraries\WiFiManager\0.14.0" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Wire" -I"E:\David\eclipseWS\SmartLightingSW\include" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -D__IN_ECLIPSE__=1 "$<"  -o  "$@"
	@echo 'Finished building: $<'
	@echo ' '

core\core\spiffs\spiffs_check.c.o: E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_check.c
	@echo 'Building file: $<'
	@echo 'Starting C compile'
	"E:\eclipse\arduinoPlugin\packages\esp8266\tools\xtensa-lx106-elf-gcc\1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-gcc" -D__ets__ -DICACHE_FLASH -U__STRICT_ANSI__ "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/lwip2/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/libc/xtensa-lx106-elf/include" "-IE:/David/eclipseWS/SmartLightingSW/esp8266/core" -c -Wall -Wextra  -Os -g -Wpointer-arith -Wno-implicit-function-declaration -Wl,-EL -fno-inline-functions -nostdlib -mlongcalls -mtext-section-literals -falign-functions=4 -MMD -std=gnu99 -ffunction-sections -fdata-sections -DF_CPU=80000000L -DLWIP_OPEN_SRC -DTCP_MSS=536   -DARDUINO=10802 -DARDUINO_ESP8266_WEMOS_D1MINI -DARDUINO_ARCH_ESP8266 -DARDUINO_BOARD="\"ESP8266_WEMOS_D1MINI\""   -DESP8266   -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\variants\d1_mini" -I"E:\eclipse\arduinoPlugin\libraries\ArduinoJson\5.13.4\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\DNSServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\EEPROM" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\esp8266\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266HTTPClient\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WebServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WiFi\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Ticker" -I"E:\eclipse\arduinoPlugin\libraries\WiFiManager\0.14.0" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Wire" -I"E:\David\eclipseWS\SmartLightingSW\include" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -D__IN_ECLIPSE__=1 "$<"  -o  "$@"
	@echo 'Finished building: $<'
	@echo ' '

core\core\spiffs\spiffs_gc.c.o: E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_gc.c
	@echo 'Building file: $<'
	@echo 'Starting C compile'
	"E:\eclipse\arduinoPlugin\packages\esp8266\tools\xtensa-lx106-elf-gcc\1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-gcc" -D__ets__ -DICACHE_FLASH -U__STRICT_ANSI__ "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/lwip2/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/libc/xtensa-lx106-elf/include" "-IE:/David/eclipseWS/SmartLightingSW/esp8266/core" -c -Wall -Wextra  -Os -g -Wpointer-arith -Wno-implicit-function-declaration -Wl,-EL -fno-inline-functions -nostdlib -mlongcalls -mtext-section-literals -falign-functions=4 -MMD -std=gnu99 -ffunction-sections -fdata-sections -DF_CPU=80000000L -DLWIP_OPEN_SRC -DTCP_MSS=536   -DARDUINO=10802 -DARDUINO_ESP8266_WEMOS_D1MINI -DARDUINO_ARCH_ESP8266 -DARDUINO_BOARD="\"ESP8266_WEMOS_D1MINI\""   -DESP8266   -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\variants\d1_mini" -I"E:\eclipse\arduinoPlugin\libraries\ArduinoJson\5.13.4\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\DNSServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\EEPROM" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\esp8266\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266HTTPClient\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WebServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WiFi\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Ticker" -I"E:\eclipse\arduinoPlugin\libraries\WiFiManager\0.14.0" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Wire" -I"E:\David\eclipseWS\SmartLightingSW\include" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -D__IN_ECLIPSE__=1 "$<"  -o  "$@"
	@echo 'Finished building: $<'
	@echo ' '

core\core\spiffs\spiffs_hydrogen.c.o: E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_hydrogen.c
	@echo 'Building file: $<'
	@echo 'Starting C compile'
	"E:\eclipse\arduinoPlugin\packages\esp8266\tools\xtensa-lx106-elf-gcc\1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-gcc" -D__ets__ -DICACHE_FLASH -U__STRICT_ANSI__ "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/lwip2/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/libc/xtensa-lx106-elf/include" "-IE:/David/eclipseWS/SmartLightingSW/esp8266/core" -c -Wall -Wextra  -Os -g -Wpointer-arith -Wno-implicit-function-declaration -Wl,-EL -fno-inline-functions -nostdlib -mlongcalls -mtext-section-literals -falign-functions=4 -MMD -std=gnu99 -ffunction-sections -fdata-sections -DF_CPU=80000000L -DLWIP_OPEN_SRC -DTCP_MSS=536   -DARDUINO=10802 -DARDUINO_ESP8266_WEMOS_D1MINI -DARDUINO_ARCH_ESP8266 -DARDUINO_BOARD="\"ESP8266_WEMOS_D1MINI\""   -DESP8266   -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\variants\d1_mini" -I"E:\eclipse\arduinoPlugin\libraries\ArduinoJson\5.13.4\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\DNSServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\EEPROM" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\esp8266\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266HTTPClient\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WebServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WiFi\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Ticker" -I"E:\eclipse\arduinoPlugin\libraries\WiFiManager\0.14.0" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Wire" -I"E:\David\eclipseWS\SmartLightingSW\include" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -D__IN_ECLIPSE__=1 "$<"  -o  "$@"
	@echo 'Finished building: $<'
	@echo ' '

core\core\spiffs\spiffs_nucleus.c.o: E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266\spiffs\spiffs_nucleus.c
	@echo 'Building file: $<'
	@echo 'Starting C compile'
	"E:\eclipse\arduinoPlugin\packages\esp8266\tools\xtensa-lx106-elf-gcc\1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-gcc" -D__ets__ -DICACHE_FLASH -U__STRICT_ANSI__ "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/lwip2/include" "-IE:\eclipse\/arduinoPlugin/packages/esp8266/hardware/esp8266/2.4.2/tools/sdk/libc/xtensa-lx106-elf/include" "-IE:/David/eclipseWS/SmartLightingSW/esp8266/core" -c -Wall -Wextra  -Os -g -Wpointer-arith -Wno-implicit-function-declaration -Wl,-EL -fno-inline-functions -nostdlib -mlongcalls -mtext-section-literals -falign-functions=4 -MMD -std=gnu99 -ffunction-sections -fdata-sections -DF_CPU=80000000L -DLWIP_OPEN_SRC -DTCP_MSS=536   -DARDUINO=10802 -DARDUINO_ESP8266_WEMOS_D1MINI -DARDUINO_ARCH_ESP8266 -DARDUINO_BOARD="\"ESP8266_WEMOS_D1MINI\""   -DESP8266   -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\cores\esp8266" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\variants\d1_mini" -I"E:\eclipse\arduinoPlugin\libraries\ArduinoJson\5.13.4\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\DNSServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\EEPROM" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\esp8266\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266HTTPClient\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WebServer\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\ESP8266WiFi\src" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Ticker" -I"E:\eclipse\arduinoPlugin\libraries\WiFiManager\0.14.0" -I"E:\eclipse\arduinoPlugin\packages\esp8266\hardware\esp8266\2.4.2\libraries\Wire" -I"E:\David\eclipseWS\SmartLightingSW\include" -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -D__IN_ECLIPSE__=1 "$<"  -o  "$@"
	@echo 'Finished building: $<'
	@echo ' '


