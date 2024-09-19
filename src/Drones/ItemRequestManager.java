package Drones;

import CommonUtils.BetterQueue;
import CommonUtils.BetterStack;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Manages everything regarding the requesting of items in our game.
 * Will be integrated with the other drone classes.
 *
 * You may only use java.util.List, java.util.ArrayList, java.io.* and java.util.Scanner
 * from the standard library.  Any other containers used must be ones you created.
 */
public class ItemRequestManager implements ItemRequestManagerInterface {

    /**
     * stores information about an item request
     * @param index the index of the item
     * @param timeRequested the time at which the item is requested
     */
    record ItemRequestRecord(long index, long timeRequested) {
        /**
         * generates the data
         * @param sc the scanner to read from
         * @param N the number of records
         * @return the list of records
         */
        public static BetterQueue<ItemRequestRecord> parseAll(Scanner sc, int N) {
            BetterQueue<ItemRequestRecord> list = new BetterQueue<>();
            for (int i = 0; i < N; i++) {
                long timeRequested = sc.nextLong();
                list.add(new ItemRequestRecord(i, timeRequested));
            }
            return list;
        }
    }

    /**
     * contains the data of an item mid-handling
     * @param index the item index
     * @param distanceFromPlayer the current distance from the player
     */
    record ItemHandleRecord(long index, long distanceFromPlayer) {}

    private class Game {
        private long currentTime = 0;
        private long currentDistanceFromPlayer = 0;
        private final long fieldLength;
        private ArrayList<ItemRetrievalTimes> retrievalTimes;
        private BetterStack<ItemHandleRecord> itemstoHandle;
        private BetterQueue<ItemRequestRecord> requests;

        public Game(Scanner scan) throws IOException {
            /* parse data
                N (num. of entries) T (one-way travel time)
                [...
                ...
                N lines
                ...]
             */
            int numItems = scan.nextInt();
            this.fieldLength = scan.nextLong();
            this.itemstoHandle = new BetterStack<>();
            this.retrievalTimes = new ArrayList<>(numItems);
            this.requests = ItemRequestRecord.parseAll(scan, numItems);
        }
        public ArrayList<ItemRetrievalTimes> simulateGame() {
            while (!requests.isEmpty()) {
                /* we have a request! */
                ItemRequestRecord req = requests.remove();
                this.currentTime = req.timeRequested;
                long nextTime = requests.isEmpty() ? Long.MAX_VALUE : requests.peek().timeRequested;
                this.itemstoHandle.push(new ItemHandleRecord(req.index, fieldLength));
                this.attemptToEmptyStack(nextTime);
            }
            return this.retrievalTimes;
        }
        private void attemptToEmptyStack(long interruptTime) {
            while (!itemstoHandle.isEmpty()) {
                ItemHandleRecord item = itemstoHandle.pop();
                long timeAvailable = interruptTime - this.currentTime;
                /* now let's try to handle this item! */
                long timeToGetThere = Math.abs(item.distanceFromPlayer - this.currentDistanceFromPlayer);
                /* lteq because if we get there but can't do anything it effectively stays the same; just dropped there */
                if (timeAvailable <= timeToGetThere) {
                    /* we did literally nothing to advance, just moved around */
                    itemstoHandle.push(item);
                    /* we travel a distance of size timeAvailable... but unsure about direction */
                    long direction = (item.distanceFromPlayer > this.currentDistanceFromPlayer) ? 1 : -1;
                    long directedDistance = direction * timeAvailable;
                    this.currentTime += timeAvailable;
                    this.currentDistanceFromPlayer += directedDistance;
                    /* interruption time, stop trying to empty the stack */
                    return;
                }
                /* we got there, yay */
                this.currentTime += timeToGetThere;
                this.currentDistanceFromPlayer = item.distanceFromPlayer;
                timeAvailable -= timeToGetThere;
                /* did we get back? */
                long timeToGetBack = item.distanceFromPlayer;
                if (timeToGetBack > timeAvailable) {
                    /* noooo - we could not get back :( :(
                        ...but, we still got part of the way!
                     */
                    // thankfully, distance always towards 0 this time.
                    long newItemPosition = item.distanceFromPlayer - timeAvailable;
                    /* push this to the stack so we don't forget to take care of it */
                    itemstoHandle.push(new ItemHandleRecord(item.index, newItemPosition));
                    this.currentTime += timeAvailable;
                    this.currentDistanceFromPlayer = newItemPosition;
                    /* interruption time, stop trying to empty the stack */
                    return;
                }
                /* we got there??? we made progress ?!?! */
                this.currentTime += timeToGetBack; // spent however much time getting back
                this.currentDistanceFromPlayer = 0; // got back to player
                this.retrievalTimes.add(new ItemRetrievalTimes((int)item.index, this.currentTime));
                /* time for the next one! */
            }
        }
    }



    /**
     * Get the retrieval times as per the specifications
     *
     * @param filename file to read input from
     * @return the list of times requests were filled and index of the original request, per the specifications
     */
    @Override
    public ArrayList<ItemRetrievalTimes> getRetrievalTimes(String filename) {
        try {
            // as all of the inputs are on the same line, it is actually more efficient to use scanner's nextInt since
            // with BufferedReader you would have to read in the entire line (possibly 10m integers long) at once
            Scanner scan = new Scanner(new FileReader(filename));
            /* create a game */
            Game game = new Game(scan);
            return game.simulateGame();
        } catch (IOException e) {
            //This should never happen... uh oh o.o
            System.err.println("ATTENTION TAs: Couldn't find test file: \"" + filename + "\":: " + e.getMessage());
            System.exit(1);
        }
        /* UNREACHABLE */
        return null;
    }
}
