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

Checkpoint 8 Benchmark Info"
The benchmark tests run large batches of Collatz computations across several versions of the compute engine:

Original ComputeEngineAPIImpl
Fast FastComputeEngineAPIImpl
Optimized ComputeEngineOptimizedAPIImpl
Ultra Fast ComputeEngineAPIUltraFastImpl

Each benchmark run evaluates thousands of Collatz sequences and logs the timing results.

The Fast version provides noticeable speed improvements compared to the original.
The Optimized version consistently outperforms the original.
The Ultra Fast version performs the best overall and demonstrates the highest percentage improvement.

With the new tests/code added, results now include all four versions and print out average times along with improvement percentages over the original.
Detailed results from the runs are saved in checkpoint8output.txt and checkpoint9output.txt.

![System Diagram](https://github.com/CPS353-Suny-New-Paltz/project-starter-code-johncavanaugh426-source/blob/main/Part2Checkpoint2.png?raw=true)
