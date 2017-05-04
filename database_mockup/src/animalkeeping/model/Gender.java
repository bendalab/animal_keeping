package animalkeeping.model;

/**
 * Created by jan on 28.03.17.
 */

public enum Gender {
    female {
        public String toString() {
            return "female";
        }
    },
    male {
        public String toString() {
            return "male";
        }
    },
    hermaphrodite {
        public String toString() {
            return "hermaphrodite";
        }
    },
    unknown {
        public String toString() {
            return "unknown";
        }
    }
}