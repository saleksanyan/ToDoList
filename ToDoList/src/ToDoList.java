import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ToDoList {
   static ArrayList<ArrayList<String>> list = new ArrayList<>();

    public ToDoList(ArrayList<ArrayList<String>> list) {
        assert list != null;
        ToDoList.list.addAll(list);
    }

    private static void changeTask(ArrayList<String> defaultAction,
                                                           ArrayList<String> changeToAction) {
        if(sameTiming(changeToAction,defaultAction)) {
            list.remove(defaultAction);
            list.add(changeToAction);
            System.out.println("Entered data is changed!");
        }else
            System.out.println("We cannot change given information");

    }

    //helps you to insert data in a file
    private static void insertTask(ArrayList<String> insertableAction, Scanner readUser){

        if(!isTheTimeOpen(insertableAction))
            System.out.println("You already have something to do by that deadline. " +
                    "\nAre you sure you want to add one more?(Y or N)");
       if(!readUser.nextLine().equalsIgnoreCase("N")){
            list.add(insertableAction);
            System.out.println("The task has been inserted!");
            return;
        }
        System.out.println("Okay, bye!");
   }
    //helps you to remove from a data in a file
    private static void removeTask(ArrayList<String> task) {
        if(isInToDoList(task)) {
            list.remove(findTaskByTimeAndName(task));
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i).toString());
            }
            System.out.println("The task has been removed!");
        }
        else
            System.out.println("There is no task like that in the ToDo list");
    }

    //compares given names and surnames
    private static boolean sameTiming(ArrayList<String> task1, ArrayList<String> task2){
        if(task1.get(1).equals(task2.get(1)))
            return true;
        return false;
    }
    private static boolean sameTiming(ArrayList<String> task, String time){
        if(task.get(1).strip().equals(time.strip()))
            return true;
        return false;
    }
    private static boolean isNumeric(String num) {
        return num != null && num.matches("[-+]?\\d*\\.?\\d+");
    }

    private static boolean isTheTimeOpen(ArrayList<String> task){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).get(1).strip().equals(task.get(1).strip()))
                return false;
        }
        return true;
    }
    private static boolean isInToDoList(ArrayList<String> task){
        for (int i = 0; i < list.size(); i++) {
            if(!isTheTimeOpen(task) && list.get(i).get(0).strip().equals(task.get(0).strip()))
                return true;
        }
        if(list.contains(task))
            return true;
        return false;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("toDoList.txt");
        file.createNewFile();
        Scanner readFile = new Scanner(file);
        Scanner readUser = new Scanner(System.in);
        TaskDeadline taskDeadline;
        Timer timer = null;
        if(readFile.hasNext()){
            while(readFile.hasNext()) {
                String listReader = readFile.nextLine();
                list.add(toArrayList(listReader));
            }
            timer = new Timer();
            LocalDateTime updatedTime;
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < list.size(); i++) {
                taskDeadline = new TaskDeadline(list.get(i));
                int deadlineHour = getHour(list.get(i).get(1));
                int deadlineMin = Math.abs(getMin(list.get(i).get(1)) - now.getMinute());
                if(now.getHour() <= deadlineHour)
                    timer.schedule(taskDeadline, ((deadlineHour - now.getHour())* 3600000L+ deadlineMin*60000L)-3600000L);
            }

            printList();
            System.out.println("Hi there! Is there something that you have done from ToDo list?(enter Y or N) " +
                    "\nOr is there something that you want to add/delete/edit(enter A, D or E)");
            String answer = readUser.nextLine();
            ArrayList<String> task;
            if(answer.equalsIgnoreCase("a")) {
                System.out.println("Enter the task that you want to add to the list");
                task = toArrayList(readUser.nextLine());
                insertTask(task, readUser);
            }
            else if(answer.equalsIgnoreCase("d")){
                System.out.println("Enter the task that you want to remove from the list");
                task = toArrayList(readUser.nextLine());
                removeTask(task);
            }
            else if(answer.equalsIgnoreCase("e")){
                System.out.println("Enter the task that you want to change(task name, timing)");
                task = toArrayList(readUser.nextLine());
                System.out.println("Enter the task that you want to enter instead of previous one(task name, timing)");
                ArrayList<String> task1 = toArrayList(readUser.nextLine());
                changeTask(task,task1);
            }else {
                if (answer.equalsIgnoreCase("N")) {
                    timer.cancel();
                    return;
                }
                while (answer.equalsIgnoreCase("Y")) {
                    System.out.println("Enter the name and timing of the task that you have done ");
                    ArrayList<String> s = toArrayList(readUser.nextLine());
                    ArrayList<String> finishedTask = findTaskByTimeAndName(s);
                    if (finishedTask == null){
                        timer.cancel();
                        return;
                }
                    printList(finishedTask);
                    System.out.println("Is there anything else that you have done from ToDo list?(Y or N)");
                    answer = readUser.nextLine();
                }

            }
//            Thread.sleep(5000);
        }else {
            System.out.println("There is no list written yet" +
                    "\nEnter tasks that you want to do today and this program will notify you if you miss doing it" +
                    "\nFirstly enter the task name than the deadline (enter 'S' if you want to stop inserting), " +
                    "\nGood luck with the tasks *◡*");
            String task = readUser.nextLine();
            while (!task.equalsIgnoreCase("S")) {
                list.add(toArrayList(task));
                task = readUser.nextLine();
            }
        }
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < 2; j++) {
                printWriter.print(list.get(i).get(j) + " ");
            }
            printWriter.print("\n");
        }
        printWriter.close();
        timer.cancel();
    }
    
    private static int getMin(String time){
        for (int i = 0; i < time.length(); i++) {
            if(time.charAt(i) == ':') {
                if (time.contains("✓"))
                    return Integer.parseInt(time.substring(i + 1, time.length() - 4).strip());
                return Integer.parseInt(time.substring(i+1).strip());
            }
        }
        return 0;
    }

    private static int getHour(String time){
        if(isNumeric(time.charAt(1)+""))
            return Integer.parseInt(time.substring(0,2));
        return Integer.parseInt(time.substring(0,1).strip());
    }
    private static ArrayList<String> findTaskByTimeAndName(ArrayList<String> task){

        for (int i = 0; i < list.size(); i++) {
            if(sameTiming(list.get(i), task.get(1)) && list.get(i).get(0).strip().equalsIgnoreCase(task.get(0).strip()))
                return list.get(i);
        }
        System.out.println("There is no task at that timing and with that name");
        return null;
    }

    private static ArrayList<String> toArrayList(String task){
        ArrayList<String> resultedAction = new ArrayList<>();
        for (int i = 0; i < task.length(); i++) {
            if(isNumeric(task.charAt(i)+"") && (i > 0)) {
                resultedAction.add(task.substring(0, i - 1));
                resultedAction.add(task.substring(i));
                return resultedAction;
            }
        }
        return resultedAction;
    }

    private static void printList( ){
        printList(null);
    }
    private static void printList(ArrayList<String> task){

        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i).get(0) + " deadline: "+ list.get(i).get(1));
            if(task != null && !task.get(1).contains("✓") && sameTiming(list.get(i), task.get(1)) &&
                    list.get(i).get(0).strip().equalsIgnoreCase(task.get(0).strip())) {
                System.out.print("   ✓");
                list.get(i).set(1, list.get(i).get(1)+ "   ✓");
            }
            System.out.println( );

        }

    }

}
