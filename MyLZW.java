///////////////////////////////////////////////////////////////////////////////
///////////////////////////////// MyLZW Class /////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

public class MyLZW {

    // Holds the mode of compression
    private static String mode;

    // Number of input characters
    private static final int R = 256;

    // Number of 9-bit codewords (2^9)
    private static int L = 512;

    // Number of 16-bit codewords (2^16)
    private static int M = 65535;

    // Codeword width
    private static int W = 9;

    // Contains size of original data
    private static double numerator = 0;

    // Size of compressed output data
    private static double denominator = 0;

    // Compression ratio of numerator / denominator
    private static double ratio = 0;

    // The previous recorded compression ratio
    private static double set_ratio = 0;

    // Boolean value for compression result check
    private static boolean ratio_check = true;

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////// Compress ////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    public static void compress() {

        // Do nothing mode
        if (mode.equals("n")) {
          BinaryStdOut.write('n',8);
        }

        // Reset mode
        else if (mode.equals("r")) {
          BinaryStdOut.write('r',8);
        }

        // Monitor mode
        else if (mode.equals("m")) {
          BinaryStdOut.write('m',8);
        }

        String input = BinaryStdIn.readString();

        // Create new ternary search trie object to hold the symbol table
        TST<Integer> st = new TST<Integer>();

        for (int i = 0; i < R; i++) {
            st.put("" + (char) i, i);
        }

        // R is codeword for EOF
        int code = R+1;

        while (input.length() > 0) {

            // Allows the number of codewords to change based
            // on codeword width (2^W)
            L = (int)Math.pow(2,W);

            // Find max prefix match s.
            String s = st.longestPrefixOf(input);

            // Calculate the ratio
            numerator = numerator + (s.length() * 8);

            // Print s's encoding.
            BinaryStdOut.write(st.get(s), W);

            denominator = denominator + W;
            ratio = numerator / denominator;

            int t = s.length();

            // Add s to symbol table.
            if (t < input.length() && code < L) {
                st.put(input.substring(0, t + 1), code++);
            }

            if (W < 16 && ((int)Math.pow(2,W) == code)) {
                W++;
                L = (int)Math.pow(2,W);
                st.put(input.substring(0,t+1), code++);
            }

            if (code == M && mode.equals("r")) {

              // Create new ternary search trie object to hold the symbol table
              st = new TST<Integer>();

              for (int i = 0; i < R; i++) {
                  st.put("" + (char) i, i);
              }

              // R is codeword for EOF
              code = R+1;

              L = 512;
              W = 9;
            }

            if (code == M && mode.equals("m")) {

              if (ratio_check == true) {
                set_ratio = ratio;
                ratio_check = false;
              }

              if ((set_ratio / ratio) > 1.1) {

                // Create new ternary search trie object to hold the symbol table
                st = new TST<Integer>();

                for (int i = 0; i < R; i++) {
                    st.put("" + (char) i, i);
                }

                // R is codeword for EOF
                code = R+1;

                // Reset the parameters
                ratio_check = true;
                set_ratio = 0;
                ratio = 0;
                L = 512;
                W = 9;
              }
            }

            // Scan past s in input.
            input = input.substring(t);
        }

        BinaryStdOut.write(R, W);

        BinaryStdOut.close();
    }

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// Expand ////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    public static void expand() {

        // Detect compression mode
        char compress_mode = BinaryStdIn.readChar(8);

        // Set the expansion mode to m if located
        if (compress_mode == 'm') {
          mode = "m";
        }

        // Set the expansion mode to r if located
        if (compress_mode == 'r') {
          mode = "r";
        }

        String[] st = new String[M];

        // Next available codeword value
        int i;

        // Initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++) {
            st[i] = "" + (char) i;
        }

        // (Unused) Lookahead for EOF
        st[i++] = "";

        int codeword = BinaryStdIn.readInt(W);

        // Expanded message is empty string
        if (codeword == R) {
            return;
        }

        String val = st[codeword];

        while (true) {

            BinaryStdOut.write(val);

            numerator = numerator + (val.length() * 8);

            codeword = BinaryStdIn.readInt(W);

            denominator = denominator + W;

            ratio = numerator / denominator;

            if (codeword == R) {
                break;
            }

            String s = st[codeword];

            // Special case hack
            if (i == codeword) {
                s = val + val.charAt(0);
            }

            if (i < L) {
                st[i++] = val + s.charAt(0);
            }

            if (i == L-1 && W < 16) {
                st[i++] = val + s.charAt(0);
                W++;
                L = (int)(Math.pow(2,W));
            }

            val = s;

            if (i == M && mode.equals("r")) {

                L = 512;
                W = 9;

                st = new String[M];

                for (i = 0; i < R; i++) {
                    st[i] = "" + (char)i;
                }

                st[i++] = "";

                codeword = BinaryStdIn.readInt(W);

                if (codeword == R) {
                    return;
                }

                val = st[codeword];
            }

            if (i == M && mode.equals("m")) {

                if (ratio_check == true) {
                  set_ratio = ratio;
                  ratio_check = false;
                }

                System.err.println();

                if ((set_ratio / ratio) > 1.1) {

                  st = new String[M];

                  L = 512;
                  W = 9;

                  for (i = 0; i < R; i++) {
                    st[i] = "" + (char)i;
                  }

                  st[i++] = "";

                  codeword = BinaryStdIn.readInt(W);

                  if (codeword == R) {
                    return;
                  }

                  val = st[codeword];

                  ratio_check = true;
                  set_ratio = 0;
                  ratio = 0;
                }
            }
        }
        BinaryStdOut.close();
    }

///////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// Main //////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Do Nothing Mode / Compression
        if (args[0].equals("-") && args[1].equals("n")) {
          mode = "n";
          compress();
        }

        // Reset Mode / Compression
        else if (args[0].equals("-") && args[1].equals("r")) {
          mode = "r";
          compress();
        }

        // Monitor Mode / Compression
        else if (args[0].equals("-") && args[1].equals("m")) {
          mode = "m";
          compress();
        }

        // Expansion
        else if (args[0].equals("+")) {
          expand();
        }

        // Exception
        else {
          throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
