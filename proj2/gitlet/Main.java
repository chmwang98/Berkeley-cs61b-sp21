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
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;
            case "add":
                validateNumArgs("init", args, 2);
                Repository.checkInitialized();
                Repository.addCommand(args[1]);
                break;
            case "commit":
                break;
            case "checkout":
                break;
            case "log":
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
