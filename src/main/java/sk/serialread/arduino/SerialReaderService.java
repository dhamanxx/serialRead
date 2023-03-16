package sk.serialread.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.UnsupportedEncodingException;
import javax.swing.JLabel;

public class SerialReaderService implements SerialPortDataListener {

    int baudRate = 9600;
    int dataBits = 8;
    int stopBits = SerialPort.ONE_STOP_BIT;
    int parity = SerialPort.NO_PARITY;
    private SerialPort activePort;
    private JLabel lblL;
    private JLabel lblP;

    public void connect(SerialPort port) {
        this.activePort = port;
        this.activePort.setComPortParameters(baudRate, dataBits, stopBits, parity); // default connection settings for Arduino
        this.activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        if (this.activePort.openPort()) {
            System.out.println(activePort.getSystemPortName() + ": Port is open :)");
            this.activePort.addDataListener(this);
        } else {
            System.out.println(activePort.getSystemPortName() + ": Failed to open port :(");
            return;
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
        int size = event.getSerialPort().bytesAvailable();
        byte[] buffer = new byte[size];
        int numRead = this.activePort.readBytes(buffer, size);
        System.out.print("Read " + numRead + " bytes -");

        //Convert bytes to String
        String S = null;
        try {
            S = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // TODO: read vales from SerialPort and fill lblL, lblP
        System.out.println("Received -> " + S);
    }
}
