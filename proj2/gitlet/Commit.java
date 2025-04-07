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
    private Map<String, String> mapFileNameToBlobID;
    private List<String> parents;
    private String id;
    private File commitFile;

    public Commit() {
        message = "initial commit";
        currentTime = new Date(0);
        timeStamp = generateTimeStamp();
        mapFileNameToBlobID = new HashMap<>();
        parents = new ArrayList<>();
        id = generateID();
        commitFile = generateCommitFile();
    }

    public String generateTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss zzz, EEEE, d MMM, yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    public boolean isBlobInCommit(Blob blob) {
        return mapFileNameToBlobID.containsValue(blob.getID());
    }

    public String getID() {
        return id;
    }
}
