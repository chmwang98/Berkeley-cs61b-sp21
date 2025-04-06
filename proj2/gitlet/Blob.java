package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
import static gitlet.Repository.OBJECT_DIR;

public class Blob implements Serializable {
    // name, path and content of the file to be converted to blob
    private File file;
    private String filePath;
    private String content;

    private String id;
    private File blobFile;

    public Blob(File file) {
        this.file = file;
        filePath = file.getPath();
        content = readContentsAsString(file);
        id = generateID();
        blobFile = generateBlobFile();
    }

    public String getFileName() {
        return file.getName();
    }

    public String getContent() {
        return content;
    }

    public String getBlobID() {
        return id;
    }

    private String generateID() {
        return sha1(filePath, content);
    }

    private File generateBlobFile() {
        return join(OBJECT_DIR, id);
    }

    public void save() {
        writeObject(blobFile, this);
    }
}
