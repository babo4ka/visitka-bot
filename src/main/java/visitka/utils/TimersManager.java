package visitka.utils;

import utils.tuples.Pair;
import visitka.utils.functionalInterfaces.MessagesDumpClearer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimersManager {
    private final long delay = 12 * 60 * 60 * 1000;

    private final MessagesDumpClearer messagesDumpClearer;

    public TimersManager(MessagesDumpClearer messagesDumpClearer){
        this.messagesDumpClearer = messagesDumpClearer;
    }

    Map<Long, Pair<Timer, TimerTask>> tasksOfUsers = new HashMap<>();

    public void scheduleTaskForUser(long chatId){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                messagesDumpClearer.clear(chatId);
            }
        };

        var userTask = tasksOfUsers.get(chatId);

        Timer timer = new Timer(chatId + " task");
        timer.schedule(task, delay);

        if(userTask == null)
            tasksOfUsers.put(chatId, new Pair<>(timer, task));
        else{
            userTask.getFirst().cancel();
            userTask.setFirst(timer);
        }
    }
}
