package com.mservicetech.campsite.sample;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Heap2 {

    PriorityQueue<Integer> minQueue = new PriorityQueue<>();
    PriorityQueue<Integer> maxQueue = new PriorityQueue<>(Comparator.reverseOrder());

    public Heap2()
    {

    }

    public void addNum(int num) {
        minQueue.add(num);
        maxQueue.add(minQueue.poll());
        if (minQueue.size()<maxQueue.size()) {
            minQueue.add(maxQueue.poll());
        }
    }

    public double getMedian() {
        if (minQueue.size()>maxQueue.size()) {
            return minQueue.peek();
        } else {
            return (minQueue.peek() + maxQueue.peek())/2.0;
        }

    }

    public int availableRoom(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparing((int[] i)->i[0]));
        PriorityQueue<Integer> heap = new PriorityQueue<>();
     //   PriorityQueue<String> heap1 = new PriorityQueue<>(Comparator.comparing(String::length));
        int count = 0;
        for (int[] itv : intervals) {
             if (heap.isEmpty()) {
                 count++;
                 heap.add(itv[1]);
             } else {
                 if (itv[0] >= heap.peek()) {
                     heap.poll();
                     heap.add(itv[1]);
                 } else {
                     count++;
                     heap.add(itv[1]);
                 }
             }

        }
        return count;
    }
}
