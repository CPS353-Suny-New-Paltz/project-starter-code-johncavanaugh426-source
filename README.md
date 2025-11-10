# John_Cavanaugh_Software_Engineering

The computation I chose is the Collatz Sequence: 
This sequence takes in a positive integer n and follows these rules:
If n is even, divide n by 2
If n is odd, multiply n by 3 and add 1
Once n = 1, the sequence is over

The output is a string of all the numbers seen during the computation

Example input and output:
Input: 6
Output: 6,3,10,5,16,8,4,2,1

The current thread limit is 8

Checkpoint 8 Test info: 
The benchmark test repeatedly computes 100,000 Collatz sequences using both the original ComputeEngineAPIImpl and the optimized FastComputeEngineAPIImpl.
The optimized version parallelizes the computations using multiple threads to improve performance. 
It measures the execution time for each run, calculates averages, and compares performance. 
The optimized version was found to be up to 40% faster than the original on average.
All detailed results from the 10 runs are saved in checkpoint8output.txt.

![System Diagram](https://github.com/CPS353-Suny-New-Paltz/project-starter-code-johncavanaugh426-source/blob/main/Part2Checkpoint2.png?raw=true)
