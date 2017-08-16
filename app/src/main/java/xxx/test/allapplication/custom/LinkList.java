package xxx.test.allapplication.custom;

/**
 * Created by neo on 17/2/20.
 */

public class LinkList<T> {

    private class Node{
        T data;
        Node next;

        public Node(){

        }

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
    private Node head;
    private Node tail;
    private int size;

    public LinkList(){
        head = null;
        tail = null;
        size = 0;
    }

    public int length(){
        return size;
    }

    public void add(T element){
        if(head == null){
            head = new Node(element,null);
            tail = head;
        }else {
            Node node = new Node(element,null);
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void addAtHeader(T element){
//        if(head == null){
//            head = new Node(element,null);
//            tail = head;
//        }else {
//            Node node = new Node(element,null);
//            node.next = head;
//            head = node;
//        }
        head = new Node(element,head);
        if(tail == null){
            tail = head;
        }
        size++;
    }

    public void insert(T element,int index){
        if(index>size-1){return;}
        Node current = head;
        Node currentBefore = head;//当前节点的前一个
        while (index > 0){
            currentBefore = current;
            current = current.next;
            index--;
        }
        currentBefore.next = new Node(element,current);
        size++;
    }
    public T get(int index){
        Node curent = head;
        while (index > 0){
            curent = curent.next;
            index--;
        }
        if(curent == null)return null;
        return curent.data;
    }

    private Node getNode(int index){
        Node curent = head;
        while (index > 0){
            curent = curent.next;
            index--;
        }
       return curent;
    }

    public void delete(int index){
        if(index>size-1){return;}
        Node current = head;
        Node currentBefore = head;//当前节点的前一个
        while (index > 0){
            currentBefore = current;
            current = current.next;
            index--;
        }
        currentBefore.next = current.next;
        size--;
    }

    public void reverse(){
//        int si = size;
//        System.out.println("........."+size);
//        Node tempHead = null;
//        Node tempTail = null;
//        while(si >1){
//            Node node = getNode(si - 1);
//            Node nodePre = getNode(si -2);
//
//            node.next = nodePre;
//            if(si == size){
//                tempHead = node;
//            }
//            if(si == 2){
//                tempTail = nodePre;
//            }
//
//            si--;
//        }
//        head = tempHead;
//        tail = tempTail;
        Node pre = null;
        Node next = null;
        while(head != null){
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
            head = pre;
    }

    public static void main(String []ar){
        LinkList<String> linkList = new LinkList<>();
        linkList.add("1");
        linkList.add("2");
        linkList.add("3");
        linkList.add("4");
        linkList.add("5");

        linkList.reverse();
        System.out.println(linkList.get(0));
        System.out.println(linkList.get(1));
        System.out.println(linkList.get(2));
        System.out.println(linkList.get(3));
        System.out.println(linkList.get(4));
    }
}
