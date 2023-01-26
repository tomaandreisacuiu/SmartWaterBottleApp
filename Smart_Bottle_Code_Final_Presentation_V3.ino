#include <U8g2lib.h>
#include <WiFi.h>
#include "FS.h"
#include "SD.h"
#include "SPI.h"
#include <Wire.h>
#include <ESP32Time.h>
#include <MPU6050_light.h>
#include <ArduinoBLE.h>

#define SD_CS 5

MPU6050 mpu(Wire);
U8G2_SH1106_128X64_NONAME_F_HW_I2C u8g2(U8G2_R0, /* reset=*/ U8X8_PIN_NONE); //screen definition
//GPIO declaration
const int functionBut = 33, bluetoothBut = 32, ballswitch = 4, led = 27, buzzer = 13, waterSensor = 15, batteryVoltage = 36, hardwarePower = 14;
//Variable declaration
boolean bluetoothState = false, bluetoothIsOn = false, bluetoothButState = true, sleeping = false, ledState = false, pillAlarm = false, buzzerState = false;
int timeBeforeSleep = 10, measureID = 0, buzzerFrequency = 1517, timePressedF = 0, timePressedB = 0, ledFrequency = 5;
String logDir = "/userLogs-2022", pillsToTake = "";
byte batteryLevel = 0x035;
int measurements[10] = {0,0,0,0,0,0,0,0,0,0};
unsigned long timer100ms = 0, lastMeasure = 0, buzzerTimer = 0;

//Declaration of variables that are not deleted during sleep
RTC_DATA_ATTR int waterLevel = 0, day = 0, totalConsumption = 0, targetConsumption = 2000;
RTC_DATA_ATTR boolean firstBoot = true;
RTC_DATA_ATTR unsigned long pillAlarmStart = 0;
RTC_DATA_ATTR ESP32Time rtc(0); 

//BLE services
BLEService waterService("3cfc9609-f2be-4336-a58e-a5010a43559e"); //https://www.guidgenerator.com/online-guid-generator.aspx
BLEIntCharacteristic currWaterIntakeCharacteristic("8edb60b0-0b93-4403-8e39-44f1abf18e93", BLEIndicate);
BLEIntCharacteristic waterIntakeGoalCharacteristic("6d29b2f4-5726-4e37-9658-762311292d45", BLEWrite);  
BLEService timeService("a14e71f3-ce19-44f9-86f9-8df1a1c71537"); //https://www.guidgenerator.com/online-guid-generator.aspx
BLEStringCharacteristic pillScheduleCharacteristic("747437fd-7337-4b35-9244-c2b29251ec16", BLEWrite | BLEIndicate, 20);
BLEIntCharacteristic currTimeCharacteristic("ef391c7e-6464-44fb-be1b-bd83afc1035e", BLEWrite);  

//BLE variables

String currSch = "";
String prevSch;
String aSch = "_";
String bSch = "_";
String cSch = "_";
String dSch = "_";
String eSch = "_";
String fSch = "_";

String tempDayWeek = "";
String tempHM = "";
String tempBox = "Anything";
String tempAmount = "";

int pillN = 0;
int control = 0;

int BLEon = 0, canTurnOffBLE = 0;

