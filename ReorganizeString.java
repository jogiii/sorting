import java.util.PriorityQueue;

/**
 * Problem: Reorganize String
 * ----------------------------
 * Given a string s, rearrange its characters so that no two ADJACENT
 * characters are the same. Return any valid rearrangement, or "" if
 * it's impossible.
 *
 * Example 1:
 *   Input:  s = "axyy"
 *   Output: "xyay"   (any valid arrangement is accepted)
 *
 * Example 2:
 *   Input:  s = "abbccdd"
 *   Output: "abcdbcd"
 *
 * Example 3:
 *   Input:  s = "ccccd"
 *   Output: ""       (impossible - 'c' appears 4 times but n=5,
 *                      max allowed for any char is ceil(5/2) = 3)
 *
 * ------------------------------------------------------------------------
 * KEY INSIGHT: When is it even POSSIBLE?
 * ------------------------------------------------------------------------
 * If the most frequent character appears more than ceil(n/2) times, it's
 * impossible to separate all its occurrences with different characters
 * (there just aren't enough "gaps" to place them in). Otherwise, it's
 * always possible.
 *
 * Example: "ccccd" -> n=5, 'c' appears 4 times. ceil(5/2) = 3. Since 4 > 3,
 * impossible -> return "".
 *
 * ------------------------------------------------------------------------
 * APPROACH: Greedy + Max-Heap (always place the MOST frequent remaining
 * character next, as long as it's not the same as the last placed one)
 * ------------------------------------------------------------------------
 * 1. Count frequency of each character (26 lowercase letters).
 * 2. Push all characters with count > 0 into a MAX-HEAP keyed by frequency
 *    (so the most frequent character is always on top).
 * 3. Repeatedly:
 *      a. Pop the most frequent character (call it `current`).
 *      b. If it's the SAME as the last character we appended to result,
 *         we can't use it yet - it needs a subsequent character.
 *         Peek the NEXT... simplest approach:
 *         - Pop `current` (most frequent).
 *         - If result is non-empty and last char == current's char, we
 *           instead need a "buffer" trick: hold the previous popped
 *           character back and push it AFTER we place a different one.
 *      Actual clean approach (used below):
 *      a. Pop the most frequent character `first`.
 *      b. Append it to result. Decrement its count.
 *      c. If the heap is non-empty, pop the next most frequent `second`
 *         (guaranteed different from `first` UNLESS heap only has one
 *         distinct char left with count > 1, which is the impossible case).
 *         Append `second`. Decrement its count.
 *      d. Push back `first` (if count > 0) and `second` (if count > 0)
 *         into the heap.
 *      e. Repeat until heap is empty.
 *   This "always pick top-2 most frequent and alternate them" strategy
 *   naturally avoids placing the same character twice in a row, because
 *   we always interleave with the second-most-frequent remaining char.
 *
 * ------------------------------------------------------------------------
 * DIAGRAM WALKTHROUGH for s = "aab"
 * ------------------------------------------------------------------------
 *   counts: a=2, b=1
 *   heap (by freq desc): [a:2, b:1]
 *
 *   Round 1:
 *     pop first = a (freq 2) -> append 'a' -> result = "a", a becomes freq 1
 *     pop second = b (freq 1) -> append 'b' -> result = "ab", b becomes freq 0
 *     push back a(freq1) since >0; b(freq0) NOT pushed back
 *     heap = [a:1]
 *
 *   Round 2:
 *     pop first = a (freq 1) -> append 'a' -> result = "aba", a becomes freq 0
 *     heap empty, no second to pop
 *     nothing pushed back
 *     heap = []
 *
 *   Done. result = "aba"  (valid: no two adjacent same chars) ✔
 *
 * ------------------------------------------------------------------------
 * DIAGRAM WALKTHROUGH for s = "ccccd"  (impossible case)
 * ------------------------------------------------------------------------
 *   counts: c=4, d=1
 *   heap: [c:4, d:1]
 *
 *   Round 1: pop first=c(4)->append 'c', c=3. pop second=d(1)->append 'd', d=0.
 *            push back c(3). result="cd", heap=[c:3]
 *   Round 2: pop first=c(3)->append 'c', c=2. heap empty, no second to pop!
 *            -> Here's the check: we appended 'c' again right after 'c'
 *               was NOT just appended (last char was 'd'), so "cd" + "c"
 *               = "cdc" is still fine so far... but now c=2 remains and
 *               heap is empty -> we can't continue safely.
 *            Actually the algorithm detects failure when: after popping
 *            `first`, the heap is empty AND first's remaining count > 1
 *            (meaning we'd be forced to place it adjacent to itself next).
 *            Here after Round 2, c still has count 2 but nothing else
 *            left to interleave with -> IMPOSSIBLE -> return "".
 *
 * Time Complexity:  O(n log k) where k = number of distinct characters (<=26),
 *                    so effectively O(n).
 * Space Complexity: O(k) for the heap/counts, O(n) for the result string.
 */
public class ReorganizeString {

    public String reorganizeString(String s) {
        int n = s.length();

        // Step 1: Count frequency of each lowercase letter.
        int[] counts = new int[26];
        for (char c : s.toCharArray()) {
            counts[c - 'a']++;
        }

        // Step 1b: Quick feasibility check - if any character's count
        // exceeds ceil(n/2), it's impossible to arrange without repeats.
        int maxAllowed = (n + 1) / 2; // ceil(n/2)
        for (int count : counts) {
            if (count > maxAllowed) {
                return "";
            }
        }

        // Step 2: Max-heap keyed by frequency (most frequent char on top).
        // Each heap entry is [count, charIndex] (charIndex = 0..25 for 'a'..'z').
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
        for (int idx = 0; idx < 26; idx++) {
            if (counts[idx] > 0) {
                maxHeap.offer(new int[]{counts[idx], idx});
            }
        }

        StringBuilder result = new StringBuilder(n);

        // Step 3: Greedily place the two most frequent remaining characters
        // in each round, alternating them so the same char never repeats
        // back-to-back.
        while (!maxHeap.isEmpty()) {
            int[] first = maxHeap.poll();

            // Place `first`'s character, then decrement its remaining count.
            result.append((char) ('a' + first[1]));
            first[0]--;

            if (maxHeap.isEmpty()) {
                // No second character available to interleave with.
                // If `first` still has more than 0 occurrences left,
                // it would have to sit next to itself -> impossible.
                if (first[0] > 0) {
                    return "";
                }
                // Otherwise we're done (this was the very last character).
                break;
            }

            int[] second = maxHeap.poll();
            result.append((char) ('a' + second[1]));
            second[0]--;

            // Push both back into the heap if they still have remaining count.
            if (first[0] > 0) {
                maxHeap.offer(first);
            }
            if (second[0] > 0) {
                maxHeap.offer(second);
            }
        }

        return result.toString();
    }

    // Simple manual tests to demonstrate the algorithm.
    public static void main(String[] args) {
        ReorganizeString solution = new ReorganizeString();

        System.out.println(solution.reorganizeString("axyy"));    // e.g. "xyay" or "xyya"->invalid, any valid one
        System.out.println(solution.reorganizeString("abbccdd")); // e.g. "abcdbcd"
        System.out.println(solution.reorganizeString("ccccd"));   // "" (impossible)
        System.out.println(solution.reorganizeString("aab"));     // "aba"
    }
}

