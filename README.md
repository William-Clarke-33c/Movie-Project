# Movie-Project
A Movie Recommendaiton Engine created using JDBC


#### Summary
  The Movie Recommendation takes in user input, a movie name, and search’s a database containing 5,000 movies to find the 5 most 
relevant to the title given. It does this by taking the given title, passing it into a JDBC query that returns the movies ID number. 
Then, using that ID number, we compare it to the IDs of all the movies in the database. If the movie does not match the current ID, 
which it won’t 4,999 out of 5,000 times, we get the Keywords, and Genres related to those 4,999 films. 
We compare them to the inputted movies Keywords and Genres. We then use an algorithm to compute the distance the related 
movies are to the user movie in our matrix. We take the top 15 closest movies, and get their  vote_average  along with their  v
ote_count  from the database. We then computed the total  vote_count , represented as  maxVote , for all the movies.
We then calculate each movies  Score  with the following.

    float score = (float) ((((vote_average * vote_average)*vote_count)/maxVote)*100) 
    
The 5 movies with the highest score are than recommended to the user.


### Sample Runs

      What Movie Do You Like? ( You Can Enter 'e' To Exit The Program At Anytime)

      Alice in Wonderland

      Suggesting Movies.....

      Here Are Your Suggested Movies For 'Alice in Wonderland' (2010)
      __________________________________________________

                 Title                             Year                            
      1.) Harry Potter and the Philosopher's Stone 2001                                                                                                  
      2.) Charlie and the Chocolate Factory        2005                                                                                                  
      3.) The Chronicles of Narnia:                2005                                                                                                  
          The Lion, the Witch and the Wardrobe                                                                                                         
      4.) The Wizard of Oz                         1939                                                                                                  
      5.) Shrek Forever After                      2010                                                                                                  

      __________________________________________________
      

      What Movie Do You Like? ( You Can Enter 'e' To Exit The Program At Anytime)

      Avatar

      Suggesting Movies.....

      Here Are Your Suggested Movies For 'Avatar' (2009)
      __________________________________________________

                 Title                             Year                            
      1.) Star Trek Into Darkness                  2013                                                                                                  
      2.) The Fifth Element                        1997                                                                                                  
      3.) Pirates of the Caribbean:                2011                                                                                                  
          On Stranger Tides                                                                                                                            
      4.) Aliens                                   1986                                                                                                  
      5.) Independence Day                         1996                                                                                                  

      __________________________________________________