void setup() {
  Serial.begin(115200);
  //Seting up the GPIOs  
  pinMode(functionBut, INPUT_PULLUP);
  pinMode(bluetoothBut, INPUT_PULLUP);
  pinMode(ballswitch, INPUT_PULLUP);
  pinMode(led, OUTPUT);
  pinMode(buzzer, OUTPUT);
  pinMode(hardwarePower, OUTPUT);

  digitalWrite(led, 0);
  digitalWrite(buzzer, 0);
  digitalWrite(hardwarePower, 0);//Powering off screen, SD card and gyroscope
  delay(500);
  digitalWrite(hardwarePower, 1);//Powering on screen, SD card and gyroscope

  delay(500);
  //enabling protocols for perifeals
  Wire.begin();
  u8g2.begin();
  mpu.begin();
  //Seting up the SD card
  if(!SD.begin(SD_CS)){
      Serial.println("Card mount failed!");
      return;
  }
  else Serial.printf("Card mount succesfull, the size is %lluMB.\n", SD.cardSize() / (1024 * 1024));
  delay(500);
  //if we boot up for the first time, get time from config and setup variables
  if(firstBoot){
    rtc.setTime(40, 59, 11, 19, 1, 2023);
    //rtc.setTime(getIntData("seconds"), getIntData("minutes"), getIntData("hours"), getIntData("day"), getIntData("month"), getIntData("year"));
    targetConsumption = getIntData("intakeGoal");
    totalConsumption = getIntData("intake");
    day = rtc.getDay();
    waterLevel = touchRead(waterSensor);
    replaceData("sensor", waterLevel);
    firstBoot = false;
  }
  //If we are another day, reset water consumption  
  if(day != rtc.getDay()){
    totalConsumption = 0;
    day = rtc.getDay();
  }
  //Create directories on the SD card if not there
  if(!SD.open("/userData"))SD.mkdir("/userData");
  logDir = "/userLogs-"+String(rtc.getYear());
  if(!SD.open(logDir))SD.mkdir(logDir);
  //Setting up gyroscope and screen
  mpu.setGyroOffsets(0.0, 0.0 , 0.0);
  
  u8g2.setFontRefHeightExtendedText();
  u8g2.setDrawColor(1);
  u8g2.setFontPosTop();
  u8g2.setFontDirection(0);
  u8g2.clearDisplay();
  //turn on WiFi if not by default
  WiFi.disconnect(true);
  WiFi.mode(WIFI_OFF);

  analogReadResolution(8);
  measureBattery();
  //setup timer values for accurate timing
  timer100ms = millis() / 100;
  lastMeasure = millis();

  timeBeforeSleep = 10;
  //Check if pills need to be taken and update the water sensor value with data from of config
  pillsToTake = getPillsToTake();
  waterLevel = getIntData("sensor");
  
  Serial.println("____________________________________________________");
  Serial.println(rtc.getTime("%A, %B %d %Y %H:%M:%S"));
  Serial.printf("User intake goal: %d, user current intake: %d, current sensor reading: %d\n", targetConsumption, totalConsumption, waterLevel);
  Serial.println("____________________________________________________");
}

