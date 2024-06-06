# webCrawler

Clone the repository and import as existing maven project.
Run the WebCrawlerApplication as java Application the embedded tomcat server will start on port 5000.
To create the jar file. run as maven build with goal:package
In target location: java -jar webCrawler-0.0.1-SNAPSHOT.jar

Rest API endpoint description

Method: Get			

Sample Url: /{domainLink}/crawl

Example {domainLink} : /yahoo.com/crawl
						/google.com/crawl
						
The path variable for domain Link should be passed in this format to start the crawling


Url param: limit(optional)
			Max pages to be crawled
			
Example: /yahoo.com/crawl?limit=100
		Limit param is optional with default crawling to 200 pages.
		
		
Output:
Json object in String

For documentation visit : http://localhost:5000/swagger-ui/index.html#/crawler-controller/crawlDomain

Live RestApi deployed on AWS : http://webcrawler-env.eba-ssdxbt2p.us-east-1.elasticbeanstalk.com/yahoo.com/crawl?limit=10

If given more time then following are the steps that could have been possible:
Following are the steps that could be done :
	1. MongoDB Integration: Instead of crawling the webpage every time would have stored the crawled data in MongoDB. So whenever a request is made it will get a quick response. This will increase the data fetching.
	2. CronJob: Would have made a separate API that will flush existing data from the database and pull the latest data and store it in the database, so whenever a webservice is used it makes sure to give the latest data in response.
	3. Retry after few minutes if the particular link is not responding.
	4. Test cases on multiple html pages.









 import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class JsonParserExample {

    public static void main(String[] args) {
        try {
            // Read JSON from a file
            String content = new String(Files.readAllBytes(Paths.get("path/to/your/jsonfile.json")));
            JSONObject root = new JSONObject(content);

            Set<GroupDetails> groupDetailsSet = new HashSet<>();

            // Navigate to the 'dle' array
            JSONArray dleArray = root.getJSONArray("dle");
            for (int i = 0; i < dleArray.length(); i++) {
                JSONObject dleNode = dleArray.getJSONObject(i);
                GroupDetails groupDetails = new GroupDetails();

                // Parse man_geo
                JSONArray manGeoArray = dleNode.getJSONArray("man_geo");
                for (int j = 0; j < manGeoArray.length(); j++) {
                    JSONObject manGeoNode = manGeoArray.getJSONObject(j);
                    groupDetails.setGeoGroupId(manGeoNode.getString("groudId"));
                    groupDetails.setGeoGroupName(manGeoNode.getString("groupName"));
                }

                // Parse man_seg
                JSONArray manSegArray = dleNode.getJSONArray("man_seg");
                for (int j = 0; j < manSegArray.length(); j++) {
                    JSONObject manSegNode = manSegArray.getJSONObject(j);
                    groupDetails.setSegGroupId(manSegNode.getString("groudId"));
                    groupDetails.setSegGroupName(manSegNode.getString("groupName"));
                }

                // Add the GroupDetails object to the set
                groupDetailsSet.add(groupDetails);
            }

            // Print or use the set of GroupDetails
            for (GroupDetails details : groupDetailsSet) {
                System.out.println(details);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
