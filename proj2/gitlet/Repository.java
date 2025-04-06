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
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit that contains no files and has the commit message.
     * It will have a single branch: master, which initially points to this initial commit.
     * TODO: Master will be the current branch.
     * The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970.
     * Since the initial commit in all repositories created by Gitlet will have exactly the same content,
     * it follows that all repositories will automatically share this commit (they will all have the same UID)
     * and all commits in all repositories will trace back to it.
     * */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            String msg = "A Gitlet version-control system already exists in the current directory.";
            Utils.message(msg);
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        Commit initialCommit = new Commit();
        initialCommit.save();
    }
}
