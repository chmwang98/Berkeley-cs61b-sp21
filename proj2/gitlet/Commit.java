package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Repository.OBJECT_DIR;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ming
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date currentTime;
    private String timeStamp;
    private Map<String, String> mapFilePathToBlobID;
    private List<String> parents;
    private String id;
    private File commitFile;

    public Commit() {
        message = "initial commit";
        currentTime = new Date(0);
        timeStamp = generateTimeStamp();
        mapFilePathToBlobID = new TreeMap<>();
        parents = new ArrayList<>();
        id = generateID();
        commitFile = generateCommitFile();
    }

    public Commit(String message, Map<String, String> mapFilePathToBlobID, List<String> parents) {
        this.message = message;
        currentTime = new Date();
        timeStamp = generateTimeStamp();
        this.mapFilePathToBlobID = mapFilePathToBlobID;
        this.parents = parents;
        id = generateID();
        commitFile = generateCommitFile();
    }

    public String generateTimeStamp() {
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss zzz, EEEE, d MMM, yyyy");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy ZZZZ");
        return dateFormat.format(currentTime);
    }

    private String generateID() {
        return Utils.sha1(message, timeStamp, mapFilePathToBlobID.toString(), parents.toString());
    }

    private File generateCommitFile() {
        return join(OBJECT_DIR, id);
    }

    public void save() {
        writeObject(commitFile, this);
    }

    public boolean isFilePathInCommit(String filePath) {
        return mapFilePathToBlobID.containsKey(filePath);
    }

    public boolean isBlobInCommit(Blob blob) {
        return mapFilePathToBlobID.containsValue(blob.getID());
    }

    public String getBlobIDByFilePath(String filePath) {
        return mapFilePathToBlobID.get(filePath);
    }

    public String getID() {
        return id;
    }

    public Map<String, String> getMapFilePathToBlobID() {
        return mapFilePathToBlobID;
    }

    public List<String> getParentsID() {
        return parents;
    }

    public void printCommit() {
        System.out.println("===");
        System.out.println("commit " + id);
        if (isMerged()){
            System.out.println("Merge:" + parents.get(0).substring(0, 7) + parents.get(1).substring(0, 7));
        }
        System.out.println("Date: " + timeStamp);
        System.out.println(message);
        System.out.println();
    }

    private boolean isMerged() {
        return parents.size() > 1;
    }

    public List<String> getFilePaths() {
        List<String> filePaths = new ArrayList<>();
        for (String filePath : mapFilePathToBlobID.keySet()) {
            filePaths.add(filePath);
        }
        return filePaths;
    }
}
