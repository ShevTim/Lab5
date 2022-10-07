/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MovieCollection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.PriorityQueue;
import java.util.regex.Pattern;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;
import java.lang.IllegalArgumentException;
import java.io.*;
import java.time.LocalDate;
import java.text.ParsePosition;
/**
 *
 * @author Администратор
 */
public class MovieColl {
    
public static void SaveInt(BufferedOutputStream bos, int value) throws IOException
{
    ByteArrayOutputStream bas = new ByteArrayOutputStream();
    DataOutputStream ds = new DataOutputStream(bas);
    ds.writeInt(value);byte[] value_bytes = bas.toByteArray();
    bos.write(value_bytes,0,value_bytes.length);      
}
public static void SaveString(BufferedOutputStream bos, String value) throws IOException
{
        byte[] name_bytes=value.getBytes();
        bos.write(name_bytes.length);
        bos.write(name_bytes,0,name_bytes.length);
}
public static void SaveLong(BufferedOutputStream bos, long value) throws IOException
{
    ByteArrayOutputStream bas = new ByteArrayOutputStream();
    DataOutputStream ds = new DataOutputStream(bas);
    ds.writeLong(value);byte[] value_bytes = bas.toByteArray();
    bos.write(value_bytes,0,value_bytes.length);    
}
public static void SaveFloat(BufferedOutputStream bos, float value) throws IOException
{
    ByteArrayOutputStream bas = new ByteArrayOutputStream();
    DataOutputStream ds = new DataOutputStream(bas);
    ds.writeFloat(value);byte[] value_bytes = bas.toByteArray();
    bos.write(value_bytes,0,value_bytes.length);        
}
public static void SaveDate(BufferedOutputStream bos, LocalDateTime value) throws IOException
{
    int dd=value.getDayOfMonth(), mm=value.getMonthValue();
    bos.write(dd);
    bos.write(mm);
    SaveInt(bos, value.getYear());     
}

public static int ReadInt(FileInputStream in) throws IOException
{
    byte buf[]=new byte[4];
    in.read(buf, 0, 4);
    ByteArrayInputStream bas = new ByteArrayInputStream(buf);
    DataInputStream ds = new DataInputStream(bas);
    return ds.readInt();   
}
public static long ReadLong(FileInputStream in) throws IOException
{
    byte buf[]=new byte[8];
    in.read(buf, 0, 8);
    ByteArrayInputStream bas = new ByteArrayInputStream(buf);
    DataInputStream ds = new DataInputStream(bas);
    return ds.readLong();   
}
public static float ReadFloat(FileInputStream in) throws IOException
{
    byte buf[]=new byte[4];
    in.read(buf, 0, 4);
    ByteArrayInputStream bas = new ByteArrayInputStream(buf);
    DataInputStream ds = new DataInputStream(bas);
    return ds.readFloat();   
}
public static String ReadString(FileInputStream in) throws IOException
{
    int len=in.read();
    byte buf[]=new byte[len];
    in.read(buf, 0,len);
    return new String(buf, "utf-8");   
}
public static LocalDateTime ReadDate(FileInputStream in) throws IOException
{
    int dd=in.read(),mm=in.read();
    int yyyy=ReadInt(in);
    return LocalDateTime.of(yyyy, mm, dd, 0, 0);
}

public static Coordinates ReadCoordinates(FileInputStream in) throws IOException
{
    return new Coordinates(ReadLong(in),ReadFloat(in));   
}
public static Person ReadPerson(FileInputStream in) throws IOException
{
    return new Person(ReadString(in),ReadDate(in),ReadFloat(in),ReadLong(in));   
}

public static class Coordinates
{
    long x;float y;
    public Coordinates(long x,float y)
    {
        this.x=x;this.y=y;
    }
    public String ToString()
    {
        return "("+x+";"+y+")";
    }
    public void Save(BufferedOutputStream bos) throws IOException
    {
        SaveLong(bos,x);
        SaveFloat(bos,y);
    }
}
static public class Person
{
    String name;
    LocalDateTime birthday;
    float height;
    long weight;
    public Person(String name,LocalDateTime birthday,float height,long weight)
    {
        this.name=name;this.birthday=birthday;this.height=height;this.weight=weight;
    }
    public String ToString()
    {
        return "Режиссёр: "+name+"; День рождения: "+birthday.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"))+"; Рост: "+height+"; Вес: "+weight;
    }
    public void Save(BufferedOutputStream bos) throws IOException
    {
        SaveString(bos,name);
        SaveDate(bos,birthday);
        SaveFloat(bos,height);
        SaveLong(bos,weight);
    }
}


