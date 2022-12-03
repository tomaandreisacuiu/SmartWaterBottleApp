//https://www.arduino.cc/reference/en/libraries/arduinoble/
//simulate sending the water level using a potentiometer to vary the values sent to the app

#include <ArduinoBLE.h>

   // create service
BLEService waterService("3cfc9609-f2be-4336-a58e-a5010a43559e"); //https://www.guidgenerator.com/online-guid-generator.aspx
BLEFloatCharacteristic waterLevelCharacteristic("8edb60b0-0b93-4403-8e39-44f1abf18e93", BLEIndicate); //not sure if read, indicate or notify. Read seems to be easier to implement, but requires the app to activaly ask for the readings(?). Not sure about the size as well
//Indicate -> reads always/value changes constantly
//Read -> App choses when to receive newest info
//Notify -> mix of indicate and read. App can chose when to receive data constantly and when to stop receiving

BLEService timeService("a14e71f3-ce19-44f9-86f9-8df1a1c71537"); //https://www.guidgenerator.com/online-guid-generator.aspx
BLEStringCharacteristic pillScheduleCharacteristic("747437fd-7337-4b35-9244-c2b29251ec16", BLEWrite | BLERead, 20); //not sure about the size


const int waterSensor = 2; //any analog pin
float waterLevelValue = 0;
String schedule = "";

void setup() {
  Serial.begin(9600);
  while (!Serial); //IDK, example codes had it

  pinMode(waterSensor, INPUT); // input from water level sensor

  // begin initialization
  if (!BLE.begin()) {
    Serial.println("starting Bluetooth® Low Energy module failed!");
    while (1);
  }

  // set the local name peripheral advertises --- Name of the device (?)
  BLE.setLocalName("Water Bottle");
  
  // set the UUID for the service this peripheral advertises:
  BLE.setAdvertisedService(waterService);
  // add the characteristics to the service
  waterService.addCharacteristic(waterLevelCharacteristic);
  // add the service
  BLE.addService(waterService);
  // set the initial value for the characeristic:
  waterLevelCharacteristic.writeValue(0);

  BLE.setAdvertisedService(timeService);
  timeService.addCharacteristic(pillScheduleCharacteristic);
  BLE.addService(timeService);
  pillScheduleCharacteristic.writeValue("");
  

  // start advertising
  BLE.advertise();

  //display on serial monitor
  Serial.println("Bluetooth® device active, waiting for connections...");


}

void loop() {
  // poll for Bluetooth® Low Energy events (probably optional)
  BLE.poll();

    // listen for Bluetooth® Low Energy peripherals to connect:
  BLEDevice central = BLE.central();

  // if a central is connected to peripheral:
  if (central) {
    Serial.print("Connected to central: ");
    // print the central's MAC address:
    Serial.println(central.address());

    // while the central is still connected to peripheral:
    while (central.connected()) {
      //read from sensor
       waterLevelValue = analogRead(waterSensor);
      //prints on serial
      Serial.print("Current water level:");
      Serial.println(waterLevelValue);
      
      //sends to phone
      waterLevelCharacteristic.writeValue((float)waterLevelValue); //not sure how to send the data with the type we want


      //get data from phone and print on serial
      if (pillScheduleCharacteristic.written()){
        schedule = pillScheduleCharacteristic.value();
        Serial.println("Pill schedule (send from phone): ");
        Serial.println(schedule);
       }
    }

    // when the central disconnects, print it out:
    Serial.print(F("Disconnected from central: "));
    Serial.println(central.address());
  }

  //read from sensor
  waterLevelValue = analogRead(waterSensor);

  //sends to phone
  waterLevelCharacteristic.writeValue((float)waterLevelValue); //not sure how to send the data with the type we want

  delay(100);
}
