package com.mservicetech.campsite.sample;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Heap1 {

    static class ArrayContainer implements Comparable<ArrayContainer> {
        int[] arr;
        int index;
        public ArrayContainer(int[] arr, int index) {
            this.arr = arr;
            this.index = index;
        }

        @Override
        public int compareTo(ArrayContainer o) {
            return this.arr[index] - o.arr[o.index];

        }
    }

    public ListNode mergeKListNode(ListNode[] lists) {
        PriorityQueue<ListNode> queue = new PriorityQueue<>(new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                return o1.val-o2.val;
            }
        });

        ListNode head = new ListNode(0);
        ListNode p = head;
        for (ListNode list: lists) {
            queue.add(list);
        }
        while (!queue.isEmpty()) {
            ListNode node = queue.poll();
            p.next = node;
            p = p.next;
            if (node.next!=null) {
                queue.add(node.next);
            }
        }
        return head.next;
    }

    public static int[] mergeKSortArrays(int[][] arr) {
        PriorityQueue<ArrayContainer> queue = new PriorityQueue<>();
        int total = 0;
        for (int i=0; i<arr.length; i++) {
            queue.add(new ArrayContainer(arr[i], 0));
            total = total + arr[i].length;
        }
        int m=0;
        int result[] = new int[total];
        while (!queue.isEmpty()) {
            ArrayContainer temp = queue.poll();
            result[m++] = temp.arr[temp.index];
            if (temp.index<temp.arr.length-1) {
                queue.add(new ArrayContainer(temp.arr, temp.index+1));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr1 = new int[] {1,3,5,7};
        int[] arr2 = new int[] {2,4,6,8};
        int[] arr3 = new int[] {0,9,10,11};

        int[][] arr = new int[][] {arr1, arr2, arr3};
        int[] result = mergeKSortArrays(arr);
        Arrays.stream(result).forEach(r->System.out.print(r + " "));
    }
}
