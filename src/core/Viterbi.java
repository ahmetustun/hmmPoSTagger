package core;

/**
 * Created by ahmet on 22/01/16.
 */
import utils.Bigram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Viterbi
{
     static final String H = "H";
        static final String L = "L";

        static final String A = "A";
        static final String C = "C";
        static final String G = "G";
        static final String T = "T";

    public static void main(String[] args)
    {
        String[] states = new String[] {H, L};

        String[] observations = new String[] {G, G, C, A, C, T, G, A, A};

        HashMap<String, Double> start_probability = new HashMap<String, Double>();
        start_probability.put(H, 0.5d);
        start_probability.put(L, 0.5d);

        // transition_probability
        HashMap<String, HashMap<String, Double>> transition_probability =
                new HashMap<String, HashMap<String, Double>>();
        HashMap<String, Double> t1 = new HashMap<String, Double>();
        t1.put(H, 0.5d);
        t1.put(L, 0.5d);
        HashMap<String, Double> t2 = new HashMap<String, Double>();
        t2.put(H, 0.4d);
        t2.put(L, 0.6d);
        transition_probability.put(H, t1);
        transition_probability.put(L, t2);

        // emission_probability
        HashMap<String, HashMap<String, Double>> emission_probability =
                new HashMap<String, HashMap<String, Double>>();
        HashMap<String, Double> e1 = new HashMap<String, Double>();
        e1.put(A, 0.2d);
        e1.put(C, 0.3d);
        e1.put(G, 0.3d);
        e1.put(T, 0.2d);
        HashMap<String, Double> e2 = new HashMap<String, Double>();
        e2.put(A, 0.3d);
        e2.put(C, 0.2d);
        e2.put(G, 0.2d);
        e2.put(T, 0.3d);
        emission_probability.put(H, e1);
        emission_probability.put(L, e2);

        ArrayList<String> a = forwardViterbiForBigrams(observations,
                states,
                start_probability,
                transition_probability,
                emission_probability);
        System.out.println("");
    }


   public static ArrayList<String> forwardViterbiForBigrams(String[] obs, String[] states,
                                                            HashMap<String, Double> start_p,
                                                            HashMap<String, HashMap<String, Double>> trans_p,
                                                            HashMap<String, HashMap<String, Double>> emit_p) {
        ArrayList<String> statesList = new ArrayList<>();
        HashMap<String, Object[]> T = new HashMap<String, Object[]>();
        for (String state : states)
            T.put(state, new Object[] {start_p.get(state), state, start_p.get(state)});

        for (String output : obs)
        {
            HashMap<String, Object[]> U = new HashMap<String, Object[]>();
            for (String next_state : states)
            {
                double total = 0;
                String argmax = "";
                double valmax = 0;

                double prob = 1;
                String v_path = "";
                double v_prob = 1;

                for (String source_state : states)
                {
                    Object[] objs = T.get(source_state);
                    prob = ((double) objs[0]);
                    v_path = (String) objs[1];
                    v_prob = ((double) objs[2]);

                    double p = emit_p.get(source_state).get(output) *
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

        double total = 0;
        String argmax = "";
        double valmax = 0;

        double prob;
        String v_path;
        double v_prob;

        for (String state : states)
        {
            Object[] objs = T.get(state);
            prob = ((double) objs[0]);
            v_path = (String) objs[1];
            v_prob = ((double) objs[2]);
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

/*
    public static ArrayList<String> forwardViterbiForTrigrams_Test(String[] obs, String[] states,
                                                              HashMap<String, double> start_p,
                                                              HashMap<String, HashMap<String, double>> bigram_trans_p, HashMap<Bigram<String, String>, HashMap<String, double>> trigram_trans_p,
                                                              HashMap<String, HashMap<String, double>> emit_p) {

        ArrayList<String> statesList = new ArrayList<>();
        String[] firstTwo = new String[]{obs[0], obs[1]};
        ArrayList<String> firstTwoTags = forwardViterbiForBigrams(firstTwo, states, start_p, bigram_trans_p, emit_p);

        for (String s : firstTwoTags){
            statesList.add(s);
        }

        String first = firstTwoTags.get(0);
        String second = firstTwoTags.get(1);

        Bigram<String, String> startingTwo = new Bigram(first, second);

        HashMap<String, Object[]> T = new HashMap<>();


    }
*/

    public static ArrayList<String> forwardViterbiForBigrams_(String[] obs, String[] states,
                                                             HashMap<String, Double> start_p,
                                                             HashMap<String, HashMap<String, Double>> trans_p,
                                                             HashMap<String, HashMap<String, Double>> emit_p){
        ArrayList<String> statesList = new ArrayList<>();

        String first = "";
        double max_p = 0f;
        for (String next : states){
            HashMap<String, Double> e_m = emit_p.get(next);
            double e = e_m.get(obs[0]);

            double p = start_p.get(next);

            double curr_p = e * p;

            if (curr_p > max_p){
                max_p = curr_p;
                first = next;
            }
        }

        statesList.add(first);

        String curr = first;
        String next_tag = "";
        for (int i=1; i<obs.length; i++){

            double max_prob = 0f;
            for (String next : states){
                HashMap<String, Double> e_m = emit_p.get(next);
                double e = e_m.get(obs[i]);

                HashMap<String, Double> t_m = trans_p.get(curr);
                 double p = t_m.get(next);

                double curr_p = e * p;

                if (curr_p > max_prob){
                    max_prob = curr_p;
                    next_tag = next;
                }
            }

            statesList.add(next_tag);
            curr = next_tag;

        }

        return statesList;
    }

   public static ArrayList<String> forwardViterbiForTrigrams(String[] obs, String[] states,
                                                             HashMap<String, Double> start_p,
                                                             HashMap<String, HashMap<String, Double>> bigram_trans_p, HashMap<Bigram<String, String>, HashMap<String, Double>> trigram_trans_p,
                                                             HashMap<String, HashMap<String, Double>> emit_p) {
       ArrayList<String> statesList = new ArrayList<>();

       String[] firstTwo = new String[]{obs[0], obs[1]};
       ArrayList<String> firstTwoTags = forwardViterbiForBigrams(firstTwo, states, start_p, bigram_trans_p, emit_p);

       for (String s : firstTwoTags){
           statesList.add(s);
       }

       String first = firstTwoTags.get(0);
       String second = firstTwoTags.get(1);

       for (int i=2; i<obs.length; i++){

           Bigram<String, String> bigram = new Bigram<>(first, second);

           double max_p = 0f;
           String next_tag = "";
           for (String next : states){
               HashMap<String, Double> e_m = emit_p.get(next);
               double e = e_m.get(obs[i]);

               HashMap<String, Double> t_m = trigram_trans_p.get(bigram);
               double t = t_m.get(next);

               double curr_p = e * t;

               if (curr_p == max_p){
                   System.out.println("ERROR");
               } else if (curr_p > max_p){
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

    private static double log2( double x )
    {
        // Math.log is base e, natural log, ln

        return (double) (Math.log( x ) / Math.log( 2 ));
    }

}