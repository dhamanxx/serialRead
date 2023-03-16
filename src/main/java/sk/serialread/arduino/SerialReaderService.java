package sk.serialread.arduino;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.JLabel;

public class SerialReaderService {

    private SerialPort activePort;
    private JLabel lblL;
    private JLabel lblP;

    public void connect(SerialPort port) {
        this.activePort = port;
        this.activePort.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
        this.activePort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written

        if (this.activePort.openPort()) {
            System.out.println(activePort.getSystemPortName() + ": Port is open :)");
        } else {
            System.out.println(activePort.getSystemPortName() + ": Failed to open port :(");
            return;
        }
    }

    public void readSerialPort() {
        // TODO: read vales from SerialPort and fill lblL, lblP

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
}
