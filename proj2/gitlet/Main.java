package gitlet;

import gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author ming
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Repository.printErrorAndExit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.initCommand();
                break;
            case "add":
                validateNumArgs(args, 2);
                Repository.checkInitialized();
                Repository.addCommand(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                Repository.checkInitialized();

                break;
            case "rm":
                validateNumArgs(args, 2);

                break;
            case "checkout":
                break;
            case "log":
                break;
            default:
                Repository.printErrorAndExit("No command with that name exists.");
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Repository.printErrorAndExit("Incorrect operands.");
        }
    }
}
