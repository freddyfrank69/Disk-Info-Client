import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Main {
    private JFrame frame;
    private JTextField driveTextField;
    private JTextArea infoTextArea;

    public Main() {
        frame = new JFrame("Disk Info Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLayout(new FlowLayout());

        driveTextField = new JTextField(5);
        infoTextArea = new JTextArea(15, 35);
        infoTextArea.setEditable(false);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String driveLetter = driveTextField.getText();
                if (driveLetter.equalsIgnoreCase("All")) {
                    getAllDiskInfo();
                } else if (driveLetter.length() == 1) {
                    getInfoForDrive(driveLetter);
                } else {
                    infoTextArea.setText("Invalid input. Enter a single drive letter or 'All'.");
                }
            }
        });

        frame.add(new JLabel("Enter Drive Letter (or 'All'): "));
        frame.add(driveTextField);
        frame.add(searchButton);
        frame.add(new JLabel("Disk Information: "));
        frame.add(new JScrollPane(infoTextArea));
        frame.setVisible(true);
    }

    private void getInfoForDrive(String driveLetter) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(driveLetter);

            DiskInfo diskInfo = (DiskInfo) in.readObject();

            if (diskInfo != null) {
                displayDiskInfo(diskInfo);
            } else {
                infoTextArea.setText("Disk not found.");
            }

            socket.close();
            out.close();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static long sectorSize = 512; // Typical sector size in bytes

    private void getAllDiskInfo() {
        File[] roots = File.listRoots();
        StringBuilder infoText = new StringBuilder();

        for (File root : roots) {
            String driveLetter = root.getPath().substring(0, 1);
            DiskInfo diskInfo = getDiskInfoForDrive(driveLetter);
            if (diskInfo != null) {
                // Convert bytes to gigabytes (1 GB = 1024^3 bytes)
                double totalSpaceGB = diskInfo.getTotalSpace() / Math.pow(1024, 3);
                double usedSpaceGB = diskInfo.getUsedSpace() / Math.pow(1024, 3);
                double availableSpaceGB = diskInfo.getAvailableSpace() / Math.pow(1024, 3);

                infoText.append("All Drive: \n");
                infoText.append("Drive ").append(driveLetter).append(" \nSerial Number: ").append(diskInfo.getSerialNumber()).append("\n");
                infoText.append("Disk Name: ").append(diskInfo.getDiskName()).append("\n");
                infoText.append("File System: ").append(diskInfo.getFileSystem()).append("\n");
                infoText.append("Total Space: ").append(String.format("%.2f", totalSpaceGB)).append(" GB\n");
                infoText.append("Used Space: ").append(String.format("%.2f", usedSpaceGB)).append(" GB\n");
                infoText.append("Available Space: ").append(String.format("%.2f", availableSpaceGB)).append(" GB\n");
                infoText.append("Bytes per Sector: ").append(diskInfo.getTotalSpace() / sectorSize).append("\n");
                infoText.append("Sectors per Cluster: ").append(diskInfo.getAvailableSpace() / sectorSize).append("\n\n");
            } else {
                infoText.append(driveLetter).append(" - Not found\n\n");
            }
        }

        infoTextArea.setText(infoText.toString());
    }

    private DiskInfo getDiskInfoForDrive(String driveLetter) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(driveLetter);

            DiskInfo diskInfo = (DiskInfo) in.readObject();

            socket.close();
            out.close();
            in.close();

            return diskInfo;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayDiskInfo(DiskInfo diskInfo) {
        // Convert bytes to gigabytes (1 GB = 1024^3 bytes)
        double totalSpaceGB = diskInfo.getTotalSpace() / Math.pow(1024, 3);
        double usedSpaceGB = diskInfo.getUsedSpace() / Math.pow(1024, 3);
        double availableSpaceGB = diskInfo.getAvailableSpace() / Math.pow(1024, 3);

        String infoText = "Disk Name: " + diskInfo.getDiskName() + "\n" +
                "Serial Number: " + diskInfo.getSerialNumber() + "\n" +
                "File System: " + diskInfo.getFileSystem() + "\n" +
                "Total Space: " + String.format("%.2f", totalSpaceGB) + " GB\n" +
                "Used Space: " + String.format("%.2f", usedSpaceGB) + " GB\n" +
                "Available Space: " + String.format("%.2f", availableSpaceGB) + " GB\n" +
                "Bytes per Sector: " + diskInfo.getTotalSpace() / sectorSize + "\n" +
                "Sectors per Cluster: " + diskInfo.getAvailableSpace() / sectorSize;
        infoTextArea.setText(infoText);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
