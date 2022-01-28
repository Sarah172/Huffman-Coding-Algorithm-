package src;
import java.io.*;
import java.util.*;

/**
 * @author Sarah Morshed - B00881746
 * CSCI 2110
 * Assignment 4 - Huffman Coding Algorithm 
 * */

public class Huffman {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner((System.in));

        //prompt user to enter a line of text
        System.out.print("Enter a line (uppercase letters only): ");
        String textEntered = in.nextLine();

        //file that contains letters and their respective probabilities
        String filename = "C:\\Users\\sarah\\Documents\\Intellij\\csci2110\\Assignment 4\\src\\LettersProbability.txt";
        File lettersProbability = new File(filename);
        Scanner inputFile = new Scanner(lettersProbability);

        Pair pairObject;
        ArrayList<Pair> pairObjects = new ArrayList<Pair>();

        //read the file and split token using tab as delimiter
        while (inputFile.hasNext()){
            String readFile = inputFile.nextLine();
            StringTokenizer token = new StringTokenizer(readFile,"\t");

            String letter = token.nextToken();
            char letters = letter.charAt(0);

            String prob = token.nextToken();
            double probabilities = Double.parseDouble(prob);

            pairObject = new Pair(letters,probabilities);
            pairObjects.add(pairObject);
        }

        inputFile.close();

        ArrayList<BinaryTree<Pair>> S = new ArrayList<>(); //initializing queue S
        ArrayList<BinaryTree<Pair>> T = new ArrayList<>(); //initializing queue T

        //creates a new BinaryTree<Pair> for each pair object in the array pairObjects
        for (int i = 0; i < pairObjects.size(); i++) {
            BinaryTree<Pair> pairObjectBinaryTree = new BinaryTree<>();
            pairObjectBinaryTree.makeRoot(pairObjects.get(i));
            S.add(pairObjectBinaryTree);
        }

        while(!S.isEmpty()){
            BinaryTree<Pair> A = new BinaryTree<>();
            BinaryTree<Pair> B = new BinaryTree<>();

            //if queue T is empty then set the binary trees A and B to the first 2 elements in the queue S
            if (T.isEmpty()) {
                A = S.get(0);
                S.remove(0); //remove the first element from S after setting it to A
                B = S.get(0);
                S.remove(0);

            } else{
                /* if the probability of the first element in S is less that the probability of the first element in T
                 * then set A to the first element in S otherwise set A to the first element in T*/
                if (S.get(0).getData().getProb() < T.get(0).getData().getProb()) {
                    A = S.get(0);
                    S.remove(0);

                } else {
                    A = T.get(0);
                    T.remove(0);
                }

                /* if T is empty or the probability of the first element in S is less that the probability of the first
                 * element in T then set B to the first element in S otherwise set B to the first element in T*/
                if ((T.isEmpty()) || (S.get(0).getData().getProb() < T.get(0).getData().getProb())) {
                    B = S.get(0);
                    S.remove(0);

                } else {
                    B = T.get(0);
                    T.remove(0);
                }
            }

            /* connects the binary trees A & B (subtrees) to a new binary tree (root) of type Pair having value = 0
             * and probability = sum of the probabilities of the 2 trees, then adds the resulting binary
             * tree to the queue T */
            BinaryTree<Pair> P = new BinaryTree<>();
            Pair theRoot = new Pair('0', (A.getData().getProb() + B.getData().getProb()));
            P.makeRoot(theRoot);
            P.attachLeft(A);
            P.attachRight(B);
            T.add(P);
        }

        BinaryTree<Pair> huffmanTree = new BinaryTree<>();

        //keeps connecting two binary trees from the queue T at a time until T's size is 1
        while(T.size() > 1){
            BinaryTree<Pair> A = new BinaryTree<>();
            BinaryTree<Pair> B = new BinaryTree<>();

            A = T.get(0);
            T.remove(0);

            B = T.get(0);
            T.remove(0);

            BinaryTree<Pair> P = new BinaryTree<>();
            Pair theRoot = new Pair('0', (A.getData().getProb() + B.getData().getProb()));
            P.makeRoot(theRoot);
            P.attachLeft(A);
            P.attachRight(B);
            T.add(P);

        }

        huffmanTree = T.get(0); //sets huffmanTree to the one tree remaining in queue T

        //uses the findEncoding method to generate the huffman codes and stores them in an array
        String[] huffmanCodes = findEncoding(huffmanTree);

        //array containing the english alphabet, which will be used to decode text after it has been encoded
        char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String encodedText = "";

        //generates the encoded string of the user input
        for (int i = 0; i < textEntered.length(); i++) {
            if(textEntered.charAt(i) != ' '){
                //finds the huffman code that corresponds to each letter of the user input
                encodedText += huffmanCodes[((int)textEntered.charAt(i) - 65)];
            }else{
                encodedText+= " ";
            }
        }

        System.out.println("Here's the encoded text: " + encodedText);

        System.out.println("The decoded line is: " + decode(encodedText, huffmanCodes, alphabet));


    }


    private static String[] findEncoding(BinaryTree<Pair> bt){
        String[] result = new String[26];
        findEncoding(bt, result, "");
        return result;
    }

    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix){
        // test is node/tree is a leaf
        if (bt.getLeft()==null && bt.getRight()==null){
            a[bt.getData().getValue() - 65] = prefix;
        }
        // recursive calls
        else{
            findEncoding(bt.getLeft(), a, prefix+"0");
            findEncoding(bt.getRight(), a, prefix+"1");
        }
    }

    /**
     * uses the array containing the uppercase alphabet and the array containing the huffman codes for uppercase
     * letters to decode a string of huffman encoded text
     * @param encodedText String of huffman encoded text
     * @param huffmanCodes array containing the huffman codes for each uppercase letter in the alphabet
     * @param alphabet array containing the uppercase alphabet
     * @return the string of huffman encoded text after it has been decoded to normal words
     */
    private static String decode(String encodedText, String[] huffmanCodes, char[] alphabet){
        String result = "";
        String[] words = encodedText.split(" "); //splits the encoded line of text into an array of encoded words
        String letterCode = "";

        for(String word: words){
            for (int i = 0; i < word.length(); i++) {
                letterCode += word.charAt(i);
                for (int j = 0; j < huffmanCodes.length; j++) {
                    if(letterCode.equals(huffmanCodes[j])){
                        result += alphabet[j];
                        letterCode = "";
                    }
                }
            }

            result += " ";
        }
        return result;
    }
}
