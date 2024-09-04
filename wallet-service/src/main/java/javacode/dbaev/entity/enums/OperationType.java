package javacode.dbaev.entity.enums;

public enum OperationType {
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW");
    private final String optype;

    OperationType(String optype) {
        this.optype = optype;
    }

    @Override
    public String toString() {
        return optype;
    }

    public boolean equals(String optype) {
        return this.toString().equals(optype);
    }
}
