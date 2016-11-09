public class Pair {
    private final String name;
    private final TreeNode value;

    public Pair(String name, TreeNode value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public TreeNode getValue() {
        return value;
    }
}
