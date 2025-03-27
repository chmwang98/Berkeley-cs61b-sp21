package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;


    public void addFirst(T item) {
        if (size == items.length) {
            resize((int)(size * 1.2));
        }
        items[nextFirst] = item;
        nextFirst = indexMinusOne(nextFirst);
        size += 1;
    }

    private int indexMinusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize((int)(size * 1.2));
        }
        items[nextLast] = item;
        nextLast = indexAddOne(nextLast);
        size += 1;
    }

    private int indexAddOne(int index) {
        return (index + 1) % items.length;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for(int i = 0; i < size; i++) {
            newItems[i] = get(i);
        }
        items = newItems;
        nextFirst = indexMinusOne(0);
        nextLast = indexAddOne(size - 1);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int index = indexAddOne(nextFirst);
        while(index != nextLast) {
            System.out.print(items[index] + " ");
            index = indexAddOne(index);
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16 && (size / items.length <= 0.25)) {
            resize(size);
        }
        nextFirst = indexAddOne(nextFirst);
        size -= 1;
        return items[nextFirst];
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16 && (size / items.length <= 0.25)) {
            resize(size);
        }
        nextLast = indexMinusOne(nextLast);
        size -= 1;
        return items[nextLast];
    }

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
}
