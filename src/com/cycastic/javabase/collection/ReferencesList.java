package com.cycastic.javabase.collection;

import java.util.Iterator;
import java.util.function.Consumer;

public class ReferencesList<T> implements Iterable<ReferencesList.Element<T>> {
    private static class ReferencesHolder<T>{
        public Element<T> first = null;
        public Element<T> last = null;
        public int _size = 0;

        public boolean erase(Element<T> elem){
            if (elem.getHolder() != this) return false;
            if (first == elem){
                first = elem.next();
            }
            if (last == elem){
                last = elem.prev();
            }
            if (elem.prev() != null) {
                Element<T> e_prev = elem.prev();
                e_prev.setNext(elem.next());
            }
            if (elem.next() != null){
                Element<T> e_next = elem.next();
                e_next.setPrev(elem.prev());
            }
            _size -= 1;
            return true;
        }
    }
    public static class Element<T> implements Iterator<Element<T>> {
        private T value;
        private Element<T> _next = null;
        private Element<T> _prev = null;
        private final ReferencesHolder<T> holder;

        public Element(ReferencesHolder<T> holder) { this.holder = holder; }

        public ReferencesHolder<T> getHolder() { return holder; }
        @Override
        public boolean hasNext() {
            return _next != null;
        }
        public boolean hasPrev(){
            return _prev != null;
        }
        @Override
        public Element<T> next() {
            return _next;
        }
        Element<T> prev(){
            return _prev;
        }
        public void setNext(Element<T> n) { _next = n; }
        public void setPrev(Element<T> p) { _prev = p; }
        public T getValue() { return value; }
        public void setValue(T val) { value = val; }
        @Override
        public void remove() {
            holder.erase(this);
        }
    }

    private final ReferencesHolder<T> holder;

    public ReferencesList(){
        holder = new ReferencesHolder<>();
    }

    public int size() { return holder._size; }
    public boolean remove(T value){
        Element<T> iter = find(value);
        if (iter == null) return false;
        return holder.erase(iter);
    }
    public Element<T> find(T value){
        Element<T> iter = holder.first;
        while (iter != null) {
            if (iter.getValue() == value) return  iter;
            iter = iter.next();
        }
        return null;
    }
    public boolean erase(Element<T> elem){
        return holder.erase(elem);
    }
    public void add(T value) { pushBack(value); }
    public Element<T> pushBack(T value){
        Element<T> elem = new Element<>(holder);
        elem.setValue(value);
        if (holder.first == null){
            holder.first = elem;
        } else {
            holder.last.setNext(elem);
            elem.setPrev(holder.last);
        }
        holder.last = elem;
        holder._size += 1;
        return elem;
    }
    public Element<T> pushFront(T value){
        Element<T> elem = new Element<>(holder);
        elem.setValue(value);
        if (holder.last == null){
            holder.last = elem;
        } else {
            holder.first.setPrev(elem);
            elem.setNext(holder.first);
        }
        holder.first = elem;
        holder._size += 1;
        return elem;
    }
    public Element<T> pushBefore(T value, Element<T> anchor){
        if (anchor == null || anchor.getHolder() != holder) return null;
        Element<T> elem = new Element<>(holder);
        elem.setValue(value);
        if (anchor == holder.first){
            holder.first = elem;
        } else {
            elem.setPrev(anchor.prev());
            elem.prev().setNext(elem);
        }
        anchor.setPrev(elem);
        elem.setNext(anchor);
        holder._size += 1;
        return elem;
    }
    public Element<T> pushAfter(T value, Element<T> anchor){
        if (anchor.getHolder() != holder) return null;
        Element<T> elem = new Element<>(holder);
        elem.setValue(value);
        if (anchor == holder.last){
            holder.last = elem;
        } else {
            elem.setNext(anchor.next());
            elem.next().setPrev(elem);
        }
        anchor.setNext(elem);
        elem.setPrev(anchor);
        return elem;
    }

    @Override @Deprecated
    public Iterator<Element<T>> iterator() {
        return holder.first;
    }
    public Element<T> first() { return holder.first; }
    public Element<T> last() { return holder.last; }
    @Override
    public void forEach(Consumer<? super Element<T>> action) {
        Element<T> iter = holder.first;
        while (iter != null) {
            action.accept(iter);
            iter = iter.next();
        }
    }
    public String toString(){
        StringBuilder builder = new StringBuilder("ReferencesList[ ");
        Element<T> iter = holder.first;
        while (iter != null) {
            builder.append(iter.getValue().toString());
            if (iter.hasNext()) builder.append(", ");
            iter = iter.next();
        }
        builder.append(" ]");
        return builder.toString();
    }
}
