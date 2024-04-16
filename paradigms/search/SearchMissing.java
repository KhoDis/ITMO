package search;

import java.util.Objects;

public enum SearchMissing {
    RECURSIVE {
        @Override
        public int search(int[] a, int x) {
            Objects.requireNonNull(a);
            return search(a, -1, a.length, x);
        }

        // ⊐ a[-1] = +INF && a[n] = -INF
        // Pred: a != null && ∀i, j : i < j <=> a[i] >= a[j] && -1 ≤ l < r ≤ n
        private int search(int[] a, int l, int r, int x) {
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
                    return search(a, l, m, x);
                } else {
                    // Post: a[m] > x && Pred' => m < i ≤ r

                    // Pred: Post' && l == m
                    return search(a, m, r, x);
                }
            }
            // Post: l < i ≤ r && r - l == 1 => i == r

            // Pred: i == r
            if (r < a.length) {
                // Post: r >= 0 && a.length >= 0 => r != 0

                // Pred: r != 0
                if (a[r] == x) {
                    // Post: x exists => a[i] == x
                    return r;
                    // Post: ∃ min i : l < i <= r && x >= a[i]
                }
            }
            // Post: r == 0 || r == a.length || a[r] != x

            // Pred: a[r] != x => a[i] != x
            return -(1 + r);
            // Post: ∃ min i : l < i <= r && x >= a[i]
        }
    },
    ITERATIVE {
        @Override
        public int search(int[] a, int x) {
            Objects.requireNonNull(a);
            return search(a, -1, a.length, x);
        }

        // ⊐ a[-1] = +INF && a[n] = -INF
        // Pred: a != null && ∀i, j : i < j <=> a[i] >= a[j] && -1 ≤ l < r ≤ n
        private int search(int[] a, int l, int r, int x) {
            // Pred: -1 ≤ l ≤ r && a[l] ≥ a[r]
            // Inv: -1 ≤ l < i ≤ r ≤ n && a[l] > a[i] ≥ a[r]
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
            if (r < a.length) {
                // Post: r >= 0 && a.length >= 0 => r != 0

                // Pred: r != 0
                if (a[r] == x) {
                    // Post: x exists => a[i] == x
                    return r;
                    // Post: ∃ min i : l < i <= r && x >= a[i]
                }
            }
            // Post: r == 0 || r == a.length || a[r] != x

            // Pred: a[r] != x => a[i] != x
            return -(1 + r);
            // Post: ∃ min i : l < i <= r && x >= a[i]
        }
    };

    public abstract int search(int[] a, int x);
}