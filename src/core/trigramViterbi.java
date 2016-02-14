package core;

import utils.Bigram;
import utils.PartOfSpeech;
import utils.TrigramViterbiKey;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

/**
 * Created by ahmet on 12/02/16.
 */
public class TrigramViterbi {



    public static ArrayList<String> forwardViterbi(String[] obs, String[] states,
                                                   HashMap<String, Double> start_p,
                                                   HashMap<String, HashMap<String, Double>> bigram_trans_p, HashMap<Bigram<String, String>, HashMap<String, Double>> trigram_trans_p,
                                                   HashMap<String, HashMap<String, Double>> emit_p) throws FileNotFoundException, UnsupportedEncodingException {

        HashMap<Integer, String> stateList = new HashMap<>();
        //PrintWriter writer = new PrintWriter(System.getProperty("user.dir")+"/datas/metu/output", "UTF-8");

        HashMap<TrigramViterbiKey<Integer, String, String>, Double> piMap = new HashMap<>();
        HashMap<TrigramViterbiKey<Integer, String, String>, String> bpMap = new HashMap<>();

        if (obs.length < 4){
            return BigramViterbi.forwardViterbi(obs, states, start_p, bigram_trans_p, emit_p);
        }

        for (int k=0; k<obs.length-1; k++){

            if (k == 0) {
                for (String start : states) {
                    TrigramViterbiKey<Integer, String, String> key = new TrigramViterbiKey<>(k, "*", start);
                    double curr_pi = start_p.get(start) * emit_p.get(start).get(obs[k]);
                    //writer.println("pi("+k+",*,"+start+"): " + curr_pi);

                    if (start_p.get(start) > 1){
                        System.out.println();
                    }

                    if (emit_p.get(start).get(obs[k]) > 1){
                        System.out.println();
                    }

                    piMap.put(key, curr_pi);
                }
            } else if (k == 1) {
                for (String second_state : states) {
                    for (String start_state : states){

                        TrigramViterbiKey<Integer, String, String> prev_key = new TrigramViterbiKey<>(k-1, "*", start_state);
                        double curr_pi = piMap.get(prev_key) * bigram_trans_p.get(start_state).get(second_state) * emit_p.get(second_state).get(obs[k]);
                        //writer.println("pi("+k+"," +start_state+","+second_state+"): " + curr_pi);

                        if (piMap.get(prev_key) > 1){
                            System.out.println();
                        }
                        if (bigram_trans_p.get(start_state).get(second_state) > 1){
                            System.out.println();
                        }

                        if (emit_p.get(second_state).get(obs[k]) > 1) {
                            System.out.println();
                        }

                        TrigramViterbiKey<Integer, String, String> curr_key = new TrigramViterbiKey<>(k, start_state, second_state);
                        piMap.put(curr_key, curr_pi);
                    }
                }
            } else {
                for (String t3 : states){
                    for (String t2 : states){

                        double max_pi = 0.0;
                        String argmax = "";

                        for (String t1 : states){

                            Bigram<String, String> curr = new Bigram<>(t1, t2);
                            TrigramViterbiKey<Integer, String, String> prev_key = new TrigramViterbiKey<>(k-1, t1, t2);
                            double curr_pi = piMap.get(prev_key) * trigram_trans_p.get(curr).get(t3) * emit_p.get(t3).get(obs[k]);
                            //writer.println("........ pi("+k+"," +t2+","+t3+"): " + curr_pi + " for bp: " + t1);

                            if (piMap.get(prev_key) > 1){
                                System.out.println();
                            }

                            if (trigram_trans_p.get(curr).get(t3) > 1) {
                                System.out.println();
                            }

                            if (emit_p.get(t3).get(obs[k]) > 1){
                                System.out.println();
                            }

                            if (curr_pi > max_pi) {
                                max_pi = curr_pi;
                                argmax = t1;
                            }
                        }

                        TrigramViterbiKey<Integer, String, String> curr_key = new TrigramViterbiKey<>(k, t2, t3);
                        //writer.println("pi("+k+"," +t2+","+t3+"): " + max_pi + " with bp: " + argmax);
                        piMap.put(curr_key, max_pi);
                        bpMap.put(curr_key, argmax);
                    }
                }
            }
        }

        double last_max = 0.0;
        String last_s = "";
        String prev_last_s = "";
        //writer.println("============ LAST BIGRAM ===============");
        for (String prev_last : states){
            for (String last : states){

                Bigram<String, String> bigram = new Bigram<>(prev_last, last);
                TrigramViterbiKey<Integer, String, String> key = new TrigramViterbiKey<>(obs.length-2, prev_last, last);
                double last_pi = piMap.get(key) * trigram_trans_p.get(bigram).get(PartOfSpeech.PUNC_s);
                //writer.println("pi("+(obs.length-2)+"," +prev_last+","+last+"): " + last_pi);
                if (last_pi > last_max){
                    last_max = last_pi;
                    last_s = last;
                    prev_last_s = prev_last;
                }
            }
        }

        stateList.put(obs.length-1, PartOfSpeech.PUNC_s);
        stateList.put(obs.length-2, last_s);
        stateList.put(obs.length-3, prev_last_s);

        for (int k=obs.length-3; k>0; k--){
            TrigramViterbiKey<Integer, String, String> key = new TrigramViterbiKey<>(k+1, stateList.get(k), stateList.get(k+1));
            stateList.put(k-1, bpMap.get(key));
        }

        ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(stateList.values());

        //writer.close();

        return returnList;
    }

}
