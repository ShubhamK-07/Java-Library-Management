import java.util.*;

public final class Main {

    static void requireFields(String[] line,int n) throws InvalidInputException{
        if(line.length!=n){
            throw new InvalidInputException("error");
        }
    }

    static int parseDays(String s) throws InvalidInputException{
        try{
            return Integer.parseInt(s);
        }
        catch(NumberFormatException e){
            throw new InvalidInputException("invalid days "+s);
        }
    }


    static Member createMember(String id,String name, String type) throws InvalidInputException{
        if(type.equals("STUDENT")){
            return new Student(id,name);
        }
        else if(type.equals("STAFF")){
            return new Staff(id, name);
        }
        else if(type.equals("FACULTY")){
            return new Faculty(id, name);
        }
        else{
            throw new InvalidInputException("unknown type "+type);
        }
    }

    static List<String> processCommand(Library library, String line) throws LibraryException{
        String p[] = line.split("\\|",-1);
        String cmd = p[0];
        List<String> result = new ArrayList<>();

        switch(cmd){
            case "ADD_BOOK":{
                requireFields(p, 4);
                library.addItem(new Book(p[1], p[2], p[3]));
                result.add("OK");
                break;
            }

            case "ADD_MAGAZINE":{
                requireFields(p, 4);
                library.addItem(new Magazine(p[1],p[2],p[3]));
                result.add("OK");
                break;
            }

            case "ADD_REFERENCE":{
                requireFields(p, 4);
                library.addItem(new ReferenceBook(p[1],p[2],p[3]));
                result.add("OK");
                break;
            }

            case "ADD_MEMBER":{
                requireFields(p, 4);
                library.addMember(createMember(p[1],p[2],p[3]));
                result.add("OK");
                break;
            }

            case "BORROW":{
                requireFields(p, 3);
                Loan loan = library.borrow(p[1], p[2]);
                result.add("OK due on day "+loan.getDueDay());
                break;
            }

            case "RETURN":{
                requireFields(p, 2);
                int fine = library.returnItem(p[1]);
                Loan loan = library.findActiveLoan(p[1]);
                if(loan!=null){
                    result.add("OK fine "+fine+", issued to "+loan.getMember().getId()+" due on day "+loan.getDueDay());
                }
                else{
                    result.add("OK fine "+fine);
                }
                break;
            }

            case "ADVANCE":{
                requireFields(p, 2);
                result.add("OK today is day "+library.advance(parseDays(p[1])));
                break;
            }

            case "RESERVE":{
                requireFields(p, 3);
                library.reserve(p[1], p[2]);
                result.add("OK reserved");
                break;
            }

            case "SEARCH":{
                requireFields(p, 2);
                result.addAll(library.search(p[1]).stream().map(i -> i.getId()+"|"+i.getType()+"|"+i.getTitle()).toList());
                break;
            }

            case "LOANS":{
                requireFields(p, 2);
                result.addAll(library.loansOf(p[1]).stream().sorted(Comparator.comparing(Loan::getDueDay).thenComparing(Loan::getItemId))
                .map(b -> b.getItemId()+"|"+((LibraryItem)b.getItem()).getTitle()+"|"+b.getDueDay()).toList());
                break;

            }

            case "OVERDUE":{
                requireFields(p, 1);
                int today = library.getCurrentDay();
                result.addAll(library.overdue().stream().sorted(Comparator.comparing((Loan l) -> l.daysLate(today)).reversed().thenComparing(Loan::getItemId))
                    .map(l -> l.getItemId()+"|"+l.getMember().getId()+"|"+l.daysLate(today)+"|"+l.fine(today)).toList());
                break;
            }

            case "INVENTORY":{
                requireFields(p, 1);
                result.addAll(library.getItems().stream().sorted(Comparator.comparing(LibraryItem::getId))
                    .map(e -> e.getId()+"|"+e.getType()+"|"+library.statusOf(e)).toList());
                break;
            }
            default:
                throw new InvalidInputException("unknown command "+cmd);
        }

        if(result.isEmpty()){
            result.add("NONE");
        }

        return result;
    }

    public static void main(String[] args){
        Library library = new Library();
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            try{
                List<String> disp = processCommand(library, line);
                for(String s: disp){
                    System.out.println(s);
                }
            }
            catch(LibraryException e){
                System.out.println("ERROR "+e.getMessage());
            }
        }
    }
}
