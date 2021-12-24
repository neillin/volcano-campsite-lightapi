package com.mservicetech.campsite.sample;

import java.util.LinkedList;
import java.util.Queue;

public class Tree2 {
    static ListNode list = new ListNode(0);
    public  static void preOrder(TreeNode root) {
        if (root == null) return;
        System.out.println(root.val + " ");
        preOrder(root.left);
        preOrder(root.right);
    }

    public  static void preOrderToList(TreeNode root, ListNode list) {
        if (root == null) return;
        ListNode p = list;
        p.next = new ListNode(root.val);

        preOrderToList(root.left, p.next);
        preOrderToList(root.right, p.next);

    }

    public static ListNode getList(TreeNode root) {
        ListNode list = new ListNode(0);
        ListNode p = list;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode temp = queue.poll();
            p.next = new ListNode(temp.val);
            p = p.next;
            if (temp.left!=null) {
                queue.add(temp.left);
            }
            if (temp.right!=null) {
                queue.add(temp.right);
            }

        }
        p.next = null;
        return list.next;
    }

    public static TreeNode buildBinary() {
        return null;
    }


    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right = new TreeNode(5);
        root.right.right = new TreeNode(6);
     //   preOrder(root);
      //  preOrderToList(root, list);
//        ListNode resList = getList(root);
//        while (resList!=null) {
//            System.out.println(resList.val + "->");
//            resList = resList.next;
//        }

         int i = 5;
          int j = 9;
          System.out.println(5&4);
        String j1 = Integer.toBinaryString(j);
        String j2 = Integer.toBinaryString(-9);
          System.out.println(j1);
        System.out.println(j2);
    }


}
