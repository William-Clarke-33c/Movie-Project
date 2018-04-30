package finalproject;
import java.sql.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.*;
import java.util.*;
import java.util.Arrays; // added this
import static javax.management.Query.gt;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
//import com.google.common.collect.ArrayTable;

/**
 *
 * @author LiamClarke
 */
public class FinalProject {
    
    public static HashMap<Integer, String> Keywords = new HashMap();
    public static String format = "%-1s %-27s %-80s %-10s %-10s";
    public static String titleformat = "%-10s %-20s %-10s %-10s %-10s";
     
    
    public static void main(String[] args) throws SQLException, IOException, ParseException {
        String uI = "";
            System.out.println("Enter 'm' to use the Movie Recommendation or Enter 'c' to Create Database");
            Scanner userInput = new Scanner(System.in);
            uI = userInput.nextLine();
            if(uI.equals("m") || uI.equals("M")){
                movieRecommendation();
            }
            if(uI.equals("c") || uI.equals("C")){
                createDatabase();
            }       
    }
    
    public static HashMap<Integer, String> keywordFinder() throws IOException{
        CSVParser parser = CSVParser.parse(new File("tmdb_5000_movies.csv"), Charset.defaultCharset(), CSVFormat.RFC4180);
        List<CSVRecord> list = parser.getRecords();
        ArrayList<String> keywordSort = new ArrayList();
        ArrayList<Integer> idSort = new ArrayList();
        
        for(int i = 1; i < list.size(); i++){
        JSONArray jsonParser = new JSONArray(list.get(i).get(4));    
            for(int j = 0; j < jsonParser.length(); j++ ){
                keywordSort.add(jsonParser.getJSONObject(j).getString("name"));
                idSort.add(jsonParser.getJSONObject(j).getInt("id"));
            }
        }
        for(int i = 0; i < keywordSort.size(); i++){
            int count = Collections.frequency(keywordSort, keywordSort.get(i));
            if(count > 5){
                Keywords.put(idSort.get(i), keywordSort.get(i));
            }
        }
        return null;
    }
    
