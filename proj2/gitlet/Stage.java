package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.writeObject;

public class Stage implements Serializable {
    private Map<String, String> mapFilePathToBlobID;
    private File STAGE_FILE;

    public Stage(File file) {
        mapFilePathToBlobID = new TreeMap<>();
        STAGE_FILE = file;
    }

    public void addBlobToStage(Blob blob) {
        mapFilePathToBlobID.put(blob.getFilePath(), blob.getID());
    }

    public void addFilePathAndBlobIDToStage(String filePath, String blobID) {
        mapFilePathToBlobID.put(filePath, blobID);
    }

    public void removeFilePathFromStage(String filePath) {
        mapFilePathToBlobID.remove(filePath);
    }

    public void removeBlobFromStage(Blob blob) {
        mapFilePathToBlobID.remove(blob.getFilePath());
    }

    public void saveStage() {
        writeObject(STAGE_FILE, this);
    }

    public boolean isFilePathInStage(String filePath) {
        return mapFilePathToBlobID.containsKey(filePath);
    }

    public boolean isBlobInStage(Blob blob) {
        return mapFilePathToBlobID.containsValue(blob.getID());
    }

    public Map<String, String> getMapFilePathToBlobID() {
        return mapFilePathToBlobID;
    }

    public void clear() {
        mapFilePathToBlobID.clear();
    }
}
