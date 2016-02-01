package core;

/**
 * Created by ahmet on 22/01/16.
 */
import utils.LastTwo;
import utils.PartOfSpeech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

public class Viterbi
{

   public static ArrayList<String> forwardViterbiForBigrams(String[] obs, String[] states,
                                                            HashMap<String, Float> start_p,
                                                            HashMap<String, HashMap<String, Float>> trans_p,
                                                            HashMap<String, HashMap<String, Float>> emit_p) {
        ArrayList<String> statesList = new ArrayList<>();
        Hashtable<String, Object[]> T = new Hashtable<String, Object[]>();
        for (String state : states)
            T.put(state, new Object[] {start_p.get(state), state, start_p.get(state)});

        for (String output : obs)
        {
            Hashtable<String, Object[]> U = new Hashtable<String, Object[]>();
            for (String next_state : states)
            {
                float total = 0;
                String argmax = "";
                float valmax = 0;

                float prob = 1;
                String v_path = "";
                float v_prob = 1;

                for (String source_state : states)
                {
                    Object[] objs = T.get(source_state);
                    prob = ((Float) objs[0]).floatValue();
                    v_path = (String) objs[1];
                    v_prob = ((Float) objs[2]).floatValue();

                    float p = emit_p.get(source_state).get(output) *
                            trans_p.get(source_state).get(next_state);
                    prob *= p;
                    v_prob *= p;
                    total += prob;
                    if (v_prob > valmax)
                    {
                        argmax = v_path + "," + next_state;
                        valmax = v_prob;
                    }
                }
                U.put(next_state, new Object[] {total, argmax, valmax});
            }
            T = U;
        }

        float total = 0;
        String argmax = "";
        float valmax = 0;

        float prob;
        String v_path;
        float v_prob;

        for (String state : states)
        {
            Object[] objs = T.get(state);
            prob = ((Float) objs[0]).floatValue();
            v_path = (String) objs[1];
            v_prob = ((Float) objs[2]).floatValue();
            total += prob;
            if (v_prob > valmax)
            {
                argmax = v_path;
                valmax = v_prob;
            }
        }

        String[] sList = argmax.split(",");
        for (int i=0; i<sList.length-1; i++){
            statesList.add(sList[i]);
        }
        return statesList;
   }

    public static ArrayList<String> forwardViterbiForBigrams_Test(String[] obs, String[] states,
                                                             HashMap<String, Float> start_p,
                                                             HashMap<String, HashMap<String, Float>> trans_p,
                                                             HashMap<String, HashMap<String, Float>> emit_p){
        ArrayList<String> statesList = new ArrayList<>();

        String first = "";
        float max_p = 0f;
        for (String next : states){
            HashMap<String, Float> e_m = emit_p.get(next);
            float e = e_m.get(obs[0]);

            float p = start_p.get(next);

            float curr_p = e * p;

            if (curr_p > max_p){
                max_p = curr_p;
                first = next;
            }
        }

        statesList.add(first);

        String curr = first;
        String next_tag = "";
        for (int i=1; i<obs.length; i++){

            float max_prob = 0f;
            for (String next : states){
                HashMap<String, Float> e_m = emit_p.get(next);
                float e = e_m.get(obs[i]);

                HashMap<String, Float> t_m = trans_p.get(curr);
                float p = t_m.get(next);

                float curr_p = e * p;

                if (curr_p > max_p){
                    max_p = curr_p;
                    next_tag = next;
                }
            }

            statesList.add(next_tag);
            curr = next_tag;

        }

        return statesList;
    }

   public static ArrayList<String> forwardViterbiForTrigrams(String[] obs, String[] states,
                                                             HashMap<String, Float> start_p,
                                                             HashMap<String, HashMap<String, Float>> bigram_trans_p, HashMap<LastTwo<String, String>, HashMap<String, Float>> trigram_trans_p,
                                                             HashMap<String, HashMap<String, Float>> emit_p) {
       ArrayList<String> statesList = new ArrayList<>();

       String[] firstTwo = new String[]{obs[0], obs[1]};
       ArrayList<String> firstTwoTags = forwardViterbiForBigrams(firstTwo, states, start_p, bigram_trans_p, emit_p);

       for (String s : firstTwoTags){
           statesList.add(s);
       }

       String first = firstTwoTags.get(0);
       String second = firstTwoTags.get(1);

       for (int i=2; i<obs.length; i++){

           LastTwo<String, String> lastTwo = new LastTwo<>(first, second);

           float max_p = 0f;
           String next_tag = "";
           for (String next : states){
               HashMap<String, Float> e_m = emit_p.get(next);
               float e = e_m.get(obs[i]);

               HashMap<String, Float> t_m = trigram_trans_p.get(lastTwo);
               float t = t_m.get(next);

               float curr_p = e * t;

               if (curr_p > max_p){
                   max_p = curr_p;
                   next_tag = next;
               }
           }

           statesList.add(next_tag);

           first = second;
           second = next_tag;

       }

       return statesList;
   }

}