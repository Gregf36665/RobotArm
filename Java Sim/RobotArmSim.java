import acm.graphics.*;
import acm.program.*;
import java.awt.event.*;

public class RobotArmSim extends GraphicsProgram{
  
  public static final int BASESIZE=100;
  public static final int SHOULDERSIZE=250;
  public static final int ARMSIZE=175;
  public static final double ANGLECHANGE=0.1;
  
  public double shoulderX,shoulderY;
  public double armX, armY;
  
  public double shoulderAngle=135.0, armAngle = 45.0;
  
  public boolean shoulderRRotate = false, shoulderLRotate = false;
  public boolean armRRotate = false, armLRotate = false;
  
  public GPolygon shoulder,arm;
  public GRect box;
  
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
    
    add(shoulder);
    
    add(arm);
    
    add(box);
    
    addKeyListeners();
  }
  
  public double getXDist(){
    return (SHOULDERSIZE*Math.cos((180-shoulderAngle)*(Math.PI/180)));
  }
  
  public double getYDist(){
    return (SHOULDERSIZE*Math.sin((180-shoulderAngle)*(Math.PI/180)));
  }
  
  public void oneTimeStep(){
    if(shoulderRRotate &&shoulderAngle < 170){
      shoulder.rotate(-ANGLECHANGE);
      arm.rotate(-ANGLECHANGE);
      arm.setLocation(shoulder.getX()+getXDist(),shoulder.getY()-getYDist());
      shoulderAngle+=ANGLECHANGE;
    }
    if(shoulderLRotate && shoulderAngle > 100){
      shoulder.rotate(ANGLECHANGE);
      arm.rotate(ANGLECHANGE);
      arm.setLocation(shoulder.getX()+getXDist(),shoulder.getY()-getYDist());
      shoulderAngle-=ANGLECHANGE;
    }
    if(armRRotate && armAngle < 90){
      arm.rotate(-ANGLECHANGE);
      armAngle+=ANGLECHANGE;
    }
    if(armLRotate && armAngle > 0){
      arm.rotate(ANGLECHANGE);
      armAngle-=ANGLECHANGE;
    }
  }
  
  //left: 37
  //right:39
  //up: 38
  //down: 40
  //space: 32
  public void keyPressed(KeyEvent e){
    if(e.getKeyCode()==39){
      shoulderRRotate = true;
    }
    if(e.getKeyCode()==37){
      shoulderLRotate = true;
    }
    if(e.getKeyCode()==38){
      armLRotate = true;
    }
    if(e.getKeyCode()==40){
      armRRotate = true;
    }
  }
  
  public void keyReleased(KeyEvent e){
    if(e.getKeyCode()==39){ 
      shoulderRRotate = false;
    }
    if(e.getKeyCode()==37){
      shoulderLRotate = false;
    }
    if(e.getKeyCode()==38){
      armLRotate = false;
    }
    if(e.getKeyCode()==40){
      armRRotate = false;
    }
  }
  
    public void run(){
    while(true){
      oneTimeStep();
      pause(5);
    }
  }
}