package com.taskweaver.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.taskweaver.model.Task;

public class MinHeap {
    private List<Task> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    public void insert(Task task) {
        heap.add(task);
        heapifyUp(heap.size() - 1);
    }

    public Task extractMin() {
        if (heap.isEmpty()) {
            return null;
        }

        Task min = heap.get(0);
        Task last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }

        return min;
    }

    public Task peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(heap);
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parentIndex)) >= 0) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size && heap.get(leftChild).compareTo(heap.get(smallest)) < 0) {
                smallest = leftChild;
            }

            if (rightChild < size && heap.get(rightChild).compareTo(heap.get(smallest)) < 0) {
                smallest = rightChild;
            }

            if (smallest == index) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        Task temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}