    public enum MovieGenre
    {ACTION, WESTERN,DRAMA,HORROR}
    public enum MpaaRating{PG,PG_13,R,NC_17}
    static public class Movie implements Comparable<Movie>
    {
        private int id;
        private String name;
        private Coordinates coordinates;
        private LocalDateTime creationDate;
        private int oscarsCount=0;
        private MovieGenre genre;
        private MpaaRating mpaaRating;
        private Person screenwriter;
        public Movie(int id,String name,Coordinates coordinates,LocalDateTime creationDate,int oscarsCount,MovieGenre genre,MpaaRating mpaaRating,Person screenwriter)
        {
            this.id=id;this.name=name;this.coordinates=coordinates;this.creationDate=creationDate;
            this.oscarsCount=oscarsCount;this.genre=genre;this.mpaaRating=mpaaRating;this.screenwriter=screenwriter;
        }
        public void Show()
        {
            System.out.println("id: "+id+";"+"Название: "+name+"; Координаты: "+coordinates.ToString()+"; Дата создания: "+creationDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"))+"; Число оскаров: "+oscarsCount+"; Жанр: "+
                    genre+"; Рейтинг: "+mpaaRating+"; "+screenwriter.ToString());
        }
        public void Save(BufferedOutputStream bos) throws IOException
        {
            SaveInt(bos,id);
            SaveString(bos,name);
            coordinates.Save(bos);
            SaveDate(bos,creationDate);
            SaveInt(bos,oscarsCount);
            SaveString(bos,genre.name());
            SaveString(bos,mpaaRating.name());
            screenwriter.Save(bos);
        }
        public void Update(String cmd)
        {
            String[] str=cmd.split("[ ]*;[ ]*");
            for(String upd_comd:str)
            {
                //System.out.println(upd_comd);
                UpdateParam(upd_comd);
            }
        }
        
