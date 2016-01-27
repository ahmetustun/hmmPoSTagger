package core;

import java.util.ArrayList;

/**
 * Created by ahmetu on 27.01.2016.
 */
public class Scorer {

    ArrayList<String> originalTagList;
    ArrayList<String> generatedTagList;

    float score;

    public Scorer(ArrayList<String> originalTagList, ArrayList<String> generatedTagList){
        this.generatedTagList = generatedTagList;
        this.originalTagList = originalTagList;
    }

    public void getScore(){
        int correct = 0;
        int total = originalTagList.size();

        if (total != generatedTagList.size()){
            System.out.println("BIG PROBLEM");
            System.exit(1);
        }

        for (int i=0; i<total; i++){
            if (originalTagList.get(i).equals(generatedTagList.get(i))){
                correct++;
            }
        }

        score = (float)correct/total;
    }
}
