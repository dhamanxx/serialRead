package sk.serialread.arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;

import static jssc.SerialPort.MASK_RXCHAR;

public class SerialReaderService implements SerialPortEventListener {

    int dataBits = SerialPort.DATABITS_8;
    int stopBits = SerialPort.STOPBITS_1;
    int parity = SerialPort.PARITY_NONE;
    private SerialPort activePort;
    private String serialPort;
    private JLabel lblL;
    private JLabel lblP;
    //    private JTextArea errorLog;
    private JFrame window;
    private StringBuilder message = new StringBuilder();

    public void connect(String port, int baudRate) {
        this.serialPort = port;
        try {
            activePort = new SerialPort(port);
            activePort.openPort();
            activePort.setParams(baudRate, dataBits, stopBits, parity); // default connection settings for Arduino
            activePort.setEventsMask(MASK_RXCHAR);
            activePort.addEventListener(this);
        } catch (SerialPortException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public SerialPort getActivePort() {
        return activePort;
    }

    public void disconnect() {
        try {
            activePort.closePort();
            System.out.println(serialPort + ": Port is closed :)");
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLblL(JLabel lblL) {
        this.lblL = lblL;
    }

    public void setLblP(JLabel lblP) {
        this.lblP = lblP;
    }


    public void setWindow(JFrame window) {
        this.window = window;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                byte buffer[] = activePort.readBytes();
                for (byte b : buffer) {
                    if ((b == '\r' || b == '\n') && message.length() > 0) {
                        String inputLine = message.toString();
//                        System.out.println(inputLine);
                        String[] split = inputLine.split(",");
                        if (split.length == 2) {
                            String[] splitL = split[0].split(":");
                            String[] splitP = split[1].split(":");
                            if (splitL.length == 3) {
                                String mmL = splitL[1];
                                if (mmL.length() == 1) {
                                    mmL = "0" + mmL;
                                }
                                String msL = splitL[2];
                                if (msL.length() == 1) {
                                    msL = "00" + msL;
                                }
                                if (msL.length() == 2) {
                                    msL = "0" + msL;
                                }
                                lblL.setText("L  " + splitL[0] + ":" + mmL + ":" + msL);
                            }
                            if (splitP.length == 3) {
                                String mmP = splitL[1];
                                if (mmP.length() == 1) {
                                    mmP = "0" + mmP;
                                }
                                String msP = splitL[2];
                                if (msP.length() == 1) {
                                    msP = "00" + msP;
                                }
                                if (msP.length() == 2) {
                                    msP = "0" + msP;
                                }
                                lblP.setText("P  " + splitP[0] + ":" + mmP + ":" + msP);
                            }
                            window.revalidate();
                            window.repaint();
                        }
                        message.setLength(0);
                    } else {
                        if (b != '\n') {
                            message.append((char) b);
                        }
                    }
                }
            } catch (SerialPortException ex) {
                System.out.println(ex);
                System.out.println("serialEvent");
            }
        }
    }
}
