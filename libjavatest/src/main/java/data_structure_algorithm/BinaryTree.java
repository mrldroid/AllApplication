package data_structure_algorithm;

import java.util.Stack;

/**
 * Created by liujun on 17/8/15.
 */

public class BinaryTree {
  private Node root;

  public BinaryTree() {
    createBinaryTree();
  }

  /**
   *        A
   *    B      C
   * D    E  F    G
   */
  private void createBinaryTree() {
    root = new Node(1, "A");
    Node node2 = new Node(2, "B");
    Node node3 = new Node(3, "C");
    Node node4 = new Node(4, "D");
    Node node5 = new Node(5, "E");
    Node node6 = new Node(6, "F");
    Node node7 = new Node(7, "G");
    root.left = node2;
    root.right = node3;
    node2.left = node4;
    node2.right = node5;
    node3.left = node6;
    node3.right = node7;
  }

  /**
   * 求二叉树高度
   */
  public int getHeight() {
    return getHeight(root);
  }

  private int getHeight(Node node) {
    if (node == null) {
      return 0;
    } else {
      int left = getHeight(node.left);
      int right = getHeight(node.right);
      return Math.max(left, right) + 1;
    }
  }

  /**
   * 获取二叉树的结点数
   */
  public int getSize() {
    return getSize(root);
  }

  private int getSize(Node node) {
    if (node == null) {
      return 0;
    } else {
      return 1 + getSize(node.left) + getSize(node.right);
    }
  }

  /**
   * 前序遍历(递归实现)
   */
  public void preOrderTraversalRecursive() {
    preOrderTraversal(root);
  }

  private void preOrderTraversal(Node node) {
    if (node == null) {
      return;
    }
    System.out.print(node.value);
    preOrderTraversal(node.left);
    preOrderTraversal(node.right);
  }

  /**
   * 前序遍历(非递归实现),使用栈,先把所有左节点入栈
   */
  public void preOderTraversalIteration() {
    Stack<Node> stack = new Stack<>();
    Node node = root;
    while (node != null || !stack.isEmpty()) {
      while (node != null) {
        System.out.print(node.value);
        stack.push(node);//所有左节点入栈
        node = node.left;
      }
      if (!stack.isEmpty()) {
        Node top = stack.pop();
        node = top.right;
      }
    }
  }

  /**
   * 中序遍历(递归实现)
   */
  public void midOrderTraversalRecursive() {
    midOrderTraversalRecursive(root);
  }

  private void midOrderTraversalRecursive(Node node) {
    if (node == null) return;
    midOrderTraversalRecursive(node.left);
    System.out.print(node.value);
    midOrderTraversalRecursive(node.right);
  }

  /**
   * 中序遍历(迭代实现),先把所有左节点压栈
   */
  public void midOrderTraversalIteration() {
    Stack<Node> stack = new Stack<>();
    Node node = root;
    while (node != null || !stack.isEmpty()) {
      while (node != null) {
        stack.push(node);
        node = node.left;//把全部左节点压栈
      }
      if (!stack.isEmpty()) {
        Node top = stack.pop();
        System.out.print(top.value);
        node = top.right;
      }
    }
  }

  /**
   * 后序遍历(递归)
   */
  public void postOrderTraversalRecursive() {
    postOrderTraversalRecursive(root);
  }

  private void postOrderTraversalRecursive(Node node) {
    if (node == null) return;
    postOrderTraversalRecursive(node.left);
    postOrderTraversalRecursive(node.right);
    System.out.print(node.value);
  }

  /**
   * 后序遍历(迭代)
   */
  public void postOrderTraversalIteration() {
    Stack<Node> stack = new Stack<>();
    Node node = root;
    while (node != null) {
      stack.push(node);
      node = node.left;//把全部左节点压栈
    }
    while (!stack.isEmpty()) {
      Node top = stack.peek();//查看当前栈顶
      if (top.right == null || top.rvisited == 1) {//栈顶没有右节点或者右节点已经被访问
        node = stack.pop();
        System.out.print(node.value);
      } else {//有右节点并且没有被访问
        top.rvisited = 1;
        Node right = top.right;
        while (right != null) {
          stack.push(right);//右节点入栈
          right = right.left;//往左下方走到尽头，将路径上所有元素入栈
        }
      }
    }
  }

  private static class Node {
    int index;
    String value;
    Node left, right;
    int rvisited;//rvisited==1代表right已被访问过

    public Node(int index, String value) {
      this.index = index;
      this.value = value;
    }
  }

  public static void main(String args[]) {
    BinaryTree binaryTree = new BinaryTree();
    System.out.println(binaryTree.getHeight());
    System.out.println(binaryTree.getSize());
    binaryTree.preOrderTraversalRecursive();
    System.out.println();
    binaryTree.preOderTraversalIteration();
    System.out.println();
    binaryTree.midOrderTraversalRecursive();
    System.out.println();
    binaryTree.midOrderTraversalIteration();
    System.out.println();
    binaryTree.postOrderTraversalRecursive();
    System.out.println();
    binaryTree.postOrderTraversalIteration();



  }
}
