import java.util.ArrayList;
import java.util.List;

class NaryTreeNode<T> {
    T data;
    List<NaryTreeNode<T>> children;

    public NaryTreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void addChild(NaryTreeNode<T> child) {
        this.children.add(child);
    }
}




public class NaryTree<T> {
    private NaryTreeNode<T> root;

    public NaryTree(T rootData) {
        this.root = new NaryTreeNode<>(rootData);
    }

    public NaryTreeNode<T> getRoot() {
        return root;
    }

    public void addNode(NaryTreeNode<T> parent, T childData) {
        NaryTreeNode<T> childNode = new NaryTreeNode<>(childData);
        parent.addChild(childNode);
    }

    // Preorder traversal
    public void preorderTraversal(NaryTreeNode<T> node) {
        if (node == null) return;

        // Visit the node
        System.out.print(node.data + " ");

        // Visit all the children
        for (NaryTreeNode<T> child : node.children) {
            preorderTraversal(child);
        }
    }

    // Postorder traversal
    public void postorderTraversal(NaryTreeNode<T> node) {
        if (node == null) return;

        // Visit all the children
        for (NaryTreeNode<T> child : node.children) {
            postorderTraversal(child);
        }

        // Visit the node
        System.out.print(node.data + " ");
    }

    // Level order traversal
    public void levelOrderTraversal() {
        if (root == null) return;

        Queue<NaryTreeNode<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            NaryTreeNode<T> node = queue.poll();
            System.out.print(node.data + " ");

            for (NaryTreeNode<T> child : node.children) {
                queue.add(child);
            }
        }
    }

    // Find node by value
    public NaryTreeNode<T> find(NaryTreeNode<T> node, T value) {
        if (node == null) return null;

        if (node.data.equals(value)) return node;

        for (NaryTreeNode<T> child : node.children) {
            NaryTreeNode<T> result = find(child, value);
            if (result != null) return result;
        }

        return null;
    }

    public static void main(String[] args) {
        NaryTree<Integer> tree = new NaryTree<>(1);
        NaryTreeNode<Integer> root = tree.getRoot();
        
        tree.addNode(root, 2);
        tree.addNode(root, 3);
        tree.addNode(root, 4);

        NaryTreeNode<Integer> node2 = tree.find(root, 2);
        NaryTreeNode<Integer> node3 = tree.find(root, 3);
        NaryTreeNode<Integer> node4 = tree.find(root, 4);

        tree.addNode(node2, 5);
        tree.addNode(node2, 6);
        tree.addNode(node3, 7);
        tree.addNode(node3, 8);
        tree.addNode(node4, 9);
        tree.addNode(node4, 10);

        System.out.println("Preorder Traversal:");
        tree.preorderTraversal(root);

        System.out.println("\nPostorder Traversal:");
        tree.postorderTraversal(root);

        System.out.println("\nLevel Order Traversal:");
        tree.levelOrderTraversal();
    }
}
