package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int oldIndex = 0;
        for (int i = 0; i < size; i++) {
            oldIndex = (nextFirst + 1 + i) % items.length;
            newItems[i] = items[oldIndex];
        }
        items = newItems;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int index = (nextFirst + 1) % items.length;
        while (index != nextLast) {
            System.out.print(items[index] + " ");
            index = (index + 1) % items.length;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16 && size * 4 <= items.length) {
            resize(items.length / 2);
        }
        nextFirst = (nextFirst + 1) % items.length;
        size -= 1;
        return items[nextFirst];
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16 && size * 4 <= items.length) {
            resize(items.length / 2);
        }
        nextLast = (nextLast - 1 + items.length) % items.length;
        size -= 1;
        return items[nextLast];
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        index = (nextFirst + 1 + index) % items.length;
        return items[index];
    }

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 7;
        nextLast = 0;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
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
        } else {
            return false;
        }

        return true;
    }
}
