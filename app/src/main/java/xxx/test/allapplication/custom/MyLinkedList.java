package xxx.test.allapplication.custom;

/**
 * Created by liujun on 17/6/5.
 */

public class MyLinkedList<E> {
    Node<E> head;
    Node<E> current;
    public MyLinkedList() {
    }

    public void add(E e){
        Node<E> newNode = new Node<>(e,null);
        if(head == null){
            head = new Node<>(null,newNode);
        }else {
            current.next = newNode;
        }
        current = newNode;
    }

    public void print(){
        System.out.print("{");
        Node<E> p = head;
        while (p.next != null){
            System.out.print(p.next.item+",");
            p = p.next;
        }
        System.out.print("}");

    }

    private static class Node<E>{
        E item;
        Node<E> next;

        public Node(E item, Node<E> next){
            this.item = item;
            this.next = next;
        }
    }

    public static void main(String[] args){
        MyLinkedList<String> linkedList = new MyLinkedList<>();
        linkedList.add("i");
        linkedList.add("love");
        linkedList.add("dog");
        linkedList.print();
    }
}
