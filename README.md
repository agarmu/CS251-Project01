# Message from the Hiring Manager

Congratulations! You have been hired to work for our startup in a position one under
the Head of Software! Since we aren't very large yet, you will be working in many
areas over the course of the next few months.
We are developing a First Person Sword-Fighting (FPSF) video game. In the game,
you have many types of bladed and blunt-force weapons which we will just refer to as
"swords" internally (inside the company). Additionally, you will have drones that can
assist you with your various tasks.
We will start your programming assignments with a few smaller tasks, just so you can
get a feel for the game and so we can assess if you're ready for larger tasks. Since we
use Agile and Scrum, we call these tasks “Stories” – which you may or may not be
familiar with already. If not, don’t worry, you will catch on quickly.
We have also hired a top-tier code tester, so make sure to thoroughly test your code
before sending it to them!
Just as a side note, your tasks will be delegated to you through the Head of Software.

# Project 1
Our team has decided that we are going to build many data structures from the ground
up in order to give the most flexibility when creating the game. For example, we could
incorporate draw functions into these data structures that have access to information
under the hood in a way that they wouldn't with the standard library. We are going to
have you write two of these data structures. Since these data structures will be usable
everywhere in the company, we are going to thoroughly test them and make sure they
work in a couple of use cases.

Your task is to create a Stack object and a Queue object. These will be similar to the
ones you have dealt with before. The specifications for the objects will be in the
attached Java Interface file, which is a very common way to pass specifications to
developers. The interfaces will force you to implement the functions it contains and
allow us to build tests on those functions without knowing your implementation, like a
true black-box test. You are explicitly forbidden from using the Stack and Queue Java
libraries.

Note for 251 students: You can explore how these work in the attached files, but do
NOT change the interface file at all. When testing, we will match your implementation
with our interface and if your code does not compile because you changed the function
signature or any of our specifications, your program might not be graded and you might
receive a 0 due to compilation errors. If you sent your boss (HS) or testers a file that
didn’t meet the specifications/interface they provided, there would be strong
repercussions.

_Note 2 for 251 students: You MUST read ALL of the comments in the attached files.
Implementation details are placed in relevant places, where they would be placed in an
actual development environment. Do NOT ask for help or clarification before you
have read through and tried to understand ALL comments. This applies to all
projects and is a life lesson in independent thinking._

## Story 1: Cleaning Swords
After a certain amount of use, a sword (remember, any hitting device in the game we
refer to as a "sword" for simplicity) must be cleaned. The player’s personal drone takes
care of that by taking the sword they are currently using to clean it and handing back
the sword that was used least recently, when it is clean. If two swords were returned at
the same time, prioritize the one that appeared first in the list. Here are the
specifications:

