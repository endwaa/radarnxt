package radarnxt;



import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


public class Radar {


int totalRotation = 170*5;
int scanDensity = 25;
UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S4);
File distanceFile = new File("Distances.txt");
FileOutputStream fileStream = null;

public static void main(String[] args) {
    @SuppressWarnings("unused")
    Radar usD = new Radar();
}

public Radar() {
    Motor.A.setAcceleration(2000);
    Motor.A.setSpeed(300);   

    try {
        fileStream = new FileOutputStream(distanceFile);
    } catch (Exception e) {
        LCD.drawString("Can't make a file", 0, 0);
        Button.ESCAPE.waitForPress();
        System.exit(1);
    }
    
    DataOutputStream dataStream = new DataOutputStream(fileStream);

    Motor.A.rotate(90*5);
    Motor.A.resetTachoCount();
    Motor.A.backward();

    do {
        if (-(Motor.A.getTachoCount()) % scanDensity == 0 ) {
            int distance = ultrasonicSensor.getDistance();
            LCD.drawInt(distance, 0, 3);
            try {
                dataStream.writeBytes(String.valueOf(Motor.A.getTachoCount()/5) + ";" +String.valueOf(distance) + "\n");
                fileStream.flush();
            	
            } catch (IOException e) {
                LCD.drawString("Can't write to the file", 0, 1);
                Button.ESCAPE.waitForPress();
                System.exit(1);
            }
        }
    } while (-(Motor.A.getTachoCount()) < totalRotation);
    
    Motor.A.stop();

    try {
        fileStream.close();
    } catch (IOException e) {
        LCD.drawString("Can't save the file", 0, 1);
        Button.ESCAPE.waitForPress();
        System.exit(1);
    }

    Motor.A.rotate(95*5);
}  
}