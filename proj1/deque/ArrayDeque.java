package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;


    public void addFirst(T item) {
        if (size == items.length) {
            // todo
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
            // todo
        }
        items[nextLast] = item;
        nextLast = indexAddOne(nextLast);
        size += 1;
    }

    private int indexAddOne(int index) {
        return (index + 1) % items.length;
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
        if (size == 0) {
            return null;
        }
        nextFirst = indexAddOne(nextFirst);
        size -= 1;
        return items[nextFirst];
    }

    public T removeLast() {
        if (size == 0) {
            return null;
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
        nextFirst = 4;
        nextLast = 5;
    }
}
