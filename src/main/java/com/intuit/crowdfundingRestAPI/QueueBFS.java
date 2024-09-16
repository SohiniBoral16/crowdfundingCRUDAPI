import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public static <T> List<T> pollAll(Queue<T> queue) {
    List<T> result = new ArrayList<>();
    
    while (!queue.isEmpty()) {
        T element = queue.poll();
        // Add the element to the result list
        result.add(element);
        
        // Process the polled element (optional, if you want to do something during polling)
        System.out.println("Processing element: " + element);
    }
    
    return result;  // Return the list of all polled elements
}

public static <T> void pollAll(Queue<T> queue) {
    while (!queue.isEmpty()) {
        T element = queue.poll();
        // Process the polled element
        System.out.println("Processing element: " + element);
        
        // Add your processing logic here for each polled element
    }
}
