# LZW-compression

# A modified LZW algorithm to accommodate fixed or variable sized codewords

#Specifications:
1. First make a copy of LZW.java named "MyLZW.java".  You will be modifying this file for your assignment.  Note that LZW.java is the example LZW code provided by the textbook.
1. Before making the required changes to MyLZW.java, you will need to read through the code, and run example compressions/expansions to understand how it is currently working.  Note that LZW.java (and hence your MyLZW.java) requires the following library files (also developed by the textbook authors):  BinaryStdIn.java, BinaryStdOut.java, TST.java, Queue.java, StdIn.java, and StdOut.java.  These files have already been added to your repository.
1. With a firm understanding of the provided code in hand, you can proceed to make the following changes to MyLZW.java:
  * Make it so that the algorithm will vary the size of the output/input codewords from 9 to 16 bits.
  * The codeword size should be increased when all of the codewords of a previous size have been used
  * Modify the code to have three options when the codebook is filled up (i.e., all 16 bit codewords have been used):
    1. **Do Nothing mode**  Do nothing and continue to use the full codebook (this is already implemented by LZW.java).
    1. **Reset mode** Reset the dictionary back to empty so that new codewords can be added. Be careful to reset at the appropriate place for both compression and expansion, so that the algorithms remain in sync.  This is very tricky and may require alot of planning in order to get it working correctly.
    1. **Monitor mode**  Initially do nothing (keep using the full codebook) but begin monitoring the *compression ratio* after no more 16 bit codewords remain.  Define the compression ratio to be the size of the original data that has been consumed so far divided by the size of the (compressed) output data produced so far.  At any point in time, the size of the original data is simply the number of bits of the input file that have been processed.  The size of the output data can also be easily calculated, and is based on the number of codewords output and the size of each (which will change as the algorithm progresses).  If the compression ratio degrades by more than a set threshold from the point when the last codeword was added then reset the dictionary to empty.  To determine the threshold for resetting you will take a ratio of compression ratios [(old ratio)/(new ratio)], where old ratio is the ratio recorded when your program entered Monitor mode, and new ratio is the current compression ratio.  If the ratio of ratios exceeds 1.1 then you should reset.  For example, if the compression ratio when you start monitoring is 2.5 and the compression ratio at some later point is 2.3, the ratio of ratios at that point would be 2.5/2.3 = 1.087, so you should not reset the dictionary.  Continuing, if your compression ratio drops to 2.2, the ratio of ratios would become 2.5/2.2 or 1.136. This means that your ratio of ratios has exceeded the threshold of 1.1 and you should now reset the dictionary.  As with the changing of the codeword bits, be very careful to coordinate the code for both compression and expansion so that it works correctly.
  * Which mode should be used should be chosen by the program during compression. Whichever mode is used to compress a file should also be used to expand the file. However, you should not require the user to state the mode on expansion. The mode used to compress a file should be stored at the beginning of the output file, so that it can be automatically retrieved during expansion.  To establish the mode to be used during compression, your program should accept 3 new command line arguments:
    * "n" for Do Nothing mode
    * "r" for Reset mode
    * "m" for Monitor mode
  * Note that the provided LZW code already accepts a command line argument to determine whether compression or expansion should be performed ("-" and "+", respectively), and that input/output files are provided via standard I/O redirection ("&lt;" to indicate an input file and "&gt;" to indicate an output file).  Hence, your new arguments should be handled in addition to what is provided. For example, to compress the file foo.txt to generate foo.lzw using Reset mode, you should call:
  ```
  java MyLZW - r < foo.txt > foo.lzw
  ```
  Similarly to inflate foo.lzw into foo2.txt, you should run:
  ```
  java MyLZW + < foo.lzw > foo2.txt
  ```
  Note that this example does not overwrite foo.txt.
  This is a good approach to take in testing your programs so that you can compare foo.txt and foo2.txt to ensure that they are the same file.
1. Once all of the required changes have been made to MyLZW.java, you should evaluate its performance on the 14 provided example files:  all.tar, assig2.doc, bmps.tar, code.txt, code2.txt, edit.exe, frosty.jpg, gone_fishin.bmp, large.txt, Lego-big.gif, medium.txt, texts.tar, wacky.bmp, and winnt256.bmp.  Specifically, for each of the provided example files, measure the original file size, compressed file size, and compression ratio (original file size / compressed file size) when compressed using the following techniques:
  * The unmodified LZW.java program (i.e., 12 bit codewords)
  * Your MyLZW.java (variable width codewords) using Do Nothing mode
  * Your MyLZW.java (variable width codewords) using Reset mode
  * Your MyLZW.java (variable width codewords) using Monitor mode
  * Another existing compression application of your choice (e.g., 7zip, WinZIP, gzip, bzip2)
You should organize your results of these compressions/expansions into a table in a text file named "results.txt" and submit it along with your code.