void loop() {

  if(bluetoothState && !bluetoothIsOn){
    // bluetooth initialization
    if (!BLE.begin()) {
      Serial.println("starting Bluetooth Low Energy module failed!");
    }
    BLE.setLocalName("Smart Water Bottle");                          // set the local name peripheral advertises  
    
    BLE.setAdvertisedService(waterService);                              // set the UUID for the service this peripheral advertises:
    waterService.addCharacteristic(currWaterIntakeCharacteristic);       // add the characteristics to the service
    waterService.addCharacteristic(waterIntakeGoalCharacteristic);
    BLE.addService(waterService);                                        // add the service
    currWaterIntakeCharacteristic.writeValue((int)0);                         // set the initial value for the characeristic:
  
    BLE.setAdvertisedService(timeService);
    timeService.addCharacteristic(pillScheduleCharacteristic);
    timeService.addCharacteristic(currTimeCharacteristic);
    BLE.addService(timeService);
    
    BLE.advertise();
    Serial.println("Bluetooth device active, waiting for connections...");    //display on serial monitor
    bluetoothIsOn = true;                                                     //set boolean
  }
  else if(!bluetoothState && bluetoothIsOn){
    BLE.disconnect();
    bluetoothIsOn = false; 
  }
  if (bluetoothIsOn){
    BLE.poll();
    BLEDevice central = BLE.central();                                     // listen for Bluetooth Low Energy peripherals to connect:

    if (central) {
      if (central.connected()){
        currSch = pillScheduleCharacteristic.value();
        currWaterIntakeCharacteristic.writeValue((int)totalConsumption); //sending total consumption
          
        if (waterIntakeGoalCharacteristic.value() != targetConsumption && waterIntakeGoalCharacteristic.value() != 0){
          targetConsumption = waterIntakeGoalCharacteristic.value(); //reading new consumption target value
          replaceData("intakeGoal", targetConsumption);              
        }
        /*if (currWaterIntakeCharacteristic.value() != totalConsumption && currWaterIntakeCharacteristic.value() != 0){
          totalConsumption = currWaterIntakeCharacteristic.value(); //reading new consumption target value
          replaceData("intake", totalConsumption);              
        }*/
         
        if (currSch != "$"){          //no need to update
          if (currSch != "!"){        //app sends ! when there is no more pills to be sent
            if (currSch != prevSch){    //maybe necessary
              Serial.print("Loading schedule \n");
              getIncommingBLEdata(currSch);  
              prevSch = currSch;
            }
          }else{
            Serial.print("No more pills \n"); 
            
            replaceData("A", aSch+"\n"); // call 6 times the fuction to put the strings inside the SD card
            replaceData("B", bSch+"\n");
            replaceData("C", cSch+"\n");
            replaceData("D", dSch+"\n");
            replaceData("E", eSch+"\n");
              
            canTurnOffBLE = true;
          } 
        }else canTurnOffBLE = true;

        if (canTurnOffBLE){
          pillScheduleCharacteristic.writeValue("");                                 //reset value and be possible to connect to BLE
          //when it finishes passing the data, Bluetooth disconnects 
          BLE.disconnect();
          Serial.print("BLE disconnected \n"); 
          bluetoothState = false;                                                     //set boolean
          canTurnOffBLE = false;
        }
      }
    }
  }
  

  mpu.update();

  if(pillAlarm && micros() - buzzerTimer >= buzzerFrequency){//oscillator that makes the buzzer work at the right frequency when it should make sound
    if(millis() - pillAlarmStart <= 60000){ //Buzzer makes sound for max one minute: 60000
      buzzerTimer = micros();
      if(buzzerState){
        buzzerState = false;
        digitalWrite(buzzer, 0);
      }else{
        buzzerState = true;
        digitalWrite(buzzer, 1);
      } 
    }
    else {//After 1 min turn off alarm
      pillAlarm = false;
      ledFrequency = 5;
      digitalWrite(buzzer, 0);      
    }
  }
  
  if(millis() - timer100ms * 100 >= 100){ //loop that runs each 100ms
    timer100ms++;

    if (timeBeforeSleep < 0){ //in case of an error correct the varialbe value
      timeBeforeSleep = 10;
    }
    if(digitalRead(functionBut) == 0){ //if the button is pressed turn off pill alarm if on and in crease the timer that measures how long button is pressed (for manual sleep mode)
      if(pillAlarm == true){
        pillAlarm = false;
        digitalWrite(buzzer, 0);
        ledFrequency = 5;
      }
      timePressedF = timePressedF + 100;
    }
    else timePressedF = 0; //if not pressed, reset the pressed time

    if(digitalRead(bluetoothBut) == 0){
      if(bluetoothButState){ //check if bluetooth button is pressed and the button oreviously was not pressed
        if(!bluetoothState){ //if the bluetooth was off turn it on
          bluetoothState = true;
        }
        else bluetoothState = false; //turn bluetooth off if it was on
        bluetoothButState = false; //set the bluetooth button is pressed now
      }
      else bluetoothButState = true; //if the blutooth button is raised and it was pressed set the button as not pressed
      timePressedB = timePressedB + 100;
    }
    //reset time before sleep mode if bottle not on a flat surface or if buttons are pressed
    if (digitalRead(ballswitch) == 1 || digitalRead(bluetoothBut) == 0 || digitalRead(functionBut) == 0 || bluetoothState == true || (abs(mpu.getAngleX()) > 5 && abs(mpu.getAngleY()) > 5)){
      if(timeBeforeSleep < 10)timeBeforeSleep = 10;
      measureID = 0;//if movement or button is detected reset the water level measurment process
    }
    
    if(timer100ms % ledFrequency == 0){ //loop that runs each 500/200ms for the LED blinking, 200ms if pill alarm is on
      if(ledState){
        ledState = false;
        digitalWrite(led, 0);
      }else{
        ledState = true;
        digitalWrite(led, 1);
      }  
    }

    if(timer100ms % 10 == 0){ //loop that runs each second
      Serial.printf("Time before sleep: %d Seconds\n", timeBeforeSleep);
      Serial.printf("Current total water intake: %d.\n", totalConsumption);

      if (timer100ms % 5 == 0){ //loop that runs each 5 seconds
        measureBattery();
        if(millis() - pillAlarmStart > 60000 || pillAlarmStart == 0)pillsToTake = getPillsToTake(); //check if any pills should be taken now
      }
      if(pillsToTake != "" && (millis() - pillAlarmStart > 60000 || pillAlarmStart == 0)){ //if pills sould be taken and alarm hasn't went on during last minute start pill alarm
        pillAlarm = true;
        pillAlarmStart = millis();
        ledFrequency = 1; //increase LED frequency
        timeBeforeSleep = 30; //was 60
      }

      if((pillAlarm || (!pillAlarm && millis() - pillAlarmStart < 60000 && pillAlarmStart != 0)) && pillsToTake != ""){ //if pill alarm is on or pill alarm was on during last minute display the medecins to take
        displayMedecineStatut();
      }
      else displayConsumptionStatut(); //if nothing is happening with pills display water consumption statut
      
      if(timePressedF >= 2000 && bluetoothState == false)goToSleep(true, false); //if the function button has been pressed for sufficient time (2s) start sleep
      if(timePressedB >= 2000 && bluetoothState == false)goToSleep(false, true);

      timeBeforeSleep--;
    }

    if(timer100ms % 2 == 0){ //Loop that runs each 200ms
      if ((millis() - lastMeasure > 10000 || timeBeforeSleep == 0 || timeBeforeSleep == 1) && timeBeforeSleep != 10 && measureID <= 9){ //if the last measurement was a long time ago (10s) or bottle is nearly goin g to sleep, start water measurment

        measurements[measureID] = touchRead(waterSensor); //Add a new measurement to the list
        Serial.printf("Just measured a level of %d.\n", measurements[measureID]);
      
        if (measureID == 9){ //if 10 measurments have been done, compute
          Serial.printf("Computing the average water level value.\n");
          printArray(measurements, sizeof(measurements) / sizeof(int));
        
          float average = arrayAverage(measurements, sizeof(measurements) / sizeof(int)); // calculate the average measurement

          waterLevelGotChanged(average); //updates water consumption
          
          if(timeBeforeSleep == 0)goToSleep(false, false); //go to sleep if time has come and measurments are done
          else{
            lastMeasure = millis(); //update time last measurment was done
            measureID = 0;
          }
        }
        if (measureID > 0 && abs(measurements[measureID] - measurements[0]) > 1){ //if the measurment is not coherent, redo the measurments
          Serial.printf("Wrong measurment! Repeat.\n");
          timeBeforeSleep = 2;
          measureID = 0;
        }else if (measureID < 9)measureID++;    //if the measurments is coherent continue with next one
      }
    }
  }
  
}


