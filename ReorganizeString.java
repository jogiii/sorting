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

    public String reorganizeStringV1(String s) {
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

    /**
     * APPROACH 2: Bucket / Slot-Placement (no heap needed) - O(n) time
     * ------------------------------------------------------------------
     * Instead of repeatedly asking "what's the most frequent character
     * left?" via a heap, we exploit a key structural fact up front:
     *
     *   Once we know WHICH character is the most frequent (`letter`) and
     *   HOW MANY times it appears (`max`), we can place ALL of its
     *   occurrences directly into the EVEN indices of the result array
     *   (0, 2, 4, ...) first - guaranteed to never collide with itself,
     *   because even indices are never adjacent to each other.
     *
     *   Why is this always safe? Because we already checked
     *   `max <= ceil(n/2)`, and ceil(n/2) is EXACTLY the number of even
     *   indices available in an array of length n. So the majority
     *   character always fits perfectly into the even slots with no
     *   overflow.
     *
     * Once the majority character is placed, every remaining character
     * (including leftover instances of other, less-frequent characters)
     * gets poured into the SAME idx sequence (continuing +=2 each time),
     * wrapping around to the odd slots (starting at index 1) once we run
     * off the end of the array. Because no other character can possibly
     * have a count large enough to violate adjacency (that was the whole
     * point of the `max` feasibility check), this simple "keep walking by
     * 2, wrap to odd slots when you hit the end" strategy is guaranteed to
     * never place two identical characters next to each other.
     *
     * ------------------------------------------------------------------
     * STEP BY STEP
     * ------------------------------------------------------------------
     * 1. Count frequency of each of the 26 lowercase letters -> `hash[]`.
     * 2. Find the character with the MAX frequency -> `letter`, `max`.
     * 3. Feasibility check: if `max > ceil(n/2)`, it's impossible to
     *    separate all its occurrences -> return "".
     * 4. Walk `idx = 0, 2, 4, ...` placing `letter` into `res[idx]` for
     *    each of its `max` occurrences (draining `hash[letter]` to 0).
     * 5. For every other character (looping i = 0..25, including the
     *    now-drained majority letter, which is simply skipped since its
     *    count is already 0), keep placing remaining occurrences into
     *    `res[idx]`, incrementing `idx` by 2 each time. Whenever `idx`
     *    would run past the end of the array, wrap back to `idx = 1`
     *    (continuing to fill odd slots by +2 from there).
     * 6. Return the fully-filled array as a String.
     *
     * ------------------------------------------------------------------
     * DIAGRAM WALKTHROUGH for s = "aabb"  (n=4)
     * ------------------------------------------------------------------
     *   hash: a=2, b=2  ->  max=2 (first one found: 'a'), letter='a'
     *   maxAllowed = ceil(4/2) = 2. max(2) not > 2 -> feasible.
     *
     *   res = [_, _, _, _]   (indices 0,1,2,3)
     *
     *   Step 4 (place majority 'a' at even slots):
     *     idx=0: res[0]='a', hash[a]=1 remains -> idx becomes 2
     *     idx=2: res[2]='a', hash[a]=0 remains -> idx becomes 4
     *     hash[letter]-- > 0 now false (0 not > 0) -> loop stops
     *     res = [a, _, a, _]     idx currently = 4
     *
     *   Step 5 (place remaining chars, i loops 0..25):
     *     i='a': hash[a]=0, inner while doesn't run
     *     i='b': hash[b]=2
     *         idx=4 >= res.length(4) -> wrap: idx=1
     *         res[1]='b', hash[b]=1 remains -> idx becomes 3
     *         idx=3 < 4, ok
     *         res[3]='b', hash[b]=0 remains -> idx becomes 5
     *         hash[b]-- >0 now false -> inner loop stops
     *     res = [a, b, a, b]
     *
     *   Final result = "abab"   (valid: no two adjacent same chars) ✔
     *
     * ------------------------------------------------------------------
     * DIAGRAM WALKTHROUGH for s = "aab"  (n=3, odd length)
     * ------------------------------------------------------------------
     *   hash: a=2, b=1  ->  max=2, letter='a'
     *   maxAllowed = ceil(3/2) = 2. max(2) not > 2 -> feasible.
     *
     *   res = [_, _, _]   (indices 0,1,2)
     *
     *   Step 4: idx=0: res[0]='a', idx->2.  idx=2: res[2]='a', idx->4.
     *           hash[a] now 0 -> loop stops.
     *           res = [a, _, a]   idx currently = 4
     *
     *   Step 5: i='b': hash[b]=1
     *       idx=4 >= res.length(3) -> wrap: idx=1
     *       res[1]='b', hash[b]=0 remains -> idx becomes 3
     *       inner loop stops (hash[b] now 0)
     *       res = [a, b, a]
     *
     *   Final result = "aba"   ✔  (matches earlier trace)
     *
     * Why does the "wrap to idx=1" trick never cause a collision?
     *   Because by the time we wrap around, ALL even slots are already
     *   filled with the (safely-spaced) majority character. The only
     *   slots left empty are odd ones, which are naturally never adjacent
     *   to each other either - so pouring remaining characters into them
     *   in ANY order (even repeats of the same leftover character) can
     *   only ever place two same characters at odd indices i and i+2,
     *   which are NOT adjacent (there's always an even-slot character
     *   sitting between them). This is guaranteed safe precisely because
     *   no character (other than the already-placed majority one) can
     *   have a count large enough to need two adjacent slots - that was
     *   ruled out by the `max <= ceil(n/2)` feasibility check.
     *
     * Time Complexity:  O(n) - one pass to count, one pass (bounded by n)
     *                    to place the majority char, one pass (bounded by
     *                    n) to place everyone else. No heap, no log factor.
     * Space Complexity: O(1) extra (26-size hash array is constant) plus
     *                    O(n) for the output array/string.
     */
    public String reorganizeString(String s) {
        int[] hash = new int[26];
        for (int i=0; i<s.length(); i++) {
            hash[s.charAt(i) - 'a']++;
        }

        int max=0;
        int letter=0;
        for(int i=0;i<hash.length;i++) {
            if(hash[i]>max) {
                max=hash[i];
                letter=i;
            }
        }

        if(max >(s.length()+1)/2) return "";
        char[] res = new char[s.length()];

        //fill all the even places with majority character
        // Note: no idx-bounds/wrap check needed here - since max <= ceil(n/2),
        // the majority char's count can never exceed the number of even slots
        // (0, 2, 4, ...), so idx will always land within bounds during this loop.
        int idx = 0;
        while(hash[letter]-- > 0) {
            res[idx] = (char)(letter+'a');
            idx += 2;
        }

        // fill the remaining characters
        for(int i=0;i<hash.length;i++) {
            while(hash[i]-- >0){
                if(idx >= res.length) idx=1;
                res[idx] = (char)(i +'a');
                idx +=2;
            }
        }

        return new String(res);
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

