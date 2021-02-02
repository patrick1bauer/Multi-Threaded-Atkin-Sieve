// Patrick Bauer
// COP 4520 - Concepts of Parallel and Distributed Processing
// 2/1/2021

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class MainPrime
{
    // Private Variables
    public static final int maxValue = 100000000; // Check all primes through this number
    public static final int numberOfThreads = 8; // Number of threads to create

    // Locks
    public final Semaphore lock = new Semaphore(1, true);

    // Public Shared Variables
    public static boolean sieve[] = new boolean[maxValue];
    public static boolean done[] = new boolean[numberOfThreads];
    public static long totalPrimesCalculated = 0;
    public static long totalSumOfPrimes = 0;

    // Threads
    private List<PrimeCalculator> primeCalculators;
    private List<Thread> primeCalculatorsThreads;
    
    // Main
    public static void main(String args[])
    {
        MainPrime mainThread = new MainPrime();
        
        // Initialize the sieve array with false values
        for (int i = 0; i < maxValue; i++)
        {
            mainThread.sieve[i] = false;
        }

        // Create prime calculating threads
        mainThread.primeCalculators = new ArrayList<>();
        mainThread.primeCalculatorsThreads = new ArrayList<>();
        for(int i = 0; i < numberOfThreads; i++)
        {
            PrimeCalculator primeCalculator = new PrimeCalculator(mainThread, i, maxValue);
            mainThread.primeCalculators.add(primeCalculator);
            Thread th = new Thread(primeCalculator);
            mainThread.primeCalculatorsThreads.add(th);
        }

        // Start the clock
        long startTime = System.nanoTime();

        // Start the prime calculators
        for (Thread i : mainThread.primeCalculatorsThreads)
        {
            i.start();
        }

        // Add 2 and 3 to the primes list
        try
        {
            mainThread.lock.acquire();
            mainThread.sieve[2] = true;
            mainThread.sieve[3] = true;
            mainThread.lock.release();
        }
        catch (InterruptedException e)
        {
            System.out.println(e); 
        }

        // Wait for threads to finish
        for (Thread i : mainThread.primeCalculatorsThreads)
        {
            try
            {
                i.join();
            }
            catch(Exception e) 
            {
                System.out.println("[Exception]: " + e);
            }
        }

        // Stop the clock
        long endTime = System.nanoTime();

        // Terminate all prime calculator threads
        for (Thread i : mainThread.primeCalculatorsThreads)
        {
            i.interrupt();
        }

        // Calculate execution time
        long executionTime = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(endTime - startTime); // Miliseconds

        // Calculate total primes calculated and sum of all primes found
        for (int i = 0; i < maxValue; i++)
        {
            if (sieve[i] == true)
            {
                totalPrimesCalculated++;
                totalSumOfPrimes += i;
            }
        }

        // Write all data to primes.txt file
        try
        {
            // Create a new output file
            File myFile = new File("primes.txt");
            myFile.createNewFile();
            FileWriter myWriter = new FileWriter("primes.txt");

            // Print data to first line
            myWriter.write(executionTime + "ms " + totalPrimesCalculated + " " + totalSumOfPrimes + "\n");

            // Find and print the top 10 primes to second line.
            int printedPrimes = 0;
            int[] topTenPrimes = new int[10];
            for (int i = maxValue - 1; i > 0 && printedPrimes < 10; i--)
            {
                if (sieve[i] == true)
                {
                    // Add the primes to an array
                    topTenPrimes[printedPrimes] = i;
                    printedPrimes++;
                }
            }
            for (int i = 9; i >= 0; i--)
            {
                // Print the values backwards so they are from smallest to largest.
                myWriter.write(topTenPrimes[i] + " ");
            }

            myWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("[IOException]: ");
            e.printStackTrace();
        }
    }
}

class PrimeCalculator implements Runnable
{
    // Membervariables
    private MainPrime mainThread;
    private int threadNumber;
    private int maxValue;

    // Thread Constructor
    public PrimeCalculator(MainPrime inputThread, int inputThreadNumber, int inputMaxValue)
    {
        this.mainThread = inputThread;
        this.threadNumber = inputThreadNumber;
        this.maxValue = inputMaxValue;
    }

    // Prime calculator
    @Override
    public void run()
    {
        try
        {
            // Calculate designated primes
            SieveOfAtkin(mainThread, threadNumber, maxValue);
        }
        catch (Exception e)
        {
            System.out.println("[Exception]: " + e);
        }
    }

    // Sieve Of Atkin
    public void SieveOfAtkin(MainPrime mainThread, int threadNumber, int maxValue)
    {
        // Mark sieve[n] is true if one of the following is true:
        for (int x = 1 + threadNumber; x * x < maxValue; x += mainThread.numberOfThreads)
        {
            for (int y = 1; y * y < maxValue; y++)
            {
                // n = (4*x*x)+(y*y) has odd number of solutions, i.e., there exists
                // an odd number of distinct pairs (x,y) that satisfy the equation and
                // n % 12 = 1 or n % 12 = 5.
                int n = (4 * x * x) + (y * y);
                if (n <= maxValue && (n % 12 == 1 || n % 12 == 5))
                {
                    try
                    {
                        this.mainThread.lock.acquire();
                        this.mainThread.sieve[n] ^= true;
                        this.mainThread.lock.release();
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println(e); 
                    }
                }

                // n = (3*x*x)+(y*y) has odd number of solutions and n % 12 = 7.
                n = (3 * x * x) + (y * y);
                if (n <= maxValue && n % 12 == 7)
                {
                    try
                    {
                        this.mainThread.lock.acquire();
                        this.mainThread.sieve[n] ^= true;
                        this.mainThread.lock.release();
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println(e); 
                    }
                }

                // n = (3*x*x)-(y*y) has odd number of solutions, x > y and n % 12 = 11
                n = (3 * x * x) - (y * y);
                if (x > y && n <= maxValue && n % 12 == 11)
                {
                    try
                    {
                        this.mainThread.lock.acquire();
                        this.mainThread.sieve[n] ^= true;
                        this.mainThread.lock.release();
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println(e); 
                    }
                }
            }
        }

        // Mark this thread as done with the first section.
        mainThread.done[threadNumber] = true;
        
        // Wait for all threads to finish first section.
        boolean allDone = false;
        while(!allDone)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception e)
            {

            }
            allDone = true;
            for (int i = 0; i < mainThread.numberOfThreads; i++)
            {
                if (mainThread.done[i] == false)
                {
                    allDone = false;
                    break;
                }
            }
        }

        // Mark all multiples of squares as non-prime
        for (int r = 5 + threadNumber; r * r < maxValue; r += mainThread.numberOfThreads)
        {
            try
            {
                this.mainThread.lock.acquire();
                if (this.mainThread.sieve[r])
                {
                    for (int i = r * r; i < maxValue; i += r * r)
                    {
                        if (this.mainThread.sieve[i] == true)
                        {
                            this.mainThread.sieve[i] = false;
                        }
                    }
                }
                this.mainThread.lock.release();
            }
            catch (InterruptedException e)
            {
                System.out.println(e); 
            }
        }
    }
}