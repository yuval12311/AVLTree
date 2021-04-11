public class AVLTreeTest {
    public static void main(String[] args) {
        AVLTree t = new AVLTree();
        t.insert(6, true);
        t.insert(2, true);
        t.insert(7, true);
        t.insert(1, true);
        t.insert(3, true);
        t.insert(4, true);
        t.insert(8, true);


        t.printTree();
        t.delete(1);
        t.printTree();
        t.delete(2);
        t.printTree();


    }
}