void printArray(int list[], int listLength) {
  Serial.print("[");
  for (int i = 0; i < listLength; i++){
    if (i == listLength - 1)Serial.printf("%d]\n",list[i]);
    else Serial.printf("%d, ",list[i]);
  }
}
float arrayAverage(int list[], int listLength) {
  int sum = 0;
  for (int i = 0; i < listLength; i++){
    sum = sum + list[i];
  }
  return sum / listLength;
}

void waterLevelGotChanged(float average){
  Serial.printf("Average water level reading is %f, the user consumed %f.\n", average, average - waterLevel);
          
  if (average > waterLevel){ //If water level changed by consumption (not refil)
    float volume = getWaterVolume(waterLevel) - getWaterVolume(average);  //Formula to convert touchRead to mL
    totalConsumption = totalConsumption + int(volume); //Update the consumption level
    replaceData("intake", totalConsumption);  //save the new intake in the config
    addDataLog(String(totalConsumption));  //Add a log in the Log file
  }
  waterLevel = average;
  replaceData("sensor", waterLevel); //Update the sensor value so that it knows for the next measurment what was the previous level
}
float getWaterVolume(int sensor){
  return -0.0051*pow(sensor, 3) + 1.2978*pow(sensor, 2) - 106.02*sensor + 2794.6;
}
void measureBattery(){
  int cellVoltage = analogReadMilliVolts(batteryVoltage); //Get voltage of one cell thanks to voltage divider
  if (cellVoltage >= 1220)batteryLevel = 0x035; //Return different character codes depending on battery voltage
  else if (cellVoltage >= 1200)batteryLevel = 0x034;
  else if (cellVoltage >= 1180)batteryLevel = 0x033;
  else if (cellVoltage >= 1120)batteryLevel = 0x032;
  else if (cellVoltage >= 1110)batteryLevel = 0x031;
  else batteryLevel = 0x030;
}

