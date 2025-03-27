package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class IntNode {
        private T item;
        private IntNode prev;
        private IntNode next;

        private IntNode(T i, IntNode p, IntNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private IntNode sentinel;
    private int size;

    @Override
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

    @Override
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode temp = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    @Override
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

    @Override
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

    @Override
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

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;

        LinkedListDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (o instanceof Deque) {
            Deque<T> uddao = (Deque<T>) o;
            if (size != uddao.size()) {
                return false;
            }

            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(uddao.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
