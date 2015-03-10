#include <Servo.h> 
#include <Math.h>
/*
* The value D and H are the absolute length
* d and h are the position of the wrist before the final movement
*/
Servo myservo[5];

int pin[] = {3,5,6,9,10};
String names[] = {"Base", "Shoulder","Elbow","Wrist","Hand"};
int minimum[] = {36,65,54,18,89};
int maximum[] = {153,108,174,94,100};
int angle[] = {95,90,90,45,90};
const int wristLevel = 70; // this is the angle between the wrist and the horizon


void setup(){
  for(int i = 0; i<5;i++){
    myservo[i].attach(pin[i]);
  }
Serial.begin(9600);
}

void loop(){
  baseAngle(270,0);
  moveToDandH(radius(270,0),0);
}

/**
* Value 2 is the range of the output values
*/
int mapping(int value, int low2, int high2){
  bool outOfBoundsU = value > high2;
  bool outOfBoundsL = value < low2;
   if(outOfBoundsU) {
    return high2;
    }
    if(outOfBoundsL) {
      return low2;
    }
    return value;
}

int baseAngle(int xPos, int yPos){
  int angle = atan2(yPos,xPos) * 180 / (3.14192);
  return 95 + angle;
}

int radius(int xPos, int yPos){
  return sqrt((xPos*xPos)+(yPos*yPos));
}



/**
* To calculate the height and distance we need many cosine moves
* The connection between the shoulder and the elbow is called A
* The connection between the elbow and the wrist is called B
* The connection between the wrist and the end is called C
*/

const int A = 153;
const int B = 121;
const int C = 197;

// Angle 0 is shoulder
// Angle 1 is elbow
// Angle 2 is wrist
void moveToDandH(int d, int h){
  int angles[3];
  angles[0] = mapping(getShoulderAngle(d,h),minimum[1],maximum[1]);
  angles[1] = mapping(getElbowAngle(d,h),minimum[2],maximum[2]);
  angles[2] = mapping(getWristAngle(),minimum[3],minimum[3]);
  for(int i=0;i<3;i++){
    Serial.println(names[i+1] + ":" + angles[i]);
    myservo[i+1].write(angles[i]);
  }
}

// This sets the wrist to be at 45 degrees to the horizon
int getWristAngle(){
  int sAngle = angle[1];
  int eAngle = angle[2];
  int wAngle = - sAngle + (180-eAngle) + (90-wristLevel);
  wAngle = wAngle < 0 ? 0 : wAngle;
  return wAngle;
}

/**
* This method will move the wrist orgin to d and h
* It uses the cosine rule on the hypotonouse between
* d and h
*/
int getElbowAngle(int d, int h){
  int cosValue = ((A*A+B*B-(d*d+h*h))/(2*A*B));
  return radToDeg(acos(cosValue));
}

/* there are 2 angles we need to find to get
* the shoulder angle
*/
int getShoulderAngle(int d, int h){
  int theta_1 = atan2(d,h);
  int F = sqrt(d*d+h*h); // the hypotenouse base to wrist orgin
  int theta_2 = radToDeg(acos((A*A+F*F-B*B)/2*A*F));
  return theta_1+theta_2;
}


int radToDeg(int rad){
  return rad*180/3.14;
}

void autoLevel(){
  int sAngle = angle[1];
  int eAngle = angle[2];
  int wAngle = - sAngle + (180-eAngle) + (90-wristLevel);
  wAngle = wAngle < 0 ? 0 : wAngle;
  angle[3] = wAngle;
}