void goToSleep(boolean byButF, boolean byButB){
  digitalWrite(led, 0); //Turn off GPIOs that may be active
  digitalWrite(buzzer, 0);
  Serial.printf("go to sleep\n");
  //Eneable timer before automatic wake up depending on pill schedule
  if(!byButB)esp_sleep_enable_timer_wakeup(getSleepDelayNextPills() * 1000000ULL); //convert seconds to microseconds and specify 'ULL' for 64bit value
  //Shut down SD card communication and the protocols for the other perifeals
  SD.end();
  u8g2.clearDisplay();
  Wire.endTransmission();
  Wire.end();
  digitalWrite(hardwarePower, 0); //Shut down power of Gyroscope, SD card and screen
  
  
  if(!byButF && !byButB){ //if sleep isn't started because of button, enable wake up with ball switch
    esp_sleep_enable_ext0_wakeup(GPIO_NUM_4,1);
  }
  else if(byButF){ //Otherwise wait until button is realeased and enable wake up for that button
    while(digitalRead(functionBut) == 0){}
    delay(10);
    esp_sleep_enable_ext0_wakeup(GPIO_NUM_33,0);
  }
  else if(byButB){ //Otherwise wait until button is realeased and enable wake up for that button
    while(digitalRead(bluetoothBut) == 0){}
    firstBoot = true;
    delay(10);
    esp_sleep_enable_ext0_wakeup(GPIO_NUM_32,0);
  }
  delay(500); //give some time to finish tasks if needed
  esp_deep_sleep_start();
}


int getSleepDelayNextPills(){
  String nextPill = "000000"; //String that will contain the next pill to take
  int nextPillDelay = 0;
  
  for(int i = 0; i < 5; i++){
    String c = String("ABCDE").substring(i, i+1); //loop through different boxes or strings in the config
    String s = getStringData(c); //Get value of the string for a certain box
    int n = 0;
    
    while (n*7 < s.length()){ //Each piece of string for one medicine has length 7
      String schedule = ""; //schedule will contain a string for one medecin "XXXXXX"
      if((n+1)*7 < s.length())schedule = s.substring(s.indexOf("_", n*7) + 1, s.indexOf("_", (n+1)*7));  
      else schedule = s.substring(s.indexOf("_", n*7) + 1, s.length());

      int currentDelay = getTimeToNextTake(schedule); //get the amount of time that is left for the next take
      
      nextPillDelay = getTimeToNextTake(nextPill); //get the amount of time that is left before taking the most next pill
        
      if(nextPill == "000000" || (currentDelay < nextPillDelay && currentDelay != 0))nextPill = schedule; //change the most next pill if value of schedule is smaller
      n++;
    }
    
  }
  nextPillDelay = getTimeToNextTake(nextPill);
  return nextPillDelay * 60 - rtc.getSecond(); //return the seconds until the most next pill has to be taken
}
int getTimeToNextTake(String schedule){ //Compute the time until next take based on actual time, take sinto account the days
  return (schedule.substring(0, 1).toInt() * 1440 + schedule.substring(1, 3).toInt()*60 + schedule.substring(3, 5).toInt() - (rtc.getDayofWeek() * 1440 + rtc.getHour(true) * 60 + rtc.getMinute()) + 10080) % 10080;
}

String getPillsToTake(){
  String pillsToTake = "";
  for(int i = 0; i < 5; i++){
    String c = String("ABCDE").substring(i, i+1);
    String s = getStringData(c);
    int n = 0;
    
    while (n*7 < s.length()){
      String schedule = "";
      if((n+1)*7 < s.length())schedule = s.substring(s.indexOf("_", n*7) + 1, s.indexOf("_", (n+1)*7));  
      else schedule = s.substring(s.indexOf("_", n*7) + 1, s.length());
      //if the time of the current pill option is coherent with the real time clock, add it to the list of pills to take
      if(schedule.substring(0, 1).toInt() == rtc.getDayofWeek() && schedule.substring(1, 3).toInt() == rtc.getHour(true) && schedule.substring(3, 5).toInt() == rtc.getMinute() && rtc.getSecond() <= 10){
        pillsToTake = pillsToTake + c + schedule.substring(5, 6).toInt();
      }
      n++;
    }
  }
  return pillsToTake;
}



