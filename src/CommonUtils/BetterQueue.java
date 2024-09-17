package CommonUtils;

import java.awt.*;

/**
 * @implNote implement a queue using a circular array with initial capacity 8.
 *
 * Implement BetterQueueInterface and add a constructor
 *
 * You are explicitly forbidden from using java.util.Queue and any subclass
 * (including LinkedList, for example) and any other java.util.* library EXCEPT java.util.Objects.
 * Write your own implementation of a Queue.
 *
 * Another great example of why we are implementing our own queue here is that
 * our queue is actually FASTER than Java's LinkedList (our solution is 2x faster!). This is due
 * to a few reasons, the biggest of which are 1. the overhead associated with standard library
 * classes, 2. the fact that Java's LinkedList doesn't store elements next to each other, which
 * increases memory overhead for the system, and 3. LinkedList stores 2 pointers with each element,
 * which matters when you store classes that aren't massive (because it increases the size of each
 * element, making more work for the system).
 *
 * @param <E> the type of object this queue will be holding
 */
public class BetterQueue<E> implements BetterQueueInterface<E> {

    /**
     * Initial size of queue.  Do not decrease capacity below this value.
     */
    private final int INIT_CAPACITY = 8;


    /**
     * If the array needs to increase in size, it should be increased to
     * old capacity * INCREASE_FACTOR.
     *
     * If it cannot increase by that much (old capacity * INCREASE_FACTOR > max int),
     * it should increase by CONSTANT_INCREMENT.
     *
     * If that can't be done either throw OutOfMemoryError()
     *
     */
    private final int INCREASE_FACTOR = 2;
    private final int CONSTANT_INCREMENT = 1 << 5; // 32



    /**
     * If the number of elements stored is < capacity * DECREASE_FACTOR, it should decrease
     * the capacity of the UDS to max(capacity * DECREASE_FACTOR, initial capacity).
     *
     */
    private final double DECREASE_FACTOR = 0.5;


    /**
     * Array to store elements in (according to the implementation
     * note in the class header comment).
     *
     * Circular arrays work as follows:
     *
     *   1. Removing an element increments the "first" index
     *   2. Adding an element inserts it into the next available slot. Incrementing
     *      the "last" index WRAPS to the front of the array, if there are spots available
     *      there (if we have removed some elements, for example).
     *   3. The only way to know if the array is full is if the "last" index
     *      is right in front of the "first" index.
     *   4. If you need to increase the size of the array, put the elements back into
     *      the array starting with "first" (i.e. "first" is at index 0 in the new array).
     *   5. No other implementation details will be given, but a good piece of advice
     *      is to draw out what should be happening in each operation before you code it.
     *
     *   hint: modulus might be helpful
     */
    private E[] queue;
    private long out_idx;
    private long in_idx;
    private long capacity;
    private long size;

    /**
     * Constructs an empty queue
     */
    @SuppressWarnings("unchecked")
    public BetterQueue(){
        this.in_idx = 0;
        this.out_idx = 0;
        this.size = 0;
        this.capacity = INIT_CAPACITY;
        this.queue = (E[]) new Object[INIT_CAPACITY];
    }
    private int generateNewCapacityUpsize() throws OutOfMemoryError {
        long newCap = ((long)this.capacity) * ((long)INCREASE_FACTOR);
        if (newCap > Integer.MAX_VALUE) {
            newCap = ((long)this.capacity) + ((long)CONSTANT_INCREMENT);
            if (newCap > Integer.MAX_VALUE) {
                throw new OutOfMemoryError();
            }
        }
        return (int)newCap;
    }

    @SuppressWarnings("unchecked")
    private void sizeUp() throws OutOfMemoryError {
        if (this.size < this.capacity) {
            /* nothing to be done, yay! */
            return;
        }
        /* let's determine what the new size will be */
        int newCap = generateNewCapacityUpsize();
        E[] newQueue = (E[])new Object[newCap];
        /* copy over */
        for (int i = 0; i < size; i++) {
            long from = (this.in_idx + i) % this.capacity;
            newQueue[i] = queue[(int)from];
        }
        this.queue = newQueue;
        this.capacity = newCap;
        this.out_idx = 0;
        this.in_idx = size;
    }

    private void sizeDown() throws OutOfMemoryError {
        /* too big to downsize */
        if (this.size >= this.capacity * DECREASE_FACTOR) {
            return;
        }
        /* also, cannot go smaller than min capacity */
        if (this.size <= INIT_CAPACITY) { return; }

        /* determine new capacity & generate */
        int newCap = Integer.max((int)(DECREASE_FACTOR * this.capacity), INIT_CAPACITY);
        E[] newQueue = (E[])new Object[newCap];
        for (int i = 0; i < this.size; i++) {
            long from = (this.out_idx + i) % this.capacity;
            newQueue[i] = queue[(int)from];
        }
        this.queue = newQueue;
        this.capacity = newCap;
        this.out_idx = 0;
        this.in_idx = size;
    }
    /**
     * Add an item to the back of the queue
     *
     * @param item item to push
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void add(E item) {
        /* make sure we weren't given a bogus item */
        if (item == null) { throw new NullPointerException(); }
        /* size up (if needed!) */
        this.sizeUp();
        /* place the item */
        this.queue[(int)this.in_idx] = item;
        /* update indices */
        this.in_idx = (this.in_idx + 1) % this.capacity;
        this.size++;
    }

    /**
     * Returns the front of the queue (does not remove it) or <code>null</code> if the queue is empty
     *
     * @return front of the queue or <code>null</code> if the queue is empty
     */
    @Override
    public E peek() {
        if (this.isEmpty()) { return null; }
        return this.queue[(int)this.out_idx];
    }

    /**
     * Returns and removes the front of the queue
     *
     * @return the head of the queue, or <code>null</code> if this queue is empty
     */
    @Override
    public E remove() {
        if (this.isEmpty()) { return null; }
        /*  take the item out */
        E item = this.queue[(int)this.out_idx];
        this.queue[(int)this.out_idx] = null;
        /* update out index and size */
        this.out_idx = (this.out_idx + 1) % this.capacity;
        this.size--;
        /* size the array down, if necessary */
        this.sizeDown();
        /* return the item */
        return item;
    }

    /**
     * Returns the number of elements in the queue
     *
     * @return integer representing the number of elements in the queue
     */
    @Override
    public int size() {
        return (int)this.size;
    }

    /**
     * Returns whether the queue is empty
     *
     * @return true if the queue is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return (this.size == 0);
    }

    public int getInIndex() {
        return (int)this.in_idx;
    }
    public int getOutIndex() {
        return (int)this.out_idx;
    }

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void draw(Graphics g) {
        //DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
        if(g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: draw the queue how we discussed
        //251 STUDENTS:: YOU ARE NOT THE GRAPHICS DEVELOPER!
    }
}
