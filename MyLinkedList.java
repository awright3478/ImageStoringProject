package uk.ac.nulondon;

public class MyLinkedList {
    /**
     * first pixel in MyLinkedList
     * */
    public Node<Pixel> head;
    /**
     * constructor, passes in head
     * */
    public MyLinkedList(Pixel myPixel){
        head = new Node<Pixel>(myPixel);
    }
    /**
     * default constructor
     * */
    public MyLinkedList(){
        head = null;
    }
    /**
     * addPixel method, if head isnt initialized head becomes addition, else iterates to end and appends
     * */
    public void add(Pixel addition){
        if(head == null){
            head = new Node<Pixel>(addition);
        }
        Node<Pixel> iter = head;
        while(iter.next != null){
            iter = iter.next;
        }
        iter.next = new Node<Pixel>(addition, iter);
    }

}
