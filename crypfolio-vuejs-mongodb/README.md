As it is a Maven Project,  it was decided to separate the architecture for two separate parts (Maven modules) with 
main pom.xml in the project root:
+ frontend part with it's own pom.xml to build the frontend module via nodejs
+ backend part witch compiles java classes, copies frontend built sources to web directory of the backend project
 for then to package all that to the only WAR archive file to deploy on the WildFly (or other java application server)

**Project structure:**
```
crypfolio-vuejs-mongodb
├─┬ backend     → backend module all Java code
│ ├── src
│ ├── web
│ └── pom.xml
│ 
├─┬ frontend    → frontend module with Vue.js code
│ ├── src
│ └── pom.xml
│ 
└── pom.xml     → Maven parent pom managing both modules
```    

### MongoDB Document Structure:

![Logical Data Model](data-model/mongodb-document-structures.png)

### Some Working Screenshorts:

![Login Page](../images/login-page-vuejs-mongodb.png)