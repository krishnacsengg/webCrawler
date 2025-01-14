public String fetchUserDNByCN(String commonName, LdapContext ctx) throws Exception {
    // Define the search base - this depends on your LDAP directory structure
    String searchBase = "DC=YourDomain,DC=com"; // Adjust as per your LDAP structure

    // Define the search filter to look for the CN
    String searchFilter = "(cn=" + commonName + ")";

    // Configure search controls
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search in the entire subtree

    // Perform the search
    NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchControls);

    // Retrieve the distinguished name (DN) if a match is found
    if (results.hasMore()) {
        SearchResult result = results.next();
        return result.getNameInNamespace(); // Returns the full DN
    } else {
        throw new Exception("No user found with CN: " + commonName);
    }
}
