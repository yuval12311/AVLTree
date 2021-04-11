/**
 * public class AVLNode
 * <p>
 * This class represents an AVLTree with integer keys and boolean values.
 * <p>
 * IMPORTANT: do not change the signatures of any function (i.e. access modifiers, return type, function name and
 * arguments. Changing these would break the automatic tester, and would result in worse grade.
 * <p>
 * However, you are allowed (and required) to implement the given functions, and can add functions of your own
 * according to your needs.
 */

public class AVLTree {
    private AVLNode root;
    private AVLNode max;
    private AVLNode min;
    private int    size;




    public AVLTree() {
        this.root = null;
        this.size = 0;
    }
    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        return this.size == 0;
    }

    /**
     * public boolean search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public Boolean search(int k) {
        AVLNode node = root;
        while (node != null) {
            if (node.getKey() == k)
                return node.getValue();
            else if (node.getKey() < k)
                node = node.getLeft();
            else
                node = node.getRight();
        }
        return null;
    }

    /**
     * public int insert(int k, boolean i)
     * <p>
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
	 * returns the number of nodes which require rebalancing operations (i.e. promotions or rotations).
	 * This always includes the newly-created node.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, boolean i) {
        if (empty()) {
            max = min = root = new AVLNode(k, i);
            ++size;
            return 0;
        }
        AVLNode potentialPlace = root;
        while (potentialPlace.isRealNode()) {
            if (potentialPlace.getKey() == k) return -1;
            if (potentialPlace.getKey() < k) {
                potentialPlace = potentialPlace.getRight();
            } else
                potentialPlace = potentialPlace.getLeft();
        }
        AVLNode parent = potentialPlace.getParent();
        AVLNode node = new AVLNode(k, i);
        node.setParent(parent);
        if (parent.getKey() < k) {
            parent.setRight(node);
        } else {
            parent.setLeft(node);
        }
        setNextPrevInsert(node);
        updateMinMaxInsert(node);
        ++size;

        int totalBalancing = 0;
        while (parent != null) {
            if (!updateHeight(parent)) {
                updateNumOfTsUp(parent);
                return totalBalancing;
            }
            ++totalBalancing;
            updateNumOfTs(parent);

            if (Math.abs(parent.balanceFactor()) == 2) {
                performRotation(parent);
                updateNumOfTsUp(parent);
                return  ++totalBalancing;
            }
            parent = parent.getParent();
        }
        return totalBalancing;

    }

    private void updateNumOfTsUp(AVLNode node) {
        while (node != null) {
            updateNumOfTs(node);
            node = node.getParent();
        }
    }

    private void updateMinMaxInsert(AVLNode node) {
        if (max.getKey() < node.getKey()) max = node;
        if (min.getKey() > node.getKey()) min = node;
    }


    private void setNextPrevInsert(AVLNode node) {
        AVLNode next = slowSucc(node);
        node.setNext(next);
        if (next != null) next.setPrev(node);
        AVLNode prev = slowPrev(node);
        node.setPrev(prev);
        if (prev != null) prev.setNext(node);
    }

    private AVLNode slowPrev(AVLNode node) {
        if (node.getLeft().isRealNode()) {
            node = node.getLeft();
            while (node.getRight().isRealNode()) {
                node = node.getRight();
            }
            return node.getParent();
        }
        while (node.getParent() != null && node.getParent().getKey() > node.getKey())
            node = node.getParent();
        return node.getParent();
    }

    private AVLNode slowSucc(AVLNode node) {
        if (node.getRight().isRealNode()) {
            node = node.getRight();
            while (node.getLeft().isRealNode()) {
                node = node.getLeft();
            }
            return node.getParent();
        }
        while (node.getParent() != null && node.getParent().getKey() < node.getKey())
            node = node.getParent();
        return node.getParent();
    }

    /**
     * Choose which rotation to execute on the node, and return the number of rotations made (1 for normal rotation, 2 for double)
     */
    private void performRotation(AVLNode node) {
        if (node.balanceFactor() > 0) {
            if (node.getLeft().balanceFactor() < 0) {
                leftRotation(node.getLeft());
            }
            rightRotation(node);
        } else {
            if (node.getRight().balanceFactor() > 0) {
                rightRotation(node.getRight());
            }
            leftRotation(node);
        }
    }

    private void leftRotation(AVLNode node) {
        AVLNode parent = node.getParent();
        node.setParent(node.getRight());
        node.setRight(node.getRight().getLeft());
        node.getParent().setLeft(node);
        if (parent == null) node.getParent().setParent(null);
        else if (parent.getKey() < node.getKey())
            parent.setRight(node.getParent());
        else
            parent.setLeft(node.getParent());
        updateHeight(node);
        updateNumOfTs(node);
        updateNumOfTs(node.getParent());
    }

    private void updateNumOfTs(AVLNode node) {
        node.setNumOfTs(node.getLeft().getNumOfTs() +
                node.getRight().getNumOfTs() + (node.getValue() ? 1 : 0));
    }

    private boolean updateHeight(AVLNode node) {
        int newHeight = 1 + Math.max(node.getLeft().getHeight(), node.getRight().getHeight());
        boolean changed = node.getHeight() != newHeight;
        node.setHeight(newHeight);
        return changed;
    }

    private void rightRotation(AVLNode node) {
        AVLNode parent = node.getParent();
        node.setParent(node.getLeft());
        node.setLeft(node.getLeft().getRight());
        node.getParent().setRight(node);
        if (parent == null) node.getParent().setParent(null);
        else if (parent.getKey() < node.getKey())
            parent.setRight(node.getParent());
        else
            parent.setLeft(node.getParent());
        updateHeight(node);
        updateNumOfTs(node);
        updateNumOfTs(node.getParent());
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of nodes which required rebalancing operations (i.e. demotions or rotations).
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        if (empty()) return -1;
        AVLNode node = root;
        while (node.getKey() != k) {
            if (!node.isRealNode()) return -1;
            if (node.getKey() < k) {
                node = node.getRight();
            } else
                node = node.getLeft();
        }
        AVLNode parent = node.getParent();
        if (parent.getKey() < k) {
            parent.setRight(new AVLNode(-1, null));
        } else {
            parent.setLeft(new AVLNode(-1, null));
        }
        setNextPrevDelete(node);
        updateMinMaxDelete(node);
        --size;


        int totalBalancing = 0;
        while (parent != null) {
            if (!updateHeight(parent)) {
                updateNumOfTsUp(parent);
                return totalBalancing;
            }
            ++totalBalancing;

            if (Math.abs(parent.balanceFactor()) == 2) {
                performRotation(parent);
            }
            parent = parent.getParent();
        }
        return totalBalancing;


    }

    private void setNextPrevDelete(AVLNode node) {
        AVLNode next = node.getNext();
        AVLNode prev = node.getPrev();
        if (next != null) next.setPrev(prev);
        if (prev != null) prev.setNext(next);
    }

    private void updateMinMaxDelete(AVLNode node) {
        if (max.getKey() == node.getKey()) max = node.getPrev();
        if (min.getKey() == node.getKey()) min = node.getNext();
    }

    /**
     * public Boolean min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public Boolean min() {
        return min.getValue();
    }

    /**
     * public Boolean max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public Boolean max() {
        return max.getValue();
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        int[] arr = new int[size];
        AVLNode node = min;
        for (int i = 0; i < size; ++i, node = node.getNext()) {
            arr[i] = node.getKey();
        }
        return arr;
    }

    /**
     * public boolean[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public boolean[] infoToArray() {
        boolean[] arr = new boolean[size];
        AVLNode node = min;
        for (int i = 0; i < size; ++i, node = node.getNext()) {
            arr[i] = node.getValue();
        }
        return arr;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     */
    public int size() {
        return size;
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     */
    public AVLNode getRoot() {
        return this.root;
    }

    /**
     * public boolean prefixXor(int k)
     *
     * Given an argument k which is a key in the tree, calculate the xor of the values of nodes whose keys are
     * smaller or equal to k.
     *
     * precondition: this.search(k) != null
     *
     */
    public boolean prefixXor(int k){
        int TsLessThan = root.getNumOfTs();
        AVLNode node = root;
        while (node.getKey() != k) {
            if (node.getKey() < k) {
                node = node.getRight();
            }
            else {
                TsLessThan -= node.getRight().getNumOfTs();
                node = node.getLeft();
            }
        }
        return (TsLessThan - node.getRight().getNumOfTs()) % 2 == 1;

    }

    /**
     * public AVLNode successor
     *
     * given a node 'node' in the tree, return the successor of 'node' in the tree (or null if successor doesn't exist)
     *
     * @param node - the node whose successor should be returned
     * @return the successor of 'node' if exists, null otherwise
     */
    public AVLNode successor(AVLNode node){
        return node.getNext();
    }

    /**
     * public boolean succPrefixXor(int k)
     *
     * This function is identical to prefixXor(int k) in terms of input/output. However, the implementation of
     * succPrefixXor should be the following: starting from the minimum-key node, iteratively call successor until
     * you reach the node of key k. Return the xor of all visited nodes.
     *
     * precondition: this.search(k) != null
     */
    public boolean succPrefixXor(int k){
        boolean xor = min.getValue();
        AVLNode node = successor(min);
        while (node.getKey() <= k) {
            xor ^= node.getValue();
            node = successor(node);
        }
        return xor;
    }


    /**
     * public class AVLNode
     * <p>
     * This class represents a node in the AVL tree.
     * <p>
     * IMPORTANT: do not change the signatures of any function (i.e. access modifiers, return type, function name and
     * arguments. Changing these would break the automatic tester, and would result in worse grade.
     * <p>
     * However, you are allowed (and required) to implement the given functions, and can add functions of your own
     * according to your needs.
     */
    public class AVLNode {
        private final int key;
        private int height;
        private Boolean val;
        private int numOfTs;
        private AVLNode parent;
        private AVLNode left;
        private AVLNode right;
        private AVLNode next;
        private AVLNode prev;


        public AVLNode(int key, Boolean val) {
            this.key = key;
            this.parent = null;
            if (key == -1) {
                setLeft(null);
                setRight(null);
                setHeight(-1);
                this.val = null;
                this.numOfTs = 0;
            } else {
                setRight(virtualNode());
                setLeft(virtualNode());
                setHeight(0);
                this.val = val;
                this.numOfTs = val ? 1 : 0;


            }
        }

        private AVLNode virtualNode() {
            AVLNode virt = new AVLNode(-1, null);
            virt.setParent(this);
            return virt;
        }


        //returns node's key (for virtual node return -1)
        public int getKey() {
            return key;
        }

        //returns node's value [info] (for virtual node return null)
        public boolean getValue() {
            return false; // to be replaced by student code
        }

        //sets left child
        public void setLeft(AVLNode node) {
            this.left = node;
        }

        //returns left child (if there is no left child return null)
        public AVLNode getLeft() {
            return this.left;
        }

        //sets right child
        public void setRight(AVLNode node) {
            this.right = node;
        }

        //returns right child (if there is no right child return null)
        public AVLNode getRight() {
            return this.right;
        }

        //sets parent
        public void setParent(AVLNode node) {
            this.parent = node;
        }

        //returns the parent (if there is no parent return null)
        public AVLNode getParent() {
            return this.parent;
        }

        // Returns True if this is a non-virtual AVL node
        public boolean isRealNode() {
            return key == -1;
        }

        // sets the height of the node
        public void setHeight(int height) {
            this.height = height;
        }

        // Returns the height of the node (-1 for virtual nodes)
        public int getHeight() {
            return this.height;
        }

        public int balanceFactor() {
            return getLeft().getHeight() - getRight().getHeight();
        }


        public AVLNode getNext() {
            return next;
        }

        public void setNext(AVLNode next) {
            this.next = next;
        }

        public AVLNode getPrev() {
            return prev;
        }

        public void setPrev(AVLNode prev) {
            this.prev = prev;

        }

        public int getNumOfTs() {
            return numOfTs;
        }

        public void setNumOfTs(int numOfTs) {
            this.numOfTs = numOfTs;
        }
    }
}



