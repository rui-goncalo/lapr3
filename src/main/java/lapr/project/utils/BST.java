package lapr.project.utils;

import lapr.project.model.Ship;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author DEI-ESINF
 */

public class BST<E extends Comparable<E>> implements BSTInterface<E> {


    /** Nested static class for a binary search tree node. */

    protected static class Node<E> {
        private E element;          // an element stored at this node
        private Node<E> left;       // a reference to the left child (if any)
        private Node<E> right;      // a reference to the right child (if any)

        /**
         * Constructs a node with the given element and neighbors.
         *
         * @param e  the element to be stored
         * @param leftChild   reference to a left child node
         * @param rightChild  reference to a right child node
         */
        public Node(E e, Node<E> leftChild, Node<E> rightChild) {
            element = e;
            left = leftChild;
            right = rightChild;
        }

        // accessor methods
        public E getElement() { return element; }
        public Node<E> getLeft() { return left; }
        public Node<E> getRight() { return right; }

        // update methods
        public void setElement(E e) { element = e; }
        public void setLeft(Node<E> leftChild) { left = leftChild; }
        public void setRight(Node<E> rightChild) { right = rightChild; }
    }

    //----------- end of nested Node class -----------

    protected Node<E> root = null;     // root of the tree


    /* Constructs an empty binary search tree. */
    public BST() {
        root = null;
    }

    /*
     * @return root Node of the tree (or null if tree is empty)
     */
    protected Node<E> root() {
        return root;
    }

    /*
     * Verifies if the tree is empty
     * @return true if the tree is empty, false otherwise
     */
    public boolean isEmpty(){
        return root==null;
    }

    /*
     * Inserts an element in the tree.
     */
    public void insert(E element){
        root = insert (element, root);
    }

    private Node<E> insert(E element, Node<E> node){

        if(node == null){
            return new Node (element, null, null);
        }
        if(node.getElement().compareTo(element) == 0){
            node.setElement(element);
            return node;
        }
        if(node.getElement().compareTo(element) > 0 ){
            node.setLeft(insert(element, node.getLeft()));
            return node;
        }else{
            node.setRight(insert(element, node.getRight()));
            return node;
        }
    }