String getStringData(String arg){
  File file = SD.open("/userData/userData.txt", FILE_READ); 
  if(!file){
    file.close();
    Serial.println("Failed to open data file for reading");
    return "";
  } 
  String fileData = "";  //store the text of the file inside the fileData variable
  if(file.available()){
    fileData = file.readString();
  }
  int begin = fileData.indexOf(arg+":") + arg.length() + 2; //find begin and end of the data we are interested in
  int end = fileData.substring(begin, fileData.length()).indexOf("\n") + begin;
 
  file.close();
  return fileData.substring(begin, end); //return part of text that is our data
}



int getIntData(String arg){
  File file = SD.open("/userData/userData.txt", FILE_READ);
  if(!file){
    file.close();
    Serial.println("Failed to open data file for reading");
    return 0;
  } 
  String fileData = ""; 
  if(file.available()){
    fileData = file.readString();
  }
  int begin = fileData.indexOf(arg+":") + arg.length() + 2;
  int end = fileData.substring(begin, fileData.length()).indexOf("\n") + begin;

  file.close();
  return fileData.substring(begin, end).toInt();  //return part of text that is our data and convert it to int
}


void replaceData(String arg, String value){
  File file = SD.open("/userData/userData.txt", FILE_READ);
  if(!file){
    file.close();
    Serial.println("Failed to open data file for reading");
    return;
  } 
    
  String fileData = ""; 
  if(file.available()){
    fileData = file.readString();
  }
  file.close();
  
  String dataToReplace = getStringData(arg); 
  //replace the data of the parameter with our value
  fileData.replace(dataToReplace, value);
  
  file = SD.open("/userData/userData.txt", FILE_WRITE); //reopen file in writing and write all the data with the replaced part
  
  if(!file){
    file.close();
    Serial.println("Failed to open data file for reading");
    return;
  } 
  if(!file.print(fileData)){
    Serial.println("Write failed");
  } 
  file.close();
  return;
}
void replaceData(String arg, int value){
  File file = SD.open("/userData/userData.txt", FILE_READ);
  if(!file){
    Serial.println("Failed to open data file for reading");
    return;
  } 
    
  String fileData = ""; 
  if(file.available()){
    fileData = file.readString();
  }
  file.close();
  
  String dataToReplace = getStringData(arg);
  
  fileData.replace(dataToReplace, String(value));
  
  file = SD.open("/userData/userData.txt", FILE_WRITE);
  
  if(!file){
    file.close();
    Serial.println("Failed to open data file for writing");
    return;
  } 
  if(!file.print(fileData)){
    Serial.println("Write failed");
  } 
  file.close();
  return; 
}


void addDataLog(String value){
  String fileName = (String)rtc.getDay()+"-"+(String)rtc.getMonth()+".txt"; //create a text file for the actual day
  File file = SD.open(logDir+"/"+fileName, FILE_APPEND);
  if(!file){
    Serial.println("Failed to open file for writing");
    return;
  }
  String dataString = String(rtc.getHour(true))+":"+String(rtc.getMinute())+"-"+value+'\n'; //create the string containing our data + time
    
  file.print(dataString); //add the string to the file
  
  file.close();
  return;
}

