package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author ming
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * Initialize the directory
     * If the directory is already initialized, show the error message and exit
     */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            String msg = "A Gitlet version-control system already exists in the current directory.";
            Utils.message(msg);
            System.exit(0);
        }
        GITLET_DIR.mkdir();
    }
}