    public static void createDatabase() throws SQLException, IOException, ParseException{
        System.out.println("Creating Database...");
        keywordFinder();
        Connection Conn = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306","root","password");
        Statement s = Conn.createStatement();
        String movieData = "tmdb_5000_movies.csv";
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        ResultSet rs = Conn.getMetaData().getCatalogs();
        int Result = s.executeUpdate("DROP DATABASE MovieRecommend");
        
        
            //CREATE DATABASE MovieRecommend
                s.executeUpdate("CREATE DATABASE IF NOT EXISTS MovieRecommend");
                s.executeUpdate("USE MovieRecommend");

            //CREATE TABLE MOVIES
            String insertMovies = "INSERT INTO Movies"
                    + "(id, title, current_title, overview, language,"
                    + "homepage, budget, popularity, released, revenue, runtime,"
                    + "status, tagline, vote_average, vote_count) VALUES"
                    + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


            Result = s.executeUpdate("CREATE TABLE Movies("
                    + "id INTEGER,"//1
                    + "title VARCHAR(100),"//2
                    + "current_title VARCHAR(100),"//3
                    + "overview VARCHAR(3000),"//4
                    + "language VARCHAR(3),"//5
                    + "homepage VARCHAR(500),"//6
                    + "budget INTEGER,"//7
                    + "popularity FLOAT,"//8
                    + "released VARCHAR(30),"//9 CHANGE TO DATE
                    + "revenue BIGINT,"//10
                    + "runtime INTEGER,"//11
                    + "status VARCHAR(50),"//12
                    + "tagline VARCHAR(600),"//13
                    + "vote_average FLOAT,"//14 "IMDB RATING"
                    + "vote_count INTEGER,"//15
                    + "PRIMARY KEY(id)"
                    + ")");
            System.out.println("Created Table Movies...");

            //CREATE TABLE KEYWORDS
            String insertKeywords = "INSERT INTO Keywords"
                    + "(movie_id, keyword, keyword_id) VALUES"
                    + "(?,?,?)";

            s.executeUpdate("CREATE TABLE IF NOT EXISTS Keywords("
                    + "movie_id INTEGER,"
                    + "keyword VARCHAR(400),"
                    + "keyword_id INTEGER,"
                    + "FOREIGN KEY(movie_id) REFERENCES Movies(id),"
                    + "PRIMARY KEY(keyword_id,movie_id)"
                    + ")");

            System.out.println("Created Table Keywords...");

            //CREATE TABLE GENRES
            String insertGenres = "INSERT INTO Genres"
                    + "(movie_id, genre, genre_id) VALUES"
                    + "(?,?,?)";


            s.executeUpdate("CREATE TABLE IF NOT EXISTS Genres("
                    + "movie_id INTEGER,"
                    + "genre VARCHAR(400),"
                    + "genre_id INTEGER,"
                    + "FOREIGN KEY(movie_id) REFERENCES Movies(id),"
                    + "PRIMARY KEY(genre_id,movie_id)"
                    + ")");

            System.out.println("Created Table Genres...");



            CSVParser parser = CSVParser.parse(new File("tmdb_5000_movies.csv"), Charset.defaultCharset(), CSVFormat.RFC4180);
            List<CSVRecord> list = parser.getRecords();
            PreparedStatement movies = Conn.prepareStatement(insertMovies);
            PreparedStatement keywords = Conn.prepareStatement(insertKeywords);
            PreparedStatement genres = Conn.prepareStatement(insertGenres);

            //INSERTING INTO MOVIES
            int i = 1;
            while ((i < list.size())){
                movies.setInt(1, Integer.parseInt(list.get(i).get(3)));
                movies.setString(2, list.get(i).get(6));
                movies.setString(3, list.get(i).get(17));
                movies.setString(4, list.get(i).get(7));
                movies.setString(5, list.get(i).get(5));
                movies.setString(6, list.get(i).get(2));
                movies.setInt(7, Integer.parseInt(list.get(i).get(0)));
                movies.setFloat(8, Float.parseFloat(list.get(i).get(8)));
                if(list.get(i).get(11).equalsIgnoreCase("")){
                }else{
                    java.util.Date utilDate = format.parse(list.get(i).get(11));
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    movies.setDate(9, sqlDate);
                }
                movies.setLong(10, Long.parseLong(list.get(i).get(12)));
                if(list.get(i).get(13).equalsIgnoreCase("")){
                }else{
                    movies.setInt(11, Integer.parseInt(list.get(i).get(13)));
                }
                movies.setString(12, list.get(i).get(15));
                movies.setString(13, list.get(i).get(16));
                movies.setFloat(14, Float.parseFloat(list.get(i).get(18)));
                movies.setInt(15, Integer.parseInt(list.get(i).get(19)));
                movies.executeUpdate();
                

                //INSERTING INTO KEYWORDS
                    JSONArray jsonParser = new JSONArray(list.get(i).get(4));    
                    for(int j = 0; j < jsonParser.length(); j++ ){
                        JSONObject keywordsIn = jsonParser.getJSONObject(j);
                        int keywordID = keywordsIn.getInt("id");
                        if(Keywords.containsKey(keywordID)){
                            keywords.setInt(1, Integer.parseInt(list.get(i).get(3)));
                            keywords.setString(2, keywordsIn.getString("name"));
                            keywords.setInt(3, keywordID);
                            keywords.executeUpdate();
                        }
                    }
                    

                //INSERTING INTO GENRES
                    jsonParser = new JSONArray(list.get(i).get(1));    
                    for(int j = 0; j < jsonParser.length(); j++ ){
                        JSONObject genresIn = jsonParser.getJSONObject(j);
                        int genreID = genresIn.getInt("id");
                        genres.setInt(1, Integer.parseInt(list.get(i).get(3)));
                        genres.setString(2, genresIn.getString("name"));
                        genres.setInt(3, genreID);
                        genres.executeUpdate();
                    }
                    

                i++;
                if(i == list.size()){
                    movies.close();
                    System.out.println("Inserted Into Table Movies...");
                    keywords.close();
                    System.out.println("Inserted Into Table Keywords...");
                    genres.close();
                    System.out.println("Inserted Into Table Genres...");
                    System.out.println("Completed");
                    movieRecommendation();
                }
            }
    }
    
