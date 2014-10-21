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
		String waiting = "Waiting...";
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

		while (!Button.ESCAPE.isDown()) {
			int angle = motor.getTachoCount();
			int distance = us.getDistance();
			
			//TODO: Find a better solution so we don't get a stop every round.
			if (angle >= 360) {
				motor.resetTachoCount();
				motor.forward();
			}

			try {
				dos.writeInt(angle);
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