    /**
     * Removes an element from the tree maintaining its consistency as a Binary Search Tree.
     */
    public void remove(E element){
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {

        if (node == null) {
            return null;    //throw new IllegalArgumentException("Element does not exist");
        }
        if (element.compareTo(node.getElement())==0) {
            // node is the Node to be removed
            if (node.getLeft() == null && node.getRight() == null) { //node is a leaf (has no childs)
                return null;
            }
            if (node.getLeft() == null) {   //has only right child
                return node.getRight();
            }
            if (node.getRight() == null) {  //has only left child
                return node.getLeft();
            }
            E min = smallestElement(node.getRight());
            node.setElement(min);
            node.setRight(remove(min, node.getRight()));
        }
        else if (element.compareTo(node.getElement()) < 0)
            node.setLeft( remove(element, node.getLeft()) );
        else
            node.setRight( remove(element, node.getRight()) );

        return node;
    }

    /*
     * Returns the number of nodes in the tree.
     * @return number of nodes in the tree
     */
    public int size(){

        return size(this.root);
    }

    private int size(Node<E> node){

        if(node == null){
            return 0;
        }else{
            return size(node.left) + 1 + size(node.right);
        }
    }

    /*
     * Returns the height of the tree
     * @return height
     */
    public int height(){
        return height(root);
    }

    /*
     * Returns the height of the subtree rooted at Node node.
     * @param node A valid Node within the tree
     * @return height
     */
    protected int height(Node<E> node){

        if (node == null)
            return -1;
        else
        {
            /* compute the depth of each subtree */
            int lDepth = height(node.getLeft());
            int rDepth = height(node.getRight());

            /* use the larger one */
            if (lDepth > rDepth)
                return (lDepth + 1);
            else
                return (rDepth + 1);
        }

    }

    /**
     * Returns the smallest element within the tree.
     * @return the smallest element within the tree
     */
    public E smallestElement(){
        return smallestElement(root);
    }

    protected E smallestElement(Node<E> node){

        if(node.getLeft() == null) return node.getElement();
        return smallestElement(node.getLeft());
    }

    public Node<E> find(E element) {
        return this.find(this.root(), element);
    }

    /**
     * Returns the Node containing a specific Element, or null otherwise.
     *
     * @param element    the element to find
     * @return the Node that contains the Element, or null otherwise
     *
     * This method despite not being essential is very useful.
     * It is written here in order to be used bys this class and its
     * subclasses avoiding recoding.
     * So its access level is protected
     */
    protected Node<E> find(Node<E> node,E element){

        if (node == null){
            return null;
        }
        if(node.getElement() == element){
            return node;
        }
        if(node.getElement().compareTo(element) > 0){
            return find (node.getLeft(), element);
        }else{
            return find (node.getRight(), element);
        }
    }

    public void printTree(String prefix) {
        this.printTree(this.root(), prefix);
    }

    public void printTree(Node<E> node, String prefix) {
        if (node != null) {
            Ship ship = (Ship) node.getElement();
            System.out.println(prefix + "MMSI: " + ship.getMmsi());
            printTree(node.getLeft(), "left ");
            printTree(node.getRight(), "right ");
        }
    }

    public Ship findImo(int imo) {
        int count = 0;
        Node<E> node = this.root();
        while(count < this.height() - 1) {
            Ship right = (Ship) node.getElement();
            if (right.compareImo(imo)) {
                break;
            }
            node = node.getRight();
            count++;
        }
        return (Ship) node.getElement();
    }

    public Ship findCallSign(String callSign) {
        int count = 0;
        Node<E> node = this.root();
        while(count < this.height() - 1) {
            Ship right = (Ship) node.getElement();
            if (right.compareCallSign(callSign)) {
                break;
            }
            node = node.getRight();
            count++;
        }
        return (Ship) node.getElement();
    }

    /*
     * Returns an iterable collection of elements of the tree, reported in in-order.
     * @return iterable collection of the tree's elements reported in in-order
     */
    public Iterable<E> inOrder(){
        List<E> snapshot = new ArrayList<>();
        if (root!=null)
            inOrderSubtree(root, snapshot);   // fill the snapshot recursively
        return snapshot;
    }
    /**
     * Adds elements of the subtree rooted at Node node to the given
     * snapshot using an in-order traversal
     * @param node       Node serving as the root of a subtree
     * @param snapshot  a list to which results are appended
     */
    private void inOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node == null)
            return;
        inOrderSubtree(node.getLeft(), snapshot);
        snapshot.add(node.getElement());
        inOrderSubtree(node.getRight(), snapshot);
    }
    /**
     * Returns an iterable collection of elements of the tree, reported in pre-order.
     * @return iterable collection of the tree's elements reported in pre-order
     */
    public Iterable<E> preOrder(){
        List<E> snapshot = new ArrayList<>();
        if (root!=null)
            preOrderSubtree(root, snapshot);   // fill the snapshot recursively
        return snapshot;
    }
    /**
     * Adds elements of the subtree rooted at Node node to the given
     * snapshot using an pre-order traversal
     * @param node       Node serving as the root of a subtree
     * @param snapshot  a list to which results are appended
     */
    private void preOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node == null)
            return;

        snapshot.add(node.getElement());
        preOrderSubtree(node.getLeft(), snapshot);
        preOrderSubtree(node.getRight(), snapshot);

    }
    /**
     * Returns an iterable collection of elements of the tree, reported in post-order.
     * @return iterable collection of the tree's elements reported in post-order
     */
    public Iterable<E> posOrder(){
        List<E> lista = new ArrayList<>();
        posOrderSubtree(root, lista);
        return lista;
    }
    /**
     * Adds positions of the subtree rooted at Node node to the given
     * snapshot using an post-order traversal
     * @param node       Node serving as the root of a subtree
     * @param snapshot  a list to which results are appended
     */
    private void posOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node != null) {
            posOrderSubtree(node.getLeft(), snapshot);
            posOrderSubtree(node.getRight(), snapshot);
            snapshot.add(node.getElement());
        }
    }

    /*
     * Returns a map with a list of nodes by each tree level.
     * @return a map with a list of nodes by each tree level
     */
    public Map<Integer,List<E>> nodesByLevel(){
        Map<Integer, List<E>> map = new HashMap<>();
        processBstByLevel(root, map, 0);
        return map;
    }

    private void processBstByLevel(Node<E> node, Map<Integer,List<E>> result, int level){

        if(node != null) {
            if(!result.containsKey(level)) {
                result.put(level, new ArrayList<E>());
            }
            List<E> list = result.get(level);
            list.add(node.getElement());
            processBstByLevel(node.getLeft(), result, level +1);
            processBstByLevel(node.getRight(), result, level +1);
        }
    }

//#########################################################################

    /**
     * Returns a string representation of the tree.
     * Draw the tree horizontally
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        toStringRec(root, 0, sb);
        return sb.toString();
    }

    private void toStringRec(Node<E> root, int level, StringBuilder sb){
        if(root==null)
            return;
        toStringRec(root.getRight(), level+1, sb);
        if (level!=0){
            for(int i=0;i<level-1;i++)
                sb.append("|\t");
            sb.append("|-------"+root.getElement()+"\n");
        }
        else
            sb.append(root.getElement()+"\n");
        toStringRec(root.getLeft(), level+1, sb);
    }

    public void PrintLevels() {
        List<Node<E>> list = new LinkedList<>();

        list.add(root);

        System.out.println("=====CONTEUDO DA ARVORE=====");

        while (!list.isEmpty()) {
            Node<E> node = list.remove(0);
            System.out.println(node.getElement());
            if (node.getLeft() != null) {
                list.add(node.getLeft());
            }

            if (node.getRight() != null) {
                list.add(node.getRight());
            }
        }
    }

} //----------- end of BST class -----------