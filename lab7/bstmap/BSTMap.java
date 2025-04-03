package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private BSTNode root;

    private class BSTNode {
        private K key;  // sorted by key
        private V value;    // associated data
        private BSTNode left, right;    // left and right subtrees
        private int size;   // number of nodes in subtrees

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKey(node.left, key);
        } else if (cmp > 0) {
            return containsKey(node.right, key);
        } else {
            return true;
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value, 1);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        if (size() == 0) {
            return null;
        }
        Set<K> keySet = new HashSet<>();
        collectKeys(root, keySet);
        return keySet;
    }

    private void collectKeys(BSTNode node, Set<K> keySet) {
        if (node == null) {
            return;
        }
        collectKeys(node.left, keySet);
        keySet.add(node.key);
        collectKeys(node.right, keySet);
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        V removeValue = get(key);
        root = remove(root, key);
        return removeValue;
    }

    private BSTNode remove(BSTNode node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                BSTNode t = node;
                node = findMax(node.left);
                node.left = removeMax(t.left);
                node.right = t.right;
            }
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    private BSTNode findMax (BSTNode node) {
        if (node.right == null) {
            return node;
        }
        return findMax(node.right);
    }

    private BSTNode removeMax (BSTNode node) {
        if (node.right == null) {
            return node.left;
        }
        node.right = removeMax(node.right);
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        for (K key: keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();
    }
}
