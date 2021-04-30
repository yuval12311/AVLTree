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
 * @author Yuval Nosovitsky
 * Id: 322450883
 * username: nosovitsky
 */

public class AVLTree implements IBSTree {
    /**
     * The root of the tree
     */
    private AVLNode root;
    /**
     * The node with the highest key in the tree
     */
    private AVLNode max;
    /**
     * The node with the lowest key in the tree
     */
    private AVLNode min;
    /**
     * the amount of nodes in the tree
     */
    private int    size;


    /**
     * constructs an empty tree
     * time complexity: O(1)
     */
    public AVLTree() {
        this.root = null;
        this.size = 0;
    }
    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     * time complexity: O(1)
     */
    public boolean empty() {
        return this.size == 0;
    }

    /**
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     * uses regular binary search.
     * time complexity: O(log(size))
     */
    public Boolean search(int k) {
        AVLNode node = root;
        while (node != null) {
            if (node.getKey() == k)
                return node.getValue();
            else if (node.getKey() > k)
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
     * Algorithm is as shown in class.
     * time complexity: O(log(size))
     */
    public int insert(int k, boolean i) {
        if (empty()) {
            max = min = root = new AVLNode(k, i);
            ++size;
            return 1;
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

        int totalBalancing = 1;
        while (parent != null) {
            if (!updateHeight(parent)) {
                updateXorsUp(parent);
                return totalBalancing;
            }
            updateXor(parent);

            if (Math.abs(parent.balanceFactor()) == 2) {
                dispatchRotation(parent);
                updateXorsUp(parent);
                return  ++totalBalancing;
            }
            parent = parent.getParent();
        }
        return totalBalancing;

    }

    /**
     * updates the xorOfChildren field in all the nodes in the path from node to the root.
     * time complexity: O(log(size))
     * @param node
     */
    private void updateXorsUp(AVLNode node) {
        while (node != null) {
            updateXor(node);
            node = node.getParent();
        }
    }

    /**
     * Updates min and max with the newly added node.
     * time complexity: O(1)
     * @param node
     */
    private void updateMinMaxInsert(AVLNode node) {
        if (max.getKey() < node.getKey()) max = node;
        if (min.getKey() > node.getKey()) min = node;
    }


    /**
     * Sets the prev and next of the newly added node.
     * time complexity: O(1)
     * @param node
     */
    private void setNextPrevInsert(AVLNode node) {
        AVLNode next = slowAdj(Direction.Right, node);
        node.setNext(next);
        if (next != null) next.setPrev(node);
        AVLNode prev = slowAdj(Direction.Left, node);
        node.setPrev(prev);
        if (prev != null) prev.setNext(node);
    }


    private AVLNode slowAdj(Direction dir, AVLNode node) {
        if (node.getChild(dir).isRealNode()) {
            node = node.getChild(dir);
            while (node.getChild(dir.opposite()).isRealNode()) {
                node = node.getChild(dir.opposite());
            }
            return node.getParent();
        }
        while (node.getParent() != null && node.kindOfChild() == dir)
            node = node.getParent();
        return node.getParent();
    }

    /**
     * Choose which rotation to execute on the node
     * time complexity: O(1)
     */
    private void dispatchRotation(AVLNode node) {
        if (node.balanceFactor() > 0) {
            if (node.getLeft().balanceFactor() < 0) {
                rotate(Direction.Left, node.getLeft());
            }
            rotate(Direction.Right, node);
        } else {
            if (node.getRight().balanceFactor() > 0) {
                rotate(Direction.Right, node.getRight());
            }
            rotate(Direction.Left, node);
        }
    }

    /**
     * Rotates to the tree in the dir direction
     * @param dir
     * @param node
     */
    private void rotate(Direction dir, AVLNode node) {
        AVLNode parent = node.getParent();
        Direction dirOfNode = node.kindOfChild();
        node.setParent(node.getChild(dir.opposite()));
        node.setChild(dir.opposite(), node.getChild(dir.opposite()).getChild(dir));
        node.getChild(dir.opposite()).setParent(node);
        node.getParent().setChild(dir, node);
        if (parent == null) {
            node.getParent().setParent(null);
            root = node.getParent();
        } else   parent.setChild(dirOfNode, node.getParent());

        node.getParent().setParent(parent);
        updateHeight(node);
        updateHeight(node.getParent());
        updateXor(node);
        updateXor(node.getParent());
    }


    private void updateXor(AVLNode node) {
        node.setXorOfChildren(node.getValue() ^ node.getLeft().getXorOfChildren() ^ node.getRight().getXorOfChildren());
    }

    /**
     * updates the height of node according to its children
     * time complexity: O(1)
     * @param node
     * @return whether the height was updated
     */
    private boolean updateHeight(AVLNode node) {
        int newHeight = 1 + Math.max(node.getLeft().getHeight(), node.getRight().getHeight());
        boolean changed = node.getHeight() != newHeight;
        node.setHeight(newHeight);
        return changed;
    }
    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of nodes which required rebalancing operations (i.e. demotions or rotations).
     * returns -1 if an item with key k was not found in the tree.
     *
     * Algorithm is as shown in class.
     * time complexity: O(log(size))
     */
    public int delete(int k) {

        AVLNode parent = deleteBST(k);
        if (parent ==null) return -1;

        int totalBalancing = 1;
        while (parent != null) {
            if (!updateHeight(parent) && Math.abs(parent.balanceFactor()) < 2) {
                updateXorsUp(parent);
                return totalBalancing;
            }

            updateXor(parent);

            if (Math.abs(parent.balanceFactor()) == 2) {
                dispatchRotation(parent);
                ++totalBalancing;
                parent = parent.getParent();
            }
            parent = parent.getParent();
        }
        return totalBalancing;

    }


    /**
     * deletes the node with key k as if it was a regular Binary Searching Tree.
     * algorithm is as shown in class.
     * time complexity: O(log(size))
     * @param k
     * @return The node that one should start vertical updating of heights and rotations with
     */
    public AVLNode deleteBST(int k) {
        if (empty()) return null ;
        AVLNode node = root;
        while (node.getKey() != k) {
            if (!node.isRealNode()) return null;
            if (node.getKey() < k) {
                node = node.getRight();
            } else
                node = node.getLeft();
        }
        AVLNode parent = node.getParent();
        setNextPrevDelete(node);
        updateMinMaxDelete(node);
        --size;
        if (node.getRight().isRealNode()) {
            if (node.getLeft().isRealNode()) {
                return deleteTwoChidren(node);
            } else {

                node.getRight().setParent(parent);
                if (parent != null) parent.setChild(node.kindOfChild(), node.getRight());
                else root = node.getRight();
            }
        } else {
            node.getLeft().setParent(parent);
            if (parent != null) parent.setChild(node.kindOfChild(), node.getLeft());
            else root = node.getLeft();
        }
        return parent;
    }


    /**
     * Handles the deletion of node in the case that node has two children
     * time complexity: O(1)
     * @param node
     * @return the parent of node's former successor
     */
    private AVLNode deleteTwoChidren(AVLNode node) {
        AVLNode succ = node.getNext();
        AVLNode succParent = succ.getParent();
        if (succParent != node) {
            if (succParent != null) {
                succParent.setLeft(succ.getRight());
                succ.getRight().setParent(succParent);
            } else {
                root = succ.getLeft();
                root.setParent(null);
            }
            succ.setRight(node.getRight());
            node.getRight().setParent(succ);
        }

        succ.setLeft(node.getLeft());
        node.getLeft().setParent(succ);

        succ.setParent(node.getParent());
        if (node.getParent() != null) {
            node.getParent().setChild(node.kindOfChild(), succ);
        } else root = succ;
        return succParent == node ? succ : succParent;
    }


    /**
     * updates the prev and the next of the adjacent nodes to accommodate for node's deletion
     * time complexity: O(1)
     * @param node
     */
    private void setNextPrevDelete(AVLNode node) {
        AVLNode next = node.getNext();
        AVLNode prev = node.getPrev();
        if (next != null) next.setPrev(prev);
        if (prev != null) prev.setNext(next);
    }

    /**
     * Updates the min and max given that node will be deleted
     * time complexity: O(1)
     * @param node
     */
    private void updateMinMaxDelete(AVLNode node) {
        if (max.getKey() == node.getKey()) max = node.getPrev();
        if (min.getKey() == node.getKey()) min = node.getNext();
    }

    /**
     * public Boolean min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     * time complexity: O(1)
     */
    public Boolean min() {
        return min == null ? null : min.getValue();
    }

    /**
     * public Boolean max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     * time complexity: O(1)
     */
    public Boolean max() {
        return max == null ? null : max.getValue();
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     * time complexity: O(size)
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
     * time complexity: O(size)
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
     * time complexity: O(1)
     */
    public int size() {
        return size;
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     * time complexity: O(1)
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
     * time complexity: O(log(size))
     */
    public boolean prefixXor(int k){
        boolean xor = root.xorOfChildren; //xor of the entire tree
        AVLNode node = root;
        while (node.getKey() != k) {
            if (node.getKey() < k) {
                node = node.getRight();
            }
            else {
                xor ^= node.getValue() ^ node.getRight().getXorOfChildren(); // removing the nodes with keys bigger than k from the xor
                node = node.getLeft();
            }
        }
        return xor ^ node.getRight().getXorOfChildren();

    }

    /**
     * public AVLNode successor
     *
     * given a node 'node' in the tree, return the successor of 'node' in the tree (or null if successor doesn't exist)
     * time complexity: O(1)
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
     * time complexity: O(size)
     * precondition: this.search(k) != null
     */
    public boolean succPrefixXor(int k){
        boolean xor = min.getValue();
        AVLNode node = successor(min);
        while (node != null && node.getKey() <= k) {
            xor ^= node.getValue();
            node = successor(node);
        }
        return xor;
    }


    public void printTree() {
        String[] visual = trepr(root);
        for (String s : visual) {
            System.out.println(s);
        }
    }

    private static String[] trepr(AVLNode node) {
        // Return a list of textual representations of the levels in t
        if (node == null) return new String[]{"┴"};
        String zis = node.toString();
        String[] leftTxt;
        String[] rightTxt;
        if (!node.getLeft().isRealNode())
            leftTxt = new String[]{"┴"};
        else
            leftTxt = trepr(node.getLeft());
        if (!node.getRight().isRealNode())
            rightTxt = new String[]{"┴"};
        else
            rightTxt = trepr(node.getRight());
        return conc(leftTxt,zis,rightTxt);
    }

    private static String[] conc(String[] left, String root, String[] right) {
        // Return a concatenation of textual represantations of
        // a root node, its left node, and its right node
        int lwid = left[left.length-1].length(); // levels in left
        int rwid = right[right.length-1].length(); // levels in right
        int rootwid = root.length();
        String[] result = new String[2 + Math.max(left.length,right.length)];
        result[0] = mul(" ",lwid+1) + root + mul(" ",rwid+1); // first row
        int ls = leftspace(left[0]);
        int rs = rightspace(right[0]);
        result[1] = mul(" ",ls) + mul("",lwid-ls) + "/" + mul(" ",rootwid) + "\\" + mul("",rs) + mul(" ",rwid-rs); // second row
        String row;
        for (int i=0; i<Math.max(left.length,right.length); i++) {
            // conect the i row in left to row i in right
            row = "";

            if (i < left.length) {
                row += left[i];
            }
            else {
                row += mul(" ",lwid);
            }

            row += mul(" ",rootwid+2);

            if (i < right.length) {
                row += right[i];
            }
            else {
                row += mul(" ",rwid);
            }

            result[i+2] = row;
        }
        return result;
    }

    private static int leftspace(String row) {
        int i = row.length()-1;
        while (row.charAt(i)==' ') {i--;}
        return i+1;
        // returns the index of where the second whitespace starts
    }

    private static int rightspace(String row) {
        int i = 0;
        while (row.charAt(i)==' ') {i++;}
        return i;
        // returns the index of where the first whitespace ends
    }

    private static String mul(String a, int t) {
        if (t==0) {return "";}
        return mul(a,t-1)+a;
        // return new string of a+a...+a - t times
    }

    public enum Direction {
        Left, Right;

        public Direction opposite() {
            if (this == Left) return Right;
            return Left;
        }
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
        private boolean xorOfChildren;
        private AVLNode parent;
        private AVLNode left;
        private AVLNode right;
        private AVLNode next;
        private AVLNode prev;

        /**
         * Create leaf with given key and value
         */
        public AVLNode(int key, Boolean val) {
            this.key = key;
            this.parent = null;
            if (key == -1) {
                setLeft(null);
                setRight(null);
                setHeight(-1);
                this.val = null;
                this.xorOfChildren = false;
            } else {
                setRight(virtualNode());
                setLeft(virtualNode());
                setHeight(0);
                this.val = val;
                this.xorOfChildren = val;


            }
        }

        /**
         * Create virtual node for a leaf
         */
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
        public Boolean getValue() {
            return this.val;
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
            return key > -1;
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

        public boolean getXorOfChildren() {
            return xorOfChildren;
        }

        public void setXorOfChildren(boolean xor) {
            this.xorOfChildren = xor;
        }

        public AVLNode getChild(Direction dir) {
            if (dir == Direction.Left) return this.left;
            return this.right;
        }

        public void setChild(Direction dir, AVLNode node) {
            if (dir == Direction.Left) this.left = node;
            else this.right = node;
        }

        public Direction kindOfChild() {
            if (this.parent == null) return null;
            if (this.key < this.parent.key) return Direction.Left;
            return Direction.Right;
        }

        @Override
        public String toString() {
            return "(" + key + ")";
        }
    }
}