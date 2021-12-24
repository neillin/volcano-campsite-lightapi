package com.mservicetech.campsite.sample;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class KSmallest {

    public int kthSmallest (TreeNode root , int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode p = root;
        int result = 0;
        while (!stack.isEmpty() || p!=null) {
            if (p!=null) {
                stack.push(p);
                p = p.left;
            } else {
                TreeNode t = stack.pop();
                k--;
                if (k==0) {
                    result = t.val;
                    break;
                }
                p= t.right;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Stack<TreeNode> stack = new Stack<>();
        System.out.println(stack.isEmpty());
        Queue<String> queue = new LinkedList<>();
        int[] arr = new int[] {1,3,5,7,8,12};
        int a = Arrays.binarySearch(arr, 5);
        System.out.println(a);

    }
}
