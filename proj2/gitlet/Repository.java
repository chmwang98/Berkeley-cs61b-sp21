package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author ming
 */
public class Repository {
    /**
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

    private static Commit currentCommit;
    private static Stage addStage = new Stage(ADDSTAGE_FILE);
    private static Stage removeStage = new Stage(REMOVESTAGE_FILE);
    private static String currentBranch;

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * Start with one commit that contains no files and has the commit message.
     * It will have a single branch: master, which initially points to this initial commit.
     * The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970.
     * Initial commit in all repositories created by Gitlet will have the same content,
     * all repositories will share this commit (they will all have the same UID)
     * and all commits in all repositories will trace back to it.
     * */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            printErrorAndExit("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();

        Commit initialCommit = new Commit();
        currentCommit = initialCommit;
        initialCommit.save();

        writeContents(HEAD_FILE, "master");
        File masterHead = join(HEADS_DIR, "master");
        writeContents(masterHead, currentCommit.getID());
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * For this reason, adding a file is also called staging the file for addition.
     * Staging already-staged file overwrites in the staging area with new contents.
     * The staging area should be somewhere in .gitlet.
     * If current working version of the file is identical to the version in the current commit,
     * do not currentStage it to be added, and remove it from the staging area
     * (as can happen when a file is changed, added, and then changed back).
     * The file will no longer be staged for removal (see gitlet rm),
     * if it was at the time of the command.
     * */
    public static void addCommand(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            printErrorAndExit("File does not exist.");
        }
        // create a blob for the file
        Blob blob = new Blob(file);
        blob.storeBlob();
        currentCommit = readBranchCommit(readCurrentBranch());
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);
        // if blob is neither in current commit, nor in current stage, stage it
        if (!currentCommit.isBlobInCommit(blob) && !addStage.isBlobInStage(blob)) {
            addStage.addBlobToStage(blob);
            addStage.saveStage();
        } else if (currentCommit.isBlobInCommit(blob) && !addStage.isBlobInStage(blob)) {
            // if file is same as current commit, but added into the add stage, remove from stage
            addStage.removeFileNameFromStage(fileName);
            addStage.saveStage();
        }

        // remove file from the remove stage
        if (removeStage.isFileNameInStage(fileName)) {
            removeStage.removeFileNameFromStage(fileName);
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
        Map<String, String> addMap = addStage.getMapFileNameToBlobID();
        Map<String, String> removeMap = removeStage.getMapFileNameToBlobID();
        if (addMap.isEmpty() && removeMap.isEmpty()) {
            printErrorAndExit("No changes added to the commit.");
        }

        // get current commit and map
        currentCommit = readBranchCommit(readCurrentBranch());
        Map<String, String> commitMap = currentCommit.getMapFileNameToBlobID();

        // calculate new mappings
        if (!addMap.isEmpty()) {
            for (String fileName : addMap.keySet()) {
                commitMap.put(fileName, addMap.get(fileName));
            }
        }
        if (!removeMap.isEmpty()) {
            for (String fileName : removeMap.keySet()) {
                commitMap.remove(fileName);
            }
        }

        // create new commit
        List<String> parents = new ArrayList<>();
        parents.add(currentCommit.getID());
        Commit newCommit = new Commit(message, commitMap, parents);

        saveNewCommit(newCommit);
    }

    public static void rmCommand(String fileName) {
        File file = join(CWD, fileName);
        // create a blob for the file
        currentCommit = readBranchCommit(readCurrentBranch());
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);

