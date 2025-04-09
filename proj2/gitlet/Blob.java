package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class Blob implements Serializable {
    // name, Name and content of the file to be converted to blob
    private File file;
    private String fileName;
    private String content;

    private String id;
    private File blobFile;

    public Blob(File file) {
        this.file = file;
        fileName = file.getName();
        content = readContentsAsString(file);
        id = generateID();
        blobFile = generateBlobFile();
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public String getID() {
        return id;
    }

    private String generateID() {
        return sha1(fileName, content);
    }

    private File generateBlobFile() {
        return join(OBJECT_DIR, id);
    }

    public void storeBlob() {
        if (blobFile.exists()) {
            return;
        }
        writeObject(blobFile, this);
    }

    public void writeBlobToCWD() {
        writeContents(file, content);
    }
}
