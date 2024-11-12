#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>
#include "DHT.h"
// #define WIFI_SSID "TP-Link_C2FC"
// #define WIFI_PASSWORD "38351044"
//=============================================
// #define WIFI_SSID "Dungpro"
// #define WIFI_PASSWORD "nguyendung..."
//==============================================
#define WIFI_SSID "TP-Link_9430"
#define WIFI_PASSWORD "51563103"
//==========================================
// #define WIFI_SSID "Hotel Duc Hoang T2"
// #define WIFI_PASSWORD "duchoang"
//===========================================
#define DHTPIN 4
#define DHTTYPE DHT11
DHT dht11(DHTPIN, DHTTYPE);
#define On_Board_LED 2
#define LED_01_PIN 18
#define Fan 26
#define Pump 12
float thresholdTemperature = 28;  // Ngưỡng nhiệt độ ban đầu
float thresholdTemperature1 = 29;
float thresholdTemperature2 = 31;
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#define API_KEY "AIzaSyC96hxHKZ60wBd3D8g5AEf8wP-2tuh7xTI"
#define DATABASE_URL "assignment02-a181e-default-rtdb.firebaseio.com"
// Define Firebase Data object.
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
const long sendDataIntervalMillis = 10000;  //--> gửi dữ liệu liên fb sau 10s
//========================================
// Boolean variable for sign in status.
bool signupOK = false;
float Temp_Val;
int Humd_Val;
bool LastAutoState = false;
bool AUTO;
bool deviceStatus;
bool LED_01_State;
bool FAN_01_State;
bool PUMP_01_State;
//________________________________________________________________________________
void read_DHT11() {
  Temp_Val = dht11.readTemperature();
  Humd_Val = dht11.readHumidity();
  //---------------------------------------- Check if any reads failed.
  if (isnan(Temp_Val) || isnan(Humd_Val)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    Temp_Val = 0.00;
    Humd_Val = 0;
  }
  //----------------------------------------
  Serial.println("Read_DHT11");
  Serial.print(F("Humidity   : "));
  Serial.print(Humd_Val);
  Serial.println("%");
  Serial.print(F("Temperature: "));
  Serial.print(Temp_Val);
  Serial.println("°C");
  Serial.println("---------------");
}

