[![Build Status](https://travis-ci.com/benskov95/CA3-Backend-startcode.svg?branch=master)](https://travis-ci.com/benskov95/CA3-Backend-startcode)
# BornIT (Group 8) backend startcode

Our backend startcode comes complete with a fully functional login system (that uses JWT), with User and Role entity classes, a UserFacade containing various methods such as addUser and deleteUser, along with a resource class with corresponding endpoints.  It also contains an Example resource and the ExampleFetcher class, which by default fetches data from five different endpoints (and can be used as a template for similar operations), which can then be displayed on the frontend startcode. 

In order to get started, you MUST change:

- the database in the persistence.xml file to the one you want to use.
- the 'remote server' property in the pom.xml file to your own website's URL (for deployment).
- the connection string in the EMF_CREATOR class (located in the utils folder) to match your own connection string on your droplet (located in the docker-compose.yml file).

Also, make sure to:

- Add users to your database, either via the provided addUser endpoint - /api/users as POST request in Postman, or from the frontend startcode's register option. The backend has to be running for this to work, of course.
- Add a user with the username "admin" to get a user with the role admin to start you off. You can of course manually change user roles via WorkBench if necessary, or add more roles to one user.

Once this has been done, you can freely add more entity classes, DTOs, facades etc. to start working on your own project.
