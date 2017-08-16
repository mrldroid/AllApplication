package com.example;

public class MyClass {
    public static void print(Node head){
        while(head!=null){
            System.out.print(head.getVal());
            System.out.print(" ");
            head = head.next;
        }
        System.out.println();
    }
    public static Node reverse(Node head){
        Node ans = null;
        Node next = null;
        Node tmp = null;
        while(head!=null){
            tmp = head.next;
            head.next = next;
            next = head;
            head = tmp;
        }
        ans = next;
        return ans;
    }
    public static void main(String[] args) {

        Node tmpNode = new Node(1);
        Node head =tmpNode;
        for(int i = 2;i<=10;++i){
            Node node = new Node(i);
            tmpNode.next = node;
            tmpNode = node;
        }
        print(head);
        head = reverse(head);
        print(head);

    }

}
class Node{
    Node next;
    int val;
    public Node(int val){
        this.val = val;
    }
    public int getVal(){
        return val;
    }


}
