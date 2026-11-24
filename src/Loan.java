public class Loan {
    private final Borrowable item;
    private final Member member;
    private final int borrowDay;
    private final int dueDay;

    public Loan(Borrowable item, Member member, int borrowDay){
        this.item = item;
        this.member = member;
        this.borrowDay = borrowDay;
        this.dueDay = borrowDay+member.getLoanPeriod();
    }

    public Borrowable getItem(){return item;}
    public Member getMember(){return member;}
    public int getBorrowDay(){return borrowDay;}
    public int getDueDay(){return dueDay;}


    public int daysLate(int today){
        return Math.max(0,today-dueDay);
    }

    public boolean isOverdue(int today){
        return today>dueDay;
    }

    public int fine(int today){
        return daysLate(today)*item.getFinePerDay();
    }

    public String getItemId(){return ((LibraryItem)item).getId();}
}
