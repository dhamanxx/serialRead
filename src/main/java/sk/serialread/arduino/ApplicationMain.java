package sk.serialread.arduino;

import jssc.SerialPort;
import jssc.SerialPortList;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ApplicationMain extends JDialog implements ItemListener, ActionListener {

    private static final long serialVersionUID = 1L;

    SerialReaderService service = new SerialReaderService();

    private JComboBox cbPorts;
    private JComboBox baudRate;
    JButton btn;
    private String[] availablePorts;

    private List<Integer> baudRates = Arrays.asList(9600, 19200, 31250, 57600, 74880, 115200);

    JLabel lblL;
    JLabel lblP;
    Font csfont;
    //JTextArea errorLog;

    public ApplicationMain() {

        // load font
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("DSEG7Modern-Regular.ttf");
        try {
            csfont = Font.createFont(Font.PLAIN, stream).deriveFont(200);
        } catch (FontFormatException | IOException e) {
            //handle exeption
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(csfont);

        JFrame frame = new JFrame("Serial Reader...");
        service.setWindow(frame);

        // set frame site
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        errorLog = new JTextArea(10, 30);
//        service.setErrorLog(errorLog);
//        JScrollPane sp = new JScrollPane(errorLog);

        availablePorts = SerialPortList.getPortNames();

        List<String> sortedList = Arrays.stream(availablePorts).sorted((o1, o2) -> o1.compareTo(o2)).collect(Collectors.toList());
        System.out.println("=== Available Ports ===");
        for (String port : sortedList) {
            System.out.println(port);
        }
        System.out.println("==================");

        // create port selections
        JLabel l = new JLabel("Select serial port: ");
        btn = new JButton("Connect");
        btn.addActionListener(this);

        JLabel ll = new JLabel("Select speed: ");

        // create checkbox
        this.cbPorts = new JComboBox(sortedList.toArray());
        this.cbPorts.setPreferredSize(new Dimension(100, 25));
        this.cbPorts.addItemListener(this);

        this.baudRate = new JComboBox(baudRates.toArray());
        this.baudRate.setPreferredSize(new Dimension(100, 25));
        this.baudRate.setSelectedItem(9600);

        lblL = new JLabel("L  0:00:000", SwingConstants.CENTER);
        lblL.setFont(new Font("DSEG7 Modern", Font.PLAIN, 100));
        service.setLblL(lblL);
        JButton btn1 = new JButton("Copy L to clipboard");
        btn1.addActionListener(this);

        lblP = new JLabel("P  0:00:000", SwingConstants.CENTER);
        lblP.setFont(new Font("DSEG7 Modern", Font.PLAIN, 100));
        service.setLblP(lblP);
        JButton btn2 = new JButton("Copy P to clipboard");
        btn2.addActionListener(this);

        // create a new panel
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        p.add(ll, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        p.add(this.baudRate, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        p.add(l, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        p.add(this.cbPorts, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        p.add(btn, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        p.add(lblL, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 2;
        p.add(btn1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        p.add(lblP, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 3;
        p.add(btn2, c);

        frame.add(p);

        // display it
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(final String[] args) {

        new ApplicationMain();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String pName = e.getActionCommand();
        if (pName.equals("Connect")) {
            service.connect(this.cbPorts.getSelectedItem().toString(), (Integer) this.baudRate.getSelectedItem());
            btn.setText("Disconnect");
            this.cbPorts.setEnabled(false);
        }
        if (pName.equals("Disconnect")) {
            service.disconnect();
            btn.setText("Connect");
            this.cbPorts.setEnabled(true);
        }
        if (pName.equals("Copy L to clipboard")) {
            System.out.println(pName + " " + lblL.getText());
            StringSelection entry = new StringSelection(parseTime(lblL.getText()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(entry, entry);
        }
        if (pName.equals("Copy P to clipboard")) {
            System.out.println(pName + " " + lblP.getText());
            StringSelection entry = new StringSelection(parseTime(lblP.getText()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(entry, entry);
        }
    }

    private String parseTime(String text) {
        return text.substring(3, text.length());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // if the state combobox is changed
        if (e.getSource() == this.cbPorts) {
            //System.out.println(this.cbPorts.getSelectedItem() + " selected");
        }
    }

    public static void addTo(JTextArea txtField) {
        JPopupMenu popup = new JPopupMenu();
        Action copyAction = new AbstractAction("Copy Ctrl+C") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                txtField.copy();
            }
        };

        Action cutAction = new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                txtField.cut();
            }
        };

        Action pasteAction = new AbstractAction("Paste Ctrl+V") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                txtField.paste();
            }
        };

        Action selectAllAction = new AbstractAction("Select All Ctrl+A") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                txtField.selectAll();
            }
        };

        popup.add(copyAction);
        popup.addSeparator();
        popup.add(selectAllAction);

        txtField.setComponentPopupMenu(popup);
    }
}