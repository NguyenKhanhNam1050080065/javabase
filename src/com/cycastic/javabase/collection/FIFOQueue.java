package com.cycastic.javabase.collection;

public class FIFOQueue<T> {
    static class Node<T> {
        public final T value;
        public Node<T> next;
        public Node(T value){
            this.value = value;
            next = null;
        }
    }
    private Node<T> first;
    private Node<T> last;
    private int cachedSize = 0;
    public int size(){
        return cachedSize;
    }
    public boolean isEmpty(){
        return size() == 0;
    }
    public void enqueue(T value){
        Node<T> newNode = new Node<>(value);
        if (first == null || last == null){
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        cachedSize++;
    }
    public T dequeue(){
        Node<T> iter = first;
        if (iter == null) throw new IndexOutOfBoundsException();
        T returnValue = iter.value;
        first = iter.next;
        if (first == null) last = null;
        cachedSize--;
        return returnValue;
    }
}