    public static void movieRecommendation() throws SQLException{
        Connection Conn = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306","root","password");
        Statement s = Conn.createStatement();
        String movieData = "tmdb_5000_movies.csv";
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        ResultSet rs = Conn.getMetaData().getCatalogs();
        int Result = s.executeUpdate("USE MovieRecommend");
       
        
        String uI = "";
        String userMovie = "";
            System.out.println();
            System.out.println("__________________________________________________________");
            System.out.println(String.format("      Welcome to the Movie Recommendation Program!"));
            System.out.println(String.format("Enter 'm' to Find Movie Suggestions or Enter 'e' to Exit!",""));
            System.out.println("__________________________________________________________");
            Scanner userInput = new Scanner(System.in);
            uI = userInput.nextLine();
            if(uI.equals("m") || uI.equals("M")){
                System.out.println();
                ResultSet voteTotal = s.executeQuery("SELECT SUM(vote_count)"
                       + "AS maxVote "
                       + "FROM Movies M ");
                    int maxVote = 0;
                    while(voteTotal.next()){
                        maxVote = voteTotal.getInt("maxVote");
                    }
                    
                while(!userMovie.equals("e")){

                    //Enter Movie Recommendaiton
                    System.out.println();
                    System.out.println("What Movie Do You Like? ( You Can Enter 'e' To Exit The Program At Anytime)");
                    System.out.println();
                    Scanner movieInput = new Scanner(System.in);
                    userMovie = movieInput.nextLine();
                    ResultSet movieQuery = s.executeQuery("SELECT id,released \n" +
                    "FROM Movies\n" +
                    "WHERE title =\""+userMovie+"\"");
                    System.out.println();
                    int simMovies = 0;
                    int movieID = 0;
                    int[] similarIDMovies = new int [2];
                    int i = 0;
                    int releaseYear = 0;
                    Calendar calendar = new GregorianCalendar();
                    while (movieQuery.next()){
                            movieID = (int) movieQuery.getInt("id");
                            calendar.setTime(movieQuery.getDate("released"));
                            releaseYear = calendar.get(Calendar.YEAR);
                            simMovies++;
                            similarIDMovies[i] = movieID;
                            i++;
                    }
                    if(simMovies == 1 ){
                        movieFinder(movieID, maxVote, userMovie, releaseYear);
                        userMovie = "";
                    }else if(simMovies > 1){
                        System.out.println("I Found More Than One Movie With The Name '" + userMovie + "'");
                        System.out.println();
                        for(int p = 0; p < similarIDMovies.length; p++){
                        rs = s.executeQuery("SELECT * FROM Movies M WHERE"
                                            + " M.id = " +similarIDMovies[p]);
                                            rs.next();
                                            String title = rs.getString(2);
                                            calendar.setTime(rs.getDate(9));
                                            releaseYear = calendar.get(Calendar.YEAR);
                                            System.out.println(title + " (" + releaseYear + ")");
                                            
                        }
                        System.out.println();
                        System.out.println("Which Year Did You Mean?");
                        System.out.println();
                        Scanner movieYear = new Scanner(System.in);
                        int year = movieYear.nextInt();
                        rs = s.executeQuery("SELECT id FROM Movies M WHERE"
                                            + " YEAR(M.released) = " + year);
                                            rs.next();
                                            int id = rs.getInt(1);
                                            movieFinder(id,maxVote,userMovie, year);
                                            
                        }else if(simMovies == 0){
                            if(userMovie.equalsIgnoreCase("e")){
                                
                            }else{
                            System.out.println("Sorry, I Couldn't Find The Movie '" + userMovie +"' In My Database.");
                            }
                        }
                        
                    }
                }
                System.out.println(String.format(" Thanks For Using the Movie Recommendation Program!"));
                System.exit(0);
                
    }
    
