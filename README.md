Project to study and gain experience in the following technologies whilst also providing a useful product.

* Java - 11
* Spring Boot
* React
* Redux

### Usage

1. Build the application from IntelliJ
1. Create a redis container, using the following commands
   1. `docker pull redis` (first time only)
   1. `docker run --name redis -p 6379:6379 -d redis`
1. Launch the application from IntelliJ (use the `retrospect.web`) run configuration _committed to VCS_
1. Open a new command prompt and CD to `retrospect.web/`
   1. Run `npm install` (first time only)
   1. Run `npm run-script watch` (will watch for any changes to the React source files and compile them as required)
1. Open http://localhost:8080 in the browser and login using your GitHub account


### Other technologies
These technologies are also used, but not related to the primary purpose of gaining experience

- IntelliJ
- Node (npm)
- Docker
- Redis