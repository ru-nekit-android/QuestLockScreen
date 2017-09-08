package ru.nekit.android.qls.quest.math;

public enum MathematicalOperation {

    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION;

    public static int getLength() {
        return MathematicalOperation.values().length;
    }

    public static MathematicalOperation fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= getLength()) {
            throw new IndexOutOfBoundsException();
        }
        return MathematicalOperation.values()[ordinal];
    }

    public static MathematicalOperation fromName(String value) {
        MathematicalOperation[] values = MathematicalOperation.values();
        for (MathematicalOperation operation : values) {
            if (operation.toString().equals(value)) {
                return operation;
            }
        }
        return null;
    }

    public String toString() {
        String symbol = null;
        switch (this) {
            case ADDITION:
                symbol = "+";
                break;
            case SUBTRACTION:
                symbol = "−";
                break;
            case MULTIPLICATION:
                symbol = "×";
                break;
            case DIVISION:
                symbol = ":";
                break;
        }
        return symbol;
    }
}