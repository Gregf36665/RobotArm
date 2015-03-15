import acm.graphics.*;
import acm.program.*;
import java.awt.event.*;

/**
 * This program will help to test the robot arm without actually having one on hand
 */
public class RobotArmSim extends GraphicsProgram{
  
  private static final int BASESIZE=60;
  private static final int SHOULDERSIZE=250;
  private static final int ARMSIZE=175;
  private static final double ANGLECHANGE=0.01;
  
  private GLabel distance,height, shoulderLabel, armLabel;
  
  private double shoulderX,shoulderY;
  private double armX, armY;
  
  private double autoDist = 0, autoHeight = 0;
  
  private double shoulderAngle=135.0, armAngle = 45.0;
  
  private boolean sRR = false, sRL = false;
  private boolean aRR = false, aRL = false;
  private boolean auto;
  
  private boolean shoulderHit;
  private boolean armHit;
  
  private GPolygon shoulder,arm;
  private GRect box;
  
  public void init(){
    
    shoulder = new GPolygon(BASESIZE/2,getHeight()-BASESIZE);
    shoulderX=shoulder.getX();
    shoulderY=shoulder.getY();
    shoulder.addVertex(0,0);
    shoulder.addEdge(SHOULDERSIZE/Math.sqrt(2),-SHOULDERSIZE/Math.sqrt(2));
    
    arm = new GPolygon(BASESIZE/2+SHOULDERSIZE/Math.sqrt(2),getHeight()-BASESIZE-SHOULDERSIZE/Math.sqrt(2));
    armX=arm.getX();
    armY=arm.getY();
    arm.addVertex(0,0);
    arm.addEdge(ARMSIZE,0);
    
    box = new GRect(0,getHeight()-BASESIZE,BASESIZE,getHeight());
    
    shoulderLabel = new GLabel("Shoulder Angle: ",50,10); 
    armLabel = new GLabel("Arm angle: ",50,30);
    
    distance = new GLabel("Distance: ",50,50);
    height = new GLabel("Height: ",50,70);
    
    add(shoulder);
    add(arm);
    add(box);
    
    add(distance);
    add(height);
    add(shoulderLabel);
    add(armLabel);
    
    updateLabels();
    
    addKeyListeners();
  }
  
  private double getXDist(){
    return (SHOULDERSIZE*Math.cos((180-shoulderAngle)*(Math.PI/180)));
  }
  
  private double getYDist(){
    return (SHOULDERSIZE*Math.sin((180-shoulderAngle)*(Math.PI/180)));
  }
  
  public void shoulderRotateR(double angle){
    if(shoulderAngle < angle){
      shoulder.rotate(-ANGLECHANGE);
      arm.rotate(-ANGLECHANGE);
      arm.setLocation(shoulder.getX()+getXDist(),shoulder.getY()-getYDist());
      shoulderAngle+=ANGLECHANGE;
    }
    else sRR = false;
  }
  
  public void shoulderRotateL(double angle){
    if(shoulderAngle > angle){
      shoulder.rotate(ANGLECHANGE);
      arm.rotate(ANGLECHANGE);
      arm.setLocation(shoulder.getX()+getXDist(),shoulder.getY()-getYDist());
      shoulderAngle-=ANGLECHANGE;
    }
    else sRL = false;
  }
  
  public void armRotateR(double angle){
    if(armAngle < angle){
      arm.rotate(-ANGLECHANGE);
      armAngle+=ANGLECHANGE;
    }
    else aRR = false;
  }
  
  public void armRotateL(double angle){
    if(armAngle > angle){
      arm.rotate(ANGLECHANGE);
      armAngle-=ANGLECHANGE;
    }
    else aRL = false;
  }
  
  private void oneTimeStep(){
    if(sRR){
      shoulderRotateR(180);
      updateLabels();
    }
    if(sRL){
      shoulderRotateL(90);
      updateLabels();
    }
    if(aRR){
      armRotateR(90);
      updateLabels();
    }
    if(aRL){
      armRotateL(0);
      updateLabels();
    }
  }
  
