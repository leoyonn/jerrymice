# jerrymice
## You know tomcat? Then you should know what jerrymice is ;)
A very simple jetty-based web/http server.

## Usage
  1. Define your Handler extends BaseHttpHandler with annotation @Path
  2. Add restful apis in your handler with annotation such as @Get/@Put, with @Param for parameters
  3. Start with HttpServer.addHandler(your-handler).start(port)

## Features:
  1. parameters in query string;
  2. parameters in url;
  3. Regular expressions in path, such as /user/[0-9]+;
  4. Model to add result into web page;
  5. Jsp;
  6. Async and timeout;
  7. Upload file;

## Caution:
  1. Handler's @Path should be not empty("/" or ""), and not same with others.

## Just go to JerryMiceDemo to find out how to use, and have fun!
