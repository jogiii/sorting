import java.util.Arrays;
import java.util.PriorityQueue;


// watch this https://www.youtube.com/watch?v=9zRXNLbl0FI
/**
 * Problem: Single-Threaded CPU
 * -----------------------------
 * You are given n tasks labeled 0..n-1 where tasks[i] = [enqueueTime, processingTime].
 * A single-threaded CPU processes tasks as follows:
 *   - If idle with no available tasks, it stays idle (jumps forward in time to the
 *     next task's enqueueTime).
 *   - If idle with available tasks, it picks the task with the SMALLEST processing
 *     time; ties broken by the SMALLEST original index.
 *   - Once started, a task runs to completion (no preemption).
 *
 * Return the order (original indices) in which tasks are processed.
 *
 * Example:
 *   tasks = [[1,4],[3,3],[2,1]]
 *   Output: [0,2,1]
 *
 * ------------------------------------------------------------------------
 * WALKTHROUGH / DIAGRAM for tasks = [[1,4],[3,3],[2,1]]  (revise this later!)
 * ------------------------------------------------------------------------
 * Index | enqueueTime | processingTime
 *   0   |      1      |       4
 *   1   |      3      |       3
 *   2   |      2      |       1
 *
 * Timeline:
 *   time:        0    1    2    3    4    5    6    7    8    9
 *                |    |    |    |    |    |    |    |    |    |
 *   Task0 arrives:    ^(enqueue=1)
 *   Task2 arrives:         ^(enqueue=2)
 *   Task1 arrives:              ^(enqueue=3)
 *
 *   CPU state:   idle |------ Task0 running (4) ------|-Task2-|------ Task1 running (3) ------|
 *                     t=1                              t=5    t=6                              t=9
 *
 * Step by step:
 *   t=0: CPU idle, nothing has arrived yet (Task0 arrives at t=1).
 *        -> CPU jumps forward to t=1 (no point simulating idle ticks).
 *
 *   t=1: Task0 arrives. It's the ONLY available task -> heap = [Task0(proc=4)].
 *        CPU picks Task0. Runs from t=1 to t=5 (takes 4 units).
 *        result so far = [0]
 *
 *   While Task0 runs (t=1 -> t=5), other tasks quietly arrive and wait in the heap:
 *        t=2: Task2 arrives -> heap = [Task2(proc=1)]
 *        t=3: Task1 arrives -> heap = [Task2(proc=1), Task1(proc=3)]
 *
 *   t=5: Task0 finishes. CPU looks at everyone waiting:
 *        heap = [Task1(proc=3), Task2(proc=1)]
 *        -> shortest processingTime wins -> Task2 (proc=1 < proc=3) chosen,
 *           even though Task1 ARRIVED earlier (t=3 < t=2)... wait, Task2 arrived
 *           earlier too (t=2 < t=3) - either way, shortest processing time rule
 *           decides it here.
 *        CPU picks Task2. Runs from t=5 to t=6 (takes 1 unit).
 *        result so far = [0, 2]
 *
 *   t=6: Task2 finishes. Only Task1 left in heap.
 *        CPU picks Task1. Runs from t=6 to t=9 (takes 3 units).
 *        result = [0, 2, 1]  <-- FINAL ANSWER
 *
 * Key intuition:
 *   - A task can't be picked before it "arrives" (enqueueTime).
 *   - Once started, a task blocks the CPU until it's fully done (no preemption),
 *     which is WHY Task2 (super short, proc=1) had to wait from t=2 until t=5
 *     even though it was quick - Task0 was already running and can't be interrupted.
 *   - When the CPU finally goes idle, it looks at ALL currently-waiting tasks and
 *     always picks the one with the smallest processingTime (tie -> smallest index).
 *     This is exactly what min-heap.poll() gives us for free.
 * ------------------------------------------------------------------------
 *
 * Approach (Sort + Min-Heap simulation):
 * ---------------------------------------
 * 1. Keep track of each task's original index, since the output/order depends on
 *    indices but we need to sort tasks by enqueueTime to know what's "available".
 * 2. Sort tasks by enqueueTime (ascending). This lets us walk through time and know
 *    which tasks have "arrived" so far.
 * 3. Use a min-heap (priority queue) ordered by (processingTime, originalIndex).
 *    This heap represents tasks that have arrived but not yet processed - the CPU
 *    always wants to pick the shortest processing time (tie -> smallest index),
 *    exactly matching the heap's ordering.
 * 4. Simulate the timeline with a pointer `i` into the enqueueTime-sorted array and
 *    a `currentTime` variable:
 *      - While the next task (by enqueueTime) hasn't arrived yet AND the heap is
 *        empty, the CPU is idle -> fast-forward currentTime to that task's
 *        enqueueTime (no point simulating idle ticks one by one).
 *      - Push all tasks that have arrived (enqueueTime <= currentTime) into the heap.
 *      - Pop the best task (shortest processing time, smallest index) from the heap,
 *        record it in the result, and advance currentTime by its processingTime
 *        (since the CPU runs it to completion).
 *      - Repeat until both the array is exhausted and the heap is empty.
 * 5. Return the recorded order of original indices.
 *
 * Why sort by enqueueTime? So we always know, in the correct chronological order,
 * which new tasks become available as currentTime advances.
 *
 * Why a min-heap keyed by (processingTime, index)? Because at any idle moment the
 * CPU always wants the "shortest job next" among *available* tasks, and the heap
 * gives us that in O(log n) time as tasks flow in and out.
 *
 * Time Complexity:  O(n log n) - each task is pushed/popped from the heap once,
 *                    plus the initial sort.
 * Space Complexity: O(n) - for the heap, result array, and sorted task copies.
 */
