{
  dataSources(filter: { name: "YourDataSourceName" }) {
    id
    name
    projectName
    projectHierarchy {
      name
    }
  }
}


curl -X POST 'https://<your-tableau-server>/api/metadata/graphql' \
-H 'Content-Type: application/json' \
-H 'X-Tableau-Auth: <your-auth-token>' \
-d '{"query": "{ dataSources(filter: { name: \"YourDataSourceName\" }) { id name projectName projectHierarchy { name } } }"}'
