package com.example.smartcity;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.smartcity.tree.AVLTree;
/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class AVLTest {
    @Test(timeout = 1000)
    public void deleteTest1()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete(5);
        String expected = "{value=10, leftNode={}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test(timeout = 1000)
    public void deleteTest2()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete(3);
        String expected = "{value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test(timeout = 1000)
    public void deleteTest3()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete(5);
        avl=avl.delete(10);
        String expected = "{value=20, leftNode={}, rightNode={}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }
    @Test
    public void deleteTest4()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete(5);
        avl=avl.delete(20);
        String expected = "{value=10, leftNode={}, rightNode={}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test
    public void deleteTest5()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete(5);
        avl=avl.delete(20);
        avl=avl.delete(10);
        String expected = "{value=null, leftNode={}, rightNode={}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test
    public void delete1Test1()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete1(5);
        String expected = "{value=10, leftNode={value=null, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test
    public void delete1Test2()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete1(5);
        avl=avl.delete(10);
        String expected = "{value=20, leftNode={}, rightNode={}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }
    @Test
    public void delete1Test3()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete1(10);
        String expected = "{value=20, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=null, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }
    @Test
    public void delete1Test4()
    {
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        avl=avl.delete1(10);
        avl=avl.delete1(5);
        avl=avl.delete1(20);
        String expected = "{value=null, leftNode={value=null, leftNode={}, rightNode={}}, rightNode={value=null, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }


    @Test(timeout = 1000)
    public void immutableTest1() {
        // Simple check if the implementation is immutable. relies on insert() implementation
        AVLTree<Integer> avl = new AVLTree<>();
        avl.insert(10);
        avl=avl.insert(20);
        String expected = "{value=10, leftNode={}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }

    @Test(timeout = 1000)
    public void immutableTest2() {
        // Simple check if the implementation is immutable. relies on insert() implementation
        AVLTree<Integer> avl = new AVLTree<>(1);
        avl.insert(10);
        avl=avl.insert(20);
        String expected = "{value=1, leftNode={}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals("\nAVL tree implementation is not immutable" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                expected,avl.toString());
    }
    @Test(timeout = 1000)
    public void insertInOrderTest() {
        // Simply check if the insertion correctly places values (no rotation check).
        AVLTree<Integer> avl = new AVLTree<>(5);
        avl = avl.insert(10);
        String expected = "{value=5, leftNode={}, rightNode={value=10, leftNode={}, rightNode={}}}";
        assertNotNull(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl
                ,
                10, (int) avl.rightNode.value);

        avl = avl.insert(1);
        expected = "{value=5, leftNode={value=1, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        assertNotNull(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                1, (int) avl.leftNode.value);
    }
    @Test(timeout = 1000)
    public void insertDuplicateTest() {
        // As per the implementation requirements, duplicates should be ignored.
        AVLTree<Integer> avl = new AVLTree<>(5).insert(5);
        String expected = "{value=5, leftNode={}, rightNode={}}";
        assertEquals(
                "\nInsertion does not properly position values" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getHeight());

        // Double checking encase anyone changes height output.
        assertNull("\nInsertion does not properly handle duplicates" +
                "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl, avl.leftNode.value);

        assertNull("\nInsertion does not properly handle duplicates" +
                "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl, avl.rightNode.value);
    }

    @Test(timeout = 1000)
    public void leftRotateTest() {
        // constructing the tree manually to avoid using the insert method
        AVLTree<Integer> avlRightRightChild = new AVLTree<>(10);
        AVLTree<Integer> avlRightChild = new AVLTree<>(8,new AVLTree.EmptyAVL<>(),avlRightRightChild);
        AVLTree<Integer> avl = new AVLTree<>(5,new AVLTree.EmptyAVL<>(),avlRightChild).leftRotate();
        String expected = "{value=8, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        // Check root value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                8, (int) avl.value);

        // Check left child value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                5, (int) avl.leftNode.value);

        // Check right child value
        assertNotNull(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nLeft rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                10, (int) avl.rightNode.value);
    }

    @Test(timeout = 1000)
    public void rightRotateTest() {
        // constructing the tree manually to avoid using the insert method
        AVLTree<Integer> avlLeftLeftChild = new AVLTree<>(3);
        AVLTree<Integer> avlLeftChild = new AVLTree<>(6,avlLeftLeftChild,new AVLTree.EmptyAVL<>());
        AVLTree<Integer> avl = new AVLTree<>(10,avlLeftChild,new AVLTree.EmptyAVL<>()).rightRotate();
        String expected = "{value=6, leftNode={value=3, leftNode={}, rightNode={}}, rightNode={value=10, leftNode={}, rightNode={}}}";
        // Check root value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                6, (int) avl.value);

        // Check left child value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                3, (int) avl.leftNode.value);

        // Check right child value
        assertNotNull(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nRight rotation failed" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                10, (int) avl.rightNode.value);
    }

    // NOTE: Below test relies on insert() method
    @Test(timeout = 1000)
    public void balanceFactorTest() {
        // Ensure insertion results in balanced tree.
        AVLTree<Integer> avl = new AVLTree<>(5).insert(10).insert(20);
        String expected = "{value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );

        avl = avl.insert(22).insert(21);
        expected = "{value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=21, leftNode={value=20, leftNode={}, rightNode={}}, rightNode={value=22, leftNode={}, rightNode={}}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                -1, avl.getBalanceFactor()
        );

        avl = avl.insert(23);
        expected = "{value=21, leftNode={value=10, leftNode={value=5, leftNode={}, rightNode={}}, rightNode={value=20, leftNode={}, rightNode={}}}, rightNode={value=22, leftNode={}, rightNode={value=23, leftNode={}, rightNode={}}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, left, left rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );

        //Advanced
        avl = new AVLTree<>(10)
                .insert(5)
                .insert(6)
                .insert(4)
                .insert(7)
                .insert(2)
                .insert(1)
                .insert(0)
                .insert(3);
        expected = "{value=6, leftNode={value=2, leftNode={value=1, leftNode={value=0, leftNode={}, rightNode={}}, rightNode={}}, rightNode={value=4, leftNode={value=3, leftNode={}, rightNode={}}, rightNode={value=5, leftNode={}, rightNode={}}}}, rightNode={value=10, leftNode={value=7, leftNode={}, rightNode={}}, rightNode={}}}";
        assertEquals(
                "\nInsertion does not properly balance tree (must left, right, right, right, left, right rotate)" +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                1, avl.getBalanceFactor()
        );
    }

    // NOTE: Below test relies on insert() method
    @Test(timeout = 1000)
    public void advancedRotationsInsertionTest() {
        // Cause a situation with a RR, RL, LL or LR rotation is required.
        AVLTree<Integer> avl = new AVLTree<>(14)
                .insert(17)
                .insert(11)
                .insert(7)
                .insert(53)
                .insert(4)
                .insert(13)
                .insert(12)
                .insert(8)
                .insert(60)
                .insert(19)
                .insert(16)
                .insert(20);

        String expected = "{value=14, leftNode={value=11, leftNode={value=7, leftNode={value=4, leftNode={}, rightNode={}}, rightNode={value=8, leftNode={}, rightNode={}}}, rightNode={value=12, leftNode={}, rightNode={value=13, leftNode={}, rightNode={}}}}, rightNode={value=19, leftNode={value=17, leftNode={value=16, leftNode={}, rightNode={}}, rightNode={}}, rightNode={value=53, leftNode={value=20, leftNode={}, rightNode={}}, rightNode={value=60, leftNode={}, rightNode={}}}}}";
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                14, (int) avl.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                11, (int) avl.leftNode.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                19, (int) avl.rightNode.value
        );

        // Another double rotation requiring test.
        avl = new AVLTree<>(40)
                .insert(20)
                .insert(10)
                .insert(25)
                .insert(30)
                .insert(22)
                .insert(50);

        expected = "{value=25, leftNode={value=20, leftNode={value=10, leftNode={}, rightNode={}}, rightNode={value=22, leftNode={}, rightNode={}}}, rightNode={value=40, leftNode={value=30, leftNode={}, rightNode={}}, rightNode={value=50, leftNode={}, rightNode={}}}}";
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.leftNode.value);
        assertNotNull(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                avl.rightNode.value);
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                0, avl.getBalanceFactor()
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                25, (int) avl.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                20, (int) avl.leftNode.value
        );
        assertEquals(
                "\nInsertion cannot handle either right right, right left, left left or left right double rotations." +
                        "\nYour AVL tree should look like: " + expected + "\nBut it actually looks like: " + avl,
                40, (int) avl.rightNode.value
        );
    }





}
