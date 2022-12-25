package com.example.beslissingsondersteuningkraan;

import java.util.Stack;

public class Slot {
    int id;
    int x;
    int y;
    int maxHeight;
    Stack<Container> containers;

    public Slot(int id, int x, int y, int maxHeight) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.maxHeight = maxHeight;
        this.containers = new Stack<>();
    }

    public void addContainer(Container container) {
        containers.add(container);
        assert containers.size() <= maxHeight;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id=" + id +
                '}';
    }
}
