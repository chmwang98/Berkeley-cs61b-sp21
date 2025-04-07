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
        // if blob is already in currentStage, skip it
        if (isBlobInStage(blob)) {
            return;
        }
        mapFileNameToBlobID.put(blob.getFilePath(), blob.getID());
        saveStage();
    }

    public void saveStage() {
        writeObject(STAGE_FILE, this);
    }

    public boolean isBlobInStage(Blob blob) {
        return mapFileNameToBlobID.containsValue(blob.getID());
    }
}
