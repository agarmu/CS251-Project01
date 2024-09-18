package Drones;

import CommonUtils.BetterQueue;

import java.io.*;
import java.util.ArrayList;

/**
 * Manages everything regarding the cleaning of swords in our game.
 * Will be integrated with the other drone classes.
 *
 * You may only use java.util.List, java.util.ArrayList, and java.io.* from
 * the standard library.  Any other containers used must be ones you created.
 */
public class CleanSwordManager implements CleanSwordManagerInterface {
    private record SwordData(long receiveTime, long processingTime) {}

    private BetterQueue<Long> readRequestList(BufferedReader br, long count) throws IOException {
        BetterQueue<Long> q = new BetterQueue<>();
        for (int i = 0; i < count; i++) {
            long value = Long.parseLong(br.readLine());
            q.add(value);
        }
        return q;
    }
    private BetterQueue<SwordData> readSwordList(BufferedReader br, long count) throws IOException {
        BetterQueue<SwordData> q = new BetterQueue<>();
        for (int i = 0; i < count; i++) {
            long value = Long.parseLong(br.readLine());
            q.add(new SwordData(0,value));
        }
        return q;
    }

    private ArrayList<CleanSwordTimes> calculate(
            BetterQueue<SwordData> swords,
            BetterQueue<Long> requests,
            long cleaningTime
    ) {
        ArrayList<CleanSwordTimes> result = new ArrayList<>();
        long currentTime = 0;
        while (!requests.isEmpty()) {
            long requestReceiveTime = requests.remove();
            /* if we don't have a sword ready, we jump in time until we do */
            SwordData swordData = swords.remove();
            /* we can only clean one sword at a time--
                we start cleaning at the later of the sword receiving time & the current time
                    (after doing our other work)
                we end after the sword's processing time is over.
                also updates currentTime (since we spend that much time cleaning the next sword)
             */
            currentTime = Math.max(currentTime, swordData.receiveTime) + swordData.processingTime;

            /* now we calculate the parameters */
            long timeFilled = Math.max(requestReceiveTime,currentTime);
            long timeToFulfill = timeFilled - requestReceiveTime;
            result.add(new CleanSwordTimes(timeFilled,timeToFulfill));
            swords.add(new SwordData(requestReceiveTime,cleaningTime));
        }
        return result;
    }

    /**
     * Gets the cleaning times per the specifications.
     *
     * @param filename file to read input from
     * @return the list of times requests were filled and times it took to fill them, as per the specifications
     */
    @Override
    public ArrayList<CleanSwordTimes> getCleaningTimes(String filename) {
        long cleaningTime = 0;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            String[] firstLine = bf.readLine().split(" ");
            long numSwords = Long.parseLong(firstLine[0]);
            long numRequests = Long.parseLong(firstLine[1]);
            cleaningTime = Long.parseLong(firstLine[2]);
            BetterQueue<SwordData> swords = readSwordList(bf, numSwords);
            BetterQueue<Long> requests = readRequestList(bf, numRequests);
            bf.close();
            return calculate(swords, requests, cleaningTime);
        } catch (IOException e) {
            //This should never happen... uh oh o.o
            System.err.println("ATTENTION TAs: Couldn't find test file: \"" + filename + "\":: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }
}

