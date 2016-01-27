package core;

import utils.Parse;

import java.util.ArrayList;

/**
 * Created by ahmetu on 27.01.2016.
 */
public class Scorer {

    private ArrayList<String> sentences = new ArrayList<>();
    ArrayList<ArrayList<String>> originalTagList = new ArrayList<>();
    ArrayList<ArrayList<String>> generatedTagList;

    float score;

    public Scorer(String fileName, ArrayList<ArrayList<String>> generatedTagList){
        this.generatedTagList = generatedTagList;
        Parse.parseTrainFile(fileName, sentences);
    }

    public void countOriginalTags(String unt_sentence) {

        String[] words = unt_sentence.split(Parse.boşluk_a);
        ArrayList<String> tList = new ArrayList<>();

        for (String s : words) {

            String[] root_suffixes = s.split(Parse.tag_a);
            String tag = root_suffixes[1];

            tList.add(tag);
        }
        originalTagList.add(tList);
    }

    public void countOriginalTagsForSentences(){
        for (String sentence : sentences){
            countOriginalTags(sentence);
        }
    }

    public float getScore(){

        countOriginalTagsForSentences();

        int correct = 0;
        int total = 0;
        int s_t = originalTagList.size();

        if (s_t != generatedTagList.size()){
            System.out.println("BIG PROBLEM_0");
            System.exit(1);
        }

        for (int i=0; i<s_t; i++){
            ArrayList<String> tList = originalTagList.get(i);
            ArrayList<String> gList = generatedTagList.get(i);
            if (tList.size() != gList.size()){
                System.out.println("BIG PROBLEM_" + i+1);
            }
            for (int j=0; j<tList.size(); j++){
                if (tList.get(j).equals(gList.get(j))){
                    correct++;
                    total++;
                } else {
                    total++;
                }
            }
        }

        return score = (float)correct/total;
    }
}
