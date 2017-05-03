# Popular-Movies
An Android app that shows a list of movies movies fetched from The Movie DB API. The movies are shown in grid view using a Recycler View with an option to sort movies according to "most popular" or "highest rated". Each item is clickable and moves the user to a detail screen showing detail about the selected movie such as description, data of release, rating. The user also has the option to view trailers and read reviews. In addition, the user can sav his favorite movies to a local database.   
The app is also optimized for tablets using a Master/Detail approach. 

## Screenshots
![screenshot1](screenshots/popular%20movies.png)

## Getting Started
The below instruction will get you a copy of the project up and running on your machine for development and testing purposes.

### Prerequisites
Android Studio including SDK version 25 and build tools version 25.0.2.  
You can always update to the latest versions. 

**API KEY**
This application requires a key from The Movie DB API. 
You can get a your own API key from [The Movie Database (TMDb)](https://www.themoviedb.org).
In the gradle.properties file, substiute the value of **'MyMovieDbKey'** with your own key.

### Installing and Deployment
1. Import the project to your Android Studio
2. Build the project
3. Install the APK on your device or an emulator

### Built With
[Android Studio](https://developer.android.com/studio/index.html) - The IDE used  
[Gradle](https://gradle.org/) - Dependency Management  
[The Movie Database](https://www.themoviedb.org/) - The used API for fetching movies

### Libraries Used
[Glide](https://github.com/bumptech/glide)  

### Contributing 
Pull requests are gracefully accepted. 

### License
The project is licensed under the MIT License - see [LICENSE.txt](LICENSE.txt) file for detail.

