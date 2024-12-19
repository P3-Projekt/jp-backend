
# Micro-green Management System

A micro-green managment system made for JP-Grønt ApS. This is the Data Server made using Spring


## Authors

- [Frederik Rasmussen](https://www.github.com/Fruttee)
- [Jacob K. Autzen](https://www.github.com/JacobKautzen)
- [Jacob Højlund Simonsen](https://www.github.com/jhs232)
- [Kristoffer Holm](https://www.github.com/KristofferHolm01)
- [Nicolai Dybro](https://www.github.com/NicolaiDybro)
- [Victor Hjelmberg Feddersen](https://www.github.com/victorhjelmberg)
- [Jeppe Mortensen](https://www.github.com/Yamammao23)
## Requirements

- [Java v. 21](https://download.oracle.com/java/21/archive/jdk-21.0.4_windows-x64_bin.exe)

- [MySQL database](https://dev.mysql.com/downloads/installer/)


## Guide using Intellij

Clone the project

```bash
  git clone https://github.com/P3-Projekt/jp-backend
```

Go to the project directory

```bash
  cd jp-backend
```



1. In the top of the window of Intellij to the left of the green play button, click the button which appears there and select "Edit configurations"

2. Click New>Gradle. Under "Run" set it to "bootRun" and in the environment variables fill in:
   `DB_USERNAME=exampleUsername;`

`DB_PASSWORD=examplePassword;`

`DB_HOST=exampleHost;`

`DB_DATABASE=exampleDatabase;`

from whatever MySQL connection you want to use.

3. Make sure to click apply and okay, and close the window. The new profile you've just created names "jp-backend [bootRun]" can now be selected, and you can click on the green play icon to run the backend.


4. When you see something that looks like log entries with dates in the terminal it means it is running. The endpoints provided by src/java/com.dat3.jpgroentbackend/controllers can be found at: http://localhost:8080/swagger-ui/index.html
## Frontend

The frontend system can be found here [jp-frontend](https://github.com/P3-Projekt/jp-frontend)


## Environment Variables

To run this project, you will need to add the following environment variables:

`DB_USERNAME`

`DB_PASSWORD`

`DB_HOST`

`DB_DATABASE`

## Used By

This project is used by the following companies:

- JP-Grønt ApS

