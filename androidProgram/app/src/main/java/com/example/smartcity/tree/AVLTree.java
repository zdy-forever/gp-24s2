package com.example.smartcity.tree;

import java.util.ArrayList;
import java.util.List;


public class AVLTree<T extends Comparable<T>> extends BinarySearchTree<T> {
    /*
        As a result of inheritance by using 'extends BinarySearchTree<T>,
        all class fields within BinarySearchTree are also present here.
        So while not explicitly written here, this class has:
            - value
            - leftNode
            - rightNode
     */

    public AVLTree () {
        super(null);
        this.leftNode = new EmptyAVL<>();
        this.rightNode = new EmptyAVL<>();
    }

    public AVLTree(T value) {
        super(value);
        // Set left and right children to be of EmptyAVL as opposed to EmptyBST.
        this.leftNode = new EmptyAVL<>();
        this.rightNode = new EmptyAVL<>();
    }

    public AVLTree(T value, Tree<T> leftNode, Tree<T> rightNode) {
        super(value, leftNode, rightNode);
    }



    /**
     * @return balance factor of the current node.
     */
    public int getBalanceFactor() {
        /*
             Note:
             Calculating the balance factor and height each time they are needed is less efficient than
             simply storing the height and balance factor as fields within each tree node (as some
             implementations of the AVLTree do). However, although it is inefficient, it is easier to implement.
         */
        return leftNode.getHeight() - rightNode.getHeight();
    }

    @Override
    public AVLTree<T> insert(T element) {
        /*

            Note that what each method does is described in its superclass unless edited.
            E.g. what 'insert' does is described in Tree.java.
         */
        // Ensure input is not null.
        Tree<T> leftNode1 = this.leftNode;
        Tree<T> rightNode1 = this.rightNode;
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");
        if (this.value == null) {
            // 如果当前树是空的，设置为新值
            this.value = element;
            return this;
        }
        if (element.compareTo(value) > 0) {

            if (rightNode1.toString().equals("{}")) {
                rightNode1 = new AVLTree<>(element);
            } else {
                rightNode1 = rightNode1.insert(element);
            }
        } else if (element.compareTo(value) < 0) {
            if (leftNode1.toString().equals("{}")) {
                leftNode1 = new AVLTree<>(element);
            } else {
                leftNode1 = leftNode1.insert(element);
            }
        }
        return new AVLTree(this.value, leftNode1, rightNode1).balance(); // Change to return something different
    }



    /**
     * Conducts a left rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> leftRotate() {
        /*
            This can be quite difficult to get your head around. Try looking for visualisations
            of left rotate if you are confused.

            Note: if this is implemented correctly than the return MUST be an AVL tree. However, the
            rotation may move around EmptyAVL trees. So when moving trees, the type of the trees can
            be 'Tree<T>'. However, the return type should be of AVLTree<T>. To cast the return type into
            AVLTree<T> simply use: (AVLTree<T>).

            If you get an casting exception such as:
            'java.lang.ClassCastException: class AVLTree$EmptyAVL cannot be cast to class AVLTree
            (AVLTree$EmptyAVL and AVLTree are in unnamed module of loader 'app')'
            than something about your code is incorrect!
         */

        Tree<T> newParent = this.rightNode;
        this.rightNode= newParent.leftNode;
        newParent.leftNode=this;
        // COMPLETE

