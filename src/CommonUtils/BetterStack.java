package CommonUtils;

import java.util.EmptyStackException;

/**
 * @implNote Implement a stack using an array with initial capacity 8.
 *
 * Implement BetterStackInterface and add a constructor
 *
 * You are explicitly forbidden from using java.util.Stack and any
 * other java.util.* library EXCEPT java.util.EmptyStackException and java.util.Arrays.
 * Write your own implementation of a Stack.
 *
 *
 * @param <E> the type of object this stack will be holding
 */
public class BetterStack<E> implements BetterStackInterface<E> {

    /**
     * Initial size of stack.  Do not decrease capacity below this value.
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
     */
    private E[] stack;
    private int capacity;
    private int size;

    /**
     * Constructs an empty stack
     */
    @SuppressWarnings("unchecked")
    public BetterStack(){
        this.stack = (E[]) new Object[INIT_CAPACITY];
        this.capacity = INIT_CAPACITY;
        this.size = 0;
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
        if (this.size != this.capacity) {
            return;
        }
        int newCap = generateNewCapacityUpsize();
        E[] newStack = (E[])new Object[newCap];
        for (int i = 0; i < this.size; i++) {
            newStack[i] = this.stack[i];
        }
        this.stack = newStack;
        this.capacity = newCap;
    }

    @SuppressWarnings("unchecked")
    private void sizeDown() {
        if (this.size >= this.capacity * DECREASE_FACTOR) {
            return;
        }
        /* also, cannot go smaller than min capacity */
        if (this.size <= INIT_CAPACITY) { return; }
        int newCap = Integer.max((int)(DECREASE_FACTOR * this.capacity), INIT_CAPACITY);
        E[] newStack = (E[])new Object[newCap];
        for (int i = 0; i < this.size; i++) {
            newStack[i] = this.stack[i];
        }
        this.capacity = newCap;
        this.stack = newStack;
    }
    /**
     * Push an item onto the top of the stack
     *
     * @param item item to push
     * @throws OutOfMemoryError if the underlying data structure cannot hold any more elements
     */
    @Override
    public void push(E item) throws OutOfMemoryError {
        if (item == null) {
            throw new NullPointerException();
        }
        /* increase size if necessary */
        this.sizeUp();
        /* push to stack */
        this.stack[this.size] = item;
        this.size++;
    }

    /**
     * Remove and return the top item on the stack
     *
     * @return the top of the stack
     * @throws EmptyStackException if stack is empty
     */
    @Override
    public E pop() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        this.size--;
        E item = this.stack[this.size];
        this.stack[this.size] = null;
        this.sizeDown();
        return item;
    }

    /**
     * Returns the top of the stack (does not remove it).
     *
     * @return the top of the stack
     * @throws EmptyStackException if stack is empty
     */
    @Override
    public E peek() {
        if (this.isEmpty()) {
            throw new EmptyStackException();
        }
        return this.stack[this.size - 1];
    }

    /**
     * Returns whether the stack is empty
     *
     * @return true if the stack is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return (this.size == 0);
    }

    /**
     * Returns the number of elements in the stack
     *
     * @return integer representing the number of elements in the stack
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
     *
     * @param g graphics object to draw on
     */
    @Override
    public void draw(java.awt.Graphics g) {
        //DO NOT MODIFY NOR IMPLEMENT THIS FUNCTION
        if(g != null) g.getColor();
        //todo GRAPHICS DEVELOPER:: draw the stack how we discussed
        //251 STUDENTS:: YOU ARE NOT THE GRAPHICS DEVELOPER!
    }
}