    public static void movieFinder(int movieID, int maxVote, String userMovie, int releaseYear) throws SQLException{
        System.out.println("Suggesting Movies.....");
        System.out.println();
        Connection Conn = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306","root","password");
        Statement s = Conn.createStatement();
        PreparedStatement keywordSearch = Conn.prepareStatement("SELECT *"
                       + "FROM Keywords K "
                       + "WHERE K.movie_id = ? ");
        PreparedStatement genreSearch = Conn.prepareStatement("SELECT *"
                       + "FROM Genres G "
                       + "WHERE G.movie_id = ? ");
        int Result = s.executeUpdate("USE MovieRecommend");
        /*Keywords("
                    + "movie_id INTEGER," // 1
                    + "keyword VARCHAR(400)," // 2
                    + "keyword_id INTEGER,") // 3
	*/
	
        //Keyword Comparison
        keywordSearch.setInt(1, movieID);
        genreSearch.setInt(1, movieID);
        ResultSet rs = keywordSearch.executeQuery();
        ResultSet gs = genreSearch.executeQuery();
        HashSet<Integer> userMovieKey = new HashSet();
        HashSet<Integer> userGenreKey = new HashSet();
        while(rs.next()){
            userMovieKey.add(rs.getInt(3));
        }
        while(gs.next()){
            userGenreKey.add(gs.getInt(3));
        }
        //System.out.println(userMovieKey); // prints all the keyword ids beloning to the user's movie
        
        Matrix[] theDistance = new Matrix[4802];
        rs = s.executeQuery("SELECT id "
                + "FROM MOVIES M");
        int count = 0;
        
        while(rs.next()){
            int movieTemp = rs.getInt(1);
            if(movieTemp != movieID){
                keywordSearch.setInt(1, rs.getInt(1));
                genreSearch.setInt(1, rs.getInt(1));
                ResultSet keywordSet = keywordSearch.executeQuery();
                ResultSet genreSet = genreSearch.executeQuery();
                int sim = 0;
                int genSim = 0;
                
                
                while(keywordSet.next()){
                    if(userMovieKey.contains(keywordSet.getInt(3))){
                        sim++;
                    }
                }
                while(genreSet.next()){
                    if(userGenreKey.contains(genreSet.getInt(3))){
                        sim++;
                    }
                }
                Matrix pair = new Matrix();
                pair.id = movieTemp;
                pair.distance = Math.sqrt((userMovieKey.size()+ userGenreKey.size()) - sim);
                theDistance[count++] = pair;
            }
        }
        
        //Prints out Suggested Movies
        Arrays.sort(theDistance);
        int n = 15;
        Matrix[] thePop = new Matrix[n];
        int popCount = 0; 
        int[] similarMovies = new int [n];
        int j = 1;
        for(int p = 0; p < similarMovies.length; p++){
            Matrix popMatrix = new Matrix();
            similarMovies[p] = theDistance[p].id;
            popMatrix.id = theDistance[p].id;
                rs = s.executeQuery("SELECT * FROM Movies M WHERE"
                    + " M.id = " +similarMovies[p]);
            rs.next();
            float vote_average = rs.getFloat(14);
            int vote_count = rs.getInt(15);
            float score = (float) ((((vote_average * vote_average)*vote_count)/maxVote)*100);;
            popMatrix.distance = score;
            thePop[popCount++] = popMatrix;
        }
        Arrays.sort(thePop);
        int[] popMovies = new int [n];
        Calendar calendar = new GregorianCalendar();
        System.out.println("Here Are Your Suggested Movies For '" + userMovie +"' (" + releaseYear +")");
        System.out.println("__________________________________________________");
        System.out.println();
        System.out.println(String.format(titleformat,"", "Title","Year",
            "",""));
        for(int p = 14; p >= 10 ; p--){
            popMovies[p] = thePop[p].id;
                rs = s.executeQuery("SELECT * FROM Movies M WHERE"
                    + " M.id = " +popMovies[p]);
            rs.next();
            calendar.setTime(rs.getDate("released"));
            releaseYear = calendar.get(Calendar.YEAR);
            String title = rs.getString(2);
            String num = Integer.toString(j) + ".)";
            if (title.contains(":")){
                String[] parts = title.split(":");
                System.out.println(String.format(format,num,parts[0] +":",releaseYear,"",""));
                System.out.println(String.format(format,""," " + parts[1] +"","","",""));
                j++;
            }else{
                System.out.println(String.format(format,num, title,releaseYear,
                "",""));
                j++;
            }
        }
        System.out.println();
        System.out.println("__________________________________________________");
    }
}
    
    
