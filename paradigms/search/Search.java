package search;

public class Search {
    private Search() {}

    public static int iterative(int[] a, int x) {
        return iterative(a, -1, a.length, x);
    }

    public static int recursive(int[] a, int x) {
        return recursive(a, -1, a.length, x);
    }

    // ⊐ a[-1] = +INF; a[n] = -INF
    private static int iterative(int a[], int l, int r, int x) {
        // Pred: -1 ≤ l ≤ r && a[l] ≥ a[r]
        // Inv : -1 ≤ l < i ≤ r ≤ n && a[l] > a[i] ≥ a[r]
        while (r - l > 1) {
            // Post: ∃ k: l < k < r

            // Pred: ∃ k: l < k < r
            int m = (l + r) / 2;
            // Post: l < m < r => k == m

            // Pred: a[l] ≥ a[m] ≥ a[r] && l < m < r
            if (a[m] <= x) {
                // Post: a[m] ≤ x && Pred' => l < i ≤ m

                // Pred: l < i ≤ m < r
                r = m;
                // Post: l < i ≤ m < r && r == m => l < i ≤ r
            } else { 
                // Post: a[m] > x && Pred' => m < i ≤ r

                // Pred: l < m < i ≤ r 
                l = m;
                // Post: l < m < i ≤ r && l == m => l < i ≤ r
            }
            // Post: l < i ≤ r
        }
        // Post: l < i ≤ r && r - l == 1 => i == r

        // Pred: i == r
        return r;
        // Post: ∃ min i : l < i <= r && x >= a[i]
    }
    
    // ⊐ a[-1] = +INF && a[n] = -INF
    private static int recursive(int a[], int l, int r, int x) {
        // Post: a[l] ≥ a[r] && l < r => l < i <= r

        // Pred: -1 ≤ l ≤ r && a[l] ≥ a[r]
        // Inv: -1 ≤ l < i ≤ r ≤ n && a[l] > a[i] ≥ a[r]
        if (r - l > 1) {
            // Post: ∃ k: l < k < r

            // Pred: ∃ k: l < k < r
            int m = (l + r) / 2;
            // Post: l < m < r => k == m

            // Pred: a[l] ≥ a[m] ≥ a[r] && l < m < r
            if (a[m] <= x) {
                // Post: a[m] ≤ x && Pred' => l < i ≤ m
                
                // Pred: Post' && r == m
                return recursive(a, l, m, x);
            } else {
                // Post: a[m] > x && Pred' => m < i ≤ r
                
                // Pred: Post' && l == m
                return recursive(a, m, r, x);
            }
        }
        // Post: l < i ≤ r && r - l == 1 => i == r

        // Pred: i == r
        return r;
        // Post: ∃ min i : l < i <= r && a[m] <= x
    }
}
