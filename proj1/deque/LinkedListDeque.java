package deque;

import jh61b.junit.In;

public class LinkedListDeque<T> {
    private class IntNode {
        public T item;
        public IntNode prev;
        public IntNode next;

        private IntNode(T i, IntNode p, IntNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private IntNode sentinel;
    private int size;

    public void addFirst(T item) {
        if (size == 0) {
            IntNode temp = new IntNode(item, sentinel, sentinel);
            sentinel.prev = temp;
            sentinel.next = temp;
        } else {
            IntNode temp = new IntNode(item, sentinel, sentinel.next);
            sentinel.next.prev = temp;
            sentinel.next = temp;
        }
        size += 1;
    }

    public void addLast(T item) {
        if (size == 0) {
            IntNode temp = new IntNode(item, sentinel, sentinel);
            sentinel.prev = temp;
            sentinel.next = temp;
        } else {
            IntNode temp = new IntNode(item, sentinel.prev, sentinel);
            sentinel.prev.next = temp;
            sentinel.prev = temp;
        }
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        IntNode temp = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T value = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return value;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T value = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return value;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        IntNode temp = sentinel.next;
        while (index > 0) {
            temp = temp.next;
            index--;
        }
        return temp.item;
    }

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        size = 0;
    }

    public T getRecursive(int index) {
        IntNode pointer = sentinel.next;
        return getRecursiveHelper(index, pointer);
    }

    private T getRecursiveHelper(int index, IntNode pointer) {
        if (index == 0) {
            return pointer.item;
        }
        return getRecursiveHelper(index - 1, pointer.next);
    }
}
