import java.util.*;

// LeetCode 621: Task Scheduler
// https://leetcode.com/problems/task-scheduler/
//
// Problem:
//   Given a char array `tasks` (each 'A'-'Z') and an integer n, identical
//   tasks must be separated by at least n cycles (cooldown). Return the
//   minimum total number of CPU cycles to finish all tasks (idle cycles
//   count too, if nothing else is ready to run).
//
// Example: tasks = ["X","X","Y","Y"], n = 2  ->  answer = 5
//   One optimal schedule: X -> Y -> idle -> X -> Y
//   (X and Y each need a gap of at least 2 cycles between repeats)
//
// Key idea: greedily run the MOST FREQUENT remaining task first each
// round, so frequent tasks get spread out as early as possible - this
// minimizes forced idle time.
//
// ------------------------------------------------------------------------
// SIMPLE WORDS MEIN SAMJHO
// ------------------------------------------------------------------------
// Jo task sabse zyada baar repeat hota hai, usko pehle chalao - taaki
// woh jaldi se jaldi spread ho jaaye aur baaki tasks ke saath cooldown
// (gap) manage karna easy ho jaaye. Max-heap mein saare tasks ki
// frequency daal do, aur har round mein (n+1) size ke blocks mein sabse
// zyada frequent tasks nikaal ke "chalao" (count kam karo). Agar heap
// khaali ho jaaye toh utna hi time lagega jitne tasks chalaye, warna
// poora (n+1) round ka time lagega (kuch idle/cooldown cycles ke saath).
public class TaskScheduler {

    // ------------------------------------------------------------------
    // Approach 1: Max-Heap simulation -> O(26 log 26 * (n+1)) ~= O(total)
    //   Intuitive: literally simulate scheduling in blocks of (n+1).
    //
    // Simple algorithm (revision notes):
    //   1) Build a frequency map: count how many times each task appears.
    //   2) Push all the frequency VALUES (not the letters themselves) into
    //      a max-heap - we only care about "how many are left", not which
    //      letter it is, since identical counts are interchangeable.
    //   3) Repeat, round by round, until the heap is empty. Each round is
    //      (n + 1) cycles wide (1 task slot + n cooldown/filler slots):
    //        a) Grab up to (n + 1) of the CURRENTLY most frequent tasks
    //           from the heap into `temp` (greedy: busiest tasks first,
    //           so they get spread out early and need less idle time).
    //        b) "Run" each task in `temp` once by decrementing its count;
    //           if it still has occurrences left, push it back onto the
    //           heap for a future round.
    //        c) Add this round's cost to `time`:
    //             - If the heap is now EMPTY, no other task is waiting,
    //               so we only spent as many cycles as tasks we actually
    //               ran (temp.size()) - no trailing idle cycles needed.
    //             - If the heap still has tasks, this round needed the
    //               FULL (n + 1) width (task slots + forced idle slots),
    //               because something is still waiting to cool down.
    //   4) Return the accumulated `time`.
    // ------------------------------------------------------------------
    public int leastIntervalHeap(char[] tasks, int n) {
        // 1) Count frequency of each task.
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char task : tasks) {
            freqMap.put(task, freqMap.getOrDefault(task, 0) + 1);
        }

        // 2) Max-heap of frequencies (we only care about counts, not which
        //    letter - order among tasks with equal frequency doesn't matter).
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        maxHeap.addAll(freqMap.values());

        int time = 0;

        // 3) Process in rounds of size (n + 1): each round, take up to
        //    (n + 1) of the currently most-frequent tasks, run them once,
        //    decrement their counts, and re-add any that still have work
        //    left for a future round.
        while (!maxHeap.isEmpty()) {
           List<Integer> temp = new ArrayList<>();
// Loop 1: grab up to (n+1) of the most frequent tasks (whatever's available)
           for(int i=0;i<n+1;i++){
               if(!maxHeap.isEmpty()){
                   temp.add(maxHeap.poll());
               }
           }
// Loop 2: decrement each grabbed count, re-add if still > 0
           for(int freq : temp){
               if(--freq >0){
                   maxHeap.add(freq);
               }
           }

           // If the heap is empty, there's no more work left, so this was
           // the LAST round - we only spent as many cycles as tasks we
           // actually ran (temp.size()), no trailing idle cycles needed.
           // If the heap still has tasks, this round was a FULL round of
           // (n + 1) cycles - any unfilled slots in `temp` (fewer than
           // n + 1 tasks polled) counted as forced idle cycles, because
           // there was still other work waiting that couldn't run yet
           // (still on cooldown).
           time = time + (maxHeap.isEmpty() ? temp.size() : n + 1);
        }

        return time;
    }

    // ------------------------------------------------------------------
    // Approach 2: Math formula -> O(n) time (just counting), O(1) space
    //   The "skeleton" is built around the MOST frequent task(s):
    //     (maxFreq - 1) full gaps, each of width (n + 1), plus however
    //     many distinct tasks are tied for maxFreq (they fill the final
    //     row alongside the last occurrence of the top task).
    //   If there are enough OTHER tasks to fill every gap, no idle time
    //   is needed at all, and the answer is simply tasks.length.
    // ------------------------------------------------------------------
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char task : tasks) {
            freq[task - 'A']++;
        }

        int maxFreq = 0;
        for (int f : freq) {
            maxFreq = Math.max(maxFreq, f);
        }

        int maxCount = 0; // how many letters share the max frequency
        for (int f : freq) {
            if (f == maxFreq) {
                maxCount++;
            }
        }

        int skeleton = (maxFreq - 1) * (n + 1) + maxCount;
        return Math.max(tasks.length, skeleton);
    }

    public static void main(String[] args) {
        TaskScheduler solution = new TaskScheduler();

        char[] tasks1 = {'X', 'X', 'Y', 'Y'};
        int n1 = 2;
        System.out.println(solution.leastInterval(tasks1, n1));      // Expected: 5
        System.out.println(solution.leastIntervalHeap(tasks1, n1));  // Expected: 5

        char[] tasks2 = {'A', 'A', 'A', 'B', 'B', 'B'};
        int n2 = 2;
        System.out.println(solution.leastInterval(tasks2, n2));      // Expected: 8
        System.out.println(solution.leastIntervalHeap(tasks2, n2));  // Expected: 8
        // A -> B -> idle -> A -> B -> idle -> A -> B

        char[] tasks3 = {'A', 'A', 'A', 'B', 'B', 'B'};
        int n3 = 0;
        System.out.println(solution.leastInterval(tasks3, n3));      // Expected: 6
        System.out.println(solution.leastIntervalHeap(tasks3, n3));  // Expected: 6
        // no cooldown needed -> just run all 6 tasks back to back

        char[] tasks4 = {'A', 'A', 'A', 'A', 'B', 'B', 'B'};
        int n4 = 2;
        System.out.println(solution.leastInterval(tasks4, n4));      // Expected: 10
        System.out.println(solution.leastIntervalHeap(tasks4, n4));  // Expected: 10
        // maxFreq=4 ('A'), maxCount=1 -> skeleton = (4-1)*(2+1) + 1 = 10
        // A B _ A B _ A B _ A  (3 idle slots left over after placing all B's)
    }
}

