import java.util.ArrayList;
import java.util.List;

class OwnershipNode {
    String name;
    double directOwnership;
    double indirectOwnership;
    List<OwnershipNode> children;

    public OwnershipNode(String name, double directOwnership) {
        this.name = name;
        this.directOwnership = directOwnership;
        this.indirectOwnership = 0;
        this.children = new ArrayList<>();
    }

    public void addChild(OwnershipNode child) {
        this.children.add(child);
    }
}

public class OwnershipTree {
    private OwnershipNode root;

    public OwnershipTree(OwnershipNode root) {
        this.root = root;
    }

    // Postorder traversal to calculate indirect ownership
    public void calculateIndirectOwnership() {
        calculateIndirectOwnershipHelper(root, 1.0);
    }

    private void calculateIndirectOwnershipHelper(OwnershipNode node, double parentOwnership) {
        // Calculate current node's indirect ownership
        node.indirectOwnership = parentOwnership * node.directOwnership;

        // Traverse all children
        for (OwnershipNode child : node.children) {
            calculateIndirectOwnershipHelper(child, node.indirectOwnership);
        }
    }

    public void printOwnership(OwnershipNode node) {
        if (node == null) return;
        
        System.out.println(node.name + " - Direct: " + node.directOwnership + ", Indirect: " + node.indirectOwnership);
        
        for (OwnershipNode child : node.children) {
            printOwnership(child);
        }
    }

    public OwnershipNode getRoot() {
        return root;
    }

    public static void main(String[] args) {
        OwnershipNode A = new OwnershipNode("A", 1.0);
        OwnershipNode B = new OwnershipNode("B", 0.5);
        OwnershipNode C = new OwnershipNode("C", 0.5);
        OwnershipNode D = new OwnershipNode("D", 0.8);
        OwnershipNode E = new OwnershipNode("E", 0.2);
        OwnershipNode F = new OwnershipNode("F", 1.0);

        A.addChild(B);
        A.addChild(C);
        B.addChild(D);
        B.addChild(E);
        C.addChild(F);

        OwnershipTree tree = new OwnershipTree(A);
        tree.calculateIndirectOwnership();
        tree.printOwnership(tree.getRoot());
    }
}
