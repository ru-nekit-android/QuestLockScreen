package ru.nekit.android.qls.quest.math;

public enum MathematicalSignComparison {


    LESS,

    EQUAL,

    GREATER;

    public static MathematicalSignComparison fromName(String name) {
        MathematicalSignComparison[] values = MathematicalSignComparison.values();
        for (MathematicalSignComparison sign : values) {
            if (sign.name().equals(name)) {
                return sign;
            }
        }
        return null;
    }

    public String toString() {
        String symbol = null;
        switch (this) {
            case EQUAL:
                symbol = "=";
                break;
            case LESS:
                symbol = "<";
                break;
            case GREATER:
                symbol = ">";
                break;
        }
        return symbol;
    }
}
