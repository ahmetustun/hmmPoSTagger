package core;

/**
 * Created by ahmet on 22/01/16.
 */
import utils.Bigram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BigramViterbi
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

        ArrayList<String> a = forwardViterbi(observations,
                states,
                start_probability,
                transition_probability,
                emission_probability);
        System.out.println("");
    }


   public static ArrayList<String> forwardViterbi(String[] obs, String[] states,
                                                            HashMap<String, Double> start_p,
                                                            HashMap<String, HashMap<String, Double>> trans_p,
                                                            HashMap<String, HashMap<String, Double>> emit_p) {
        ArrayList<String> statesList = new ArrayList<>();

       HashMap<String, Object[]> T = new HashMap<String, Object[]>();
        for (String state : states)
            T.put(state, new Object[] {/*start_p.get(state), */state, start_p.get(state)});

        for (String output : obs)
        {
            HashMap<String, Object[]> U = new HashMap<String, Object[]>();
            for (String next_state : states)
            {
                //double total = 0;
                String argmax = "";
                double valmax = 0;

                double prob = 1;
                String v_path = "";
                double v_prob = 1;

                for (String source_state : states)
                {
                    Object[] objs = T.get(source_state);
                    //prob = ((double) objs[0]);
                    v_path = (String) objs[0];
                    v_prob = ((double) objs[1]);

                    double p = emit_p.get(source_state).get(output) *
                            trans_p.get(source_state).get(next_state);
                    prob *= p;
                    v_prob *= p;
                    //total += prob;
                    if (v_prob > valmax)
                    {
                       argmax = v_path + "," + next_state;
                        valmax = v_prob;
                    }
                }
                U.put(next_state, new Object[] {/*total, */argmax, valmax});
            }
            T = U;
        }

        //double total = 0;
        String argmax = "";
        double valmax = 0;

        //double prob;
        String v_path;
        double v_prob;

        for (String state : states)
        {
            Object[] objs = T.get(state);
            //prob = ((double) objs[0]);
            v_path = (String) objs[0];
            v_prob = ((double) objs[1]);
            //total += prob;
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
}