        // if the file is in the add stage, remove it
        if (addStage.isFileNameInStage(fileName)) {
            addStage.removeFileNameFromStage(fileName);
            addStage.saveStage();
        } else if (currentCommit.isFileNameInCommit(fileName)) {
            // if file is tracked in the current commit, add file to remove stage
            String blobID = currentCommit.getBlobIDByFileName(fileName);
            removeStage.addFileNameAndBlobIDToStage(fileName, blobID);
            removeStage.saveStage();
            // remove file from working directory
            restrictedDelete(fileName);
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
            try {
                currentCommit = readCommitByID(id);
                currentCommit.printCommit();
            } catch (Exception ignore) {
            }
        }
    }

    // discard changes to the file, set it to the version in given commit
    // (!!! not the latest commit in branch)
    public static void checkoutCommand(String commitID, String fileName) {
        Commit commit;
        if (commitID.equals("HEAD")) {
            commit = readBranchCommit(readCurrentBranch());
        } else {
            commit = readCommitByID(commitID);
        }

        List<String> fileNames = commit.getFileNames();
        // if file is not tracked by given commit, print error
        if (!fileNames.contains(fileName)) {
            printErrorAndExit("File does not exist in that commit.");
        }
        // get blob from given commit and write to CWD
        String blobID = commit.getBlobIDByFileName(fileName);
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
        changeCommit(newCommit);

        // check out to new branch
        writeContents(HEAD_FILE, branch);
    }

    private static void changeCommit(Commit newCommit) {
        // files only tracked by new commit will be checked
        List<String> filesToWrite = findOnlyTrackedByFirst(newCommit, currentCommit);
        writeFiles(filesToWrite, newCommit);
        // files only tracked by current commit should be deleted
        List<String> filesToDelete = findOnlyTrackedByFirst(currentCommit, newCommit);
        deleteFiles(filesToDelete);
        // files tracked by both should be overwritten
        List<String> filesToOverwrite = findBothTracked(currentCommit, newCommit);
        overwriteFiles(filesToOverwrite, newCommit);

        saveNewCommit(newCommit);
    }

    private static void writeFiles(List<String> filesToWrite, Commit newCommit) {
        // if change files which are untracked by current commit, print error
        for (String fileName : filesToWrite) {
            File file = join(CWD, fileName);
            if (file.exists()) {
                printErrorAndExit("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        overwriteFiles(filesToWrite, newCommit);
    }

    private static void overwriteFiles(List<String> filesToOverwrite, Commit newCommit) {
        for (String fileName : filesToOverwrite) {
            Blob blob = readBlobByID(newCommit.getBlobIDByFileName(fileName));
            blob.writeBlobToCWD();
        }
    }

    private static void deleteFiles(List<String> filesToDelete) {
        for (String fileName: filesToDelete) {
            File file = join(CWD,fileName);
            restrictedDelete(file);
        }
    }

    public static void findCommand(String message) {
        Boolean noSuchCommit = true;
        // find all commits with the message
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        for (String id : commitList) {
            try {
                currentCommit = readCommitByID(id);
                if (message.equals(currentCommit.getMessage())) {
                    System.out.println(currentCommit.getID());
                    noSuchCommit = false;
                }
            } catch (Exception ignore) {
            }
        }
        if (noSuchCommit) {
            printErrorAndExit("Found no commit with that message.");
        }
    }

    public static void statusCommand() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private static void printBranches() {
        System.out.println("=== Branches ===");
        currentBranch = readCurrentBranch();
        List<String> branchList = plainFilenamesIn(HEADS_DIR);
        for (String branch : branchList) {
            if (branch.equals(currentBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
    }

    private static void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        addStage = readStage(ADDSTAGE_FILE);
        for (String fileName : addStage.getMapFileNameToBlobID().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    private static void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        removeStage = readStage(REMOVESTAGE_FILE);
        for (String fileName : removeStage.getMapFileNameToBlobID().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    public static void branchCommand(String branch) {
        // if branch already exists, print error
        File branchFile = join(HEADS_DIR, branch);
        if (branchFile.exists()) {
            printErrorAndExit("A branch with that name already exists.");
        }
        // get the commit id of current branch and save to new branch
        String commitID = readBranchCommitID(readCurrentBranch());
        writeContents(branchFile, commitID);
    }

    public static void rmbranchCommand(String branch) {
        checkIfBranchExists(branch);
        // make sure the branch to delete is not the current branch
        if (branch.equals(readCurrentBranch())) {
            printErrorAndExit("Cannot remove the current branch.");
        }
        // delete the branch only, not the commits
        File branchFile = join(HEADS_DIR, branch);
        branchFile.delete();
    }

    public static void resetCommand(String commitID) {
        // check if new commit exists
        Commit newCommit = readCommitByID(commitID);
        // change current commit to new commit and write/remove files
        currentBranch = readCurrentBranch();
        currentCommit = readBranchCommit(currentBranch);
        changeCommit(newCommit);

        // set the current branch HEAD to new commit
        File branchFile = join(HEADS_DIR, currentBranch);
        writeContents(branchFile, commitID);
    }

    public static void mergeCommand(String branch) {
        checkIfStageEmpty();
        checkIfBranchExists(branch);
        checkIfMergeWithSelf(branch);

        currentBranch = readCurrentBranch();
        String currentCommitID = readBranchCommitID(currentBranch);
        String mergedID = readBranchCommitID(branch);
        String splitPoint = findSplitPoint(currentCommitID, mergedID);
        if (splitPoint.equals(mergedID)) {
            printErrorAndExit("Given branch is an ancestor of the current branch.");
        } else if (splitPoint.equals(currentCommitID)) {
            checkoutBranchCommand(branch);
            printErrorAndExit("Current branch fast-forwarded.");
        }

        // create a new commit, set current commit and merged commit as parents
        String message = "Merged" + branch + "into" + currentBranch + ".";
        currentCommit = readCommitByID(currentCommitID);
        Map<String, String> currentMap = currentCommit.getMapFileNameToBlobID();
        List<String> parents = new ArrayList<>(List.of(currentCommitID, mergedID));
        Commit newCommit = new Commit(message, currentMap, parents);
        Map<String, String> newMap = newCommit.getMapFileNameToBlobID();


        Commit mergedCommit = readCommitByID(mergedID);
        Map<String, String> mergedMap = mergedCommit.getMapFileNameToBlobID();
        Commit splitCommit = readCommitByID(splitPoint);
        Map<String, String> splitMap = splitCommit.getMapFileNameToBlobID();

        /**
         * find files modified in given branch since split, but unmodified in current
         * change those files to versions in given branch and add to stage
         */
//        List<String> modifiedInMerged = calculateOverwriteFiles(newMap, mergedMap, splitMap);
//        overwriteFiles(modifiedInMerged, mergedCommit);
        /**
         * find files ONLY exist in given branch
         * checkout these files and add to stage
         */
//        List<String> onlyInMerged = calculateWriteFiles(newMap, mergedMap, splitMap);
//        writeFiles(onlyInMerged, mergedCommit);
        /**
         * Any files present at the split point, unmodified in the current branch,
         * and absent in the given branch should be removed (and untracked).
         */
//        List<String> absentInMerged = calculateDeleteFiles(newMap, mergedMap, splitMap);
//        deleteFiles(absentInMerged);

        Set<String> allFiles = new HashSet<>();
        collectAllFiles(newCommit, allFiles);
        collectAllFiles(mergedCommit, allFiles);
        collectAllFiles(splitCommit, allFiles);
        List<String> writeList = new ArrayList<>();
        List<String> overwriteList = new ArrayList<>();
        List<String> deleteList = new ArrayList<>();
        dealWithConflicts(allFiles, newMap, mergedMap, splitMap,
                            writeList, overwriteList, deleteList);
        writeFiles(writeList, mergedCommit);
        overwriteFiles(overwriteList, mergedCommit);
        deleteFiles(deleteList);
        Commit merge = generateMergeCommit(newCommit, mergedMap, writeList, overwriteList, deleteList);
        saveNewCommit(merge);
    }

    private static void saveNewCommit(Commit commit) {
        commit.save();
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);
        addStage.clear();
        addStage.saveStage();
        removeStage.clear();
        removeStage.saveStage();
        // change the HEAD pointer
        currentBranch = readCurrentBranch();
        File branchHead = join(HEADS_DIR, currentBranch);
        writeContents(branchHead, commit.getID());
    }

    private static Commit generateMergeCommit(Commit newCommit,
                                              Map<String, String> mergedMap,
                                              List<String> writeList,
                                              List<String> overwriteList,
                                              List<String> deleteList) {
        Map<String, String> mapAfterMerge = newCommit.getMapFileNameToBlobID();
        for (String fileName : writeList) {
            String blobID = mergedMap.get(fileName);
            mapAfterMerge.put(fileName, blobID);
        }
        for (String fileName : overwriteList) {
            String blobID = mergedMap.get(fileName);
            mapAfterMerge.put(fileName, blobID);
        }
        for (String fileName : deleteList) {
            mapAfterMerge.remove(fileName);
        }
        String message = newCommit.getMessage();
        List<String> parents = newCommit.getParentsID();
        return new Commit(message, mapAfterMerge, parents);
    }

    private static void dealWithConflicts(Set<String> allFiles,
                                          Map<String, String> newMap,
                                          Map<String, String> mergedMap,
                                          Map<String, String> splitMap,
                                          List<String> writeList,
                                          List<String> overwriteList,
                                          List<String> deleteList) {
        boolean isConflict = false;
        for (String fileName : allFiles) {
            int countInMaps = 0;
            if (newMap.containsKey(fileName)) {
                countInMaps += 1;
            }
            if (mergedMap.containsKey(fileName)) {
                countInMaps += 2;
            }
            if (splitMap.containsKey(fileName)) {
                countInMaps += 4;
            }
            switch(countInMaps) {
                case 1:
                    // file only in newMap, nothing
                    break;
                case 2:
                    // file only in mergedMap, write
                    writeList.add(fileName);
                    break;
                case 3:
                    // if added in both branch in different ways, conflict
                    if (!newMap.get(fileName).equals(mergedMap.get(fileName))) {
                        writeConflictFile(fileName, newMap, mergedMap);
                        isConflict = true;
                    }
                    break;
                case 4:
                    // deleted from both branched, nothing
                    break;
                case 5:
                    // if changed in newMap and deleted in merge, conflict
                    if (!newMap.get(fileName).equals(splitMap.get(fileName))) {
                        writeConflictFile(fileName, newMap, mergedMap);
                        isConflict = true;
                    } else {
                        // if unchanged in newMap, but deleted in merge, add to delete
                        deleteList.add(fileName);
                    }
                    break;
                case 6:
                    // changed in merge, deleted from new, conflict
                    if (!mergedMap.get(fileName).equals(splitMap.get(fileName))) {
                        writeConflictFile(fileName, newMap, mergedMap);
                        isConflict = true;
                    }
                    break;
                case 7:
                    if (!newMap.get(fileName).equals(mergedMap.get(fileName))) {
                        // if in new and merged are same, nothing. So only consider unequal
                        if (newMap.get(fileName).equals(splitMap.get(fileName))) {
                            // if changed in merge, add to overwrite
                            overwriteList.add(fileName);
                        } else {
                            // if new changed since split
                            if (!mergedMap.get(fileName).equals(splitMap.get(fileName))) {
                                // if merge also changed since split, i.e. nothing equal
                                writeConflictFile(fileName, newMap, mergedMap);
                                isConflict = true;
                            }
                        }
                    }
                    break;
            }
        }
        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static void writeConflictFile(String fileName,
                                          Map<String, String> newMap,
                                          Map<String, String> mergedmap) {
        String newContent = getContentFromMap(fileName, newMap);
        String mergedContent = getContentFromMap(fileName,mergedmap);
        String contentAfterMerge = "<<<<<<< HEAD\n"
                                    + newContent
                                    + "=======\n"
                                    + mergedContent
                                    + ">>>>>>>\n";
        File file = join(CWD, fileName);
        writeContents(file, contentAfterMerge);
    }

    private static String getContentFromMap (String fileName, Map<String, String> currMap) {
        Blob blob;
        String content;
        if (currMap.get(fileName).equals(null)) {
            content = "";
        } else {
            blob = readBlobByID(currMap.get(fileName));
            content = blob.getContent();
        }
        return content;
    }

    private static void collectAllFiles(Commit newCommit, Set<String> allFilesSet) {
        for (String fileName : newCommit.getFileNames()) {
            allFilesSet.add(fileName);
        }
    }

    private static List<String> calculateOverwriteFiles(Map<String, String> newMap,
                                                        Map<String, String> mergedMap,
                                                        Map<String, String> splitMap) {
        List<String> overwriteFiles = new ArrayList<>();
        for (String fileName : splitMap.keySet()) {
            if (newMap.containsKey(fileName) && mergedMap.containsKey(fileName)) {
                if (splitMap.get(fileName).equals(newMap.get(fileName))
                        && !splitMap.get(fileName).equals(mergedMap.get(fileName))) {
                    overwriteFiles.add(fileName);
                }
            }
        }
        return overwriteFiles;
    }

    private static List<String> calculateWriteFiles(Map<String, String> newMap,
                                                    Map<String, String> mergedMap,
                                                    Map<String, String> splitMap) {
        List<String> writeFiles = new ArrayList<>();
        for (String fileName : mergedMap.keySet()) {
            if (!newMap.containsKey(fileName) && !splitMap.containsKey(fileName)) {
                writeFiles.add(fileName);
            }
        }
        return writeFiles;
    }

    private static List<String> calculateDeleteFiles(Map<String, String> newMap,
                                                     Map<String, String> mergedMap,
                                                     Map<String, String> splitMap) {
        List<String> deleteFiles = new ArrayList<>();
        for (String fileName : splitMap.keySet()) {
            if (newMap.containsKey(fileName)
                    && newMap.get(fileName).equals(splitMap.get(fileName))
                    && !mergedMap.containsKey(fileName)) {
                deleteFiles.add(fileName);
            }
        }
        return deleteFiles;
    }

    private static List<String> calculateConflicts(Map<String, String> newMap,
                                                   Map<String, String> mergedMap,
                                                   Map<String, String> splitMap) {
        List<String> conflicts = new ArrayList<>();
        for (String fileName : splitMap.keySet()) {
            // tracked by both and modified in different ways
            if (newMap.containsKey(fileName)
                    && mergedMap.containsKey(fileName)
                    && !newMap.get(fileName).equals(mergedMap.get(fileName))) {
                conflicts.add(fileName);
            }
        }
        return conflicts;
    }

    private static String findSplitPoint(String id1, String id2) {
        Set<String> ancestors1 = new HashSet<>();
        collectAncestors(id1, ancestors1);

        Queue<String> queue = new LinkedList<>();
        queue.offer(id2);

        while (!queue.isEmpty()) {
            // get commit at the front of queue
            String currentID = queue.poll();
            // found common ancestor
            if (ancestors1.contains(currentID)) {
                return currentID;
            }
            Commit commit = readCommitByID(currentID);
            // add parents to queue
            for (String parentID : commit.getParentsID()) {
                queue.offer(parentID);
            }
        }
        return null;
    }

    private static void collectAncestors(String id, Set<String> ancestors) {
        // add current id into set
        ancestors.add(id);
        Commit commit = readCommitByID(id);
        // recursively add parents id into set
        for (String parentID : commit.getParentsID()) {
            collectAncestors(parentID, ancestors);
        }
    }

    private static void checkIfStageEmpty () {
        // if stage is not empty, print error
        addStage = readStage(ADDSTAGE_FILE);
        removeStage = readStage(REMOVESTAGE_FILE);
        if (!addStage.isEmpty() || !removeStage.isEmpty()) {
            printErrorAndExit("You have uncommitted changes.");
        }
    }

    private static void checkIfBranchExists(String branch) {
        // if branch doesn't exist, print error
        File branchFile = join(HEADS_DIR, branch);
        if (!branchFile.exists()) {
            printErrorAndExit("A branch with that name does not exist.");
        }
    }

    private static void checkIfMergeWithSelf(String branch) {
        currentBranch = readCurrentBranch();
        if (branch.equals(currentBranch)) {
            printErrorAndExit("Cannot merge a branch with itself.");
        }
    }

    private static List<String> findOnlyTrackedByFirst(Commit first, Commit second) {
        List<String> firstNames = first.getFileNames();
        List<String> secondNames = second.getFileNames();
        /**
         * create a new list to save result
         * if simply remove from one list, result will be wrong
         */
        List<String> newList = new ArrayList<>();
        for (String s : firstNames) {
            if (!secondNames.contains(s)) {
                newList.add(s);
            }
        }
        return newList;
    }

    private static List<String> findBothTracked(Commit first, Commit second) {
        List<String> firstNames = first.getFileNames();
        List<String> secondNames = second.getFileNames();
        /**
         * create a new list to save result
         * if simply remove from one list, result will be wrong
         */
        List<String> newList = new ArrayList<>();
        for (String s : firstNames) {
            if (secondNames.contains(s)) {
                newList.add(s);
            }
        }
        return newList;
    }

    private static Blob readBlobByID(String id) {
        File blobFile = join(OBJECT_DIR, id);
        return readObject(blobFile, Blob.class);
    }

    private static Commit readCommitByID(String commitID) {
        if (commitID.length() == 40) {
            File currCommitFile = join(OBJECT_DIR, commitID);
            if (currCommitFile.exists()) {
                return readObject(currCommitFile, Commit.class);
            }
        } else {
            List<String> objectIDs = plainFilenamesIn(OBJECT_DIR);
            for (String id : objectIDs) {
                if (commitID.equals(id.substring(0, commitID.length()))) {
                    return readObject(join(OBJECT_DIR, id), Commit.class);
                }
            }
        }
        printErrorAndExit("No commit with that id exists.");
        return null;
    }

    private static Commit readBranchCommit(String branch) {
        String commitID = readBranchCommitID(branch);
        File commitFile = join(OBJECT_DIR, commitID);
        return readObject(commitFile, Commit.class);
    }

    private static String readBranchCommitID(String branch) {
        return readContentsAsString(join(HEADS_DIR, branch));
    }

    // the current branch is saved in HEAD, e.g. 'refs/heads/master'
    private static String readCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    // read the content in stage or create a new if empty
    private static Stage readStage(File stageFile) {
        if (!stageFile.exists()) {
            return new Stage(stageFile);
        }
        return readObject(stageFile, Stage.class);
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
