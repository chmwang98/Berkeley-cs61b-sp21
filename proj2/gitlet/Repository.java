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

    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    public static Commit currentCommit;
    public static Stage currentStage = new Stage();
    public static String currentBranch;

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
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();

        Commit initialCommit = new Commit();
        currentCommit = initialCommit;
        initialCommit.save();

        writeContents(HEAD_FILE, "master");
        File HEADS_FILE = join(HEADS_DIR, "master");
        writeContents(HEADS_FILE, currentCommit.getID());
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     * The staging area should be somewhere in .gitlet.
     * If the current working version of the file is identical to the version in the current commit,
     * do not currentStage it to be added, and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back to it’s original version).
     * The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     * */
    public static void addCommand(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        // create a blob for the file
        Blob blob = new Blob(file);
        currentCommit = readCurrentCommit();
        currentStage = readStage();
        // if the blob in neither in the current commit, nor in the current stage, add it to currentStage
        if (!currentCommit.isBlobInCommit(blob) && !currentStage.isBlobInStage(blob)) {
            currentStage.addBlobToStage(blob);
        }
    }

    private static Commit readCurrentCommit() {
        String currentCommitID = readCurrentCommitID();
        File CURR_COMMIT_FILE = join(OBJECT_DIR, currentCommitID);
        return readObject(CURR_COMMIT_FILE, Commit.class);
    }

    private static String readCurrentCommitID() {
        String currentBranch = readCurrentBranch();
        return readContentsAsString(join(HEADS_DIR, currentBranch));
    }

    // the current branch is saved in HEAD, e.g. 'refs/heads/master'
    private static String readCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    // read the content in currentStage file or create a new currentStage if empty
    private static Stage readStage() {
        if (!STAGE_FILE.exists()) {
            return new Stage();
        }
        return readObject(STAGE_FILE, Stage.class);
    }

    // Commands other than 'init' have to be executed in initialized directory
    public static void checkInitialized() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
