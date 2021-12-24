package com.mservicetech.campsite.sample;

import java.util.LinkedList;
import java.util.Queue;

public class Tree1 {

    public boolean isValidBSF(com.networknt.http.client.wrap.TreeNode node) {

        return isValidBFS(node, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public boolean isValidBFS(com.networknt.http.client.wrap.TreeNode node, int min, int max) {
        if (node == null) return true;
        if(node.val>max || node.val<min )
           return false;
        return isValidBFS(node.left, min, node.val) && isValidBFS(node.right, node.val, max);
    }

    public boolean hasMatchSum(com.networknt.http.client.wrap.TreeNode node, int sum) {
        if (node == null) return false;
        Queue<com.networknt.http.client.wrap.TreeNode> queue = new LinkedList<>();
        Queue<Integer> value = new LinkedList<>();
        queue.add(node);
        value.add(node.val);
        int total = 0;
        while (!queue.isEmpty()) {
            com.networknt.http.client.wrap.TreeNode temp = queue.poll();
            total = value.poll();
            if (temp.left==null && temp.right==null && total==sum) {
                return true;
            }
            if (temp.left!=null) {
                queue.add(temp.left);
                value.add(total + temp.left.val);
            }
            if (temp.right!=null) {
                queue.add(temp.right);
                value.add(total + temp.right.val);
            }
        }
        return false;
    }
}
