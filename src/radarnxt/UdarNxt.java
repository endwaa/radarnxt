package radarnxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class UdarNxt {
	private NXTRegulatedMotor motor = Motor.A;
	private UltrasonicSensor us;
	private BTConnection btc;
	private DataInputStream dis;
	private DataOutputStream dos;

	public UdarNxt() {
		String connected = "Connected";
		String waiting = "Waiting..!";
		us = new UltrasonicSensor(SensorPort.S1);
		LCD.drawString(waiting, 0, 0);
		LCD.refresh();

		btc = Bluetooth.waitForConnection();

		LCD.clear();
		LCD.drawString(connected, 0, 0);
		LCD.refresh();

		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
		motor.setSpeed(100);
		motor.forward();
		int rounds  = 0;
		int numberToSubtract = 0;
		int angle;
		int distance;
		while (!Button.ESCAPE.isDown()) {
			angle = motor.getTachoCount();
			distance = us.getDistance();
			numberToSubtract = rounds * 360;
			
			if (angle - numberToSubtract >= 360) {
				rounds++;
			}

			LCD.refresh();
			LCD.drawInt(rounds, 1, 1);
			
			try {
				dos.writeInt(angle - numberToSubtract);
				dos.writeInt(distance);
				dos.flush();
			} catch (IOException e) {
				motor.flt();
				e.printStackTrace();
			}
		}

		try {
			closeConnection();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void closeConnection() throws IOException, InterruptedException {
		dis.close();
		dos.close();
		Thread.sleep(100);
		LCD.clear();
		LCD.drawString("Closing", 0, 0);
		LCD.refresh();
		btc.close();
		LCD.clear();

	}

	public static void main(String[] args) throws Exception {
		new UdarNxt();
	}
}