void displayConsumptionStatut(){
  u8g2.clearBuffer();
  //Convert the mL to L and separte the decimals from the rest and convert all to char arrays
  char onesDigit[4] = "";
  char decimals[8] = "";
  sprintf(onesDigit, "%u",(totalConsumption / 1000) % 10);
  sprintf(decimals, "%u%u",(totalConsumption / 100) % 10, (totalConsumption / 10) % 10);
  u8g2.setFont(u8g2_font_fur30_tn);
  u8g2.drawStr(0,0,onesDigit);

  u8g2.setFont(u8g2_font_fur30_tn);
  u8g2.drawStr(18,0,".");
  //draw big slash
  u8g2.drawLine(70, 2, 56, 44);
  u8g2.drawLine(71, 2, 57, 44);
  
  u8g2.setFont(u8g2_font_fur20_tn);
  u8g2.drawStr(28,10, decimals);
  //Convert the target intake to L and display it
  char target[16] = "";
  float targetConsump = float(targetConsumption)/1000;
  dtostrf(targetConsump, 0, 2, target);

  u8g2.setFont(u8g2_font_helvB14_tn );
  u8g2.drawStr(65,30,target);

  u8g2.setFont(u8g2_font_logisoso24_tf);
  u8g2.drawStr(100,20,"L");
  
  if(bluetoothState){ //If bluetooth active draw the symbole  
    u8g2.setFont(u8g2_font_open_iconic_all_2x_t);
    u8g2.drawGlyph(115, 0, 0x005e);
  }
  //Draw the symbole of the batteyr level
  u8g2.setFont(u8g2_font_battery19_tn);
  u8g2.drawGlyph(119, 24, batteryLevel);
  //Create the progress bar
  u8g2.drawFrame(0,52,128,12);
  int progress = int((float(totalConsumption)/float(targetConsumption))*122); //convert intake and target to float in order to perform division, multiply by max bar length and convert to int
  if(progress > 124)progress = 124; //Block max bar length even if intake > goal
  if(progress > 0)u8g2.drawBox(2,54, progress,8); //If intake = 0, display no bar
  
  u8g2.sendBuffer(); 
}
void displayMedecineStatut(){
  u8g2.clearBuffer();
  
  u8g2.setFont(u8g2_font_unifont_t_symbols);
  u8g2.drawGlyph(0, 0, 0x23f5); 
  
  if(bluetoothState){
    u8g2.setFont(u8g2_font_open_iconic_all_2x_t);
    u8g2.drawGlyph(115, 0, 0x005e);
  }
  
  u8g2.setFont(u8g2_font_battery19_tn);
  u8g2.drawGlyph(119, 24, batteryLevel);
  
  u8g2.setFont(u8g2_font_t0_11b_mf);
  u8g2.drawStr(12,0,"Next medicine:");
  u8g2.drawLine(0, 13, 90, 13);

  if(pillsToTake != ""){
    int rows = 0, x = 0;
    for(int i = 0; i < pillsToTake.length(); i = i + 2){ //loop through pills to take
      if(pillsToTake.charAt(i) != '\n' && pillsToTake.charAt(i) != '\0' && rows < 5){
        if(rows == 3){ //create new column if it doesn't fit anymore/max 3 rows
          x++;
          rows = 0;
        }
        char box[6]; //Convert the box letter and amount to char arrays
        char amount[2];
        String("Box "+String(pillsToTake.charAt(i))).toCharArray(box, 6);
        String(pillsToTake.charAt(i + 1)).toCharArray(amount, 2);
        u8g2.setFont(u8g2_font_6x12_te);
        u8g2.drawStr(3 + x * 60,15 + rows * 11, box);
        u8g2.setFont(u8g2_font_siji_t_6x10);
        u8g2.drawGlyph(35 + x * 60,15 + rows * 11,0x0078);
        u8g2.setFont(u8g2_font_nokiafc22_tf);
        u8g2.drawStr(45 + x * 60,15 + rows * 11, amount);
        rows++;
      }
    }
  }
  u8g2.drawFrame(0,52,128,12);
  int progress = int((float(totalConsumption)/float(targetConsumption))*122);
  if(progress > 124)progress = 124;
  if(progress > 0)u8g2.drawBox(2,54, progress,8);

  u8g2.sendBuffer(); 
}
void dataTranferScreen(){
  u8g2.clearBuffer();

  u8g2.setFont(u8g2_font_t0_18b_tf);
  u8g2.drawStr(20,5,"Bluetooth");

  u8g2.setFont(u8g2_font_t0_18b_tf);
  u8g2.drawStr(0,25,"data transfer");
  
  if(bluetoothState){
    u8g2.setFont(u8g2_font_open_iconic_all_2x_t);
    u8g2.drawGlyph(115, 0, 0x005e);
  }
  
  u8g2.setFont(u8g2_font_battery19_tn);
  u8g2.drawGlyph(119, 24, batteryLevel);
  
  u8g2.drawFrame(0,46,128,16);
  u8g2.drawBox(2,48,124,12);

  u8g2.sendBuffer(); 
}

