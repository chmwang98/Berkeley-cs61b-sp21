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
            String msg = "Please enter a command.";
            Utils.message(msg);
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

                break;
            case "commit":
                break;
            case "checkout":
                break;
            case "log":
                break;
            default:
                String msg = "No command with that name exists.";
                Utils.message(msg);
                System.exit(0);
        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            String msg = "Incorrect operands.";
            Utils.message(msg);
            System.exit(0);
        }
    }
}
