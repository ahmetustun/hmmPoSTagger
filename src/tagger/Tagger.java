package tagger;

import core.Scorer;
import core.Smoother;
import core.Trainer;
import core.Viterbi;
import utils.Bigram;
import utils.PartOfSpeech;
import utils.Trigram;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Tagger {

    public static void main(String[] args) {

        Trainer trainer = new Trainer(System.getProperty("user.dir")+"/datas/Train_Set_METU.txt");
        trainer.analyse(3);

        HashMap<String, Integer> my_start_count = trainer.getStartCountMap();
        HashMap<String, Integer> my_POS_tag_count = trainer.getTagCountMap();
        HashMap<String, Integer> my_obs_count = trainer.getSuffixCountMap();
        HashMap<String, HashMap<String, Integer>> my_transmission_pair_count = trainer.getBigramTransmissionPairMap();
        HashMap<String, HashMap<String, Integer>> my_emission_pair_count = trainer.getEmissionPairMap();
        HashMap<String, Float> my_start_prob = trainer.getStartProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_transmission_prob = trainer.getBigramTransmissionProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_emission_prob = trainer.getEmissionProbabilitiesMap();
        HashMap<Trigram<String, String, String>, Integer> my_trigram = trainer.getTrigramCountMap();

        HashMap<Bigram<String, String>, Integer> my_bigramCountMap = trainer.getBigramCountMap();
        HashMap<Bigram<String, String>, HashMap<String, Integer>> my_trigramTransmissionPairMap = trainer.getTrigramTransmissionPairMap();
        HashMap<Bigram<String, String>, HashMap<String, Float>> my_trigramTransmissionProbabilityMap = trainer.getTrigramTransmissionProbabilityMap();


        Smoother smoother = new Smoother(System.getProperty("user.dir")+"/datas/Test_Set_METU.txt", my_POS_tag_count, my_bigramCountMap, my_transmission_pair_count,
                my_obs_count, my_trigramTransmissionPairMap, my_trigram, my_emission_prob, my_emission_pair_count);

        smoother.addOne(3);

        ArrayList<String> my_unseen_suffix_list = smoother.getUnseenSuffixList();
        HashMap<String, Integer> my_suffix_count_map = smoother.getLaplace_suffixCountMap();
        HashMap<String, HashMap<String, Integer>> my_s_emission_pair_count = smoother.getLaplace_emissionPairMap();
        HashMap<String, HashMap<String, Float>> my_s_emission_prob = smoother.getLaplace_emissionProbabilitiesMap();
        HashMap<Bigram<String, String>, HashMap<String, Float>> my_s_trigramProbabilityMap = smoother.getLaplace_trigramTransmissionProbabilityMap();
        ArrayList<ArrayList<String>> my_unt_sentences_suffixes = smoother.getUnTaggedSuffixesList();
        ArrayList<ArrayList<String>> generated_sentences_Tags = new ArrayList<>();
        ArrayList<ArrayList<String>> generated_sentences_Tags_2 = new ArrayList<>();

        for (ArrayList<String> a : my_unt_sentences_suffixes){
            String[] obs = a.toArray(new String[0]);
            ArrayList<String> generatedTags = Viterbi.forwardViterbiForTrigrams(obs, PartOfSpeech.tag_list, my_start_prob, my_transmission_prob, my_s_trigramProbabilityMap,  my_s_emission_prob);
            generated_sentences_Tags.add(generatedTags);
        }

        Scorer scorer = new Scorer(System.getProperty("user.dir")+"/datas/Tagged_Test_Tet_METU.txt", generated_sentences_Tags);
        float my_score = scorer.getScore();

        System.out.println("\n" + my_score);
        System.out.println("Tamamlandı");


    }

}