- It takes time T to clean each sword. There are a series of requests for swords at
  different times Z1, Z2, ..., Zt. This is to simulate when the player uses their
  weapon (sometimes fast, like during a battle, sometimes slow). The player also
  lends their weapons to their friends when they run out, so some of those times
  represent that. When a request Zj comes in for a sword, the drone is also
  handed back a used sword to clean. Your job is to output the time that each
  request is fulfilled and the time it took to fulfill that request. For overall game
  context, this time will be displayed as a countdown to the player of when the
  next weapon will be ready (but you don't need to worry about that part).
  - Consider in Minecraft when a weapon or tool slowly degrades over time.
    After enough damage, you either have to reforge it (repair it) or it breaks
    and is no longer usable. This is a similar idea except your drone
    automatically does the repairs and cleaning for you (and it occurs much
    more frequently).

- The drone can only clean one sword at a time. The drone can hand out as many
swords during one time step as are ready. If there are two swords clean at time
step 4 and two requests come in at time step 4, both requests will be filled in
time step 4.

- If the drone has an unclean sword during time step i, it will clean it (i.e. it won’t
wait for a request to begin cleaning a sword it has prior).

### Input
Input will begin with three space-separated integers N, 1 <= N <= 100 000, the number
of swords that are currently being cleaned (or are clean), M, 1 <= M <= 10 000 000, the
number of requests for a sword, and T, 0 <= T <= 10 000, the amount of time it takes to
clean each sword.

The following N lines will contain an integer Ti , 0 <= Ti <= T, the current “cleanliness” of
a sword, i.e. the amount of time left to clean sword i. It is guaranteed that all N of those
lines are in increasing order (i.e. the cleanest sword is first). The M lines after that
contain an integer Z, 0 <= Zj <= 100 000 000, the time that a sword is requested. It is
guaranteed that all M of those lines are in increasing order (i.e. first request first). Hint:
be careful with overflow.

### Output

Return an ArrayList of CleanSwordTimes (see the attached interface) with one element
for each request, in the order they were given, containing the time that request was
filled and the time it took to fulfill that request.

**Sample input 1:**
```
2 4 5 //COMMENT 1
0 //sword 1 is ready for use4 //sword 2 needs 4 more time steps to finish cleaning
0 //the first request is at time 0
1 //the second request is at time 1
1 //the third request is also at time 1
3 //the fourth request is at time 3
//COMMENT 1: n=2 swords being cleaned, m=4 requests for swords,
T=5 time steps required to clean each returned sword.
Note that the input file will not contain any of the comments written on each line above.
```

**Sample output 1:**
```
(format: time request was filled, space, time it took to fill request):
0 0
4 3
9 8
14 11
```

**Explanation:**
The first request at time 0 took the first/clean sword. The second request waited for the
next sword to finish cleaning, which happened at time 4, and took 3 time steps to finish
after the request was made. The third request (at time 1) waited for the sword returned
at time 0 to finish cleaning, which happened from time steps [4, 9) (inclusive of 4,
exclusive of 9, eg. {4, 5, 6, 7, 8}), and waited 8 time steps to get filled. The fourth
request (at time 3) waited for one of the swords returned at time 1 to finish cleaning,
which happened from time steps [9, 14), and waited 11 time steps to get filled.
Just a few contextual notes, you are not handling the actual use of the sword nor the
distribution of them, just the cleaning and time at which each request will be fulfilled.
You also don’t need to worry about keeping track of which swords are currently being
used, as someone else is handling that.

## Story 2: Requesting Items
The player can also request items from their personal drone at any time. During a
request, the player will stay in a quadrant while their drone gets that item from storage
and brings it to them. The drone can only carry one item at a time. To support more
flexibility, the content creators have decided to allow players to interrupt a request for
an item with the request for another. Here’s how it works:

- If the player requests item A and then suddenly needs item B, they can interrupt
  the drone to request item B. The drone will stop retrieving item A (by dropping it)
  and immediately start retrieving item B. Travel to the player’s storage takes time
  T (and so it takes 2*T time to retrieve a single item with no interruptions).
  Interrupting item A’s retrieval pauses item A’s timer, which begins again after item
  B is retrieved. To retrieve item B, the drone must travel from where it stopped
  retrieving A to storage to retrieve B, then to the player to deliver B, and then
  back to where it dropped A to finish retrieving A. Thus, the drone must
  remember where (or when) it dropped A.

You, the programmer helping with the logic, will get N requests for items at a series of
times. Your job is to return the times in increasing order of when the player receives
each item.

### Input
  The first line will contain two integers N, 1 <= N <= 10 000 000, representing both the
  number of items and the number of requests for items (i.e. each request is for a unique
  item), and T, 1 <= T <= 1000, the time it takes to go one-way to the storage location.
  The following line will contain N space-separated integers in increasing order
  representing the time that request i is made, 0 <= i <= 100 000 000.

### Output
  Return an ArrayList of ItemRetrievalTimes (see the attached interface) containing the
  times in increasing order of time of when the player receives each item. Identify each
  item by the order it was requested in (0-based indexing).
  
**Sample input:**
```
  2 3 //N=2, number of requests; T=3, time to storage (one-way)
  0 4 //Request 0 comes in at time 0 and request 1 comes in at time 4
``` 
  Note that the input file will not contain any of the comments written on each line above. 
  **Sample output:**
```
  (format: index of request, space, time player receives item):
  1 8
  0 12
```

**Explanation:**
  At time 0, the drone starts traveling from the player to storage. At time 4, drone D
  drops item A and travels 1 time step back to storage to retrieve item B, then travels 3
  time steps to the player to deliver item B, then travels 2 time steps back to where it
  dropped item A, then travels 2 final time steps to deliver item A.
## Testing
  There are 4 test files provided to you each worth 25 points on Vocareum:
```
  test/CommonUtilsTest/BetterQueueTest.java to test src/CommonUtils/BetterQueue.java
  test/CommonUtilsTest/BetterStackTest.java to test src/CommonUtils/BetterStack.java
  test/DronesTest/CleanSwordManagerTest.java to test src/Drones/CleanSwordManager.java
  test/DronesTest/ItemRequestManagerTest.java to test src/Drones/ItemRequestManager.java
```
  These tests act as an indicator of your progress but do not guarantee the displayed
  score. The final grade on both parts will be determined by your results on the tests run
  on Vocareum. You are encouraged to create your own main method and test files to
  debug any errors you face.
  Submission
  Your submission in Vocareum should include the following java files. Non-Java files are
  not permitted.
  src/CommonUtils/BetterQueue.javasrc/CommonUtils/BetterStack.java
  src/Drones/CleanSwordManager.java
  src/Drones/ItemRequestManager.java
  Make sure that you upload the files to the right place. Also, you should not edit any
  other files since they will be replaced before grading. Make sure that you do not add
  any imports that are not allowed. Remove all lines that contain System.out before
  submitting even the comments. The grading on Vocareum might take a few minutes so
  please be mindful.
  All submission instructions are the same for all projects, and can be found in the
  syllabus under the section “Programming Projects”. In all projects, students receive
  exactly 10 submissions to Vocareum per project. There will be no additional
  submissions given.