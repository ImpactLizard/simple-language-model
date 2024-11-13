public record Pair<L, R>(L left, R right) {
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
