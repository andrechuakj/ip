package duke;

import java.util.ArrayList;

import duke.command.*;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;


/**
 * The Parser class is responsible for parsing user input and converting data strings
 * into task objects for the Duke application.
 */
public class Parser {

    /**
     * Parses the user input command and returns the corresponding Command object.
     *
     * @param strCommand The user input command to be parsed.
     * @return A Command object representing the parsed command.
     * @throws DukeException If the input command is invalid or contains errors.
     */
    public static Command parse(String strCommand) throws DukeException {
        assert strCommand != null : "Command should not be null";
        int firstSpaceIndex = strCommand.indexOf(" ");
        Command command = null;
        String commandType = firstSpaceIndex != -1
                ? strCommand.substring(0, firstSpaceIndex)
                : strCommand;
        ArrayList<String> commandDetailList = new ArrayList<>();
        switch (commandType) {
        case "list":
            commandDetailList.add(strCommand);
            command = new ListCommand(commandDetailList);
            break;
        case "bye":
            commandDetailList.add(strCommand);
            command = new ByeCommand(commandDetailList);
            break;
        case "todo":
            if (firstSpaceIndex == -1 || strCommand.length() < 6) {
                throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
            }
            String todoDesc = strCommand.substring(firstSpaceIndex + 1);
            commandDetailList.add(todoDesc);
            command = new AddCommand(commandDetailList, "T");
            break;
        case "event":
            int fromIndex = strCommand.indexOf("/from");
            int toIndex = strCommand.indexOf("/to");
            if (firstSpaceIndex == -1 || fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                throw new DukeException("OOPS!!! The format of the event command is invalid.\n"
                        + "Here is an example of a valid format:"
                        + " event coding /from 2023-01-01 /to 2023-12-31");
            }
            String eventDesc = strCommand.substring(firstSpaceIndex + 1, fromIndex - 1);
            String from = strCommand.substring(fromIndex + "/from ".length(), toIndex - 1);
            String to = strCommand.substring(toIndex + "/to ".length());
            if (eventDesc.isBlank() || from.isBlank() || to.isBlank()) {
                throw new DukeException("OOPS!!! The format of the event command is invalid.\n"
                        + "Here is an example of a valid format:"
                        + " event coding /from 2023-01-01 /to 2023-12-31");
            }
            commandDetailList.add(eventDesc);
            commandDetailList.add(from);
            commandDetailList.add(to);
            command = new AddCommand(commandDetailList, "E");
            break;
        case "deadline":
            int byIndex = strCommand.indexOf("/by");
            if (byIndex == -1 || firstSpaceIndex == -1) {
                throw new DukeException("OOPS!!! The format of the deadline command is invalid.\n"
                        + "Here is an example of a valid format:"
                        + " deadline coding /by 2023-09-04");
            }
            String deadlineDesc = strCommand.substring(firstSpaceIndex + 1, byIndex - 1);
            String by = strCommand.substring(byIndex + "/by ".length());
            if (deadlineDesc.isBlank() || by.isBlank()) {
                throw new DukeException("OOPS!!! The format of the deadline command is invalid.\n"
                        + "Here is an example of a valid format:"
                        + " deadline coding /by 2023-09-04");
            }
            commandDetailList.add(deadlineDesc);
            commandDetailList.add(by);
            command = new AddCommand(commandDetailList, "D");
            break;
        case "mark":
            if (firstSpaceIndex == -1 || strCommand.length() < "mark ".length()) {
                throw new DukeException("OOPS!!! The task number to mark cannot be empty.");
            }
            String taskToMark = strCommand.substring(firstSpaceIndex + 1);
            if (taskToMark.isBlank()) {
                throw new DukeException("OOPS!!! The task number to mark cannot be empty.");
            }
            commandDetailList.add(taskToMark);
            command = new MarkCommand(commandDetailList);
            break;
        case "unmark":
            if (firstSpaceIndex == -1 || strCommand.length() < "unmark ".length()) {
                throw new DukeException("OOPS!!! The task number to unmark cannot be empty.");
            }
            String taskToUnmark = strCommand.substring(firstSpaceIndex + 1);
            if (taskToUnmark.isBlank()) {
                throw new DukeException("OOPS!!! The task number to unmark cannot be empty.");
            }
            commandDetailList.add(taskToUnmark);
            command = new UnmarkCommand(commandDetailList);
            break;
        case "delete":
            if (firstSpaceIndex == -1 || strCommand.length() < "delete ".length()) {
                throw new DukeException("OOPS!!! The task number to delete cannot be empty.");
            }
            String taskToDelete = strCommand.substring(firstSpaceIndex + 1);
            if (taskToDelete.isBlank()) {
                throw new DukeException("OOPS!!! The task number to delete cannot be empty.");
            }
            commandDetailList.add(taskToDelete);
            command = new DeleteCommand(commandDetailList);
            break;
        case "find":
            if (firstSpaceIndex == -1 || strCommand.length() < "find ".length()) {
                throw new DukeException("OOPS!!! The find keyword cannot be empty.");
            }
            String keyword = strCommand.substring(firstSpaceIndex + 1);
            if (keyword.isBlank()) {
                throw new DukeException("OOPS!!! The find keyword cannot be empty.");
            }
            commandDetailList.add(keyword);
            command = new FindCommand(commandDetailList);
            break;
        case "update":
            int secondSpaceIndex = strCommand.indexOf(" ", firstSpaceIndex + 1);
            int slashIndex = strCommand.indexOf("/", secondSpaceIndex);
            int thirdSpaceIndex = strCommand.indexOf(" ", slashIndex);
            if (firstSpaceIndex == -1 || strCommand.length() < "update ".length()) {
                throw new DukeException("OOPS!!! The update details cannot be empty.");
            } else if (slashIndex == -1 || secondSpaceIndex == -1) {
                throw new DukeException("OOPS!!! Please use the format: update 1 /desc newName");
            }
            String taskNumber = strCommand.substring(firstSpaceIndex + 1, secondSpaceIndex);
            String field = strCommand.substring(slashIndex + 1, thirdSpaceIndex);
            String details = strCommand.substring(thirdSpaceIndex + 1);
            commandDetailList.add(taskNumber);
            commandDetailList.add(field);
            commandDetailList.add(details);
            System.out.println(field);
            command = new UpdateCommand(commandDetailList);
            break;
        default:
            throw new DukeException("OOPS!!! This command is invalid.");
        }
        return command;
    }

