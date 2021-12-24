package com.mservicetech.campsite.sample;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ReOrderList {

    static ListNode reverseOrder(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode pre = head;
        ListNode curr = head.next;
        while (curr!=null) {
            ListNode temp = curr.next;
            curr.next = pre;
            pre = curr;
            curr = temp;
        }
        head.next = null;
        return pre;
    }

    static void printListNode (ListNode n) {
        ListNode temp = n;

        while (temp!=null) {
            System.out.print(temp.val + "->");
            temp = temp.next;
        }
        System.out.println( "null");
    }

    public static  void  main(String[] args) {
        ListNode n1 = new ListNode(1);
        ListNode n2 = new ListNode(2);
        ListNode n3 = new ListNode(3);
        ListNode n4 = new ListNode(4);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        printListNode(n1);
        ListNode reversed = reverseOrder(n1);
        printListNode(reversed);

        PriorityQueue<String> queue = new PriorityQueue<>();
        Queue<String> queue1 = new LinkedList<>();

    }


}
