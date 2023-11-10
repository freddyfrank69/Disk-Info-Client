import java.io.*;
import java.net.*;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.Files;


public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is running. Waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    String driveLetter = (String) in.readObject();
                    String diskName = driveLetter + ":";
                    File file = new File(diskName);

                    if (file.exists()) {
                        String serialNumber = getSerialKey(driveLetter.charAt(0));
                        String fileSystem = getFilesystem(driveLetter); // Get file system information
                        long totalSpace = file.getTotalSpace();
                        long usedSpace = file.getTotalSpace() - file.getFreeSpace();
                        long availableSpace = file.getFreeSpace();
                        DiskInfo diskInfo = new DiskInfo(diskName, serialNumber, fileSystem, totalSpace, usedSpace, availableSpace);
                        out.writeObject(diskInfo);
                    } else {
                        out.writeObject(null);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSerialKey(char driveLetter) {
        try {
            String line;
            String serial = null;
            Process process = Runtime.getRuntime().exec("cmd /c vol " + driveLetter + ":");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("serial number")) {
                    String[] strings = line.split(" ");
                    serial = strings[strings.length - 1];
                }
            }
            in.close();
            return serial;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFilesystem(String driveLetter) {
        File drive = new File(driveLetter + ":\\");
        String fileSystem = "Unknown";

        if (drive.exists()) {
            try {
                Path path = drive.toPath();
                FileStore store = Files.getFileStore(path);

                fileSystem = store.type();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileSystem;
    }
}