    /**
     * Converts a data string into a Task object.
     *
     * @param data The data string representing a Task.
     * @return A Task object created from the data string.
     * @throws DukeException If there are errors in the data string, or it is in an invalid format.
     */
    public static Task dataToTask(String data) throws DukeException {
        String taskType = data.substring(0, 1);
        String taskData = data.substring(4);
        Task task = null;
        int firstSplitIndex = -1;
        int secondSplitIndex = -1;
        int thirdSplitIndex = -1;
        String desc = "";
        boolean isDone = false;

        switch (taskType) {
        case "T":
            firstSplitIndex = taskData.indexOf("|");
            isDone = taskData.substring(0, firstSplitIndex - 1).equals("1");
            desc = taskData.substring(firstSplitIndex + 2);
            task = new Todo(desc);
            if (isDone) {
                task.markDone();
            }
            break;
        case "D":
            firstSplitIndex = taskData.indexOf("|");
            secondSplitIndex = taskData.indexOf("|", firstSplitIndex + 1);
            isDone = taskData.substring(0, firstSplitIndex - 1).equals("1");
            desc = taskData.substring(firstSplitIndex + 2, secondSplitIndex - 1);
            String by = taskData.substring(secondSplitIndex + 2);
            task = new Deadline(desc, by);
            if (isDone) {
                task.markDone();
            }
            break;
        case "E":
            firstSplitIndex = taskData.indexOf("|");
            secondSplitIndex = taskData.indexOf("|", firstSplitIndex + 1);
            thirdSplitIndex = taskData.indexOf("|", secondSplitIndex + 1);
            isDone = taskData.substring(0, firstSplitIndex - 1).equals("1");
            desc = taskData.substring(firstSplitIndex + 2, secondSplitIndex - 1);
            String from = taskData.substring(secondSplitIndex + 2, thirdSplitIndex - 1);
            String to = taskData.substring(thirdSplitIndex + 2);
            task = new Event(desc, from, to);
            if (isDone) {
                task.markDone();
            }
            break;
        default:
            throw new DukeException("Wrong task type.");
        }
        return task;
    }
}
