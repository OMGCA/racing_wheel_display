import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class DisplayMain {

    static HashMap<Character, String> typeMap = new HashMap<>();


    public static void main(String[] args) {
        int port = 20777;
        typeMap.put('0', "Motion");
        typeMap.put('1', "Session");
        typeMap.put('2', "Lap Data");
        typeMap.put('3', "Event");
        typeMap.put('4', "Participants");
        typeMap.put('5', "Car Setups");
        typeMap.put('6', "Car Telemetry");
        typeMap.put('7', "Car Status");
        typeMap.put('8', "Final Classification");
        typeMap.put('9', "LobbyInfo");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        frame.setTitle("F1 2020 Display");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel speed = new JLabel("Waiting UDP packet");
        //JLabel gear = new JLabel("Waiting UDP packet");
        frame.add(speed);
        //frame.add(gear);
        frame.setVisible(true);

        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[1500];

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            while(true)
            {
                serverSocket.receive(receivePacket);
                String packet = bytesToHex(receiveData);
                if (getPacketType(packet).equals("Car Telemetry")) {
                    //Speed:48 49 50 51
                    String speedHex1 = packet.substring(48, 50);
                    String speedHex2 = packet.substring(50, 52);
                    //Gear: 78 79
                    String gearHex = packet.substring(78, 80);
                    long speedDec = Long.parseLong(speedHex2 + speedHex1, 16);
                    long gearDec = Long.parseLong(gearHex, 16);
                    speed.setText(String.valueOf(speedDec) + " " + gearDec);
                    panel.updateUI();
                }
                System.out.println("Packet type: " + getPacketType(packet));

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getPacketType(String strPacket) {
        char packetTypeIdx = strPacket.charAt(11);

        return typeMap.get(packetTypeIdx);
    }
}
