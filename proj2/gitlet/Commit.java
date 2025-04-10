package gitlet;

import java.io.Serializable;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;


/** Represents a gitlet commit object.
 *
 *  @author ming
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date currentTime;
    private String timeStamp;
    private Map<String, String> mapFileNameToBlobID;
    private List<String> parents;
    private String id;
    private File commitFile;

    public Commit() {
        message = "initial commit";
        currentTime = new Date(0);
        timeStamp = generateTimeStamp();
        mapFileNameToBlobID = new TreeMap<>();
        parents = new ArrayList<>();
        id = generateID();
        commitFile = generateCommitFile();
    }

    public Commit(String message, Map<String, String> mapFileNameToBlobID, List<String> parents) {
        this.message = message;
        currentTime = new Date();
        timeStamp = generateTimeStamp();
        this.mapFileNameToBlobID = mapFileNameToBlobID;
        this.parents = parents;
        id = generateID();
        commitFile = generateCommitFile();
    }

    public String generateTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy ZZZZ");
        return dateFormat.format(currentTime);
    }

    private String generateID() {
        return Utils.sha1(message, timeStamp, mapFileNameToBlobID.toString(), parents.toString());
    }

    private File generateCommitFile() {
        return join(OBJECT_DIR, id);
    }

    public void save() {
        writeObject(commitFile, this);
    }

    public boolean isFileNameInCommit(String fileName) {
        return mapFileNameToBlobID.containsKey(fileName);
    }

    public boolean isBlobInCommit(Blob blob) {
        return mapFileNameToBlobID.containsValue(blob.getID());
    }

    public String getBlobIDByFileName(String fileName) {
        return mapFileNameToBlobID.get(fileName);
    }

    public String getID() {
        return id;
    }

    public Map<String, String> getMapFileNameToBlobID() {
        return mapFileNameToBlobID;
    }

    public List<String> getParentsID() {
        return parents;
    }

    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + id);
        if (isMerged()) {
            String s = "Merge:" + parents.get(0).substring(0, 7) + parents.get(1).substring(0, 7);
            System.out.println(s);
        }
        System.out.println("Date: " + timeStamp);
        System.out.println(message);
        System.out.println();
    }

    private boolean isMerged() {
        return parents.size() > 1;
    }

    public List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        for (String fileName : mapFileNameToBlobID.keySet()) {
            fileNames.add(fileName);
        }
        return fileNames;
    }

    public List<String> getBlobIDs() {
        List<String> blobIDs = new ArrayList<>();
        for (String blobID : mapFileNameToBlobID.values()) {
            blobIDs.add(blobID);
        }
        return blobIDs;
    }

    public String getMessage() {
        return message;
    }
}
