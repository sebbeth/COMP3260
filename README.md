# COMP3260

Testing in PowerShell with: javac Application.java ; javac AES.java ; java Application -e 'input.txt'

# Compiling and Running

This project is written in java, it can be compiled using: 
javac Application.java
and run using 
java Application [mode flag] <input file name>

e.g.
java Application -e input.txt
java Application -d input.txt


# testing

A test class has been included that generates random input to be encrypted and then decrypted.
This class feeds the output of AES encrypt to AES decrypt for each variation of AES implemented.
The test can be run by first compiling Tester.java and then running:
java Tester

# Authors

David Low - 3260947
Sebastian Brown - 3220619