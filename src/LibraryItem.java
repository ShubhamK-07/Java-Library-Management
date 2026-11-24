public abstract class LibraryItem{
    private String id;
    private String title;

    public String getId(){return id;}
    public String getTitle(){return title;}

    public abstract String getType(); 
}