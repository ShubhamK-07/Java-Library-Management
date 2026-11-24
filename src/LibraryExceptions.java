class InvalidInputException extends LibraryException {
    InvalidInputException(String message){super(message);}
}

class ItemNotFoundException extends LibraryException{
    ItemNotFoundException(String id){super("item "+id+" not found");}
}

class ItemNotAvailableException extends LibraryException {
    ItemNotAvailableException(String message){super(message);}
}

class ItemNotOnLoanException extends LibraryException {
    ItemNotOnLoanException(String itemId){super(itemId+" is not on loan");}
}

class BorrowLimitExceededException extends LibraryException {
    BorrowLimitExceededException(String memberId, int max){super(memberId+" has reached the limit of "+max);}
}

class MemberNotFoundException extends LibraryException{
    MemberNotFoundException(String id){super("member "+id+" not found");}
}

class ReservationNotAllowedException extends LibraryException{
    ReservationNotAllowedException(String message){
        super(message);
    }
}