void getIncommingBLEdata(String currString){
//get common
  tempDayWeek = currSch.substring(0,2);
  tempHM = currSch.substring(2,6);
          
  //Serial.println(tempDayWeek);
  //Serial.println(tempHM);
          
  tempBox = currSch.substring(6,7);
  pillN = 0;
          
  while (tempBox != "\0"){
    tempBox = currSch.substring(6+pillN,7+pillN);
    tempAmount = currSch.substring(7+pillN,8+pillN);
          
    Serial.print("Get tempBox: ");
    Serial.println(tempBox);
          
          
    if (tempBox == "A"){
      if (tempDayWeek == "SU"){
        aSch += "0";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "MO"){
        aSch += "1";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "TU"){
        aSch += "2";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "WE"){
        aSch += "3";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "TH"){
        aSch += "4";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "FR"){
        aSch += "5";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "SA"){
        aSch += "6";
        aSch += tempHM;
        aSch += tempAmount;
        aSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          aSch += String(i);
          aSch += tempHM;
          aSch += tempAmount;
          aSch += "_";
        }
      }
    }  
          
    if (tempBox == "B"){
      if (tempDayWeek == "SU"){
        bSch += "0";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "MO"){
        bSch += "1";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "TU"){
        bSch += "2";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "WE"){
        bSch += "3";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "TH"){
        bSch += "4";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "FR"){
        bSch += "5";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "SA"){
        bSch += "6";
        bSch += tempHM;
        bSch += tempAmount;
        bSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          bSch += String(i);
          bSch += tempHM;
          bSch += tempAmount;
          bSch += "_";
        }
      }
    }
          
    if (tempBox == "C"){
      if (tempDayWeek == "SU"){
        cSch += "0";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "MO"){
        cSch += "1";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "TU"){
        cSch += "2";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "WE"){
        cSch += "3";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "TH"){
        cSch += "4";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "FR"){
        cSch += "5";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "SA"){
        cSch += "6";
        cSch += tempHM;
        cSch += tempAmount;
        cSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          cSch += String(i);
          cSch += tempHM;
          cSch += tempAmount;
          cSch += "_";
        }
      }
    }
          
    if (tempBox == "D"){
      if (tempDayWeek == "SU"){
        dSch += "0";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "MO"){
        dSch += "1";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "TU"){
        dSch += "2";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "WE"){
        dSch += "3";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "TH"){
        dSch += "4";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "FR"){
        dSch += "5";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "SA"){
        dSch += "6";
        dSch += tempHM;
        dSch += tempAmount;
        dSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          dSch += String(i);
          dSch += tempHM;
          dSch += tempAmount;
          dSch += "_";
        }
      }
    }
          
    if (tempBox == "E"){
      if (tempDayWeek == "SU"){
        eSch += "0";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "MO"){
        eSch += "1";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "TU"){
        eSch += "2";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "WE"){
        eSch += "3";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "TH"){
        eSch += "4";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "FR"){
        eSch += "5";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "SA"){
        eSch += "6";
        eSch += tempHM;
        eSch += tempAmount;
        eSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          eSch += String(i);
          eSch += tempHM;
          eSch += tempAmount;
          eSch += "_";
        }
      }
    }
          
    if (tempBox == "F"){
      if (tempDayWeek == "SU"){
        fSch += "0";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "MO"){
        fSch += "1";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "TU"){
        fSch += "2";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "WE"){
        fSch += "3";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "TH"){
        fSch += "4";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "FR"){
        fSch += "5";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "SA"){
        fSch += "6";
        fSch += tempHM;
        fSch += tempAmount;
        fSch += "_";
      }
      if (tempDayWeek == "ED"){
        for (int i = 0; i<7; i++){
          fSch += String(i);
          fSch += tempHM;
          fSch += tempAmount;
          fSch += "_";
        }
      }
    }
    pillN = pillN+2;
  } 
          
  //only for keeping track if the strings are being built correctly
  Serial.print("aSch: "); 
  Serial.println(aSch);
  Serial.print("bSch: "); 
  Serial.println(bSch);
  Serial.print("cSch: "); 
  Serial.println(cSch);
  Serial.print("dSch: "); 
  Serial.println(dSch);
  Serial.print("eSch: "); 
  Serial.println(eSch);
  Serial.print("fSch: "); 
  Serial.println(fSch);  
}
