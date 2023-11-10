import java.io.Serializable;

public class DiskInfo implements Serializable {
    private String diskName;
    private String serialNumber;
    private String fileSystem; // Add this field for file system information
    private long totalSpace;
    private long usedSpace;
    private long availableSpace;
    private static final long serialVersionUID = 1L;

    public DiskInfo(String diskName, String serialNumber, String fileSystem, long totalSpace, long usedSpace, long availableSpace) {
        this.diskName = diskName;
        this.serialNumber = serialNumber;
        this.fileSystem = fileSystem;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
        this.availableSpace = availableSpace;
    }

    public String getDiskName() {
        return diskName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public long getAvailableSpace() {
        return availableSpace;
    }
}
