/*
 * Copyright (C) 2022 Hal Perkins.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Winter Quarter 2022 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package campuspaths;

import campuspaths.utils.CORSFilter;
import pathfinder.CampusMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkServer {

    public static void main(String[] args) {
        CORSFilter corsFilter = new CORSFilter();
        corsFilter.apply();
        // The above two lines help set up some settings that allow the
        // React application to make requests to the Spark server, even though it
        // comes from a different server.
        // You should leave these two lines at the very beginning of main().

        CampusMap map = new CampusMap();

        Spark.get("/findPath", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                String start = request.queryParams("start");
                String end = request.queryParams("end");
                if(start == null || end == null) {
                    Spark.halt(400);
                }
                return map.findShortestPath(start, end);
            }
        });

        Spark.get("/buildingNames", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return map.buildingNames();
            }
        });

        // TODO: Create all the Spark Java routes you need here.
    }

}
