package tagger;

import core.Scorer;
import core.Smoother;
import core.Trainer;
import core.Viterbi;
import utils.LastTwo;
import utils.PartOfSpeech;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Tagger {

    public static void main(String[] args) {

        Trainer trainer = new Trainer(System.getProperty("user.dir")+"/datas/train_set.txt");
        trainer.analyse(3);

        HashMap<String, Integer> my_start_count = trainer.getStartCountMap();
        HashMap<String, Integer> my_POS_tag_count = trainer.getTagCountMap();
        HashMap<String, Integer> my_obs_count = trainer.getSuffixCountMap();
        HashMap<String, HashMap<String, Integer>> my_transmission_pair_count = trainer.getBigramTransmissionPairMap();
        HashMap<String, HashMap<String, Integer>> my_emission_pair_count = trainer.getEmissionPairMap();
        HashMap<String, Float> my_start_prob = trainer.getStartProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_transmission_prob = trainer.getBigramTransmissionProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_emission_prob = trainer.getEmissionProbabilitiesMap();

        HashMap<LastTwo<String, String>, Integer> my_bigramCountMap = trainer.getBigramCountMap();
        HashMap<LastTwo<String, String>, HashMap<String, Integer>> my_trigramTransmissionPairMap = trainer.getTrigramTransmissionPairMap();
        HashMap<LastTwo<String, String>, HashMap<String, Float>> my_trigramTransmissionProbabilityMap = trainer.getTrigramTransmissionProbabilityMap();

        Smoother smoother = new Smoother(System.getProperty("user.dir")+"/datas/test_set.txt",
                my_POS_tag_count, my_obs_count,
                my_emission_pair_count);

        smoother.addOne();

        ArrayList<String> my_unseen_suffix_list = smoother.getUnseenSuffixList();
        HashMap<String, Integer> my_suffix_count_map = smoother.getS_suffixCountMap();
        HashMap<String, HashMap<String, Integer>> my_s_emission_pair_count = smoother.getS_emissionPairMap();
        HashMap<String, HashMap<String, Float>> my_s_emission_prob = smoother.getS_emissionProbabilitiesMap();
        ArrayList<ArrayList<String>> my_unt_sentences_suffixes = smoother.getUnTaggedSuffixesList();
        ArrayList<ArrayList<String>> generated_sentences_Tags = new ArrayList<>();

        for (ArrayList<String> a : my_unt_sentences_suffixes){
            String[] obs = a.toArray(new String[0]);
            ArrayList<String> generatedTags = Viterbi.forwardViterbi(obs, PartOfSpeech.tag_list, my_start_prob, my_transmission_prob, my_s_emission_prob);
            generated_sentences_Tags.add(generatedTags);
        }

        Scorer scorer = new Scorer(System.getProperty("user.dir")+"/datas/tagged_test_set.txt", generated_sentences_Tags);
        float my_score = scorer.getScore();

        System.out.println("\n" + my_score);
        System.out.println("Tamamlandı");

    }

}
