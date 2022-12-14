package com.example.beslissingsondersteuningkraan;

public class Action {
    public final Container container;
    public final Slot slot;
    public Slot prevSlot;

    public Action(Container container, Slot slot) {
        this.container = container;
        this.slot = slot;
    }

    public void execute(Terminal terminal) {
        prevSlot = container.slots.get(0);
        container.removeFromSlots();
        for (int i = 0; i < container.length; i++) {
            container.assignSlot(terminal.slots.get(slot.id + i));
        }
    }

    public void reverse(Terminal terminal) {
        container.removeFromSlots();
        for (int i = 0; i < container.length; i++) {
            container.assignSlot(terminal.slots.get(prevSlot.id + i));
        }
    }

    @Override
    public String toString() {
        return "Action{" +
                "container=" + container.id +
                ", slot=" + slot.id +
                ", prevSlot=" + prevSlot.id +
                '}';
    }
}
