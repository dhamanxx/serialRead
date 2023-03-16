package sk.serialread.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.swing.*;

public class SerialReaderService implements SerialPortDataListener {

    int baudRate = 9600;
    int dataBits = 8;
    int stopBits = SerialPort.ONE_STOP_BIT;
    int parity = SerialPort.NO_PARITY;
    private SerialPort activePort;
    private JLabel lblL;
    private JLabel lblP;
    private JTextArea errorLog;
    BufferedReader br;

    public void connect(SerialPort port) {
        this.activePort = port;
        this.activePort.setComPortParameters(baudRate, dataBits, stopBits, parity); // default connection settings for Arduino
        this.activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        if (this.activePort.openPort()) {
            System.out.println(activePort.getSystemPortName() + ": Port is open :)");
            InputStream inputStream = port.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));
            this.activePort.addDataListener(this);
        } else {
            System.out.println(activePort.getSystemPortName() + ": Failed to open port :(");
            return;
        }
    }

    public void addErrorLog(String text) {
        System.out.println(text);
        if (errorLog != null) {
            errorLog.append(text + "\n");
            errorLog.setCaretPosition(errorLog.getDocument().getLength());
        }
    }

    public SerialPort getActivePort() {
        return activePort;
    }

    public void disconnect() {
        activePort.closePort();
        System.out.println(activePort.getSystemPortName() + ": Port is closed :)");
    }

    public void setLblL(JLabel lblL) {
        this.lblL = lblL;
    }

    public void setLblP(JLabel lblP) {
        this.lblP = lblP;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            String inputLine = br.readLine();
            addErrorLog("Received -> " + inputLine);
            String[] split = inputLine.split(",");
            if (split.length == 2) {
                lblL.setText("L  " + (split[0].length() > 7 ? split[0].substring(0, 7) : split[0]));
                lblP.setText("P  " + (split[1].length() > 7 ? split[1].substring(0, 7) : split[1]));
            }
        } catch (Exception ex) {
            addErrorLog(ex.toString());
        }
    }

    public void setErrorLog(JTextArea errorLog) {
        this.errorLog = errorLog;
    }
}
