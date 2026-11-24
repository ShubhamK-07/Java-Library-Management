public abstract class LibraryItem{
    private String id;
    private String title;

    protected LibraryItem(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getId(){return id;}
    public String getTitle(){return title;}

    public abstract String getType(); 
}