void automatic() {
  float Temp_Va = dht11.readTemperature();
  float Humd_Val = dht11.readHumidity();
 
 
  if (isnan(Temp_Val) || isnan(Humd_Val)) {
    Serial.println("Không thể đọc dữ liệu từ DHT11!");
    Temp_Val = 0.00;
    Humd_Val = 0;
    return;
  }
  if (thresholdTemperature2 <= Temp_Val) {
    digitalWrite(LED_01_PIN, HIGH);
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/LED", true);
    Serial.println("Đèn đang bật");
    digitalWrite(Fan, HIGH);  // Tắt LED (led)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/FAN", true);
    Serial.println("Quạt đang bật.");
    digitalWrite(Pump, HIGH);  // Tắt LED (bơm)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/PUMP", true);
    Serial.println("Máy bơm đang bật.");
  } else if (thresholdTemperature1 <= Temp_Val) {
    digitalWrite(LED_01_PIN, HIGH);
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/LED", true);
    Serial.println("Đèn đang bật");
    digitalWrite(Fan, HIGH);  // Bật LED (quạt)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/FAN", true);
    Serial.println("Quạt đang bật.");
    digitalWrite(Pump, LOW);
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/PUMP", false);
    Serial.println("Máy bơm đang tắt");

  } else if (thresholdTemperature <= Temp_Val) {
    digitalWrite(LED_01_PIN, HIGH);
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/LED", true);
    Serial.println("Đèn đang bật");
    digitalWrite(Fan, LOW);  // Bật LED (quạt)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/FAN", false);
    Serial.println("Quạt đang tắt.");
    digitalWrite(Pump, LOW);
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/PUMP", false);
    Serial.println("Máy bơm đang tắt");

  } else {
    digitalWrite(Fan, LOW);  // Tắt LED (quạt)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/LED", false);
    Serial.println("Quạt đang tắt.");
    digitalWrite(Pump, LOW);  // Tắt LED (bơm)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/FAN", false);
    Serial.println("Máy bơm đang tắt.");
    digitalWrite(LED_01_PIN, LOW);  // Tắt LED (led)
    Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/PUMP", false);
    Serial.println("Đèn đang tắt.");
  }
}
//=================================================================================
// void effect2() {
//===============================================AUTO
void read_data_from_firebase_database_auto() {
  Serial.println();
  Serial.println("AUTO");
  digitalWrite(On_Board_LED, HIGH);

  if (Firebase.RTDB.getBool(&fbdo, "/ESP/ROOM_0/AUTO")) {
    if (fbdo.dataType() == "boolean") {
      deviceStatus = fbdo.boolData();
      digitalWrite(deviceStatus, deviceStatus);
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("AUTO: ");
      Serial.println(deviceStatus);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println("----------");
}

void store_data_to_firebase_database() {
  Serial.println();
  Serial.println("Store Data");
  digitalWrite(On_Board_LED, HIGH);

  // Write an Int number on the database path DHT11_Data/Temperature.
  if (Firebase.RTDB.setFloat(&fbdo, "ESP/ROOM_0/TEMPERATURE", Temp_Val)) {
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
  }

  // Write an Float number on the database path DHT11_Data/Humidity.
  if (Firebase.RTDB.setInt(&fbdo, "ESP/ROOM_0/HUMIDITY", Humd_Val)) {
    Serial.println("PASSED");
    Serial.println("PATH: " + fbdo.dataPath());
    Serial.println("TYPE: " + fbdo.dataType());
  } else {
    Serial.println("FAILED");
    Serial.println("REASON: " + fbdo.errorReason());
  }

  digitalWrite(On_Board_LED, LOW);
  Serial.println("");
}


void read_data_from_firebase_database_pump() {
  Serial.println();
  Serial.println("Get May Bom");
  digitalWrite(On_Board_LED, HIGH);
  // delay(50000);
  if (Firebase.RTDB.getBool(&fbdo, "/ESP/ROOM_0/PUMP")) {
    if (fbdo.dataType() == "boolean") {
      PUMP_01_State = fbdo.boolData();
      digitalWrite(Pump, PUMP_01_State);
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("PUMP_01_State: ");
      Serial.println(PUMP_01_State);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println("------------");
  // delay(50000);
}
// Đọc data quatj
void read_data_from_firebase_database_fan() {
  Serial.println();
  Serial.println("Get Quat");
  digitalWrite(On_Board_LED, HIGH);
  if (Firebase.RTDB.getBool(&fbdo, "/ESP/ROOM_0/FAN")) {
    if (fbdo.dataType() == "boolean") {
      FAN_01_State = fbdo.boolData();
      digitalWrite(Fan, FAN_01_State);
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("FAN_01_State: ");
      Serial.println(FAN_01_State);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println("------------");
}
//Đọc thông tin led
void read_data_from_firebase_database_led() {
  Serial.println();
  Serial.println("Get LED");
  digitalWrite(On_Board_LED, HIGH);
  // digitalWrite(On_Board_LED, HIGH);

  if (Firebase.RTDB.getBool(&fbdo, "/ESP/ROOM_0/LED")) {
    if (fbdo.dataType() == "boolean") {
      LED_01_State = fbdo.boolData();
      digitalWrite(LED_01_PIN, LED_01_State);
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
      Serial.print("LED_01_State: ");
      Serial.println(LED_01_State);
    }
  } else {
    Serial.println(fbdo.errorReason());
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println("----------");
}

//Gọi hàm setup
void setup() {
  // put your setup code here, to run once:

  Serial.begin(9600);
  Serial.println();

  pinMode(On_Board_LED, OUTPUT);
  pinMode(LED_01_PIN, OUTPUT);
  pinMode(Fan, OUTPUT);
  pinMode(Pump, OUTPUT);

  //----------------------------------------
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("Connection");
  Serial.print("Connecting to : ");
  Serial.println(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");

    digitalWrite(On_Board_LED, HIGH);
    delay(250);
    digitalWrite(On_Board_LED, LOW);
    delay(250);
  }
  digitalWrite(On_Board_LED, LOW);
  Serial.println();
  Serial.print("Successfully connected to : ");
  Serial.println(WIFI_SSID);
  Serial.print("IP : ");
  Serial.println(WiFi.localIP());
  Serial.println("---------------");
  //----------------------------------------

  // Assign the api key (required).
  config.api_key = API_KEY;

  // Assign the RTDB URL (required).
  config.database_url = DATABASE_URL;

  // Sign up.
  Serial.println();
  Serial.println("Sign up");
  Serial.print("Sign up new user... ");
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("  ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  Serial.println("");

  // Gán chức năng gọi lại cho tác vụ tạo mã thông báo chạy dài.
  config.token_status_callback = tokenStatusCallback;  //--> see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  dht11.begin();

  delay(1000);
}
//==============================================================================
void tudong() {
  read_DHT11();
  store_data_to_firebase_database();
  automatic();
  Serial.println("Read_DHT11");
  Serial.print(F("Humidity   : "));
  Serial.print(Humd_Val);
  Serial.println("%");
  Serial.print(F("Temperature: "));
  Serial.print(Temp_Val);
  Serial.println("°C");
  Serial.println("---------------");
  delay(2000);  // Đợi 2 giây trước khi đọc lại
}
void thucong() {
  read_DHT11();
  store_data_to_firebase_database();
  read_data_from_firebase_database_led();
  read_data_from_firebase_database_fan();
  read_data_from_firebase_database_pump();
  delay(2000);
}

//________________________________________________________________________________ VOID LOOP
void loop() {

  // put your main code here, to run repeatedly:
  if (Firebase.RTDB.getBool(&fbdo, "/ESP/ROOM_0/AUTO")) {
    int autoMode = fbdo.boolData();
    Serial.println(autoMode);
    if (autoMode == 0 && LastAutoState == 1 || autoMode == 1 && LastAutoState == 0) {
      digitalWrite(Fan, LOW);  // Tắt LED (quạt)
      Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/LED", false);
      Serial.println("Quạt đang tắt.");
      digitalWrite(Pump, LOW);  // Tắt LED (bơm)
      Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/FAN", false);
      Serial.println("Máy bơm đang tắt.");
      digitalWrite(LED_01_PIN, LOW);  // Tắt LED (led)
      Firebase.RTDB.setBool(&fbdo, "/ESP/ROOM_0/PUMP", false);
      Serial.println("Đèn đang tắt.");
      delay(1000);
    }
    if (autoMode == 1) {
      tudong();
    } else if (autoMode == 0) {
      thucong();
    }
    LastAutoState = autoMode;
  } else {
    Serial.println("ko thể kết nối đến firebase");
  }
  delay(2000);
  //   if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > sendDataIntervalMillis || sendDataPrevMillis == 0)) {
  //     sendDataPrevMillis = millis();
  //     read_DHT11();
  //     store_data_to_firebase_database();
  //     read_data_from_firebase_database_auto();
  // //     read_data_from_firebase_database_led();
  // //     read_data_from_firebase_database_fan();
  // //     read_data_from_firebase_database_pump();
  // //   }
  //  }
}

//________________________________________________________________________________
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
