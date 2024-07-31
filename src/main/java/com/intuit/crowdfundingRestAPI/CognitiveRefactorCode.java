public class OwnershipTree {
    private OwnershipNode root;

    public OwnershipTree(OwnershipNode root) {
        this.root = root;
    }

    // Postorder traversal to calculate indirect ownership and mark "pep" status
    public void calculateIndirectOwnershipAndPepStatus() {
        calculateIndirectOwnershipAndPepStatusHelper(root, 1.0);  // Start with 100% ownership for root
    }

    private boolean calculateIndirectOwnershipAndPepStatusHelper(OwnershipNode node, double parentOwnership) {
        if (node == null) return false;

        // Calculate current node's indirect ownership
        node.indirectOwnership = parentOwnership * node.directOwnership;

        boolean isAnyChildPep = false;

        // Traverse all children
        for (OwnershipNode child : node.children) {
            boolean isChildPep = calculateIndirectOwnershipAndPepStatusHelper(child, node.indirectOwnership);
            isAnyChildPep = isAnyChildPep || isChildPep;
        }

        // If any child is "pep", mark this node as "pep"
        if (isAnyChildPep) {
            node.isPep = true;
        }

        // Return whether this node or any of its children is "pep"
        return node.isPep;
    }

    public void printOwnershipAndPepStatus(OwnershipNode node) {
        if (node == null) return;

        System.out.println(node.name + " - Direct: " + node.directOwnership + ", Indirect: " + node.indirectOwnership + ", Pep: " + node.isPep);

        for (OwnershipNode child : node.children) {
            printOwnershipAndPepStatus(child);
        }
    }

    public OwnershipNode getRoot() {
        return root;
    }

    public static void main(String[] args) {
        OwnershipNode A = new OwnershipNode("A", 1.0, false);
        OwnershipNode B = new OwnershipNode("B", 0.5, true);
        OwnershipNode C = new OwnershipNode("C", 0.5, false);
        OwnershipNode D = new OwnershipNode("D", 0.8, false);
        OwnershipNode E = new OwnershipNode("E", 0.2, false);
        OwnershipNode F = new OwnershipNode("F", 1.0, true);

        A.addChild(B);
        A.addChild(C);
        B.addChild(D);
        B.addChild(E);
        C.addChild(F);

        OwnershipTree tree = new OwnershipTree(A);
        tree.calculateIndirectOwnershipAndPepStatus();
        tree.printOwnershipAndPepStatus(tree.getRoot());
    }
}

------------------------------------
import java.util.ArrayList;
import java.util.List;

class OwnershipNode {
    String name;
    boolean isPep;
    List<OwnershipNode> children;

    public OwnershipNode(String name, boolean isPep) {
        this.name = name;
        this.isPep = isPep;
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

    // Postorder traversal to mark "pep" status
    public void markPepStatus() {
        markPepStatusHelper(root);
    }

    private boolean markPepStatusHelper(OwnershipNode node) {
        if (node == null) return false;

        boolean isAnyChildPep = false;

        // Traverse all children
        for (OwnershipNode child : node.children) {
            isAnyChildPep = markPepStatusHelper(child) || isAnyChildPep;
        }

        // If any child is "pep", mark this node as "pep"
        if (isAnyChildPep) {
            node.isPep = true;
        }

        // Return whether this node or any of its children is "pep"
        return node.isPep;
    }

    public void printPepStatus(OwnershipNode node) {
        if (node == null) return;

        System.out.println(node.name + " - Pep: " + node.isPep);

        for (OwnershipNode child : node.children) {
            printPepStatus(child);
        }
    }

    public OwnershipNode getRoot() {
        return root;
    }

    public static void main(String[] args) {
        OwnershipNode A = new OwnershipNode("A", false);
        OwnershipNode B = new OwnershipNode("B", true);
        OwnershipNode C = new OwnershipNode("C", false);
        OwnershipNode D = new OwnershipNode("D", false);
        OwnershipNode E = new OwnershipNode("E", false);
        OwnershipNode F = new OwnershipNode("F", true);

        A.addChild(B);
        A.addChild(C);
        B.addChild(D);
        B.addChild(E);
        C.addChild(F);

        OwnershipTree tree = new OwnershipTree(A);
        tree.markPepStatus();
        tree.printPepStatus(tree.getRoot());
    }
}

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
