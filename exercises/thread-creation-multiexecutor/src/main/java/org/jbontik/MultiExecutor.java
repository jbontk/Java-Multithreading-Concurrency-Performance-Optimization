package org.jbontik;


import java.util.List;
import java.util.Objects;

public class MultiExecutor {


    private final List<Runnable> tasks;

    /**
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
        this.tasks = Objects.requireNonNull(tasks);
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        tasks.parallelStream().forEach(t -> new Thread(t).start());
    }
}