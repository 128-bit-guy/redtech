package _128_bit_guy.redtech.common.part.wire;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.attribute.wire.WSElement;

import java.util.*;

public class WirePowerPropagator {
    public static void propagate(WSElement element, boolean shouldSetZeroes) {
        if(shouldSetZeroes) {
            Set<RAWSElement> toStartPropagation = new HashSet<>();
            zeroingDfs((RAWSElement)element, new HashSet<>(), toStartPropagation);
            ((RAWSElement) element).setPower(((RAWSElement) element).getIncomingRedstonePower());
            toStartPropagation.add((RAWSElement)element);
            propagateInternal(toStartPropagation);
        } else {
//            ((RAWSElement) element).setPower(Math.max(((RAWSElement) element).getIncomingRedstonePower(), ((RAWSElement) element).getPower()));
            propagateInternal(Collections.singleton((RAWSElement)element));
            System.out.println("");
        }
    }

    private static void propagateInternal(Set<RAWSElement> start) {
        System.out.println("Calling propagate internal");
        PriorityQueue<WirePPQueueEntry> queue = new PriorityQueue<>(Comparator.reverseOrder());
        for(RAWSElement element : start) {
            queue.add(new WirePPQueueEntry(Math.max(element.getPower(), element.getIncomingRedstonePower()), element, true));
        }
        while (!queue.isEmpty()) {
            WirePPQueueEntry entry = queue.poll();
            if(!entry.isValid()) {
                continue;
            }
            System.out.println(entry.element.getPtr().getBlockPos() + " " + entry.power);
            entry.element.setPower(entry.power);
            for(WSElement element : entry.element.getConnections().values()) {
                RAWSElement element2 = (RAWSElement)element;
                queue.add(new WirePPQueueEntry(Math.max(entry.power - 1, Math.max(element2.getIncomingRedstonePower(), element2.getPower())), element2));
            }
        }
    }

    private static void zeroingDfs(RAWSElement element, Set<WirePointer> was, Set<RAWSElement> found) {
        System.out.println(element.getPtr().getBlockPos() + "setting to zero");
        if(was.contains(element.getPtr())) {
            return;
        }
        was.add(element.getPtr());
        int wasPower = element.getPower();
        element.setPower(0);
        for(WSElement element1 : element.getConnections().values()) {
            if(element1 instanceof RAWSElement) {
                RAWSElement element2 = (RAWSElement)element1;
                if(element2.getPower() < wasPower && element2.getPower() != 0) {
                    zeroingDfs(element2, was, found);
                } else {
                    found.add(element2);
                }
            }
        }
//        found.remove(element);
    }
}