        void UpdateParam(String cmd)
        {
                String[] str=cmd.split("[ ]*[=][ ]*");
                String param=(str.length>1)?str[1]:"";
                //System.out.println(str[0]+" "+param);
                switch(str[0])
                {
                    case "name":name=param;break;
                    case "x":coordinates.x=Integer.parseInt(param);break;
                    case "y":coordinates.y=Integer.parseInt(param);break;
                    case "creationDate":creationDate=LocalDate.parse(param, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();break;
                    case "genre":genre=MovieGenre.valueOf(param);break;
                    case "mpaaRating":mpaaRating.valueOf(param);break;
                    case "FIO":screenwriter.name=param;break;
                    case "weight":screenwriter.weight=Long.parseLong(param);break;
                    case "height":screenwriter.height=Float.parseFloat(param);break;
                    case "birthday":screenwriter.birthday=LocalDate.parse(cmd, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();break;//LocalDateTime.parse(cmd, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
                    default : System.out.println("Неверный ввод"); break;
                }            
        }
        @Override
        public int compareTo(Movie o) {
            return o.id; 
}
    }
    static class CollectionMovies
    {
        PriorityQueue<Movie> queue=new PriorityQueue<Movie>();
        
        public void Show()
        {
            for(Movie movie:queue)
                movie.Show();
        }
        public void add(Movie movie)
        {
            queue.add(movie);
        }
        public Movie GetMovie_By_id(int id)
        {
            for(Movie movie:queue)
                if(movie.id==id)
                    return movie;
            return null;
        }
        public void remove_by_id(int id)
        {
            for(Movie movie:queue)
            {
                if(movie.id==id)
                {
                    queue.remove(movie);
                    return;
                }
            }
        }
        public void Clear()
        {
            queue.clear();
        }
        public Movie RemoveHead()
        {
            return queue.remove();
        }
        int find_min()
        {
            int min=(queue.size()>0) ? queue.element().id: -1;
            for(Movie movie:queue)
                if(movie.id<min)
                    min=movie.id;
            return min;
        }
        public void add_if_min(Movie movie)
        {
            int min=find_min();
            if(movie.id<min || queue.size()==0)
                queue.add(movie);
        }
        public void remove_greather(Movie element)
        {
            int id=element.id;
            for(Movie movie:queue)
            {
                if(movie.id>id)
                {
                    queue.remove(movie);
                    return;
                }
            }
        }
        public void remove_greather(int id)
        {
            for(Movie movie:queue)
            {
                if(movie.id>id)
                {
                    queue.remove(movie);
                    return;
                }
            }
        }       
        public Movie max_by_screenwriter()
        {
            Movie movie_max=queue.element();
            if(queue.size()==0) return null;
            float max=queue.element().screenwriter.height;
            for(Movie movie:queue)
            {
                if(movie.screenwriter.height>max)
                {
                    max=queue.element().screenwriter.height;
                    movie_max=movie;
                }
            }
            return movie_max;
        }
        int count_less_than_oscars_count(int oscarsCount)
        {
            int count=0;
            for(Movie movie:queue)
                if(movie.oscarsCount<oscarsCount)
                    count++;
            return count;
        }
        public void filter_starts_with_name(String name)
        {
            String regex="^"+name+".+$";
            for(Movie movie:queue)
            {
                if(Pattern.matches(regex, movie.name))
                    movie.Show();
            }
        }
        public void Save()
        {
            try
            (
                FileOutputStream out=new FileOutputStream("data.xml");
                BufferedOutputStream bos = new BufferedOutputStream(out))
            {
                SaveInt(bos,queue.size());
                for(Movie movie:queue)
                    movie.Save(bos);
                bos.close();
                out.close();

            }
            catch(IOException ex){}
        }
    }
    /**
     * @param args the command line arguments
     */
    static class InteractiveConsole
    {
        CollectionMovies collection=new CollectionMovies();
        public void Show() throws IOException
        {
            Read();
            //Movie movie=new Movie(27,"Война миров",new Coordinates(45,67),LocalDateTime.of(2005,4,12,0,0),2,MovieGenre.ACTION,MpaaRating.PG,new Person("Стивен Спилберг",LocalDateTime.of(1955,4,12,0,0),142,75));
            //collection.add(movie);
            String cmd="";
            while(cmd.compareTo("exit")!=0)
            {
                Scanner in = new Scanner(System.in, "windows-1251");
                cmd=in.nextLine();
                ExecuteCmd(cmd);
            }
        }
        /*byte[] ByteToChar(char arr[])
        {
            byte b[]=new byte[arr.length*2];
            for(int i=0;i<arr.length;i++)
            {
                b[2*i]=(byte)(arr[i]>>8);
                b[2*i+1]=(byte)(arr[i]&0xFF);
            }
            return b;
        }*/
        void Read() throws IOException
        {
            try(FileInputStream in = new FileInputStream("data.xml");)
            {
               int count=ReadInt(in);
               for(int i=0;i<count;i++)
               {
                   int id=ReadInt(in);
                   String name=ReadString(in);
                   Coordinates coord=ReadCoordinates(in);
                   //System.out.println(id+" "+name+" "+coord.x+" "+coord.y);
                   LocalDateTime date_create=ReadDate(in);
                   int oscar=ReadInt(in);
                   MovieGenre genre=MovieGenre.valueOf(ReadString(in));
                   MpaaRating mpaaRating=MpaaRating.valueOf(ReadString(in));
                   Person screenwriter=ReadPerson(in);
                   collection.add(new Movie(id,name,coord,date_create,oscar,genre,mpaaRating,screenwriter));
               }
            }
               catch(IOException ex){}
        }
        void ExecuteCmd(String cmd)
        {
                String[] str=cmd.split("[ ]+");
                String param=(str.length>1)?str[1]:"";
                switch(str[0])
                {
                    case "help":help();break;
                    case "info":info();break;
                    case "show":collection.Show();break;
                    case "add":Add();break;
                    case "update":update_id(param);break;
                    case "remove_by_id":remove_by_id(param);break;
                    case "clear":collection.Clear();break;
                    case "save":collection.Save();break;
                    case "execute_script":execute_script(param);break;
                    case "remove_head":remove_head();break;
                    case "add_if_min":add_if_min();break;
                    case "remove_greather":remove_greather(param);break;
                    case "max_by_screenwriter":max_by_screenwriter();break;
                    case "count_less_than_oscars_count":count_less_than_oscars_count(param); break;
                    case "filter_starts_with_name":filter_starts_with_name(param); break;
                    case "exit": break;
                    default : System.out.println("Неверная команда"); break;
                }
        }
        void info()
        {
            System.out.println("Тип: "+collection.queue.getClass()+"; Число элементов: "+collection.queue.size());
        }
        void help()
        {
            System.out.println("help - вывести справку по доступным командам");
            System.out.println("info - вывести стандартный поток вывода информацию с коллекции (тип, дата инициализации, количество элементов и т.д.)");
            System.out.println("update id - обновить значение элемента коллекции, 1(1 которого равен заданному, после ввести несколько параметров, пример: name=Список Шиндлера; x=23; y=47; creationDate=11.11.1999; genre=DRAMA; mpaaRating=PG; FIO=Стивен Спилбер; weight=80; height=120; birtday=21.07.1964");
            System.out.println("add - добавить ноеый элемент в коллекцию");
            System.out.println("remove_by_id - удалить элемент из коллекции по его id");
            System.out.println("clear - очистись коллекцию");
            System.out.println("save - ссхранить коллекцию в файл");
            System.out.println("execute_script - считать и исголнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит псльзователь з интерактивном режиме.");
            System.out.println("exit - завершить пгрограмму (без сохранения в файл)");
            System.out.println("remove_head - вывести первый элемент коллекции и удалить его");
            System.out.println("add_if_min - добавить ноеый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
            System.out.println("remove_greather - удалить из коллекции все элементы, превышающие заданный");
            System.out.println("max_by_screenwriter - вывести любой объект из коллекции, значение поля screenwriter которого является максимальным");
            System.out.println("count_less_than_oscars_count - количество элементов, значение поля oscarsCount которых меньше заданного");
            System.out.println("filter_starts_with_name - вывести элементы, значение поля пате которых начинается с заданной подстроки");
            
            
        }
        void execute_script(String file)
        {
            try(FileReader in = new FileReader(file);BufferedReader bf = new BufferedReader(in);)
            {
                for(String str=bf.readLine();str!=null;str=bf.readLine())
                {
                    Execute(str);
                }
            }
            catch(IOException ex){}
        }
        void Add()
        {
            collection.add(Input());
        }
        Movie Input()
        {
            int id=-1;
            while ( collection.GetMovie_By_id(id)!=null || id==-1) 
                id=InputInt("Введите id: ");
            
            String name=InputString("Введите название фильма: ");
            
            long x=900;float y=0;boolean not_correct=true;
            while (x>870 && not_correct) 
            {
                System.out.print("Введите координаты: ");
                Scanner in = new Scanner(System.in, "windows-1251");
                not_correct=false;
                try
                {
                    x=in.nextLong();
                    y=in.nextFloat();
                }
                catch(InputMismatchException fg){not_correct=true;}
            }
            Coordinates coord=new Coordinates(x,y);
            LocalDateTime date=InputDate("Введите дату создания: ");
            int oscar=InputInt("Введите число оскаров: ");
            MovieGenre genre= MovieGenre.ACTION;not_correct=true;
            while (not_correct) 
            {
                not_correct=false;
                System.out.print("Введите жанр(ACTION, WESTERN,DRAMA,HORROR): ");
                try
                {
                    Scanner in = new Scanner(System.in, "windows-1251");
                    String g=in.nextLine();
                    genre=MovieGenre.valueOf(g);
                }
                catch(InputMismatchException fg){not_correct=true;}
                catch(IllegalArgumentException fg){not_correct=true;}
            }
            MpaaRating rating=MpaaRating.NC_17;not_correct=true;
            while (not_correct) 
            {
                not_correct=false;
                System.out.print("Введите рейтинг(PG,PG_13,R,NC_17): ");
                try
                {
                    Scanner in = new Scanner(System.in, "windows-1251");
                    String g=in.nextLine();
                    rating=MpaaRating.valueOf(g);
                }
                catch(InputMismatchException fg){not_correct=true;}
                catch(IllegalArgumentException fg){not_correct=true;}
            }
            
            String FIO=InputString("ФИО режиссёра: ");
            
            LocalDateTime birth=InputDate("Введите дату рождения: ");
            
            long w=0;float h=0;
            while (w<=0 || h<=0) 
            {
                System.out.print("Введите вес и рост ");
                Scanner in = new Scanner(System.in, "windows-1251");
                try
                {
                    w=in.nextLong();
                    h=in.nextFloat();
                }
                catch(InputMismatchException fg){}
            } 
            return new Movie(id,name,coord,date,oscar,genre,rating,new Person(FIO,birth,h,w));
        }
        void Execute(String str){}
        LocalDateTime InputDate(String str)
        {
            int dd=0,mm=0,yyyy=0;
            LocalDateTime date=null;boolean not_correct=true;
            while (not_correct) 
            {
                System.out.print(str);
                Scanner in = new Scanner(System.in, "windows-1251");
                not_correct=false;
                try
                {
                    dd=in.nextInt();
                    mm=in.nextInt();
                    yyyy=in.nextInt();
                    date=LocalDateTime.of(yyyy,mm,dd,0,0);
                }
                catch(InputMismatchException fg){not_correct=true;}
                catch(DateTimeException fg){not_correct=true;}
            }
            return date;
        }
        int InputInt(String str)
        {
            int val=-1;
            while (val<=0) 
            {
                System.out.print(str);
                Scanner in = new Scanner(System.in, "windows-1251");
                try
                {
                    val=in.nextInt();
                }
                catch(InputMismatchException fg){}
            }
            return val;
        }
        String InputString(String str)
        {
            System.out.print(str);
            Scanner in = new Scanner(System.in, "windows-1251");
            return in.next();
        }
        
        public void max_by_screenwriter()
        {
            if(collection.queue.size()>0)
            collection.max_by_screenwriter().Show();
        }
        public void remove_head()
        {
            if(collection.queue.size()>0)
            collection.RemoveHead().Show();
        }
        void remove_greather(String str)
        {
            try
            {
                int id=Integer.parseInt(str);
                collection.remove_greather(id);
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Неверный ввод параметров");
            }
        }
        void count_less_than_oscars_count(String str)
        {
            try
            {
               int count=Integer.parseInt(str);
               System.out.println( collection.count_less_than_oscars_count(count));
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Неверный ввод параметров");
            }            
        }
        void add_if_min()
        {
            Movie movie=Input();
            collection.add_if_min(movie);
        }
        void filter_starts_with_name(String name)
        {
            collection.filter_starts_with_name(name);
        }
        void remove_by_id(String str)
        {
            try
            {
               int id=Integer.parseInt(str);
               collection.remove_by_id(id);
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Неверный ввод параметров");
            }   
        }
        void update_id(String str)
        {
            try
            {
               int id=Integer.parseInt(str);
               Movie movie=collection.GetMovie_By_id(id);
               Scanner in = new Scanner(System.in, "windows-1251");
               System.out.println("Введите параметры: ");
               String params=in.nextLine();
               movie.Update(params);
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Неверный ввод параметров");
            }               
        }
    }

    public static void main(String[] args) throws IOException {
        
        InteractiveConsole con=new InteractiveConsole();
        con.Show();
    }
    
}
