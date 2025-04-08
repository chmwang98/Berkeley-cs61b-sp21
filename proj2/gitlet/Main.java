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
                Repository.commitCommand(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.checkInitialized();
                Repository.rmCommand(args[1]);
                break;
            case "checkout":
                Repository.checkInitialized();
                switch (args.length) {
                    case 3:
                        if (!args[1].equals("--")) {
                            Repository.printErrorAndExit("Incorrect operands.");
                        }
                        Repository.checkoutCommand("HEAD", args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            Repository.printErrorAndExit("Incorrect operands.");
                        }
                        Repository.checkoutCommand(args[1], args[3]);
                        break;
                    case 2:
                        Repository.checkoutBranchCommand(args[1]);
                        break;
                    default:
                        Repository.printErrorAndExit("Incorrect operands.");
                }
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.checkInitialized();
                Repository.logCommand();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.checkInitialized();
                Repository.globallogCommand();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.checkInitialized();
                Repository.findCommand(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.checkInitialized();
                Repository.statusCommand();
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.checkInitialized();
                Repository.branchCommand(args[1]);
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
