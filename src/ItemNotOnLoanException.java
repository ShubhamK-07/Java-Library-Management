public class ItemNotOnLoanException extends LibraryException {
    ItemNotOnLoanException(String itemId){super(itemId+" is not on loan");}
}

