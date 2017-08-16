package xxx.test.allapplication.custom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 17/2/22.
 */

public class HuffmanTree {
    public static class Node<T>{
        T data;
        double weight;
        Node<T> left;
        Node<T> right;

        public Node(T data, double weight) {
            this.data = data;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Node[data = +"+data+"weight = "+weight+"]";
        }
    }
    private static Node huffman(List<Node> list) {
        quickSort(list,0,list.size()-1);
        return null;
    }

    private static void quickSort(List<Node> list,int start,int end) {
        if(start < end){
            Node base = list.get(start);
            while (list.get(++start).weight>base.weight);

        }
    }

    public static void main(String []a){
        List<Node> list = new ArrayList<>();

        list.add(new Node<String>("A",40.0));
        list.add(new Node<String>("B",8.0));
        list.add(new Node<String>("C",10.0));
        list.add(new Node<String>("D",30.0));
        list.add(new Node<String>("E",10.0));
        list.add(new Node<String>("F",2.0));

        Node root = huffman(list);
    }


}