        return (AVLTree<T>) newParent; // Change to return something different
    }

    /**
     * Conducts a right rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> rightRotate() {
        /*
            This can be quite difficult to get your head around. Try looking for visualisations
            of right rotate if you are confused.

            Note: if this is implemented correctly than the return MUST be an AVL tree. However, the
            rotation may move around EmptyAVL trees. So when moving trees, the type of the trees can
            be 'Tree<T>'. However, the return type should be of AVLTree<T>. To cast the return type into
            AVLTree<T> simply use: (AVLTree<T>).

            If you get an casting exception such as:
            'java.lang.ClassCastException: class AVLTree$EmptyAVL cannot be cast to class AVLTree
            (AVLTree$EmptyAVL and AVLTree are in unnamed module of loader 'app')'
            than something about your code is incorrect!
         */

        Tree<T> newParent = this.leftNode;
        this.leftNode= newParent.rightNode;
        newParent.rightNode=this; // Change to return something different
        return (AVLTree<T>) newParent;
    }

    public AVLTree<T> delete(T element) {
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");
        List<T> elements = this.inOrder();
        if (!elements.contains(element)) {
            return this;
        }
        else {
            elements.remove(element);
            if (elements.equals(new ArrayList<>()))
            {
                this.value=null;
                this.leftNode = new EmptyAVL<>();
                this.rightNode = new EmptyAVL<>();
                return this;
            }
            AVLTree<T> newAVLTree = new AVLTree<>(elements.get(0));
            for (int i = 1; i < elements.size(); i++) {
                newAVLTree = newAVLTree.insert(elements.get(i));
            }
            return newAVLTree;
        }
    }

    public AVLTree<T> delete1(T element) {
        if (element == null) {
            return this;
        }
        if (this.value == null) {
            return this;
        }


        if (element.compareTo(this.value) < 0) {
            this.leftNode = this.leftNode.delete1(element);
        } else if (element.compareTo(this.value) > 0) {
            this.rightNode = this.rightNode.delete1(element);
        } else {
            // if this is the node that equals to the given element
            if (this.leftNode.toString().equals("{}") || this.rightNode.toString().equals("{}")) {
                //
                Tree<T> temp = this.leftNode.toString().equals("{}") ? this.rightNode : this.leftNode;
                if (temp.toString().equals("{}")) {
                    // if this is a leaf
                    return new AVLTree<>();
                } else {
                    // if it has only 1 child.
                    return (AVLTree<T>) temp;
                }
            } else {
                // The case with two children, find the minimum value node in the right subtree
                AVLTree<T> minNode = (AVLTree<T>) ((AVLTree<T>) this.rightNode).findMin();
                // Replace the value of the current node with the minimum value
                this.value = minNode.value;
                // Delete the minimum value node in the right subtree
                this.rightNode = this.rightNode.delete1(minNode.value);
            }
        }

        return this.balance();
    }

    private AVLTree<T> findMin() {
        if (this.leftNode.toString().equals("{}")) {
            return this;
        } else {
            return ((AVLTree<T>) this.leftNode).findMin();
        }
    }




    /**
     * Note that this is not within a file of its own... WHY?
     * The answer is: this is just a design decision. 'insert' here will return something specific
     * to the parent class inheriting Tree from BinarySearchTree. In this case an AVL tree.
     */
    public static class EmptyAVL<T extends Comparable<T>> extends EmptyTree<T> {
        @Override
        public Tree<T> delete1(T value) {
            return null;
        }

        @Override
        public Tree<T> insert(T element) {
            // The creation of a new Tree, hence, return tree.
            return new AVLTree<T>(element);
        }
    }


    public AVLTree<T> balance() {
        if (this.getBalanceFactor() > 1) {
            // Left-heavy
            if (((AVLTree<T>) leftNode).getBalanceFactor() < 0) {
                // Left-right case, need to perform left rotation on left child first
                this.leftNode = ((AVLTree<T>) leftNode).leftRotate();
                return this.rightRotate();
            }
            // Perform right rotation on the current node
            return rightRotate();
        } else if (this.getBalanceFactor() < -1) {
            // Right-heavy
            if (((AVLTree<T>) rightNode).getBalanceFactor() > 0) {
                // Right-left case, need to perform right rotation on right child first
                this.rightNode = ((AVLTree<T>) rightNode).rightRotate();
                return this.leftRotate();
            }
            // Perform left rotation on the current node
            return leftRotate();
        }
        // The tree is already balanced
        return this;
    }


    public static void main(String[] args) {
        AVLTree<String> emptyTree = new AVLTree();

        emptyTree=emptyTree.insert("a");
        emptyTree=emptyTree.insert("b");
        emptyTree=emptyTree.insert("c");
        emptyTree=emptyTree.insert("d");
        emptyTree=emptyTree.insert("e");
        System.out.println(emptyTree.inOrder().toString());
    }

}