public class SingleThreadedCPU {

    public int[] getOrder(int[][] tasks) {
        int n = tasks.length;

        // Step 1: Build an array of [enqueueTime, processingTime, originalIndex]
        // so we don't lose track of each task's original index after sorting.
        int[][] indexedTasks = new int[n][3];
        for (int i = 0; i < n; i++) {
            indexedTasks[i][0] = tasks[i][0]; // enqueueTime
            indexedTasks[i][1] = tasks[i][1]; // processingTime
            indexedTasks[i][2] = i;           // originalIndex
        }

        // Step 2: Sort tasks by enqueueTime so we can process arrivals in order.
        Arrays.sort(indexedTasks, (a, b) -> a[0] - b[0]);

        // Step 3: Min-heap of available (arrived, not yet processed) tasks.
        // Ordered by processingTime first, then originalIndex, to match the
        // CPU's "shortest job, smallest index on tie" selection rule.
        PriorityQueue<int[]> available = new PriorityQueue<>((a, b) -> {
            if (a[1] != b[1]) return a[1] - b[1]; // shorter processingTime first
            return a[2] - b[2];                    // tie-break: smaller index first
        });

        int[] result = new int[n];
        int resultIndex = 0; // where to write the next chosen task in result[]
        int i = 0;           // pointer into indexedTasks (sorted by enqueueTime)
        long currentTime = 0; // use long to be safe with large enqueue/processing times

        // Step 4: Simulate until we've scheduled every task.
        while (resultIndex < n) {

            // If no tasks are currently available and the next task hasn't
            // arrived yet, the CPU is idle -> jump forward in time to when
            // the next task becomes available (no need to tick one by one).
            if (available.isEmpty() && i < n && indexedTasks[i][0] > currentTime) {
                currentTime = indexedTasks[i][0];
            }

            // Push every task that has arrived by currentTime into the heap.
            while (i < n && indexedTasks[i][0] <= currentTime) {
                available.offer(indexedTasks[i]);
                i++;
            }

            // Pick the best available task: shortest processingTime, then
            // smallest original index.
            int[] chosen = available.poll();

            // Record this task's original index in the output order.
            result[resultIndex++] = chosen[2];

            // The CPU runs the chosen task to completion before doing anything else.
            currentTime += chosen[1];
        }

        return result;
    }

    // Simple manual test to demonstrate the algorithm.
    public static void main(String[] args) {
        SingleThreadedCPU solution = new SingleThreadedCPU();

        int[][] tasks1 = {{1, 4}, {3, 3}, {2, 1}};
        System.out.println(Arrays.toString(solution.getOrder(tasks1)));
        // Expected: [0, 2, 1]

        int[][] tasks2 = {{7, 10}, {7, 12}, {7, 5}, {7, 4}, {7, 2}};
        System.out.println(Arrays.toString(solution.getOrder(tasks2)));
        // Expected: [4, 3, 2, 0, 1]
    }
}

