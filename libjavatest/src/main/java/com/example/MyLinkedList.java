package com.example;

/**
 * Created by liujun on 17/6/5.
 */

public class MyLinkedList<E> {
    private Node<E> head;
    private Node<E> last;
    public MyLinkedList() {
    }

    public void add(E e){
        Node<E> newNode = new Node<>(e,null);
        if(head == null){
            head = newNode;
        }else {
            last.next = newNode;
        }
        last = newNode;
    }

    public void print(){
        System.out.print("{");
        Node<E> p = head;
        while (p != null){
            System.out.print(p.item+",");
            p = p.next;
        }
        System.out.print("}");

    }
    public void reverse(){
        Node<E> current = head;
        Node<E> prev = null;
        Node<E> next;
        while (current != null){
            next = current.next;
            if(next == null){//遍历到了最后一个
                head = current;
            }
            current.next = prev;
            prev = current;
            current = next;
        }
    }

    private static class Node<E>{
        E item;
        Node<E> next;

        Node(E item, Node<E> next){
            this.item = item;
            this.next = next;
        }
    }

    public static void main(String[] args){
        MyLinkedList<String> linkedList = new MyLinkedList<>();
        linkedList.add("i");
        linkedList.print();

        linkedList.reverse();
        linkedList.print();
    }
}