  public double getShoulderAngle(){
    return (180-shoulderAngle)*(Math.PI/180);
  }
  
  public double getArmAngle(){
    return (shoulderAngle+armAngle)*(Math.PI/180);
  }
  
  public double getL(double distance, double height){
    return Math.sqrt(distance*distance+height*height);
  }
  
  public double getTheta(double distance, double height){
    double lVal = getL(distance,height);
    double constants = lVal*lVal -(SHOULDERSIZE*SHOULDERSIZE + ARMSIZE*ARMSIZE);
    double cosTheta = constants/(-2*SHOULDERSIZE*ARMSIZE);
    double theta = Math.acos(cosTheta)*(180/Math.PI);
    return theta;
  }
  
  public double getThetaOne(double distance, double height){
    double lVal = getL(distance,height);
    double theta = getTheta(distance,height);
    double numerator = ARMSIZE*Math.sin(theta*(Math.PI/180));
    double thetaOne = Math.asin(numerator/lVal)*(180/Math.PI);
    return thetaOne;
  }
  
  public double getCalculatedShoulderAngle(double distance, double height){
    double thetaOne = getThetaOne(distance, height);
    double thetaH = Math.atan2(height,distance)*(180/Math.PI);
    double thetaS = 180 - thetaOne - thetaH;
    return thetaS;
  }
  
  public void updateLabels(){
    distance.setLabel("Distance: " + (SHOULDERSIZE*Math.cos(getShoulderAngle())-ARMSIZE*Math.cos(getArmAngle())));
    height.setLabel("Height: " + (SHOULDERSIZE*Math.sin(getShoulderAngle())+ARMSIZE*Math.sin(getArmAngle())));
    shoulderLabel.setLabel("Shoulder Angle: " + shoulderAngle);
    armLabel.setLabel("Arm Angle: " + armAngle);
  }
  
  public void goToSpot(double dist, double height){
    auto=!auto;
    autoDist = dist;
    autoHeight = height;
  }
  
  public void autoStep(double dist, double height){
    double shoulderMax = getCalculatedShoulderAngle(dist,height);
    double armMax = 180-getTheta(dist,height);
    
    if(!shoulderHit){
      if(shoulderMax > shoulderAngle) shoulderRotateR(shoulderMax);
      else shoulderRotateL(shoulderMax);
    }
    
    if(!armHit){
      if(armMax > armAngle) armRotateR(armMax);
      else armRotateL(armMax);
    }
    
    if(Math.abs(shoulderAngle - shoulderMax) < 0.1) shoulderHit = true;
    if(Math.abs(armAngle - (armMax)) < 0.1) armHit = true;
    
    if(shoulderHit && armHit){
      System.out.println("done");
      auto = false;
    }
    updateLabels();
  }
  
  //left: 37
  //right:39
  //up: 38
  //down: 40
  //space: 32
  public void keyPressed(KeyEvent e){
    if(e.getKeyCode()==39){
      sRR = true;
    }
    if(e.getKeyCode()==37){
      sRL = true;
    }
    if(e.getKeyCode()==38){
      aRL = true;
    }
    if(e.getKeyCode()==40){
      aRR = true;
    }
    if(e.getKeyCode()==32){          
      shoulderHit = false;
      armHit = false;
      goToSpot(250,60);
    }
  }
  
  public void keyReleased(KeyEvent e){
    if(e.getKeyCode()==39){ 
      sRR = false;
    }
    if(e.getKeyCode()==37){
      sRL = false;
    }
    if(e.getKeyCode()==38){
      aRL = false;
    }
    if(e.getKeyCode()==40){
      aRR = false;
    }
  }
  
  public void run(){
    while(true){
      oneTimeStep();
      if(auto) autoStep(autoDist,autoHeight);
      pause(1);
    }
  }
}