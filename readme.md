### How to run
1. Install java v 21 https://download.oracle.com/java/21/archive/jdk-21.0.4_windows-x64_bin.exe
2. Install MySQL https://dev.mysql.com/downloads/installer/ take note of the password and username
3. Open this folder in IntelliJ. 
4. In the far right of the IntelliJ window click the database button that looks like a burger. Then in the extended window select "New (plus icon)>Data source>MySQL"
5. Provide the username and password from the MySQL installation process and click "test connection" if this test fails there was a problem connecting to the database. Maybe it wasn't set up right. Otherwise click apply and then okay.
6. Right click "jpdatabase@localhost" then select New>Schema. The schema name must be "jpdatabase".
7. In the top of the window of Jira to the left of the green play button, click the button which appears there and select "Edit configurations"
8. Click New>Gradle. Under "Run" set it to "bootRun" and in the environment variables fill in DB_USERNAME=exampleUsername;DB_PASSWORD=examplePassword where you replace exampleUsername and examplePassword with the ones from the installations process.
9. Make sure to click apply and okay, and close the window. The new profile you've just created names "jp-backend [bootRun]" can now be selected, and you can click on the green play icon to run the backend.
10. When you see something that looks like log entries with dates in the terminal it means it is running. The endpoints provided by src/java/com.dat3.jpgroentbackend/controllers can be found at: http://localhost:8080/swagger-ui/index.html