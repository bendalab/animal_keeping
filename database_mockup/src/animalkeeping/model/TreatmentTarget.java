package animalkeeping.model;

/**
 * Created by jan on 28.03.17.
 */

public enum TreatmentTarget {
    subject {
        public String toString() {
            return "subject";
        }
    },
    housing {
        public String toString() {
            return "housing unit";
        }
    }
}