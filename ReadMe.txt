===================================================================================================
Multi-Threaded-Atkin-Sieve

Patrick Bauer
COP 4520 - Concepts of Parallel and Distributed Processing
2/1/2021

FileName: MainPrime.java

===================================================================================================
Problem:
Your non-technical manager assigns you the task to find all primes between 1 and 108.  The 
assumption is that your company is going to use a parallel machine that supports eight concurrent 
threads. Thus, in your design you should plan to spawn 8 threads that will perform the necessary 
computation. Your boss does not have a strong technical background but she is a reasonable person. 
Therefore, she expects to see that the work is distributed such that the computational execution 
time is approximately equivalent among the threads. Finally, you need to provide a brief summary of
your approach and an informal statement reasoning about the correctness and efficiency of your 
design. Provide a summary of the experimental evaluation of your approach. Remember, that your 
company cannot afford a supercomputer and rents a machine by the minute, so the longer your 
program takes, the more it costs. Feel free to use any programming language of your choice that 
supports multi-threading as long as you provide a ReadMe file with instructions for your manager 
explaining how to compile and run your program from the command prompt.  

Zero and one are neither prime nor composite, so they are not included in the total number of
primes found and the sum of all primes found. The execution time starts prior to spawning the
threads and ends after all threads complete.

===================================================================================================
Instructions

1. Open a terminal
2. Navigate to the location of MainPrime.java
3. Compile the program with the following command:
    javac MainPrime.java
4. Run the compiled program with the following command:
    java MainPrime

===================================================================================================
Output

The output of the program is printed to primes.txt with the following format:

<execution time> <total number of primes found> <sum of all primes found>
<top ten maximum primes, listed in order from lowest to highest>

===================================================================================================
Proof of Correctness

This program uses a Java Semaphore lock to only allow 1 of the 8 threads the permit to modify
values in a boolean array at a time. This prevents the threads from overwriting each others' data.
Once the 8 threads are finished and the boolean array has prime indexes marked as true, the main
thread continues, and calculates the total primes found, the sum of all the primes found, and
finds the 10 largest primes.

The output of the program is proven correct by running a single-threaded brute force algorithm and
comparing the results with my multi-threaded program. Each of my program iterations listed below
all gave the same output for any input for 'maxValue' and 'numberofThreads'.

===================================================================================================
Experimental Evaluation, Efficiency

Processor Used: Intel® Core™ i7-6700 CPU @ 3.40GHz (4 cores, 8 threads)

My approach to this problem started by creating a single-threaded brute force algorithm for finding
prime numbers. The program struggled calculating primes throuh 1 million and took too long to
calculate primes through 100 million.

Execution Time: Unknown

Implementing threads into my brute force algorithm was challenging. At first I split up the work in
such a way for 8 threads to check a set of numbers. The first thread would check whether numbers
1-12500000 were prime, the second thread would check whether numbers 12500001-25000000 were prime,
... , and the eigth thread would check numbers 87500001-100000000 were prime. At a first glance
this seems like the approach was distributing the work evenly between the 8 threads but running the
program showed that the first thread was doing much less work than the eighth thread because the
first thread's numbers were significantly smaller than the eigth thread's numbers.

Execution Time: ~8 hours

Next I adjusted the distribution of my work to the threads by giving them each a "threadNumber"
value which each thread used to calculate every 8th number. This was more efficient than the
previous algorithm but still took quite a bit of time to calculate all the primes.

Execution Time: ~4 hours

After digging into my code, I realized that half the threads were not doing any work. The threads
starting on an even number were only checking if even numbers were prime because an even number
plus 8 is also an even number. My next adjustment to my brute force algorithm gave all my threads
odd numbers to check (and added the even number 2 manually). This vastly improved my runtime but
was nowhere near where I wanted my program to be efficiency-wise.

Execution Time: ~2 hours

Thinking about my odd-only adjustment from my previous iteration, I started looking online for more
adjustments I could use and came upon Atkin's Sieve algorithm. I swapped out my odd-only brute
force algorithm for Atkin's Sieve which provided me with a huge execution time decrease. The
distribution of work to the 8 threads still involves giving each thread a threadNumber value, and
after thinking about this some more, I realize that the threads with a small threadNumber value
(Ex: 0) are doing less work than the threads with a large threadNumber value (Ex: 7) but this is 
negligent. Setting breakpoints in the debugger proved that all 8 threads were ending relatively at
the same time.

Execution Time: 121196ms (~2 minutes)

More work can be done to increase the efficiency of the program by changing the lock system to
a synchronized method system. This has the potential to decrease the execution time to around 5
seconds.

===================================================================================================
