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


Minutes of Meeting (MoM)
Date: September 5, 2024
Attendees:

Bel Prasad 
Nizam 
Aniket
Shailesh

Agenda:
Address the Jenkins builds being in a prolonged waiting state, which impact the retrieval of the Jenkins build ID.

Suggested Solution:

Bel Prasad reached out to the support team, who recommended changing the Jenkins build configuration:
Restrict where this project will run: KUB.

RLM Callback Enhancement:

To ensure the usecaseId is included in the RLM callback payload, updates are required in the scripts.

 scripts in both Knime and Tableau will be updated to retrieve the usecaseId from a configuration file.
Action Items:
OF Team:

Update the Knime Configuration File Naming Convention
The Knime cfg file name should be updated to: WFName_UsecaseID.cfg.
Knime Team:

Update PostInstall Script
Modify the PostInstall script to parse the updated cfg file (WFName_UsecaseID.cfg), retrieve the usecaseId, and include it in the RLM callback payload.
Tableau Team:

Update PostInstall Script
Modify the PostInstall script to parse the updated cfg file (WFName_UsecaseID.cfg), retrieve the usecaseId, and ensure it is included in the RLM callback payload.
POC for Jenkins Callback (Triggered After Build Completion)

OF Team:
Develop a REST API endpoint to receive the Jenkins build completion callbacks.
The API should handle a payload that includes the Jenkins build ID, usecaseId, and other relevant metadata.
A sample payload format will be provided.
Bel (Tableau):
Create a Jenkins pipeline script that triggers the REST API call upon build completion. The script should send the necessary metadata (e.g., Jenkins build ID, status, usecaseId) in the payload.


