import java.util.ArrayList;
import java.util.TimerTask;

public class TaskDeadline extends TimerTask {
    private ArrayList<String> task = new ArrayList<>();

    public TaskDeadline(ArrayList<String> task) {
        this.task.addAll(task);
    }

    @Override
    public void run(){
        if(!task.get(1).contains("✓"))
            System.out.println("Just reminding you that your '"+ task.get(0) + "' task's deadline is "+ task.get(1));
    }



}
