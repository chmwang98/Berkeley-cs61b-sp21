package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.writeObject;

public class Stage implements Serializable {
    private Map<String, String> mapFileNameToBlobID;
    private File STAGE_FILE;

    public Stage(File file) {
        mapFileNameToBlobID = new TreeMap<>();
        STAGE_FILE = file;
    }

    public void addBlobToStage(Blob blob) {
        mapFileNameToBlobID.put(blob.getFileName(), blob.getID());
    }

    public void addFileNameAndBlobIDToStage(String fileName, String blobID) {
        mapFileNameToBlobID.put(fileName, blobID);
    }

    public void removeFileNameFromStage(String fileName) {
        mapFileNameToBlobID.remove(fileName);
    }

    public void removeBlobFromStage(Blob blob) {
        mapFileNameToBlobID.remove(blob.getFileName());
    }

    public void saveStage() {
        writeObject(STAGE_FILE, this);
    }

    public boolean isFileNameInStage(String fileName) {
        return mapFileNameToBlobID.containsKey(fileName);
    }

    public boolean isBlobInStage(Blob blob) {
        return mapFileNameToBlobID.containsValue(blob.getID());
    }

    public Map<String, String> getMapFileNameToBlobID() {
        return mapFileNameToBlobID;
    }

    public void clear() {
        mapFileNameToBlobID.clear();
    }

    public boolean isEmpty() {
        return mapFileNameToBlobID.size() == 0;
    }
}
