public class BSTree implements IBSTree {
    private BSTNode root;

    public BSTree() {this.root = null;}

    public boolean empty() {
        return root == null;
    }

    @Override
    public int insert(int k, boolean b) {
        if (empty()) {
            root = new BSTNode(k, b);
            return 0;
        }
        BSTNode potentialPlace = root;
        while (potentialPlace.isRealNode()) {
            if (potentialPlace.getKey() == k) return -1;
            if (potentialPlace.getKey() < k) {
                potentialPlace = potentialPlace.getRight();
            } else
                potentialPlace = potentialPlace.getLeft();
        }
        BSTNode parent = potentialPlace.getParent();
        BSTNode node = new BSTNode(k, b);
        node.setParent(parent);
        if (parent.getKey() < k) {
            parent.setRight(node);
        } else {
            parent.setLeft(node);
        }
        return 0;
    }

    public Boolean search(int k) {
        BSTNode node = root;
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

    public int delete(int k) {
        if (empty()) return 0 ;
        BSTNode node = root;
        while (node.getKey() != k) {
            if (!node.isRealNode()) return 0;
            if (node.getKey() < k) {
                node = node.getRight();
            } else
                node = node.getLeft();
        }
        BSTNode parent = node.getParent();

        if (node.getRight().isRealNode()) {
            if (node.getLeft().isRealNode()) {
                return deleteTwoChidren(node);
            } else {

                node.getRight().setParent(parent);

                if (parent.getKey() < k) {
                    parent.setRight(node.getRight());
                } else {
                    parent.setLeft(node.getRight());
                }
            }
        } else {
            if (node.getLeft().isRealNode()) {
                node.getLeft().setParent(parent);
            }
            if (parent.getKey() < k) {
                parent.setRight(node.getLeft());
            } else {
                parent.setLeft(node.getLeft());
            }
        }
        return 0;
    }


    /**
     * Handles the deletion of node in the case that node has two children
     * time complexity: O(1)
     * @param node
     * @return the parent of node's former successor
     */
    private int deleteTwoChidren(BSTNode node) {
        BSTNode succ = slowSucc(node);
        BSTNode succParent = succ.getParent();
        if (succParent.getKey() != node.getKey()) {
            succParent.setLeft(succ.getRight());
            succ.getRight().setParent(succParent);
            succ.setRight(node.getRight());
            node.getRight().setParent(succ);
        }




        succ.setLeft(node.getLeft());
        node.getLeft().setParent(succ);

        succ.setParent(node.getParent());
        if (node.getParent() != null) {
            if (node.getParent().getKey() < node.getKey()) {
                node.getParent().setRight(succ);
            } else {
                node.getParent().setLeft(succ);
            }
        }
        return 0;
    }

    private BSTNode slowSucc(BSTNode node) {
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


    public void printTree() {
        String[] visual = trepr(root);
        for (int i=0; i<visual.length; i++) {
            System.out.println(visual[i]);
        }
    }

    private static String[] trepr(BSTNode node) {
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



    public class BSTNode {
        private final int key;
        private Boolean val;
        private BSTNode parent;
        private BSTNode left;
        private BSTNode right;
        private BSTNode prev;


        /**
         * Create leaf with given key and value
         */
        public BSTNode(int key, Boolean val) {
            this.key = key;
            this.parent = null;
            if (key == -1) {
                setLeft(null);
                setRight(null);
                this.val = null;
            } else {
                setRight(virtualNode());
                setLeft(virtualNode());
                this.val = val;
            }
        }

        /**
         * Create virtual node for a leaf
         */
        private BSTNode virtualNode() {
            BSTNode virt = new BSTNode(-1, null);
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
        public void setLeft(BSTNode node) {
            this.left = node;
        }

        //returns left child (if there is no left child return null)
        public BSTNode getLeft() {
            return this.left;
        }

        //sets right child
        public void setRight(BSTNode node) {
            this.right = node;
        }

        //returns right child (if there is no right child return null)
        public BSTNode getRight() {
            return this.right;
        }

        //sets parent
        public void setParent(BSTNode node) {
            this.parent = node;
        }

        //returns the parent (if there is no parent return null)
        public BSTNode getParent() {
            return this.parent;
        }

        // Returns True if this is a non-virtual AVL node
        public boolean isRealNode() {
            return key > -1;
        }


        @Override
        public String toString() {
            return "(" + key + ")";
        }
    }
}
