package uk.ac.nulondon;

public class Node<Pixel>{
    /**
     * pixel field
     * */
    public Pixel myPixel;
    /**
     * previous pixel
     * */
    public Node<Pixel> prev;
    /**
     * next pixel
     * */
    public Node<Pixel> next;
    /**
     * Node constructor, passes in pixel
     * */
    public Node(Pixel p){
        myPixel = p;
    }
    /**
     * Node constructor, passes in pixel and previous
     * */
    public Node(Pixel p, Node<Pixel> previous){
        myPixel = p;
        prev = previous;
    }
    /**
     * function for setting next
     * */
    public void setNext(Node<Pixel> p){
        this.next = p;
    }
    /**
     * function for setting previous
     * */
    public void setPrev(Node<Pixel> p){
        this.prev = p;
    }
}
