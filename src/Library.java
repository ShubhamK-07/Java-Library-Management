import  java.util.*;

public final class Library {
    private final Map<String,LibraryItem> items = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Loan> loans = new HashMap<>();
    private int currentDay;
    private final Map<String, Queue<Member>> reservations = new HashMap<>();


    public void addItem(LibraryItem item){
        items.put(item.getId(),item);
    }

    public void addMember(Member member){
        members.put(member.getId(),member);
    }

    public int advance(int days) throws InvalidInputException{
        if(days<0){throw new InvalidInputException("days must be non-negative");}

        currentDay+=days;
        return currentDay;
    }

    public List<LibraryItem> search(String text){
        String p = text.toLowerCase();
        List<LibraryItem> f  = new ArrayList<>();
        for(LibraryItem i : items.values()){
            if(i.getTitle().toLowerCase().contains(p)){f.add(i);}
        }

        f.sort(Comparator.comparing(LibraryItem::getTitle).thenComparing(LibraryItem::getId));

        return f;
    }

    public List<Loan> loansOf(String memberId) throws MemberNotFoundException{
        if(!members.containsKey(memberId)){throw new MemberNotFoundException(memberId);}
        List<Loan> result = new ArrayList<>();

        for(Loan l : loans.values()){
            if(l.getMember().getId().equals(memberId)){
                result.add(l);
            }
        }

        return result;
    } 

    public List<Loan> overdue(){
        List<Loan> result = new ArrayList<>();

        for(Loan l: loans.values()){
                if(l.isOverdue(currentDay)){
                    result.add(l);
                }
        }

        return result;
    }

    private boolean isBorrowable(LibraryItem item){
        return item instanceof Borrowable;
    }

    public String statusOf(LibraryItem item){
        if(!isBorrowable(item)){return "NOT_BORROWABLE";}

        Loan l = loans.get(item.getId());

        if(l==null){return "AVAILABLE";}
        return "ON_LOAN| "+l.getMember().getId();
    }

    public List<LibraryItem> getItems(){
        return new ArrayList<>(items.values());
    }

    public int getCurrentDay(){return currentDay;}

    public Loan findActiveLoan(String itemId){
        return loans.get(itemId);
    }

    public void reserve(String memberId, String itemId)throws LibraryException{
        Member mem = members.get(memberId);
        if(mem==null){
            throw new MemberNotFoundException(memberId);
        }

        LibraryItem item = items.get(itemId);

        if(item==null){throw new ItemNotFoundException(itemId);}

        if(!isBorrowable(item)){
            throw new ItemNotAvailableException(itemId+" cannot be borrowed");
        }

        if(!loans.containsKey(itemId)){
            throw new ReservationNotAllowedException(itemId+" is available");
        }

        Loan curr_l = loans.get(itemId);
        if(curr_l.getMember().getId().equals(memberId)){
            throw new ReservationNotAllowedException(memberId+" already has "+itemId);
        }

        Queue<Member> line = reservations.get(itemId);

        if(line==null){
            line = new LinkedList<>();
            reservations.put(itemId,line);
        }

        for(Member in_line: line){
            if(in_line.getId().equals(memberId)){
                throw new ReservationNotAllowedException(memberId+" already reserved "+itemId);
            }
        }

        line.add(mem);

    }

    public Loan borrow(String memberId, String itemId) throws LibraryException{
        Member mem = members.get(memberId);
        if(mem==null){
            throw new MemberNotFoundException(memberId);
        }

        LibraryItem item = items.get(itemId);

        if(item==null){throw new ItemNotFoundException(itemId);}

        if(!isBorrowable(item)){
            throw new ItemNotAvailableException(itemId+" cannot be borrowed");
        }

        if(loans.containsKey(itemId)){
            throw new ItemNotAvailableException(itemId+" is on loan");
        }

        int lc =0;
        for(Loan al: loans.values()){
            if(al.getMember().getId().equals(memberId)){
                lc++;
            }
        }

        if(mem.hasReachedLimit(lc)){
            throw new BorrowLimitExceededException(memberId, mem.getMaxItems());
        }

        Loan l = new Loan((Borrowable)item, mem, currentDay);
        loans.put(itemId,l);
        return l;

    }

    public int returnItem(String itemId) throws LibraryException{
        Loan l = loans.remove(itemId);
        if(l==null){
            if(!loans.containsKey(itemId)){
                throw new ItemNotFoundException(itemId);
            }

            throw new ItemNotOnLoanException(itemId);
        }

        int fine = l.fine(currentDay);

        Queue<Member> line = reservations.get(itemId);

        if(line!=null){
            Iterator<Member> curr = line.iterator();

            while(curr.hasNext()){
                Member mem = curr.next();
                int lc =0;
                for(Loan al: loans.values()){
                    if(al.getMember().getId().equals(mem.getId())){
                        lc++;
                    }
                }

                if(!mem.hasReachedLimit(lc)){
                    curr.remove();
                    loans.put(itemId,new Loan((Borrowable)items.get(itemId),mem,currentDay));
                    break;
                }
                }
                if(line.isEmpty()){reservations.remove(itemId);}
        }
        return fine;

    }





}
