public abstract class Member {
    private final String id;
    private final String name;

    protected Member(String id, String name){
        this.id = id;
        this.name=name;
    }

    public String getId(){return id;}
    public String getName(){return name;}

    public abstract int getMaxItems();

    public abstract int getLoanPeriod();

    public boolean hasReachedLimit(int loanCount){
        return loanCount >= getMaxItems();
    }


}
