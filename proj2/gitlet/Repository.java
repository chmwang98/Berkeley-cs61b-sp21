package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

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

    public static final File ADDSTAGE_FILE = join(GITLET_DIR, "add_stage");
    public static final File REMOVESTAGE_FILE = join(GITLET_DIR, "remove_stage");

    public static Commit currentCommit;
    public static Stage addStage = new Stage(ADDSTAGE_FILE);
    public static Stage removeStage = new Stage(REMOVESTAGE_FILE);
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
            printErrorAndExit("A Gitlet version-control system already exists in the current directory.");
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
        String filePath = file.getPath();
        if (!file.exists()) {
            printErrorAndExit("File does not exist.");
        }
        // create a blob for the file
        Blob blob = new Blob(file);
        blob.storeBlob();
        currentCommit = readBranchCommit(readCurrentBranch());
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);
        // if the blob in neither in the current commit, nor in the current stage, add it to currentStage
        if (!currentCommit.isBlobInCommit(blob) && !addStage.isBlobInStage(blob)) {
            addStage.addBlobToStage(blob);
            addStage.saveStage();
        } else if (currentCommit.isBlobInCommit(blob) && !addStage.isBlobInStage(blob)) {
            // if the file is the same as current commit, but added into the add stage, remove it from stage
            addStage.removeFilePathFromStage(filePath);
            addStage.saveStage();
        }

        // remove file from the remove stage
        if (removeStage.isFilePathInStage(filePath)) {
            removeStage.removeFilePathFromStage(filePath);
            removeStage.saveStage();
        }
    }

    public static void commitCommand(String message) {
        if (message.equals("")) {
            printErrorAndExit("Please enter a commit message.");
        }

        // if no files in add and remove stage, abort it
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);
        Map<String, String> addMap = addStage.getMapFilePathToBlobID();
        Map<String, String> removeMap = removeStage.getMapFilePathToBlobID();
        if (addMap.isEmpty() && removeMap.isEmpty()) {
            printErrorAndExit("No changes added to the commit.");
        }

        // get current commit and map
        currentCommit = readBranchCommit(readCurrentBranch());
        Map<String, String> commitMap = currentCommit.getMapFilePathToBlobID();

        // calculate new mappings
        if (!addMap.isEmpty()) {
            for (String filePath : addMap.keySet()) {
                commitMap.put(filePath, addMap.get(filePath));
            }
        }
        if (!removeMap.isEmpty()) {
            for (String filePath : removeMap.keySet()) {
                commitMap.remove(filePath);
            }
        }

        // create new commit
        List<String> parents = new ArrayList<>();
        parents.add(currentCommit.getID());
        Commit newCommit = new Commit(message, commitMap, parents);

        // clean add/remove stage files
        addStage.clear();
        addStage.saveStage();
        removeStage.clear();
        removeStage.saveStage();

        // set the commit as "current commit" and save
        currentCommit = newCommit;
        currentCommit.save();

        // change the HEAD pointer
        String currentBranch = readCurrentBranch();
        File HEADS_FILE = join(HEADS_DIR, currentBranch);
        writeContents(HEADS_FILE, currentCommit.getID());
    }

    public static void rmCommand(String fileName) {
        File file = join(CWD, fileName);
        // create a blob for the file
        String filePath = file.getPath();
        currentCommit = readBranchCommit(readCurrentBranch());
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);

        // if the file is in the add stage, remove it
        if (addStage.isFilePathInStage(filePath)) {
            addStage.removeFilePathFromStage(filePath);
            addStage.saveStage();
        } else if (currentCommit.isFilePathInCommit(filePath)) {
            // if file is tracked in the current commit, add file to remove stage
            String blobID = currentCommit.getBlobIDByFilePath(filePath);
            removeStage.addFilePathAndBlobIDToStage(filePath, blobID);
            removeStage.saveStage();
            // remove file from working directory
            restrictedDelete(filePath);
        } else {
            printErrorAndExit("No reason to remove the file.");
        }

    }

    public static void logCommand() {
        currentCommit = readBranchCommit(readCurrentBranch());
        while (!currentCommit.getParentsID().isEmpty()) {
            currentCommit.printCommit();
            // ignore second parent in merge
            String parentCommitID = currentCommit.getParentsID().get(0);
            currentCommit = readCommitByID(parentCommitID);
        }
        currentCommit.printCommit();
    }

    public static void globallogCommand() {
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        for (String id : commitList) {
            currentCommit = readCommitByID(id);
            currentCommit.printCommit();
        }
    }

    // discard changes to the file, set it to the version in given commit (!!! not the latest commit in branch)
    public static void checkoutCommand(String commitID, String fileName) {
        Commit commit;
        if (commitID.equals("HEAD")) {
            commit = readBranchCommit(readCurrentBranch());
        } else {
            commit = readCommitByID(commitID);
        }

        List<String> filePaths = commit.getFilePaths();
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        // if file is not tracked by given commit, print error
        if (!filePaths.contains(filePath)) {
            printErrorAndExit("File does not exist in that commit.");
        }
        // get blob from given commit and write to CWD
        String blobID = commit.getBlobIDByFilePath(filePath);
        Blob blob = readBlobByID(blobID);
        blob.writeBlobToCWD();
    }

    public static void checkoutBranchCommand(String branch) {
        // if no such branch, print error
        File branchHead = join(HEADS_DIR, branch);
        if (!branchHead.exists()) {
            printErrorAndExit("No such branch exists.");
        }
        // if branch is current branch, print error
        if (branch.equals(readCurrentBranch())) {
            printErrorAndExit("No need to checkout the current branch.");
        }
        // get current commit and checked-out commit
        currentCommit = readBranchCommit(readCurrentBranch());
        Commit newCommit = readBranchCommit(branch);

        // files only tracked by new commit will be checked
        List<String> onlyNewCommitTrackedPaths = findOnlyTrackedByFirst(newCommit, currentCommit);
        // if change files which are untracked by current commit, print error
        for (String filePath : onlyNewCommitTrackedPaths) {
            File file = new File(filePath);
            if (file.exists()) {
                printErrorAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        // files only tracked by current commit should be deleted
        List<String> onlyCurrentCommitTrackedPaths = findOnlyTrackedByFirst(currentCommit, newCommit);
        for (String filePath: onlyCurrentCommitTrackedPaths) {
            File file = new File(filePath);
            restrictedDelete(file);
        }
        // write all files from new commit into CWD
        for (String blobID : newCommit.getBlobIDs()) {
            Blob blob = readBlobByID(blobID);
            blob.writeBlobToCWD();
        }

        // clear stage of add and delete
        addStage = readStage(ADDSTAGE_FILE);
        addStage.clear();
        addStage.saveStage();
        removeStage = readStage(REMOVESTAGE_FILE);
        removeStage.clear();
        removeStage.saveStage();

        // check out to new branch
        writeContents(HEAD_FILE, branch);
    }

    private static List<String> findOnlyTrackedByFirst(Commit first, Commit second) {
        List<String> firstPaths = first.getFilePaths();
        List<String> secondPaths = second.getFilePaths();
        for (String s : firstPaths) {
            if (secondPaths.contains(s)) {
                firstPaths.remove(s);
            }
        }
        return firstPaths;
    }

    private static Blob readBlobByID(String id) {
        File blobFile = join(OBJECT_DIR, id);
        return readObject(blobFile, Blob.class);
    }

    private static Commit readCommitByID(String commitID) {
        if (commitID.length() == 40) {
            File CURR_COMMIT_FILE = join(OBJECT_DIR, commitID);
            if (CURR_COMMIT_FILE.exists()){
                return readObject(CURR_COMMIT_FILE, Commit.class);
            }
        } else {
            List<String> objects = plainFilenamesIn(OBJECT_DIR);
            for (String id : objects) {
                if (commitID.equals(id.substring(0, commitID.length()))) {
                    return readCommitByID(id);
                }
            }
        }
        printErrorAndExit("No commit with that id exists.");
        return null;
    }

    private static Commit readBranchCommit(String branch) {
        String commitID = readBranchCommitID(branch);
        File COMMIT_FILE = join(OBJECT_DIR, commitID);
        return readObject(COMMIT_FILE, Commit.class);
    }

    private static String readBranchCommitID(String branch) {
        return readContentsAsString(join(HEADS_DIR, branch));
    }

    // the current branch is saved in HEAD, e.g. 'refs/heads/master'
    private static String readCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    // read the content in stage or create a new if empty
    private static Stage readStage(File STAGE_FILE) {
        if (!STAGE_FILE.exists()) {
            return new Stage(STAGE_FILE);
        }
        return readObject(STAGE_FILE, Stage.class);
    }

    // Commands other than 'init' have to be executed in initialized directory
    public static void checkInitialized() {
        if (!GITLET_DIR.exists()) {
            printErrorAndExit("Not in an initialized Gitlet directory.");
        }
    }

    public static void printErrorAndExit(String message) {
        System.out.println(message);
        System.exit(0);
    }
}
