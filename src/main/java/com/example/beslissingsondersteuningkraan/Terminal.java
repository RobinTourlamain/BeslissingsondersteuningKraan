package com.example.beslissingsondersteuningkraan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Terminal {
    public String name;
    public int length;
    public int width;
    public int maxHeight;
    public int targetHeight;
    public final List<Container> containers;
    public final List<Slot> slots;
    public final List<List<Slot>> area;
    public final List<Crane> cranes;
    public final Map<Crane, List<Slot>> moves = new HashMap<>();

    public Terminal(String name, int length, int width, int maxHeight, int targetHeight, List<Container> containers, List<Slot> slots, List<List<Slot>> area, List<Crane> cranes) {
        this.name = name;
        this.length = length;
        this.width = width;
        this.maxHeight = maxHeight;
        this.targetHeight = targetHeight;
        this.containers = containers;
        this.slots = slots;
        this.area = area;
        for(Crane c: cranes){
            moves.put(c, new ArrayList<>());
        }
        this.cranes = cranes;
    }
}
