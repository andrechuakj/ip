package duke.command;

import java.util.ArrayList;

import duke.DukeException;
import duke.Storage;
import duke.Ui;
import duke.task.Task;
import duke.task.TaskList;

public class MarkCommand extends Command {
    public MarkCommand(ArrayList<String> commandDetails) {
        super(commandDetails);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        int taskNumber = 0;
        try {
            taskNumber = Integer.parseInt(commandDetails.get(0));
        } catch (NumberFormatException e) {
            throw new DukeException("☹ OOPS!!! The task number cannot be parsed.");
        }
        if (taskNumber > tasks.size()) {
            throw new DukeException("☹ OOPS!!! The task number is out of range.");
        }
        Task markedTask = tasks.get(taskNumber - 1);
        markedTask.markAsDone();
        storage.writeListToFile(tasks);
        ui.printTaskMarked(markedTask);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MarkCommand) {
            MarkCommand other = (MarkCommand) obj;
            if (this.commandDetails == null || other.commandDetails == null) {
                return false;
            }
            return this.commandDetails.equals(other.commandDetails);
        }
        return false;
    }
}
