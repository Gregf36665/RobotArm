#include <Servo.h> 
#include <Math.h>

/*
* The value D and H are the absolute length
* d and h are the position of the wrist before the final movement
*/
Servo myservo[5];

int pin[] = {3,5,6,9,10};
String names[] = {"Base", "Shoulder","Elbow","Wrist","Hand"};
int minimum[] = {36,10,54,18,89};
int maximum[] = {153,108,174,94,100};
int currentAngle[] = {95,65,130,60,110};
int resetAngle[] = {95,65,130,60,110};
const int wristLevel = 65; // this is the angle between the wrist and the horizon
#define PI 3.1415

void setup(){
  for(int i = 0; i<5;i++){
    myservo[i].attach(pin[i]);
  }
Serial.begin(9600);
}

void loop(){
  for(int i=0; i < 5; i++){
    myservo[i].write(resetAngle[i]);
    currentAngle[i] = resetAngle[i];
    Serial.println("here");
  }
  delay(1000);
  moveToCoord(202,168,-68,true);
  moveToCoord(108,-123,-77,false);
  
  moveToCoord(268,0,-66,true);
  moveToCoord(112,-123,-56,false);
  
  moveToCoord(250,100,-66,true);
  moveToCoord(115,-126,-34,false);

  moveToCoord(254,-104,-66,true);
  moveToCoord(116,-127,-10,false);
  
  moveToCoord(268,0,0,false);
  
  //unstack
  moveToCoord(119,-130,-10,true);
  moveToCoord(252,-100,-66,false);
  
  moveToCoord(115,-126,-34,true);
  moveToCoord(250,100,-66,false);
 
  moveToCoord(114,-125,-56,true);
  moveToCoord(262,0,-66,false);

  moveToCoord(108,-123,-77,true);
  moveToCoord(199,165,-69,false);
  
  moveToCoord(268,0,0,false);
  
}

void moveToCoord(int x, int y, int z, boolean closed){
  slowMove(0,baseAngle(x,y));
  delay(100);
  moveToDandH(sqrt(x*x+y*y),z);
  Serial.println("done");
  if(closed && currentAngle[4] != 85) slowMove(4,85);
  else if(!closed && currentAngle[4] != 110) slowMove(4,110);
  delay(500);
  moveUp();
}

void slowMove(int servoNum, int moveTo){
  int steps = moveTo-currentAngle[servoNum];
  boolean incr = steps > 0;
  steps = steps > 0 ? steps : -steps; 
  for(int j=0; j < steps; j++){
    int add = incr ? j : -j;
    myservo[servoNum].write(currentAngle[servoNum]+add);
    Serial.print(names[servoNum]);
    Serial.println(currentAngle[servoNum]+add);
    delay(20);
  }
  Serial.println("done");
  currentAngle[servoNum] = moveTo;
}

int moveUp(){
  slowMove(1,100);
  slowMove(2,130);
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
  return 96 + angle;
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
const int B = 120;
const int C = 175;

// Angle 0 is shoulder
// Angle 1 is elbow
// Angle 2 is wrist
void moveToDandH(int d, int h){
  int angles[3];
  double newDist = (d-cos(degToRad(wristLevel))*C);
  double newHeight = (h+sin(degToRad(wristLevel))*C);
  angles[0] = 180-getCalculatedShoulderAngle(newDist,newHeight);
  angles[1] = getTheta(newDist,newHeight);
  angles[2] = getWristAngle(angles[0],angles[1]);//mapping(getWristAngle(),minimum[3],minimum[3]);
  for(int i=0; i < 3; i++){
    slowMove(i+1,angles[i]);
  }
}

// This sets the wrist to be at 45 degrees to the horizon
int getWristAngle(int sAngle, int eAngle){
  int wAngle = 270 - sAngle - eAngle - wristLevel; //- sAngle + (180-eAngle) + (90-wristLevel);
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

double getL(int distance, int myHeight){
  return sqrt(distance*distance+myHeight*myHeight);
}

double getTheta(double distance, double myHeight){
  double lVal = getL(distance,myHeight);
  double constants = (lVal*lVal -(A*A + B*B));
  double cosTheta = (constants/((-2)*A*B));
  double theta = acos(cosTheta)*(180/PI);
  return theta;
}

double getThetaOne(double distance, double myHeight){
  double lVal = getL(distance,myHeight);
  double theta = getTheta(distance,myHeight);
  double numerator = B*sin(theta*(PI/180));
  double thetaOne = asin(numerator/lVal)*(180/PI);
  return thetaOne;
}

int getCalculatedShoulderAngle(double distance, double myHeight){
  double thetaOne = getThetaOne(distance, myHeight); 
  double thetaH = atan2(myHeight,distance)*(180/PI);
  double thetaS = (180 - thetaOne - thetaH);
  return thetaS;
}

double radToDeg(int rad){
  return rad*180/PI;
}

double degToRad(int deg){
  return deg*PI/180;
}
