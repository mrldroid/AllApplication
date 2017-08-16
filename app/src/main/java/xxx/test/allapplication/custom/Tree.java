package xxx.test.allapplication.custom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 17/2/21.
 */

public class Tree<E> {
    public class Node<T> {
        T data;
        int parent;

        public Node(T data, int parent) {
            this.data = data;
            this.parent = parent;
        }
    }

    private Node<E>[] nodes;
    private int nodeNum;
    private int treeSize;
    private final int DEFUALT_SIZE = 100;

    public Tree(E root) {
        treeSize = DEFUALT_SIZE;
        nodes = new Node[treeSize];
        nodes[0] = new Node<>(root, -1);
        nodeNum++;
    }

    private int findPosition(Node node) {
        for (int i = 0; i < treeSize; i++) {
            if (nodes[i] == node) {
                return i;
            }
        }
        return 0;
    }

    public void add(E child, Node parent) {
        for (int i = 0; i < treeSize; i++) {
            if (nodes[i] == null) {
                nodes[i] = new Node<>(child, findPosition(parent));
                nodeNum++;
                return;
            }
        }
        System.out.println("树已满");
    }

    public Node root() {
        return nodes[0];
    }

    public Node parent(Node node) {
        if (findPosition(node) != -1) {
            return nodes[node.parent];
        }
        return null;
    }

    public List<Node> child(Node parent) {
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < treeSize; i++) {
            if (nodes[i] == null) {
                break;
            }
            int parentPos = findPosition(parent);
            if (nodes[i].parent == parentPos) {
                list.add(nodes[i]);
            }
        }
        return list;
    }

//    public int deep(){
//        int deep = 0;
//        for(int i = 0; i < treeSize; i++){
//            if(nodes[i] == null){
//                return deep;
//            }
//
//        }
//    }
}
