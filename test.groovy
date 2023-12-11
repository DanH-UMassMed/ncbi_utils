class SearchHelper {
    static def staticSearch(Map params) {
        def searchTerm = params.containsKey("searchTerm") ? params.searchTerm : ""
        def db = params.containsKey("db") ? params.db : "pubmed"
        def dumpResult = params.containsKey("dumpResult") ? params.dumpResult : false

        println("Search term: $searchTerm, Database: $db, Dump result: $dumpResult")
    }
}

SearchHelper.staticSearch("abc", dumpResult: true)